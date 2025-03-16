// src/types/auth.ts
// User role and permission types
export type Role = {
  id: number;
  name: string;
  description: string;
};

export type Permission = {
  name: string;
  description: string;
};

// User profile with roles and permissions
export interface UserProfile {
  id: number;
  userName: string;
  email: string;
  bio?: string;
  avatar?: string;
  phone?: string;
  address?: string;
  businessLicense?: string;
  merchantStatus?: string;
  createdAt: string;
  roles: Role[];
  permissions: Permission[];
}

// Auth store state interface
export interface AuthState {
  token: string | null;
  user: UserProfile | null;
  isAuthenticated: boolean;
  roles: string[];
  permissions: string[];
  activeRole: string;
}

// Login request types
export interface LoginRequest {
  email: string;
  password: string;
}

// Register request types
export interface RegisterRequest {
  userName: string;
  email: string;
  password: string;
  verifyCode?: string;
  isMerchant?: boolean;
  phone?: string;
  address?: string;
  businessLicense?: string;
}
