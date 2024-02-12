package edu.missouristate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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