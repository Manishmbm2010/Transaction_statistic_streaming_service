package com.stat.model;

public class Transaction {

	double amount;
	long timestamp;

	public Transaction() {

	}

	public Transaction(double amount, long timestamp) {
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}