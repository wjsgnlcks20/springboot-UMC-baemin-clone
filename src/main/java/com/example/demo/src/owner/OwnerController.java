package com.example.demo.src.owner;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.owner.OwnerProvider;
import com.example.demo.src.owner.OwnerService;
import com.example.demo.src.owner.model.*;
import com.example.demo.utils.JwtOwnerService;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/owners")
public class OwnerController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.

    private final OwnerProvider ownerProvider;
    @Autowired
    private final OwnerService ownerService;
    @Autowired
    private final JwtOwnerService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    public OwnerController(OwnerProvider ownerProvider, OwnerService ownerService, JwtOwnerService jwtService) {
        this.ownerProvider = ownerProvider;
        this.ownerService = ownerService;
        this.jwtService = jwtService;
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /owners
     */
    // Body
    @ResponseBody
    @PostMapping("/")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostOwnerRes> createOwner(@RequestBody PostOwnerReq postOwnerReq) {

        try {
            // validation
            Validation.validateEmail(postOwnerReq.getEmail());
            Validation.validatePassword(postOwnerReq.getPassword());
            Validation.validatePhoneNum(postOwnerReq.getPhoneNum());

            PostOwnerRes postownerRes = ownerService.createOwner(postOwnerReq);
            return new BaseResponse<>(postownerRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
     * [POST] /owners/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        // TODO : 로그인 값에 대한 validation 처리
        // TODO : 1. 이메일이 존재하는 이메일인지
        // TODO : 2. 계정 status 관리는 안하고 있지만, 하게 된다면 비활성화된 유저, 탈퇴한 유저 등으로 validation 처리 해줘야함.
        // TODO : JWT 생성 후 반환

        try {
            // validation 1. 이메일이 존재하는 이메일인지 - provider 에서
            // validation 2. 비활성화, 유지, 탈퇴 계정 validation - provider 에서
            // jwt 발행 및 반환. ownerService.logIn에서, postLoginRes 생성자로 jwt 주입해서.
            PostLoginRes postLoginRes = ownerProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 모든 회원들의  조회 API
     * [GET] /owners
     *
     * 또는
     *
     * 해당 닉네임을 같는 유저들의 정보 조회 API
     * [GET] /owners? NickName=
     */
    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/owners
    public BaseResponse<List<GetOwnerRes>> getOwners(@RequestParam(required = false) String nickname) {
        try {
            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
                List<GetOwnerRes> getownersRes = ownerProvider.getOwners();
                return new BaseResponse<>(getownersRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
            List<GetOwnerRes> getownersRes = ownerProvider.getOwnersByNickname(nickname);
            return new BaseResponse<>(getownersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 1명 조회 API
     * [GET] /owners/:ownerIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{ownerIdx}") // (GET) 127.0.0.1:9000/app/owners/:ownerIdx
    public BaseResponse<GetOwnerRes> getOwner(@PathVariable("ownerIdx") int ownerIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 ownerId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get owners
        try {
            GetOwnerRes getownerRes = ownerProvider.getOwner(ownerIdx);
            return new BaseResponse<>(getownerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /owners/:ownerIdx
     */
    @ResponseBody
    @PatchMapping("/{ownerIdx}")
    public BaseResponse<String> modify(@PathVariable("ownerIdx") int ownerIdx, @RequestBody PatchOwnerReq patchOwnerReq) {
        // JWT를 탈취해서 회원 정보 수정 api에 접근할 수 있는 가능성이 있기 때문에 PathVariable도 읽어와 Validation 처리를 해준다.

        try {
            /** validation */
            // 수정하려는 멤버 정보가 잘못 들어온 경우 -> validation을 통해 걸러줘야함.
            // 의도적으로 입력하지 않았을 수 있으므로 null이 아닌 경우에만 검사를 진행.(Validation 메서드 내 에러 throw 방지)

            if(patchOwnerReq.getPhoneNum() != null){
                Validation.validatePhoneNum(patchOwnerReq.getPhoneNum());
            }
            /*************************************************/

            /** jwt */
            //jwt에서 idx 추출.
            System.out.println("현재 jwtOwnerSevice로 jwt가 일치하는지 확인중입니다.");
            int ownerIdxByJwt = jwtService.getUserIdx();
            //ownerIdx와 접근한 유저가 같은지 확인
            //다르다면 접근 권한이 없는 상태로 api 호출
            if(ownerIdx != ownerIdxByJwt){
                return new BaseResponse<>(INVALID_OWNER_JWT);
            }
            Date expirationDate = jwtService.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if(now.after(expirationDate)){
                return new BaseResponse<>(INVALID_JWT);
            }
            //같다면 유저네임 변경
            /************************************************/

//            PatchownerReq patchownerReq = new PatchownerReq(ownerIdx, owner.getNickname(), owner.getPhoneNum());
            // PatchownerReq를 @RequestBody로 받아왔으므로 생략
            // 대신 Patch로 받은 json에 ownerIdx가 담겨있지 않을 수 있으므로
            patchOwnerReq.setOwnerIdx(ownerIdx);
            ownerService.modifyOwner(patchOwnerReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
