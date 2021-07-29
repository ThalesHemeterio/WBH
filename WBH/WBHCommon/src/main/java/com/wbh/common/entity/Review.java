package com.wbh.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "review")
public class Review {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;
		
		@Column(length = 240, nullable = false)
		private String title;
		
		@Column(length = 240, nullable = false)
		private String text;
		
		@Column(name = "created_time", nullable = false)
		private Date createdTime;
		
		@Column(nullable = false)
		private Integer rate;
		 
		private boolean enabled;
		
		@ManyToOne
		@JoinColumn(name="professional_id")	
		private Professional professional;

		@ManyToOne
		@JoinColumn(name="customer_id")	
		private Customer customer;
		
		@ManyToOne
		@JoinColumn(name="adm_id")	
		private User user;
	
		public Review(String text, String title, Integer rate, Professional professional, Customer customer,
				User user) {
			super();
			this.text = text;
			this.title = title;
			this.rate = rate;
			this.professional = professional;
			this.customer = customer;
			this.user = user;
		}

		public Review() {
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Date getCreatedTime() {
			return createdTime;
		}

		public void setCreatedTime(Date createdTime) {
			this.createdTime = createdTime;
		}

		public Integer getRate() {
			return rate;
		}

		public void setRate(Integer rate) {
			this.rate = rate;
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

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		@Override
		public String toString() {
			return "Review [id=" + id + ", review=" + text + ", title=" + title + ", createdTime=" + createdTime
					+ ", rate=" + rate + ", enabled=" + enabled + ", professional=" + professional + ", customer="
					+ customer + ", user=" + user + "]";
		}

}
