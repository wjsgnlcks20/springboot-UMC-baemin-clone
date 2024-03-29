package com.example.demo.src.owner;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.owner.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtOwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
// [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class OwnerProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final OwnerDao ownerDao;
    private final JwtOwnerService jwtOwnerService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public OwnerProvider(OwnerDao ownerDao, JwtOwnerService jwtOwnerService) {
        this.ownerDao = ownerDao;
        this.jwtOwnerService = jwtOwnerService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************


    // 로그인(password 검사)
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {

        // 현재 로그인하려는 이메일이 DB에 존재하는지 validate
        if(checkEmail(postLoginReq.getEmail()) == 0) {
            throw new BaseException(FAILED_TO_LOGIN);
        }

        Owner owner = ownerDao.getPwd(postLoginReq);
        String password;

        // 비밀번호 암호화
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(owner.getPassword()); // 복호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
            // 라고 적혀있지만, 여기서는 저장된 키를 복호화시켜 서로를 비교하는 듯 하다.
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (postLoginReq.getPassword().equals(password)) { //비말번호가 일치한다면 ownerIdx를 가져온다.
            int ownerIdx = ownerDao.getPwd(postLoginReq).getOwnerIdx();
//            return new PostLoginRes(ownerIdx);
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해제 및 대체해주세요!  **************** //
            String jwt = jwtOwnerService.createJwt(ownerIdx);
            return new PostLoginRes(ownerIdx,jwt);
//  **************************************************************************

        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    // 해당 이메일이 이미 owner Table에 존재하는지 확인
    public int checkEmail(String email) throws BaseException {
        try {
            return ownerDao.checkEmail(email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // owner들의 정보를 조회
    public List<GetOwnerRes> getOwners() throws BaseException {
        try {
            List<GetOwnerRes> getOwnerRes = ownerDao.getOwners();
            return getOwnerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 nickname을 갖는 owner들의 정보 조회
    public List<GetOwnerRes> getOwnersByNickname(String nickname) throws BaseException {
        try {
            List<GetOwnerRes> getownersRes = ownerDao.getOwnersByNickname(nickname);
            return getownersRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해당 ownerIdx를 갖는 owner의 정보 조회
    public GetOwnerRes getOwner(int ownerIdx) throws BaseException {
        try {
            GetOwnerRes getownerRes = ownerDao.getOwner(ownerIdx);
            return getownerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
