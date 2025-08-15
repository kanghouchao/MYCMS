"use client";

import React, { createContext, useContext, useState, useEffect } from "react";
import { Admin } from "@/types/api";
import { authApi } from "@/services/central/api";
import Cookies from "js-cookie";

interface AuthContextType {
  admin: Admin | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<boolean>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [admin, setAdmin] = useState<Admin | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // 检查认证状态
  useEffect(() => {
    const checkAuth = async () => {
      const token = Cookies.get("admin_token");
      if (token) {
        try {
          const response = await authApi.me();
          if (response.success && response.data) {
            setAdmin(response.data);
          } else {
            Cookies.remove("admin_token");
          }
        } catch (error) {
          Cookies.remove("admin_token");
        }
      }
      setIsLoading(false);
    };

    checkAuth();
  }, []);

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const response = await authApi.login({ email, password });
      if (response.success && response.data) {
        const { admin: adminData, token } = response.data;
        setAdmin(adminData);
        Cookies.set("admin_token", token, { expires: 7 }); // 7天过期
        return true;
      }
      return false;
    } catch (error) {
      return false;
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      // 即使API调用失败，也要清除本地状态
    } finally {
      setAdmin(null);
      Cookies.remove("admin_token");
    }
  };

  const value: AuthContextType = {
    admin,
    isLoading,
    login,
    logout,
    isAuthenticated: !!admin,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
