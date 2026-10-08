package com.example.myapplicationjt.model;

/**
 * Dog class demonstrating Inheritance and Polymorphism in OOP
 */
public class Dog extends AbstractPet {
    private String breed;

    public Dog(String pName, int pAge, double pWeight, String breed) {
        super(pName, pAge, pWeight);
        this.breed = breed;
    }

    public Dog(String id, String pName, int pAge, double pWeight, String breed) {
        super(id, pName, pAge, pWeight);
        this.breed = breed;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    @Override
    public String getSpecies() {
        return "สุนัข (Dog)";
    }

    @Override
    public String getInfo() {
        return "สุนัข: " + getPetName() + " (สายพันธุ์: " + breed + ", อายุ: " + getAge() + " ปี, น้ำหนัก: " + getWeight() + " กก.)";
    }
}
