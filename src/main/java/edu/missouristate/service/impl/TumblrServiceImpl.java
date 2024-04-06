package edu.missouristate.service.impl;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import edu.missouristate.dao.TumblrRepository;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.service.SocialMediaAccountService;
import edu.missouristate.service.TumblrService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@Service
public class TumblrServiceImpl implements TumblrService {

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private TumblrRepository tumblrRepository;

    private OAuth10aService oauthService;
    private OAuth1RequestToken requestToken;
    private OAuth1AccessToken accessToken;
    private String blogIdentifier;

    @Value("${tumblr.consumerKey}")
    private String consumerKey;

    @Value("${tumblr.consumerSecret}")
    private String consumerSecret;

    @Value("${tumblr.callbackUrl}")
    private String callbackUrl;

    @PostConstruct
    private void init() {
        oauthService = new ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .callback(callbackUrl)
                .build(TumblrApi.instance());
    }


    @Override
    public String getAuthorizationUrl() {
        try {
            requestToken = oauthService.getRequestToken();
            return oauthService.getAuthorizationUrl(requestToken);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public String getUserInfo(String oauthVerifier) {
        try {
            accessToken = oauthService.getAccessToken(requestToken, oauthVerifier);
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.tumblr.com/v2/user/info");
            oauthService.signRequest(accessToken, request);
            Response response = oauthService.execute(request);
            System.out.println(response);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            System.out.println(jsonResponse);

            String blogUrl = jsonResponse.getJSONObject("response")
                    .getJSONObject("user")
                    .getJSONArray("blogs")
                    .getJSONObject(0)
                    .getString("url");

            System.out.println(blogUrl);
            blogIdentifier = blogUrl.substring(blogUrl.lastIndexOf('/') + 1);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving user info";
        }
    }

    private Date getPostDate(String postId) throws IOException, ExecutionException, InterruptedException {

        String url = "https://api.tumblr.com/v2/blog/" + blogIdentifier + "/posts?api_key=" + consumerKey + "&id=" + postId;

        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        request.addBodyParameter("type", "text");
//        request.addBodyParameter("body", postContent);
        oauthService.signRequest(accessToken, request);

        Response response = oauthService.execute(request);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        String dateString = jsonResponse.getJSONObject("response").getJSONArray("posts").getJSONObject(0).getString("date");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);

        ZonedDateTime dateTime = ZonedDateTime.parse(dateString, formatter);

        return Date.from(dateTime.toInstant());
    }

    @Override
    public void postToBlog(String postContent) throws Exception {

        Tumblr post = new Tumblr();

        String postUrl = "https://api.tumblr.com/v2/blog/" + blogIdentifier + "/post";
//        System.out.println(postUrl);

        OAuthRequest request = new OAuthRequest(Verb.POST, postUrl);
        request.addBodyParameter("type", "text");
        request.addBodyParameter("body", postContent);
        oauthService.signRequest(accessToken, request);


        Response response = oauthService.execute(request);
        JSONObject jsonResponse = new JSONObject(response.getBody());

        String postId = jsonResponse.getJSONObject("response").getString("id_string");

        Date date = getPostDate(postId);
        Timestamp timestampDate = new Timestamp(date.getTime());

        System.out.println(timestampDate);

        post.setPostId(postId);
        post.setContent(postContent);
        post.setBlogIdentifier(blogIdentifier);
        post.setPostUrl("https://www.tumblr.com/blog/view/" + blogIdentifier + "/" + postId);
        post.setNoteCount(0);
        post.setDate(timestampDate);
        String tokenValue = accessToken.getToken();
        String tokenSecret = accessToken.getTokenSecret();

//        post.setAccessToken(tokenValue);
//        post.setAccessTokenSecret(tokenSecret);

        tumblrRepository.save(post);

        if (response.getCode() != 201) {
            throw new RuntimeException("Failed to post to Tumblr: " + response.getBody());
        }

        socialMediaAccountService.saveSocialMediaAccount(4, "Tumblr", String.valueOf(requestToken));
    }

    @Override
    public List<Tumblr> getPostsByBlog() {

        List<Tumblr> posts = new ArrayList<>();

        try {

            posts = tumblrRepository.getPostsByBlogIdentifier(blogIdentifier);

            // this might be useful later
//            OAuthRequest request = new OAuthRequest(Verb.GET, url);
//            oauthService.signRequest(accessToken, request);
//            Response response = oauthService.execute(request);

//            JSONObject jsonResponse = new JSONObject(response.getBody());
//            JSONArray postArray = jsonResponse.getJSONObject("response").getJSONArray("posts");

//            for (int i = 0; i < postArray.length(); i++) {
//                JSONObject postObject = postArray.getJSONObject(i);
//                String postUrl = postObject.getString("post_url");
//                postUrlList.add(postUrl);
//            }


        } catch (Exception e) {
            throw new RuntimeException("Failed to get user post urls: " + e.getMessage());
        }

        return posts;
    }

    @Override
    public void updatePosts() throws IOException, ExecutionException, InterruptedException {

//        TumblrAccessToken tumblrAccessTokenDetails = tumblrRepository.getTumblrAccessToken(blogIdentifier);
//
//        if (tumblrAccessTokenDetails == null ||
//                tumblrAccessTokenDetails.getAccessToken() == null ||
//                tumblrAccessTokenDetails.getAccessTokenSecret() == null) {
//            throw new IllegalStateException("Stored access token or secret is missing.");
//        }
//
//        OAuth1AccessToken accessToken = new OAuth1AccessToken(
//                tumblrAccessTokenDetails.getAccessToken(),
//                tumblrAccessTokenDetails.getAccessTokenSecret()
//        );


        Tumblr post = new Tumblr();

        String url = "https://api.tumblr.com/v2/blog/" + blogIdentifier + "/posts?api_key=" + consumerKey;

        String postId;
        String content;
        String postUrl;
        int notesCount;
        String timestamp;
        Timestamp timestamp1;

        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        request.addBodyParameter("type", "text");
//        request.addBodyParameter("body", postContent);
        oauthService.signRequest(accessToken, request);

        Response response = oauthService.execute(request);
        JSONObject jsonResponse = new JSONObject(response.getBody());

        System.out.println(jsonResponse);

        int postNum = jsonResponse.getJSONObject("response").getJSONArray("posts").length();

        for (int i = 0; i < postNum; i++) {

            postId = jsonResponse.getJSONObject("response").getJSONArray("posts").getJSONObject(i).getString("id_string");
            content = jsonResponse.getJSONObject("response").getJSONArray("posts").getJSONObject(i).getString("body");
            postUrl = "https://www.tumblr.com/blog/view/" + blogIdentifier + "/" + postId;
            notesCount = jsonResponse.getJSONObject("response").getJSONArray("posts").getJSONObject(i).getInt("note_count");
            timestamp = jsonResponse.getJSONObject("response").getJSONArray("posts").getJSONObject(i).getString("date");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);

            ZonedDateTime dateTime = ZonedDateTime.parse(timestamp, formatter);

            timestamp1 = new Timestamp(Date.from(dateTime.toInstant()).getTime());

            post.setPostId(postId);
            post.setBlogIdentifier(blogIdentifier);
            post.setContent(content);
            post.setPostUrl(postUrl);
            post.setNoteCount(notesCount);
            post.setDate(timestamp1);

            tumblrRepository.updatePost(post);

        }
    }

    @Override
    public List<String> getAllTubmlrIds() {
        return tumblrRepository.getAllTumblrIds();
    }

    @Override
    public List<Tumblr> tumblrPosts(List<String> tublrIds) {
        return tumblrRepository.tumblrPosts(tublrIds);
    }
}