package ru.fiarr4ik.oceanblockseller.dto;

    public class Item {

        private String name;
        private int amount;
        private double minPrice;
        private double maxPrice;

        public Item() {
        }

        public Item(String name, int amount, double minPrice, double maxPrice) {
            this.name = name;
            this.amount = amount;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAmount() {
            return amount;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public double getMaxPrice() {
            return maxPrice;
        }
    }
