package ru.fiarr4ik.oceanblockseller.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public class Item {

        private String name;
        private int amount;
        private int minPrice;
        private int maxPrice;

    }
