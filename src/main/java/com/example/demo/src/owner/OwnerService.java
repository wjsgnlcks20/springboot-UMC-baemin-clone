package com.example.demo.src.owner;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.owner.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtOwnerService;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

/**
 * Service란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Create, Update, Delete 의 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
// [Business Layer]는 컨트롤러와 데이터 베이스를 연결
@Transactional
public class OwnerService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final OwnerDao ownerDao;
    private final OwnerProvider ownerProvider;
    private final JwtOwnerService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    @Autowired //readme 참고
    public OwnerService(OwnerDao ownerDao, OwnerProvider ownerProvider, JwtOwnerService jwtService) {
        this.ownerDao = ownerDao;
        this.ownerProvider = ownerProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************
    // 회원가입(POST)
    public PostOwnerRes createOwner(PostOwnerReq postOwnerReq) throws BaseException {
        // 중복 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (ownerProvider.checkEmail(postOwnerReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try {
            // 암호화: postownerReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postOwnerReq.getPassword()); // 암호화코드
            postOwnerReq.setPassword(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int ownerIdx = ownerDao.createOwner(postOwnerReq);
//            return new PostownerRes(ownerIdx);

//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
            //jwt 발급.
            String jwt = jwtService.createJwt(ownerIdx);
            return new PostOwnerRes(ownerIdx, jwt);
//  *********************************************************************
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원정보 수정(Patch)
    public void modifyOwner(PatchOwnerReq patchownerReq) throws BaseException {
        try {
            int result;

            // nickname 수정
            // patch 이므로 값이 수정하는 요소가 존재하지 않는다고 해서 에러가 아니기에 if문 처리해준다.
            if(patchownerReq.getNickname() != null) {
                result = ownerDao.modifyOwnerName(patchownerReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
                if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                    throw new BaseException(MODIFY_FAIL_USERNAME);
                }
            }
            // phoneNum 수정
            if(patchownerReq.getPhoneNum() != null){
                result = ownerDao.modifyOwnerPhoneNum(patchownerReq);
                if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                    throw new BaseException(MODIFY_FAIL_USERPHONENUM);
                }
            }
            // 위에서 result 0인 경우에 대해서는 지정된 에러 반환시켰는데, result != 0이면서도 또 다른 에러가 발생 될 수 있는것인가?
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
