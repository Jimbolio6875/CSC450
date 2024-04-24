package edu.missouristate.controller;

import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.domain.Twitter;
import edu.missouristate.service.MastodonService;
import edu.missouristate.service.RedditPostsService;
import edu.missouristate.service.TumblrService;
import edu.missouristate.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class PostHistoryController {

    @Autowired
    MastodonService mastodonService;

    @Autowired
    TumblrService tumblrService;

    @Autowired
    RedditPostsService redditPostsService;

    @Autowired
    TwitterService twitterService;

    @GetMapping("/postHistory")
    public ModelAndView getPostHistory(HttpSession session) throws IOException, ExecutionException, InterruptedException {
        ModelAndView modelAndView = new ModelAndView("postHistory");

        Integer userId = (Integer) session.getAttribute("userId");

        // This guy you get from the repository
        // List<String> redditPostIds = redditPostsService.getAllRedditPostIdsWhereNotNullAndSameUserid(userId);

        List<String> redditPostIds = redditPostsService.getAllRedditPostIdsByUserIdWithNonNullAuthor(userId);

        // Gets the existing list of posts that have fetches
        List<RedditPosts> redditPosts = redditPostsService.fetchRedditPostDetails(redditPostIds);


        List<Twitter> tweets = twitterService.getAllTweetsWhereCreationIsNotNullAndSameUserid(userId);

        List<Mastodon> mastodonPosts = mastodonService.getAllMasterpostsWherePostIsNotNullAndSameUserId(userId);
        mastodonService.updateAllPosts(session, mastodonPosts);

        List<Tumblr> tumblrPosts = tumblrService.getAllPostsWhereCreationIsNotNullAndSameUserid(userId);
        tumblrService.updateAllPosts(tumblrPosts);

        modelAndView.addObject("redditPosts", redditPosts);
        modelAndView.addObject("tweets", tweets);
        modelAndView.addObject("mastodonPosts", mastodonPosts);
        modelAndView.addObject("tumblrPosts", tumblrPosts);

        return modelAndView;
    }
}

