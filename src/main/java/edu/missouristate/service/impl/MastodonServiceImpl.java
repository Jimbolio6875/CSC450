package edu.missouristate.service.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.dao.MastodonRepository;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.service.MastodonService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Transactional
@Service
public class MastodonServiceImpl implements MastodonService {

    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String SCOPE = "read write";
    @Autowired
    MastodonRepository mastodonRepository;
    @Value("${mastodon.clientId}")
    private String CLIENT_ID;
    @Value("${mastodon.clientSecret}")
    private String CLIENT_SECRET;

    @Override
    // set client_id/secret/auth to get token
    public String getAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenEndpoint = "https://mastodon.social/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formData = "client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET +
                "&grant_type=authorization_code" +
                "&code=" + authorizationCode +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        System.out.println(formData);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            System.out.println(response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String accessToken = jsonResponse.getString("access_token");
            System.out.println("access token: " + accessToken);


            Mastodon mastodon = new Mastodon();
            mastodon.setAccessToken(accessToken);
            mastodonRepository.save(mastodon);

            return accessToken;

        } catch (Exception e) {
            System.out.println("Error during request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // gets auth url for user
    @Override
    public String getAuthorizationUrl() {

        return "https://mastodon.social/oauth/authorize" +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);
    }

    // gets user id using access token
    @Override
    public String getUserId(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://mastodon.social/api/v1/accounts/verify_credentials",
                HttpMethod.GET, entity, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject userObject = new JSONObject(response.getBody());
            return userObject.getString("id");
        } else {
            throw new RuntimeException("Failed to get user id: " + response.getStatusCode());
        }
    }

    @Override
    // post message and add message attributes to database
    public Mastodon postMessageToMastodon(String message, String accessToken) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        String postParams = "status=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(postParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://mastodon.social/api/v1/statuses", request, String.class);
        System.out.println(response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {

            JSONObject postObject = new JSONObject(response.getBody());
            JSONObject userObject = postObject.getJSONObject("account");

//            System.out.println(postObject);
            String id = postObject.getString("id");
            String userId = userObject.getString("id");
            String content = postObject.getString("content");
            String url = postObject.getString("url");
            Integer favourites = postObject.getInt("favourites_count");

//            System.out.println(userObject.getString("id"));

            mastodonRepository.updateWherePostIdIsNull(accessToken, id, userId, content, url, favourites);

            Mastodon post = new Mastodon();
            post.setPostId(id);
            post.setUserId(userId);
            post.setContent(content);
            post.setPostUrl(url);
            post.setFavouriteCount(favourites);


            return post;
//            System.out.println(id.concat(" ").concat(content).concat(" ").concat(url).concat(" ").concat(String.valueOf(favourites)));
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to post to Mastodon, response code: " + response.getStatusCode());
        }

        return null;
    }

    @Override
    public void savePost(Mastodon post) {
        mastodonRepository.save(post);
    }

    @Override
    public List<Mastodon> getPostsByUserId(String userId) {
        return mastodonRepository.getPostsByUserId(userId);
    }

    @Override
    public Tuple getLatestAccessToken() {
        return mastodonRepository.getLatestAccessToken();
    }

    @Override
    public Mastodon findExistingPostByTokenAndNoText(String accessToken) {
        return mastodonRepository.findExistingPostByTokenAndNoText(accessToken);
    }

    @Override
    public void updateOrCreateMastodonPost(String mastroAccessToken, String message) {
        Mastodon existingPost = mastodonRepository.findExistingPostByTokenAndNoText(mastroAccessToken);

        if (existingPost != null) {
            existingPost.setContent(message);
            mastodonRepository.save(existingPost);
        } else {
            Mastodon newPost = new Mastodon();
            newPost.setAccessToken(mastroAccessToken);
            newPost.setContent(message);
            mastodonRepository.save(newPost);
        }
    }


}
