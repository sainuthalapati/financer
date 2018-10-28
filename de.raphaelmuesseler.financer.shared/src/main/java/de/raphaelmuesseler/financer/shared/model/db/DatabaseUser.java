package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;

public class DatabaseUser implements DatabaseObject, Serializable {
    private static final long serialVersionUID = 8551108621522985674L;
    protected int id;
    protected String email, password, salt, name, surname;

    public DatabaseUser() {

    }

    public DatabaseUser(String email, String password, String salt, String name, String surname) {
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.surname = surname;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName() {
        return this.name + " " + this.surname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
