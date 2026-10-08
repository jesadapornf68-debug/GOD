package com.example.myapplicationjt.model;

public class VaccineRecord {
    private String vaccineName;
    private String dateAdministered;
    private String nextDueDate;

    public VaccineRecord() {}

    public VaccineRecord(String vaccineName, String dateAdministered, String nextDueDate) {
        this.vaccineName = vaccineName;
        this.dateAdministered = dateAdministered;
        this.nextDueDate = nextDueDate;
    }

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getDateAdministered() { return dateAdministered; }
    public void setDateAdministered(String dateAdministered) { this.dateAdministered = dateAdministered; }

    public String getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(String nextDueDate) { this.nextDueDate = nextDueDate; }
}
