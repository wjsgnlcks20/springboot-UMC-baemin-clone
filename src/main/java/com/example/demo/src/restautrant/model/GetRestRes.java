package com.example.demo.src.restautrant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Setter
@Getter
@NoArgsConstructor
public class GetRestRes {
    private int restaurantIdx;
    private int ownerIdx;
    private String name;
    private String category;
    private int minimumPaymentCost;
    private String imageUrl;
    private int deliveryTime;
    private int deliveryCost;
}
