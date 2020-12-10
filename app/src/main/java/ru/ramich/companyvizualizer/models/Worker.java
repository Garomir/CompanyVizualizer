package ru.ramich.companyvizualizer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;

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
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("foto")
    @Expose
    private String foto;

    public Worker() {
    }

    public Worker(String firstname, String lastname, String position, int salary, String birthday) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.position = position;
        this.salary = salary;
        this.birthday = birthday;
    }

    public Worker(int id, String firstname, String lastname, String position, int salary, String birthday) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.position = position;
        this.salary = salary;
        this.birthday = birthday;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
