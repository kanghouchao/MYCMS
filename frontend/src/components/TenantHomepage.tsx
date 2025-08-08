import React from "react";
import { useTenantInfo } from "@/hooks/useTenantInfo";

// 精简后的租户首页，仅依赖统一解析端点返回的最小字段
interface SimpleTenantInfo {
  tenant_name: string;
  domain: string;
  tenant_id: string;
}

interface TenantHomepageProps {
  tenant: SimpleTenantInfo;
}

function TenantHomepage({ tenant }: TenantHomepageProps) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50">
      <div className="container mx-auto px-4 py-20 max-w-3xl">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            欢迎来到{" "}
            <span className="text-indigo-600">{tenant.tenant_name}</span>
          </h1>
          <p className="text-gray-600">域名：{tenant.domain}</p>
        </div>
        <div className="bg-white rounded-xl shadow-md p-8 border border-gray-100 text-center">
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">
            网站建设中
          </h2>
          <p className="text-gray-600 mb-6">
            该租户站点正在搭建，稍后将提供更多内容与功能。
          </p>
          <div className="text-sm text-gray-500">
            Tenant ID: {tenant.tenant_id}
          </div>
        </div>
      </div>
    </div>
  );
}

export default function TenantPageWrapper() {
  const { tenant, loading, error } = useTenantInfo();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-indigo-600 mx-auto mb-4"></div>
          <p className="text-lg text-gray-600">加载中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 via-white to-pink-50">
        <div className="text-center max-w-md p-6">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">
            无法加载租户
          </h1>
          <p className="text-gray-600 mb-6">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-gray-900 transition-colors"
          >
            重新加载
          </button>
        </div>
      </div>
    );
  }

  if (!tenant) {
    return null; // 让其它路由继续处理
  }

  return (
    <TenantHomepage
      tenant={{
        tenant_name: tenant.tenant_name,
        domain: tenant.domain,
        tenant_id: tenant.tenant_id,
      }}
    />
  );
}
