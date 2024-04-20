package edu.missouristate.dto;

public class MastodonPostDTO {
    private String id;
    private String userId;
    private String content;
    private String url;
    private int favouritesCount;

    public MastodonPostDTO() {
    }

    public MastodonPostDTO(String id, String userId, String content, String url, int favouritesCount) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.url = url;
        this.favouritesCount = favouritesCount;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }
}
