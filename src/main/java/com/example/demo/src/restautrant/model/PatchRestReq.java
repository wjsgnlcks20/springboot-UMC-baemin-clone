package com.example.demo.src.restautrant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PatchRestReq {
    private int restaurantIdx;
    private String name;
    private String category;
    private int minimumPaymentCost;
    private String imageUrl;
}

