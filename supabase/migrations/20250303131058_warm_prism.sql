-- WanderAI Database Schema
-- This script creates all necessary tables for the WanderAI application

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS verification_codes;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS post_images;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    bio VARCHAR(500),
    avatar VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create posts table
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create comments table
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create post_images table
CREATE TABLE post_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(255) NOT NULL,
    post_id BIGINT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Create likes table
CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_post_user (post_id, user_id)
);

-- Create verification_codes table
CREATE TABLE verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create indexes for better performance
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_author ON comments(author_id);
CREATE INDEX idx_post_images_post ON post_images(post_id);
CREATE INDEX idx_likes_post ON likes(post_id);
CREATE INDEX idx_likes_user ON likes(user_id);
CREATE INDEX idx_verification_codes_email ON verification_codes(email);

-- Insert sample data for testing
-- Sample user (password is hashed version of 'password')
INSERT INTO users (user_name, email, password, bio, created_at)
VALUES ('Admin User', 'admin@example.com', '$2a$10$eDIJO.xBnS6JK5xm6HMqAOKUGcWAZFyLKXwzVX5mVZUXJzxTT5xMi', 'Platform administrator', NOW());

-- Sample posts
INSERT INTO posts (title, content, author_id, created_at)
VALUES 
('日本东京三日游记', '这次去东京的旅行真是太棒了！我们参观了浅草寺、东京塔和涩谷十字路口。东京是一个充满活力的城市，现代与传统完美融合。', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('巴黎浪漫之旅', '埃菲尔铁塔、卢浮宫、凯旋门，巴黎的每一处都充满了浪漫。巴黎被称为"光之城"，这里的建筑、艺术和美食都让人流连忘返。', 1, DATE_SUB(NOW(), INTERVAL 10 DAY));

-- Sample post images
INSERT INTO post_images (image_url, post_id)
VALUES 
('https://images.unsplash.com/photo-1536098561742-ca998e48cbcc', 1),
('https://images.unsplash.com/photo-1526481280693-3bfa7568e0f3', 1),
('https://images.unsplash.com/photo-1502602898657-3e91760cbb34', 2);

-- Sample comments
INSERT INTO comments (content, post_id, author_id, created_at)
VALUES 
('照片拍得真好！请问你用的是什么相机？', 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('东京真的很棒，我也去过浅草寺，那里的氛围很特别。', 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('巴黎真的很浪漫，你的照片拍得太美了！', 2, 1, DATE_SUB(NOW(), INTERVAL 9 DAY));

-- Sample likes
INSERT INTO likes (post_id, user_id, created_at)
VALUES 
(1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 1, DATE_SUB(NOW(), INTERVAL 8 DAY));