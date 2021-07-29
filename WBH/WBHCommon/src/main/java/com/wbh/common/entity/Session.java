package com.wbh.common.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;


@Entity
@Table(name = "sessions")
public class Session {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 240, nullable = false, unique = true)
	private String name;
	
	@Column(length = 64, nullable = false)
	private String description;
	
	@Column(name = "created_time", nullable = false)
	private Date createdTime;
	
	@Column(length = 45, nullable = false)
	private String duration;
	
	@Column(name = "start_time", nullable = false)
	private String startTime;
	
	@Column(nullable = false)
	private String date;
	
	private float price;
	
	private boolean enabled;
	
	@ManyToOne
	@JoinColumn(name="professional_id")	
	private Professional professional;

	@ManyToOne
	@JoinColumn(name="customer_id")	
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name="categories_id")	
	private Category categories;

	
	
	public Session(String name, String description, String duration, String startTime, String date,
			float price) {
		this.name = name;
		this.description = description;
		this.duration = duration;
		this.startTime = startTime;
		this.date = date;
		this.price = price;
	}

	public Session() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Professional getProfessional() {
		return professional;
	}

	public void setProfessional(Professional professional) {
		this.professional = professional;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Category getCategories() {
		return categories;
	}

	public void setCategories(Category categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "Session [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
	


}
