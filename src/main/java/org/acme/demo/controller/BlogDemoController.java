package org.acme.demo.controller;

import lombok.RequiredArgsConstructor;
import org.acme.demo.model.Blog;
import org.acme.demo.service.BlogService;
import org.acme.demo.service.CommentService;
import org.acme.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
public class BlogDemoController {

    private final UserService userService;
    private final BlogService blogService;
    private final CommentService commentService;

    @GetMapping("/stats")
    public ResponseEntity<BlogStats> getBlogStats() {
        BlogStats stats = new BlogStats(
            userService.getAllUsers().size(),
            blogService.getAllBlogs().size(),
            commentService.getAllComments().size()
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-blogs")
    public List<Blog> getRecentBlogs() {
        return blogService.getPublishedBlogs();
    }

    // Simple stats class for demo purposes
    public static class BlogStats {
        private final int userCount;
        private final int blogCount;
        private final int commentCount;

        public BlogStats(int userCount, int blogCount, int commentCount) {
            this.userCount = userCount;
            this.blogCount = blogCount;
            this.commentCount = commentCount;
        }

        // Getters
        public int getUserCount() {
            return userCount;
        }

        public int getBlogCount() {
            return blogCount;
        }

        public int getCommentCount() {
            return commentCount;
        }
    }
}
