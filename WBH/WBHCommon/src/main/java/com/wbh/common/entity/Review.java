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
@Table(name = "sessions")
public class Review {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;
		
		@Column(length = 240, nullable = false)
		private String review;
		
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

}
