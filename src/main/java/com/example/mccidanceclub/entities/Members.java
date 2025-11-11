package com.example.mccidanceclub.entities;

public class Members {
    private int memberId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String memberClass;

    // Constructeur vide (obligatoire pour certaines librairies comme Hibernate)
    public Members() {
    }

    // Constructeur complet
    public Members(int memberId, String firstName, String lastName, String email, String phone, String memberClass) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.memberClass = memberClass;
    }

    // Constructeur sans l'id (utile quand il est auto-incrémenté dans la BDD)
    public Members (String firstName, String lastName, String email, String phone, String memberClass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.memberClass = memberClass;
    }

    // Getters et Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMemberClass() {
        return memberClass;
    }

    public void setMemberClass(String memberClass) {
        this.memberClass = memberClass;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", memberClass='" + memberClass + '\'' +
                '}';
    }
}

