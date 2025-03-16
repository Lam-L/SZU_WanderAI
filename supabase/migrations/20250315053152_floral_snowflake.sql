/*
  # Add RBAC Support

  1. New Tables
    - `roles`: Stores system roles
    - `permissions`: Stores system permissions
    - `role_permissions`: Maps roles to permissions
    - `user_roles`: Maps users to roles

  2. Changes
    - Remove role column from users table
    - Add default roles and permissions
    - Migrate existing user roles to new structure
    - Update RLS policies

  3. Security
    - Enable RLS on new tables
    - Add appropriate policies
*/

-- Create roles table
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create permissions table
CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create role_permissions table
CREATE TABLE role_permissions (
    role_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
    permission_id INTEGER REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id)
);

-- Create user_roles table
CREATE TABLE user_roles (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- Enable RLS on new tables
ALTER TABLE roles ENABLE ROW LEVEL SECURITY;
ALTER TABLE permissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE role_permissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_roles ENABLE ROW LEVEL SECURITY;

-- Create RLS policies for new tables
CREATE POLICY "Roles viewable by authenticated users" ON roles
    FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Permissions viewable by authenticated users" ON permissions
    FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Role permissions viewable by authenticated users" ON role_permissions
    FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "User roles viewable by authenticated users" ON user_roles
    FOR SELECT
    TO authenticated
    USING (true);

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('admin', 'System administrator with full access'),
('merchant', 'Merchant who can manage their own store and products'),
('user', 'Regular user who can browse and purchase products');

-- Insert default permissions
INSERT INTO permissions (name, description) VALUES
-- Admin permissions
('manage_users', 'Can manage all users'),
('manage_merchants', 'Can manage merchant accounts'),
('manage_roles', 'Can manage roles and permissions'),
('view_admin_dashboard', 'Can view admin dashboard'),

-- Merchant permissions
('manage_products', 'Can manage own products'),
('view_merchant_dashboard', 'Can view merchant dashboard'),
('manage_orders', 'Can manage orders for own products'),

-- User permissions
('browse_products', 'Can browse products'),
('create_orders', 'Can create orders'),
('manage_profile', 'Can manage own profile'),
('create_posts', 'Can create posts');

-- Assign permissions to roles
-- Admin role permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'admin';

-- Merchant role permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'merchant'
AND p.name IN (
    'manage_products',
    'view_merchant_dashboard',
    'manage_orders',
    'browse_products',
    'create_orders',
    'manage_profile',
    'create_posts'
);

-- User role permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'user'
AND p.name IN (
    'browse_products',
    'create_orders',
    'manage_profile',
    'create_posts'
);

-- Migrate existing users to new role structure
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON LOWER(u.role) = r.name;

-- Add merchant role for merchant users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.role = 'MERCHANT'
AND r.name = 'user';

-- Remove role column from users table
ALTER TABLE users DROP COLUMN role;