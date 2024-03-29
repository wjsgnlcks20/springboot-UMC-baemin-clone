package com.example.demo.src.restautrant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Restaurant {
    private int restaurantIdx;
    private int ownerIdx;
    private String name;
    private String category;
    private int minimumPaymentCost;
    private String imageUrl;
}