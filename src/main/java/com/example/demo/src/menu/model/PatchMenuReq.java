package com.example.demo.src.menu.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchMenuReq {
    private int menuIdx;
    private String name;
    private int price;
    private String imageUrl;
}
