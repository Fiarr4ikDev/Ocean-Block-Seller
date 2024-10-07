package ru.fiarr4ik.oceanblockseller.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public class Item {

        private String name;
        private int amount;
        private double minPrice;
        private double maxPrice;

    }
