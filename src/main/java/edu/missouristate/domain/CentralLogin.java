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
	
	
	@OneToMany(mappedBy = "centralLogin")
	  private List<Mastodon> mastodonPosts;
	
	@OneToMany(mappedBy = "centralLogin")
	  private List<RedditPosts> redditPosts;
	
	@OneToMany(mappedBy = "centralLogin")
	  private List<Tumblr> tumblrPosts;
	
	@OneToMany(mappedBy = "centralLogin")
	  private List<Twitter> twitterPosts;

	public List<Mastodon> getMastodonPosts() {
		return mastodonPosts;
	}

	public void setMastodonPosts(List<Mastodon> mastodonPosts) {
		this.mastodonPosts = mastodonPosts;
	}

	public List<RedditPosts> getRedditPosts() {
		return redditPosts;
	}

	public void setRedditPosts(List<RedditPosts> redditPosts) {
		this.redditPosts = redditPosts;
	}

	public List<Tumblr> getTumblrPosts() {
		return tumblrPosts;
	}

	public void setTumblrPosts(List<Tumblr> tumblrPosts) {
		this.tumblrPosts = tumblrPosts;
	}

	public List<Twitter> getTwitterPosts() {
		return twitterPosts;
	}

	public void setTwitterPosts(List<Twitter> twitterPosts) {
		this.twitterPosts = twitterPosts;
	}

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