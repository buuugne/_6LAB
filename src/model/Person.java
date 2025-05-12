package model;

import java.time.LocalDate;

public class Person {
    public int id;
    public String firstName;
    public String lastName;
    public String email;
    public String gender;
    public String country;
    public String domainName;
    public LocalDate birthDate;

    public Person(int id, String firstName, String lastName, String email,
                  String gender, String country, String domainName, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.country = country;
        this.domainName = domainName;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return String.format("%d | %s %s | %s | %s | %s | %s | %s",
                id, firstName, lastName, email, gender, country, domainName, birthDate);
    }
}