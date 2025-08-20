"use client";

import React, { createContext, useContext, useEffect } from "react";
import { authApi as centralAuthApi } from "@/services/central/api";
import { authApi as tenantAuthApi } from "@/services/tenant/api";
import Cookies from "js-cookie";
import { useRouter } from "next/navigation";

interface AuthContextType {
  logout: () => void;
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
  const router = useRouter();
  const getAuthApi = () => (isTenantDomain() ? tenantAuthApi : centralAuthApi);

  useEffect(() => {
    const checkAuth = async () => {
      const token = Cookies.get("token");
      if (!token) {
        router.push("/login");
      }
    };
    checkAuth();
  }, [router]);

  const logout = async () => {
    try {
      await getAuthApi().logout();
      router.push("/login");
    } finally {
      Cookies.remove("token");
    }
  };

  const value: AuthContextType = {
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
