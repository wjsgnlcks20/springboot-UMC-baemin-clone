package com.example.demo.src.user;

import com.example.demo.config.BaseResponseStatus;
import com.example.demo.utils.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/app/users")
// method가 어떤 HTTP 요청을 처리할 것인가를 작성한다.
// 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
// URL(/app/users)을 컨트롤러의 메서드와 매핑할 때 사용
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class UserController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
    @PostMapping("/sign-up")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        // TODO: 여기에서는 controller에서 직접 BaseResponseStatus 를 생성해 반환했다.
        // TODO: 다만 validation이 다양해지며 코드가 길어지기에 validation 객체를 생성해 exception을 thorw 하는 형식으로 refactor 하면 어떨까.

        try {
            // validation
            Validation.validateEmail(postUserReq.getEmail());
            Validation.validatePassword(postUserReq.getPassword());
            Validation.validatePhoneNum(postUserReq.getPhoneNum());

            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        }catch(BaseException exception){
            // 왜 그냥 BaseResponceStatus enum을 반환하지 않고 Exception으로 반환받아 전달하냐?
            // 그야 Exception 을 throw 해야하니까... Exception 클래스를 상속받을 객체를 만들어줘야지...
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        // TODO : 로그인 값에 대한 validation 처리
        // TODO : 1. 이메일이 존재하는 이메일인지
        // TODO : 2. 계정 status 관리는 안하고 있지만, 하게 된다면 비활성화된 유저, 탈퇴한 유저 등으로 validation 처리 해줘야함.
        try {
            // validation
            // createUser 메서드에서 새로운 유저를 생성시 등록한 이메일이 중복되는지를 검증하기위해 checkEmail 메서드를 사용하였지만,
            // 로그인 시 입력한 이메일이 등록된 이메일인지 확인하는 로직은 없다.

            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 모든 회원들의  조회 API
     * [GET] /users
     *
     * 또는
     *
     * 해당 닉네임을 같는 유저들의 정보 조회 API
     * [GET] /users? NickName=
     */
    //Query String
    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    // GET 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickname) {
        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
        try {
            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    // 왜 Patch인데, 심지어 PatchUserRequest 도 만들어 두었는데 @RequestBody를 User로 받았을까...
    // 이렇게 하면 User에 @NoArgsConstructor 설정해주던가 해야할텐데...
    public BaseResponse<String> modify(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq) {
        // TODO : JWT를 탈취해서 회원 정보 수정 api에 접근할 수 있는 가능성이 있기 때문에 PATHVARIABLE로도 읽어와 Validation 처리를 해준다.

        // 수정하려는 멤버 정보가 잘못 들어온 경우 -> validation을 통해 걸러줘야함.
        // 문제는 수정이기 때문에 의도적으로 null 값도 인정한것이라면?
        // 따라서 정규 표현식에 관해서만 확인해준다.

        // 까지가 jwt를 배우기 이전의 validation 이었다면, 현재는 jwt claim 내부의 userIdx 값을 파싱해와 비교해 validation을 진행.
        // 근데 아직도 왜 user를 사용하는지 이해가 안된다.
//        if(patchUserReq.getPhoneNum() != null && !isRegexPhoneNum(patchUserReq.getPhoneNum())){
//            return new BaseResponse<>(POST_USERS_INVALID_PHONENUM);
//        }
        try {
////  *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
////  **************************************************************************

//            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickname(), user.getPhoneNum());
//            근데 왜 이렇게 구현했을까? 매개변수로 받을때부터 수정할 정보만 받으면 되는데.

            userService.modifyUser(patchUserReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
