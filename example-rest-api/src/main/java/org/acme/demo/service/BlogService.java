package org.acme.demo.service;

import lombok.RequiredArgsConstructor;
import org.acme.demo.model.Blog;
import org.acme.demo.model.User;
import org.acme.demo.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {
    
    private final BlogRepository blogRepository;
    
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }
    
    public List<Blog> getPublishedBlogs() {
        return blogRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }
    
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }
    
    public List<Blog> getBlogsByAuthor(User author) {
        return blogRepository.findByAuthor(author);
    }
    
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }
    
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}