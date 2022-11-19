package com.example.demo.src.menu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    private int menuIdx;
    private int restaurantIdx;
    private String name;
    private int price;
    private String imageUrl;
}
