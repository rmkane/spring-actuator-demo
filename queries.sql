-- Query all blogs with their authors
SELECT b.id, b.title, b.content, b.created_at, u.username as author_username, u.first_name, u.last_name
FROM blogs b
JOIN users u ON b.author_id = u.id
WHERE b.published = TRUE
ORDER BY b.created_at DESC;

-- Query a specific blog with its comments
SELECT b.title, b.content, b.created_at, 
       u.username as author_username, u.first_name, u.last_name,
       c.content as comment_content, c.created_at as comment_date,
       cu.username as comment_author
FROM blogs b
JOIN users u ON b.author_id = u.id
LEFT JOIN comments c ON b.id = c.blog_id
LEFT JOIN users cu ON c.author_id = cu.id
WHERE b.id = 1
ORDER BY c.created_at;

-- Query all users and their blog counts
SELECT u.username, u.first_name, u.last_name, COUNT(b.id) as blog_count
FROM users u
LEFT JOIN blogs b ON u.id = b.author_id
GROUP BY u.id, u.username, u.first_name, u.last_name
ORDER BY blog_count DESC;

-- Query recent comments with blog titles
SELECT c.content as comment_content, c.created_at,
       b.title as blog_title,
       u.username as commenter_username
FROM comments c
JOIN blogs b ON c.blog_id = b.id
JOIN users u ON c.author_id = u.id
ORDER BY c.created_at DESC
LIMIT 10;