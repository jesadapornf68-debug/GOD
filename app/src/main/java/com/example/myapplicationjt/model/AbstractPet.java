package com.example.myapplicationjt.model;

/**
 * Abstract class representing the Abstraction and Encapsulation principles of OOP
 */
public abstract class AbstractPet {
    private String id;
    private String pName;
    private int pAge;
    private double pWeight;

    public AbstractPet(String pName, int pAge, double pWeight) {
        this.pName = pName;
        this.pAge = pAge;
        this.pWeight = pWeight;
    }

    public AbstractPet(String id, String pName, int pAge, double pWeight) {
        this.id = id;
        this.pName = pName;
        this.pAge = pAge;
        this.pWeight = pWeight;
    }

    // Abstract methods (Polymorphism & Abstraction)
    public abstract String getInfo();
    public abstract String getSpecies();

    // Encapsulation: Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPetName() {
        return pName;
    }

    public void setPetName(String name) {
        this.pName = name;
    }

    public int getAge() {
        return pAge;
    }

    public void setAge(int age) {
        this.pAge = age;
    }

    public double getWeight() {
        return pWeight;
    }

    public void setWeight(double weight) {
        this.pWeight = weight;
    }
}
