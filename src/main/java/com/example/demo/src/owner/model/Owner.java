package com.example.demo.src.owner.model;

import com.example.demo.src.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
/**
 * 가게 사장님들만 새로운 가게를 신청해 등록할 수 있다.
 */
public class Owner {
    private int ownerIdx;
    private String email;
    private String password;
    private String nickname;
    private String phoneNum;
}
