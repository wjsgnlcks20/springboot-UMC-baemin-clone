package com.example.demo.src.menu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostMenuReq {
    private int menuIdx;
    private int restaurantIdx;
    private String name;
    private int price;
    private String imageUrl;
}
