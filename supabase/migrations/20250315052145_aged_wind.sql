-- WanderAI Database Schema

-- Users table - Stores all users including regular users, merchants and admins
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'MERCHANT', 'ADMIN')),
    bio TEXT,
    avatar VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    business_license VARCHAR(100),
    merchant_status VARCHAR(20) CHECK (merchant_status IN ('pending', 'approved', 'rejected', 'blocked')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tour packages table - Stores travel packages created by merchants
CREATE TABLE tour_packages (
    id SERIAL PRIMARY KEY,
    merchant_id INTEGER NOT NULL REFERENCES users(id),
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) NOT NULL,
    location VARCHAR(100) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    features TEXT[] NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'active', 'rejected', 'expired')),
    sold_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Package itinerary table - Stores daily itinerary for tour packages
CREATE TABLE package_itinerary (
    id SERIAL PRIMARY KEY,
    package_id INTEGER NOT NULL REFERENCES tour_packages(id) ON DELETE CASCADE,
    day INTEGER NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL
);

-- Package includes/excludes table - Stores what's included/excluded in packages
CREATE TABLE package_details (
    id SERIAL PRIMARY KEY,
    package_id INTEGER NOT NULL REFERENCES tour_packages(id) ON DELETE CASCADE,
    type VARCHAR(10) NOT NULL CHECK (type IN ('include', 'exclude', 'note')),
    content TEXT NOT NULL
);

-- Orders table - Stores tour package orders
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    package_id INTEGER NOT NULL REFERENCES tour_packages(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    merchant_id INTEGER NOT NULL REFERENCES users(id),
    quantity INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'confirmed', 'canceled', 'completed')),
    notes TEXT[],
    cancel_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    canceled_at TIMESTAMP
);

-- Posts table - Stores user travel posts
CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    author_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_hidden BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Post images table - Stores images for posts
CREATE TABLE post_images (
    id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    image_url VARCHAR(255) NOT NULL
);

-- Comments table - Stores comments on posts
CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Likes table - Stores post likes
CREATE TABLE likes (
    id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(post_id, user_id)
);

-- Verification codes table - Stores email verification codes
CREATE TABLE verification_codes (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_tour_packages_merchant ON tour_packages(merchant_id);
CREATE INDEX idx_tour_packages_status ON tour_packages(status);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_merchant ON orders(merchant_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_author ON comments(author_id);
CREATE INDEX idx_likes_post ON likes(post_id);
CREATE INDEX idx_likes_user ON likes(user_id);

-- Add RLS (Row Level Security) policies
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE tour_packages ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE posts ENABLE ROW LEVEL SECURITY;
ALTER TABLE comments ENABLE ROW LEVEL SECURITY;
ALTER TABLE likes ENABLE ROW LEVEL SECURITY;

-- RLS Policies for users
CREATE POLICY users_view_policy ON users
    FOR SELECT
    USING (
        auth.role() = 'admin' OR 
        auth.uid() = id OR 
        (role = 'MERCHANT' AND merchant_status = 'approved')
    );

-- RLS Policies for tour packages
CREATE POLICY packages_view_policy ON tour_packages
    FOR SELECT
    USING (
        status = 'active' OR 
        auth.role() = 'admin' OR
        merchant_id = auth.uid()
    );

CREATE POLICY packages_modify_policy ON tour_packages
    FOR ALL
    USING (
        auth.role() = 'admin' OR
        merchant_id = auth.uid()
    );

-- RLS Policies for orders
CREATE POLICY orders_view_policy ON orders
    FOR SELECT
    USING (
        auth.role() = 'admin' OR
        user_id = auth.uid() OR
        merchant_id = auth.uid()
    );

-- RLS Policies for posts
CREATE POLICY posts_view_policy ON posts
    FOR SELECT
    USING (
        NOT is_hidden OR 
        auth.role() = 'admin' OR
        author_id = auth.uid()
    );

CREATE POLICY posts_modify_policy ON posts
    FOR ALL
    USING (
        auth.role() = 'admin' OR
        author_id = auth.uid()
    );

-- Insert test data
INSERT INTO users (user_name, email, password, role, created_at) VALUES
('Admin', 'admin@qq.com', '$2a$10$eDIJO.xBnS6JK5xm6HMqAOKUGcWAZFyLKXwzVX5mVZUXJzxTT5xMi', 'ADMIN', CURRENT_TIMESTAMP),
('Test User', 'user@qq.com', '$2a$10$eDIJO.xBnS6JK5xm6HMqAOKUGcWAZFyLKXwzVX5mVZUXJzxTT5xMi', 'USER', CURRENT_TIMESTAMP),
('Test Merchant', 'merchant@qq.com', '$2a$10$eDIJO.xBnS6JK5xm6HMqAOKUGcWAZFyLKXwzVX5mVZUXJzxTT5xMi', 'MERCHANT', CURRENT_TIMESTAMP);