package ru.ramich.companyvizualizer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Worker {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("salary")
    @Expose
    private int salary;

    public Worker() {
    }

    public Worker(String firstname, String lastname, String position, int salary) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.position = position;
        this.salary = salary;
    }

    public Worker(int id, String firstname, String lastname, String position, int salary) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.position = position;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
