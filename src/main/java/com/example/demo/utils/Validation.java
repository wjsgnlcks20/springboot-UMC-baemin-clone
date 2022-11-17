package com.example.demo.utils;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

public class Validation {
    /**
     * Controller : [/app/users/sign-up]
     * Validation Method 모음
     */
    public static void validateEmail(String email) throws BaseException{
        if (email == null) {
            throw new BaseException(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(email)) {
            throw new BaseException(POST_USERS_INVALID_EMAIL);
        }
    }

    public static void validatePassword(String password) throws BaseException{
        if(password == null){
            throw new BaseException(POST_USERS_EMPTY_PASSWORD);
        }
        if(!isRegexPassword(password)){
            throw new BaseException(POST_USERS_INVALID_PASSWORD);
        }
    }

    public static void validatePhoneNum(String phoneNum) throws BaseException{
        if(phoneNum == null){
            throw new BaseException(POST_USERS_EMPTY_PHONENUM);
        }
        if(!isRegexPhoneNum(phoneNum)){
            throw new BaseException(POST_USERS_INVALID_PHONENUM);
        }
    }

    // LoginValidation

}
