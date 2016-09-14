package com.qf.entity;

public class ContactEntity {
	private String name ;
	private int id ;//id
	private String phone ;
//	private String email;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
//	public String getEmail() {
//		return email;
//	}
//	public void setEmail(String email) {
//		this.email = email;
//	}
	@Override
	public String toString() {
		return "ContactEntity [name=" + name + ", id=" + id + ", phone="
				+ phone + "]";
	}
	
	
	
}
