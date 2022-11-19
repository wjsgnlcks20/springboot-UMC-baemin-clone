package com.example.demo.src.owner.model;

import lombok.*;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
/**
 * Req.java: From Client To Server
 * 회원정보 수정 요청(Patch Request)을 하기 위해 서버에 전달할 데이터의 형태
 * 템플릿에서 제공된 것을 보면 당장은 이름만 변경 가능하도록 한 듯 하다.
 */
public class PatchOwnerReq {
    private int ownerIdx;
    private String nickname;
    private String phoneNum;
}
