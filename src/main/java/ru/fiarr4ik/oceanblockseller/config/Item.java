package ru.fiarr4ik.oceanblockseller.config;

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

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            this.minPrice = minPrice;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }
    }
