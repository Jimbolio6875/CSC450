package edu.missouristate.service.impl;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.querydsl.core.Tuple;
import edu.missouristate.dao.CentralLoginRepository;
import edu.missouristate.dao.TumblrRepository;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.service.CentralLoginService;
import edu.missouristate.service.SocialMediaAccountService;
import edu.missouristate.service.TumblrService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@Transactional
@Service
public class TumblrServiceImpl implements TumblrService {

    @Autowired
    EntityManager entityManager;
    @Autowired
    CentralLoginRepository centralLoginRepository;

    @Autowired
    CentralLoginService centralLoginService;
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


    /**
     * uses consumer key, consumer secret and callback url to initialize an oauthService
     * @return OAuth10aService used for signing request
     */
    private OAuth10aService getOauthService() {
        if (this.oauthService == null) {
            this.oauthService = new ServiceBuilder(consumerKey)
                    .apiSecret(consumerSecret)
                    .callback(callbackUrl)
                    .build(TumblrApi.instance());
        }
        return this.oauthService;
    }

    /**
     * uses consumer key, consumer secret and callback url to initialize an oauthService
     * @return tumblr authorization url
     */
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

    /**
     * uses oauthVerifier and current session to get current user's connected tumblr account info
     * @param oauthVerifier oauthVerifier from tumblr oauth callback
     * @param session current user session
     * @return tumblr response body
     */
    @Override
    public String getUserInfo(String oauthVerifier, HttpSession session) {
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

            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            CentralLogin user = centralLoginService.getUserById(userId);
            if (user == null) {
                return "redirect:/login";
            }

            Tumblr tumblrCredentials = new Tumblr();
            tumblrCredentials.setAccessToken(accessToken.getToken());
            tumblrCredentials.setTokenSecret(accessToken.getTokenSecret());
            tumblrCredentials.setCentralLogin(user);
            tumblrCredentials.setBlogIdentifier(blogIdentifier);
            tumblrRepository.save(tumblrCredentials);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving user info";
        }
    }

    /**
     * posts to user's tumblr blog
     * @param postContent content to be posted to blog
     * @return postId if post was successful
     * @throws Exception if POST response wasn't a 201 throw exception
     */
    @Override
    public String postToBlog(String postContent) throws Exception {

        Tuple tumblrUser = tumblrRepository.getLatestUser();
        if (tumblrUser == null) {
            throw new IllegalStateException("No Tumblr credentials available");
        }

        String accessTokenValue = tumblrUser.get(0, String.class);
        String tokenSecretValue = tumblrUser.get(1, String.class);
        String blogIdentifierValue = tumblrUser.get(2, String.class);

        OAuth10aService service = getOauthService();
        OAuth1AccessToken accessToken = new OAuth1AccessToken(accessTokenValue, tokenSecretValue);

        String postUrl = "https://api.tumblr.com/v2/blog/" + blogIdentifierValue + "/post";
        OAuthRequest request = new OAuthRequest(Verb.POST, postUrl);
        request.addBodyParameter("type", "text");
        request.addBodyParameter("body", postContent);
        service.signRequest(accessToken, request);

        Response response = service.execute(request);

        if (response.getCode() == 201) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String postId = jsonResponse.getJSONObject("response").getString("id_string");

            return postId;
        } else {
            throw new RuntimeException("Failed to post to Tumblr: " + response.getCode() + " - " + response.getBody());
        }
    }

    /**
     * gets post by currently logged-in user's id
     * @param userId login id
     * @return list of tumblr posts
     */
    @Override
    public List<Tumblr> getPostsByBlog(Integer userId) {
        try {
            return tumblrRepository.findByCentralLogin_CentralLoginId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Tumblr posts for user " + userId + ": " + e.getMessage(), e);
        }
    }

    /**
     * update tumblr posts in database based on tumblr JSON changes (new notes (likes), edits, deletion)
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
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

    /**
     * gets latest user
     * @return
     */
    @Override
    public Tuple getLatestUser() {
        return tumblrRepository.getLatestUser();
    }

    /**
     * checks if user has existing access token in database, if they do then update. else create new tumblr instance
     * @param accessToken user access token
     * @param tokenSecret user token secret
     * @param blogIdentifier user blog identifier
     * @param postId post's id
     * @param message message to be set in update or new creation
     * @param userId login id
     */
    @Override
    public void updateOrCreateTumblrPost(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message, Integer userId) {

        Tumblr exists = tumblrRepository.findExistingPostByTokenAndNoTextAndCentralLoginId(accessToken, userId);

        if (exists != null) {
            exists.setContent(message);
            exists.setDate(Timestamp.valueOf(LocalDateTime.now()));
            exists.setBlogIdentifier(blogIdentifier);
            exists.setPostId(postId);
            exists.setAccessToken(accessToken);
            CentralLogin user = entityManager.getReference(CentralLogin.class, userId);
            exists.setCentralLogin(user);
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
            CentralLogin user = entityManager.getReference(CentralLogin.class, userId);
            tumblr.setCentralLogin(user);
            tumblr.setPostUrl("https://www.tumblr.com/blog/view/" + blogIdentifier + "/" + postId);
            tumblrRepository.save(tumblr);
        }

    }

    /**
     * gets posts that have not been deleted from tumblr
     * @param userId login id
     * @return list of existing tumblr posts
     */
    @Override
    public List<Tumblr> getAllPostsWhereCreationIsNotNullAndSameUserid(Integer userId) {
        return tumblrRepository.getAllPostsWhereCreationIsNotNullAndSameUserid(userId);
    }

    /**
     * delete entries in tumblr repo for posts that no longer exist
     * @param userId login id
     */
    @Override
    public void cleanTable(Integer userId) {
        tumblrRepository.cleanTable(userId);
    }

    /**
     * check if user has access tumblr access token in database or not
     * @param userId login id
     * @return bool
     */
    @Override
    public boolean hasToken(Integer userId) {
        return tumblrRepository.hasToken(userId);
    }

    /**
     * update content and note count of user's posts and check if post has been deleted
     * @param tumblrPosts list of user's tumblr posts
     */
    @Override
    public void updateAllPosts(List<Tumblr> tumblrPosts) {

        RestTemplate restTemplate = new RestTemplate();

        try {

            for (Tumblr tumblrPost : tumblrPosts) {

                try {

                    String url = "https://api.tumblr.com/v2/blog/" + tumblrPost.getBlogIdentifier() + "/posts?api_key=" + consumerKey + "&id=" + tumblrPost.getPostId();

                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                    System.out.println(response.getBody());

                    JSONObject object = new JSONObject(response.getBody());

                    Integer noteCount = object.getJSONObject("response").getJSONArray("posts").getJSONObject(0).getInt("note_count");
                    String content = object.getJSONObject("response").getJSONArray("posts").getJSONObject(0).getString("body");

                    tumblrRepository.updateByPostId(tumblrPost.getPostId(), noteCount, content);

                } catch (HttpClientErrorException httpError) {

                    if (httpError.getStatusCode() == HttpStatus.NOT_FOUND) {
                        tumblrRepository.updateDeletedPost(tumblrPost.getPostId(), "[deleted]");
                    }

                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}