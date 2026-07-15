package org.acme.demo.repository;

import org.acme.demo.model.Blog;
import org.acme.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByAuthor(User author);
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();
}