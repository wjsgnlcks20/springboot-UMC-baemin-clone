package com.example.demo.src.owner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(OwnerIdx, nickname, email, password)를 받는 생성자를 생성
@NoArgsConstructor // Dao에서 RowMapper를 위해 생성자에 매개변수가 없더라도 인스턴스를 생성한다.
public class GetOwnerRes {
    private int ownerIdx;
    private String nickname;
    private String email;
    private String password;
    private String phoneNum;
}
