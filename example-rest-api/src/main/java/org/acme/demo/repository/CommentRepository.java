package org.acme.demo.repository;

import org.acme.demo.model.Blog;
import org.acme.demo.model.Comment;
import org.acme.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBlog(Blog blog);
    List<Comment> findByAuthor(User author);
}