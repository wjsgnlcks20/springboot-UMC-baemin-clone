package com.example.demo.src.basket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BasketDao {
    private int basketIdx;
    private int userIdx;
    private int menuIdx;
    private int price;
    private int amount;
}
