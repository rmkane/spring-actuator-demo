package org.acme.demo.service;

import lombok.RequiredArgsConstructor;
import org.acme.demo.model.Blog;
import org.acme.demo.model.Comment;
import org.acme.demo.model.User;
import org.acme.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }
    
    public List<Comment> getCommentsByBlog(Blog blog) {
        return commentRepository.findByBlog(blog);
    }
    
    public List<Comment> getCommentsByAuthor(User author) {
        return commentRepository.findByAuthor(author);
    }
    
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
    
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}