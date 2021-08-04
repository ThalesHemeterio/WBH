package com.wbh.common.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 128, nullable = false, unique = true)
	private String email;
	
	@Column(length = 64, nullable = false)
	private String password;
	
	@Column(name = "first_name", length = 45, nullable = false)
	private String firstName;
	
	@Column(name = "last_name", length = 45, nullable = false)
	private String lastName;
	
	@Column(name = "date_of_birth", length = 45, nullable = false)
	private String dob;
	
	@Column(length = 45, nullable = false)
	private String contact;
	
	@Column(name = "address_line_1", length = 64, nullable = false)
	private String addressLine1;
	
	@Column(name = "address_line_2", length = 128)
	private String addressLine2;
	
	@Column(name = "postal_code", length = 128, nullable = false)
	private String postalCode;
	
	@Column(length = 128, nullable = false)
	private String city;
	
	@Column(name = "country", length = 128, nullable = false)
	private String country;
	
	@Column(length = 128)
	private String title;
	
	@Column(length = 128)
	private String qualification;
	
	@Column(name="created_time")
	private Date createdTime;
	
	@Column(name="verification_code")
	private String verificationCode;
	
	@Column(length = 64)
	private String photos;
	
	private boolean enabled;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name="user_id"),
			inverseJoinColumns = @JoinColumn(name="role_id")	
			)
	private Set<Role> roles = new HashSet<>();

	public User() {
		
	}
	
	public User(String email, String password, String firstName, String lastName, String dob, String contact,
			String addressLine1, String postalCode, String city, String country) {
		super();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.contact = contact;
		this.addressLine1 = addressLine1;
		this.postalCode = postalCode;
		this.city = city;
		this.country = country;
	}



	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getVerificationCode() {
		return verificationCode;
	}
	
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		this.roles.add(role);
	}

	public Role getFirstRole() {
	    for (Iterator<Role> it = roles.iterator(); it.hasNext(); ) {
	    	Role f = it.next();
	        if (f!=null) {
	            return f;}
	    }
	    return null;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName;
	}
	
	@Transient
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	@Transient
	public String getPhotosImagePath() {
		if(id==null || photos == null) return "/img/default-user.png";
		return "/user-photos/"+this.id+"/"+this.photos;
	}
}
