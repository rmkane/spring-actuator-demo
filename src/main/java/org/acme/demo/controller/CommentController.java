package org.acme.demo.controller;

import lombok.RequiredArgsConstructor;
import org.acme.demo.model.Comment;
import org.acme.demo.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Comment createComment(@RequestBody Comment comment) {
        return commentService.saveComment(comment);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment commentDetails) {
        Optional<Comment> comment = commentService.getCommentById(id);
        if (comment.isPresent()) {
            Comment updatedComment = comment.get();
            updatedComment.setContent(commentDetails.getContent());
            return ResponseEntity.ok(commentService.saveComment(updatedComment));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}