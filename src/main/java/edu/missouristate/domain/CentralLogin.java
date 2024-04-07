package edu.missouristate.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "central_login")
public class CentralLogin {

	public CentralLogin() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "central_login_id", columnDefinition = "Integer")
	private Integer centralLoginId;

	@Column(name = "username", columnDefinition = "VARCHAR(32)")
	private String username;

	@Column(name = "password", columnDefinition = "VARCHAR(32)")
	private String password;
	
	@Column(name="first_name", columnDefinition = "VARCHAR(32)")
	private String firstName;
	
	@Column(name="last_name", columnDefinition = "VARCHAR(32")
	private String lastName;

	@Override
	public String toString() {
		return "CentralLogin [centralLoginId=" + centralLoginId + ", username=" + username + ", password=" + password
				+ ", firstName=" + firstName + ", lastName=" + lastName + "]";
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

	public Integer getCentralLoginId() {
		return centralLoginId;
	}

	public void setCentralLoginId(Integer centralLoginId) {
		this.centralLoginId = centralLoginId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}