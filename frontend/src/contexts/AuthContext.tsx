"use client";

import React, { createContext, useContext, useState, useEffect } from "react";
import { authApi as centralAuthApi } from "@/services/central/api";
import { authApi as tenantAuthApi } from "@/services/tenant/api";
import Cookies from "js-cookie";
import { useRouter } from "next/navigation";

interface AuthContextType {
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

function isTenantDomain(): boolean {
  if (typeof window === "undefined") return false;
  const hostname = window.location.hostname;
  const centralDomain =
    process.env.NEXT_PUBLIC_CENTRAL_DOMAIN || "oli-cms.test";
  return hostname !== centralDomain;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  const getAuthApi = () => (isTenantDomain() ? tenantAuthApi : centralAuthApi);
  const tokenKey = "token";

  useEffect(() => {
    const checkAuth = async () => {
      const token = Cookies.get(tokenKey);
      setIsLoading(false);
    };
    checkAuth();
  }, []);

  const redirectAfterLogin = () => {
    const centralDomain =
      process.env.NEXT_PUBLIC_CENTRAL_DOMAIN || "oli-cms.test";
    const isTenant =
      typeof window !== "undefined" &&
      window.location.hostname !== centralDomain;
    router.push(isTenant ? "/tenant/dashboard" : "/admin/dashboard");
  };

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      const response = await getAuthApi().login({ email, password });
      if (response.token && response.expires_at) {
        Cookies.set(tokenKey, response.token, { expires: response.expires_at });
        redirectAfterLogin();
        return true;
      }
      return false;
    } catch (error) {
      console.error("Login failed:", error);
      return false;
    }
  };

  const logout = async () => {
    try {
      await getAuthApi().logout();
    } finally {
      Cookies.remove(tokenKey);
    }
  };

  const value: AuthContextType = {
    isLoading,
    login,
    logout,
    isAuthenticated: !!Cookies.get(tokenKey),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
