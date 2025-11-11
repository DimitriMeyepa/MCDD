package com.example.mccidanceclub.entities;

public class Contribution {
    private int contributionId;
    private Members member;   // celui qui a donné
    private double amount;    // combien il a donné
    private double balance;   // solde global après cette contribution

    public Contribution(int contributionId, Members member, double amount, double balance) {
        this.contributionId = contributionId;
        this.member = member;
        this.amount = amount;
        this.balance = balance;
    }

    public Contribution(Members member, double amount) {
        this.member = member;
        this.amount = amount;
    }

    public int getContributionId() { return contributionId; }
    public Members getMember() { return member; }
    public double getAmount() { return amount; }
    public double getBalance() { return balance; }

    public void setContributionId(int contributionId) { this.contributionId = contributionId; }
    public void setMember(Members member) { this.member = member; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setBalance(double balance) { this.balance = balance; }
}
