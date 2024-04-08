package edu.missouristate.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.querydsl.core.Tuple;

import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.dao.TumblrRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.service.SocialMediaAccountService;
import edu.missouristate.service.TumblrService;

@Transactional
@Service
public class TumblrServiceImpl implements TumblrService {

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;
    
    @Autowired
    private CentralLoginRepository centralLoginRepo;

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

    private OAuth10aService getOauthService() {
        if (this.oauthService == null) {
            this.oauthService = new ServiceBuilder(consumerKey)
                    .apiSecret(consumerSecret)
                    .callback(callbackUrl)
                    .build(TumblrApi.instance());
        }
        return this.oauthService;
    }


    @Override
    public String getAuthorizationUrl() {
        oauthService = new ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .callback(callbackUrl)
                .build(TumblrApi.instance());
        try {
            requestToken = oauthService.getRequestToken();
            return oauthService.getAuthorizationUrl(requestToken);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getUserInfo(String oauthVerifier, Integer centralLoginId) {
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

            // Save or update the Tumblr credentials for the user
            Tumblr tumblrCredentials = new Tumblr();
            tumblrCredentials.setAccessToken(accessToken.getToken());
            tumblrCredentials.setTokenSecret(accessToken.getTokenSecret());
            tumblrCredentials.setBlogIdentifier(blogIdentifier);
            tumblrCredentials.setCentralLogin(centralLoginRepo.findById(centralLoginId).get());
            tumblrRepository.save(tumblrCredentials);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving user info";
        }
    }

    public List<Tumblr> findCentralUserTumblrs(Integer centralLoginId){
    	CentralLogin login = centralLoginRepo.findById(centralLoginId).get();
    	return tumblrRepository.findTumblrsByCentralLogin(login);
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
    public String postToBlog(String postContent) throws Exception {
        // Fetch the latest Tumblr credentials from the repository
        Tuple tumblrUser = tumblrRepository.getLatestUser();
        if (tumblrUser == null) {
            throw new IllegalStateException("No Tumblr credentials available");
        }

        String accessTokenValue = tumblrUser.get(0, String.class);
        String tokenSecretValue = tumblrUser.get(1, String.class);
        String blogIdentifierValue = tumblrUser.get(2, String.class);

        // Ensure the OAuth service is initialized
        OAuth10aService service = getOauthService();
        OAuth1AccessToken accessToken = new OAuth1AccessToken(accessTokenValue, tokenSecretValue);

        String postUrl = "https://api.tumblr.com/v2/blog/" + blogIdentifierValue + "/post";
        OAuthRequest request = new OAuthRequest(Verb.POST, postUrl);
        request.addBodyParameter("type", "text");
        request.addBodyParameter("body", postContent);
        service.signRequest(accessToken, request); // Sign the request with the accessToken

        Response response = service.execute(request);

        if (response.getCode() == 201) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String postId = jsonResponse.getJSONObject("response").getString("id_string");

            // Save post information in the database
//            Tumblr post = new Tumblr();
//            post.setPostId(postId);
//            post.setContent(postContent);
//            post.setBlogIdentifier(blogIdentifierValue);
//            post.setPostUrl("https://www.tumblr.com/blog/view/" + blogIdentifierValue + "/" + postId);
//            post.setNoteCount(0);
//            // Use LocalDateTime.now() to set the current timestamp
//            post.setDate(Timestamp.valueOf(LocalDateTime.now()));
//            post.setAccessToken(accessTokenValue); // Ensure accessToken is set
//            post.setTokenSecret(tokenSecretValue); // Ensure tokenSecret is set
//            tumblrRepository.save(post);

            return postId; // Return the postId if the post was successfully created
        } else {
            // Log or throw an exception with response details for debugging
            throw new RuntimeException("Failed to post to Tumblr: " + response.getCode() + " - " + response.getBody());
        }
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
    public Tuple getLatestUser() {
        return tumblrRepository.getLatestUser();
    }

    @Override
    public void updateOrCreateTumblrPost(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message) {

        Tumblr exists = tumblrRepository.findExistingPostByTokenAndNoText(accessToken);

        //post.setPostUrl("https://www.tumblr.com/blog/view/" + blogIdentifierValue + "/" + postId);

        if (exists != null) {
            exists.setContent(message);
            exists.setDate(Timestamp.valueOf(LocalDateTime.now()));
            exists.setBlogIdentifier(blogIdentifier);
            exists.setPostId(postId);
            exists.setAccessToken(accessToken);
            exists.setTokenSecret(tokenSecret);
            exists.setNoteCount(0);
            exists.setPostUrl("https://www.tumblr.com/blog/view/" + blogIdentifier + "/" + postId);
            tumblrRepository.save(exists);
        } else {
            Tumblr tumblr = new Tumblr();
            tumblr.setContent(message);
            tumblr.setDate(Timestamp.valueOf(LocalDateTime.now()));
            tumblr.setBlogIdentifier(blogIdentifier);
            tumblr.setPostId(postId);
            tumblr.setAccessToken(accessToken);
            tumblr.setTokenSecret(tokenSecret);
            tumblr.setNoteCount(0);
            tumblr.setPostUrl("https://www.tumblr.com/blog/view/" + blogIdentifier + "/" + postId);
            tumblrRepository.save(tumblr);
        }

    }

    @Override
    public List<Tumblr> getAllPosts() {
        return (List<Tumblr>) tumblrRepository.findAll();
    }
}