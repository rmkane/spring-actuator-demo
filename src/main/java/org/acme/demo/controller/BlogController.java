package org.acme.demo.controller;

import lombok.RequiredArgsConstructor;
import org.acme.demo.model.Blog;
import org.acme.demo.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public List<Blog> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    @GetMapping("/published")
    public List<Blog> getPublishedBlogs() {
        return blogService.getPublishedBlogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        Optional<Blog> blog = blogService.getBlogById(id);
        return blog.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Blog createBlog(@RequestBody Blog blog) {
        return blogService.saveBlog(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long id, @RequestBody Blog blogDetails) {
        Optional<Blog> blog = blogService.getBlogById(id);
        if (blog.isPresent()) {
            Blog updatedBlog = blog.get();
            updatedBlog.setTitle(blogDetails.getTitle());
            updatedBlog.setContent(blogDetails.getContent());
            updatedBlog.setPublished(blogDetails.getPublished());
            return ResponseEntity.ok(blogService.saveBlog(updatedBlog));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
}
