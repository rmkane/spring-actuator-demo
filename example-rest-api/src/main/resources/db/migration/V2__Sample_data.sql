-- Insert sample users
INSERT INTO users (username, email, password_hash, first_name, last_name) VALUES
('john_doe', 'john.doe@example.com', '$2b$12$example_hash_1', 'John', 'Doe'),
('jane_smith', 'jane.smith@example.com', '$2b$12$example_hash_2', 'Jane', 'Smith'),
('bob_wilson', 'bob.wilson@example.com', '$2b$12$example_hash_3', 'Bob', 'Wilson');

-- Insert sample blogs
INSERT INTO blogs (title, content, author_id, published) VALUES
('My First Blog Post', 'This is the content of my first blog post. It''s a great day to write!', 1, TRUE),
('Learning PostgreSQL', 'PostgreSQL is a powerful open-source database system with many advanced features.', 2, TRUE),
('Spring Boot Tips', 'Here are some useful tips for working with Spring Boot applications.', 1, TRUE);

-- Insert sample comments
INSERT INTO comments (blog_id, author_id, content) VALUES
(1, 2, 'Great post! I learned a lot from this.'),
(1, 3, 'Thanks for sharing your insights.'),
(2, 1, 'I''ve been using PostgreSQL for years and it''s amazing!'),
(3, 2, 'These tips are very helpful. Thanks!');