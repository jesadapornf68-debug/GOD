package com.example.myapplicationjt.model;

/**
 * Cat class demonstrating Inheritance and Polymorphism in OOP
 */
public class Cat extends AbstractPet {
    private String color;

    public Cat(String pName, int pAge, double pWeight, String color) {
        super(pName, pAge, pWeight);
        this.color = color;
    }

    public Cat(String id, String pName, int pAge, double pWeight, String color) {
        super(id, pName, pAge, pWeight);
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getSpecies() {
        return "แมว (Cat)";
    }

    @Override
    public String getInfo() {
        return "แมว: " + getPetName() + " (สี/สายพันธุ์: " + color + ", อายุ: " + getAge() + " ปี, น้ำหนัก: " + getWeight() + " กก.)";
    }
}
