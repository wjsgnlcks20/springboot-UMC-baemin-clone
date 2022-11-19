package com.example.demo.src;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceClient {
    private int idx;
    private String email;
    private String password;
    private String nickname;
    private String phoneNum;
}
