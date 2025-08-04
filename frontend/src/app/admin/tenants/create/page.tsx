"use client";

import { useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { tenantApi } from "@/services/api";
import { CreateTenantRequest } from "@/types/api";
import toast from "react-hot-toast";

export default function CreateTenantPage() {
  const { admin, isAuthenticated, isLoading, logout } = useAuth();
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState<CreateTenantRequest>({
    name: "",
    domain: "",
    email: "",
    plan: "basic",
  });

  const [errors, setErrors] = useState<Partial<CreateTenantRequest>>({});

  const validateForm = (): boolean => {
    const newErrors: Partial<CreateTenantRequest> = {};

    if (!formData.name.trim()) {
      newErrors.name = "租户名称不能为空";
    }

    if (!formData.domain.trim()) {
      newErrors.domain = "域名不能为空";
    } else if (!/^[a-z0-9]([a-z0-9-]*[a-z0-9])?$/.test(formData.domain)) {
      newErrors.domain =
        "域名格式不正确（只能包含小写字母、数字和连字符，且不能以连字符开头或结尾）";
    }

    if (!formData.email.trim()) {
      newErrors.email = "邮箱不能为空";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "邮箱格式不正确";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await tenantApi.create(formData);

      if (response.success) {
        toast.success("租户创建成功！");
        router.push("/admin/tenants");
      } else {
        toast.error(response.message || "创建失败");
      }
    } catch (error: any) {
      if (error.response?.data?.errors) {
        setErrors(error.response.data.errors);
      }
      toast.error("创建租户失败，请检查输入信息");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleInputChange = (
    field: keyof CreateTenantRequest,
    value: string
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  const handleLogout = async () => {
    await logout();
    router.push("/admin/login");
  };

  if (isLoading || !isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 导航栏 */}
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => router.push("/admin/tenants")}
                className="text-indigo-600 hover:text-indigo-800 text-sm font-medium"
              >
                ← 返回租户列表
              </button>
              <h1 className="text-xl font-semibold text-gray-900">创建租户</h1>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">欢迎，{admin?.name}</span>
              <button
                onClick={handleLogout}
                className="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
              >
                退出
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* 主要内容 */}
      <div className="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="bg-white shadow rounded-lg">
            <div className="px-4 py-5 sm:p-6">
              <div className="mb-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  租户信息
                </h3>
                <p className="mt-1 text-sm text-gray-500">
                  请填写新租户的基本信息。域名将用于访问租户的独立站点。
                </p>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* 租户名称 */}
                <div>
                  <label
                    htmlFor="name"
                    className="block text-sm font-medium text-gray-700"
                  >
                    租户名称 <span className="text-red-500">*</span>
                  </label>
                  <div className="mt-1">
                    <input
                      type="text"
                      name="name"
                      id="name"
                      value={formData.name}
                      onChange={(e) =>
                        handleInputChange("name", e.target.value)
                      }
                      className={`shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md ${
                        errors.name ? "border-red-300" : ""
                      }`}
                      placeholder="例如：ABC公司"
                    />
                    {errors.name && (
                      <p className="mt-2 text-sm text-red-600">{errors.name}</p>
                    )}
                  </div>
                </div>

                {/* 域名 */}
                <div>
                  <label
                    htmlFor="domain"
                    className="block text-sm font-medium text-gray-700"
                  >
                    域名 <span className="text-red-500">*</span>
                  </label>
                  <div className="mt-1 flex rounded-md shadow-sm">
                    <input
                      type="text"
                      name="domain"
                      id="domain"
                      value={formData.domain}
                      onChange={(e) =>
                        handleInputChange(
                          "domain",
                          e.target.value.toLowerCase()
                        )
                      }
                      className={`flex-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full min-w-0 rounded-none rounded-l-md sm:text-sm border-gray-300 ${
                        errors.domain ? "border-red-300" : ""
                      }`}
                      placeholder="abc-company"
                    />
                    <span className="inline-flex items-center px-3 rounded-r-md border border-l-0 border-gray-300 bg-gray-50 text-gray-500 text-sm">
                      .yourdomain.com
                    </span>
                  </div>
                  {errors.domain && (
                    <p className="mt-2 text-sm text-red-600">{errors.domain}</p>
                  )}
                  <p className="mt-2 text-sm text-gray-500">
                    域名只能包含小写字母、数字和连字符，且不能以连字符开头或结尾
                  </p>
                </div>

                {/* 联系邮箱 */}
                <div>
                  <label
                    htmlFor="email"
                    className="block text-sm font-medium text-gray-700"
                  >
                    联系邮箱 <span className="text-red-500">*</span>
                  </label>
                  <div className="mt-1">
                    <input
                      type="email"
                      name="email"
                      id="email"
                      value={formData.email}
                      onChange={(e) =>
                        handleInputChange("email", e.target.value)
                      }
                      className={`shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md ${
                        errors.email ? "border-red-300" : ""
                      }`}
                      placeholder="contact@abc-company.com"
                    />
                    {errors.email && (
                      <p className="mt-2 text-sm text-red-600">
                        {errors.email}
                      </p>
                    )}
                  </div>
                </div>

                {/* 套餐选择 */}
                <div>
                  <label
                    htmlFor="plan"
                    className="block text-sm font-medium text-gray-700"
                  >
                    套餐类型 <span className="text-red-500">*</span>
                  </label>
                  <div className="mt-1">
                    <select
                      id="plan"
                      name="plan"
                      value={formData.plan}
                      onChange={(e) =>
                        handleInputChange(
                          "plan",
                          e.target.value as CreateTenantRequest["plan"]
                        )
                      }
                      className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md"
                    >
                      <option value="basic">基础版</option>
                      <option value="premium">高级版</option>
                      <option value="enterprise">企业版</option>
                    </select>
                  </div>
                  <div className="mt-2 text-sm text-gray-500">
                    <ul className="space-y-1">
                      <li>
                        <strong>基础版：</strong>适合小型团队，基本功能
                      </li>
                      <li>
                        <strong>高级版：</strong>适合中型团队，高级功能
                      </li>
                      <li>
                        <strong>企业版：</strong>适合大型企业，全部功能
                      </li>
                    </ul>
                  </div>
                </div>

                {/* 预览信息 */}
                <div className="bg-gray-50 rounded-lg p-4">
                  <h4 className="text-sm font-medium text-gray-900 mb-2">
                    预览信息
                  </h4>
                  <dl className="grid grid-cols-1 gap-x-4 gap-y-3 sm:grid-cols-2">
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        租户名称
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {formData.name || "未填写"}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        访问域名
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {formData.domain
                          ? `${formData.domain}.yourdomain.com`
                          : "未填写"}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        联系邮箱
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {formData.email || "未填写"}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        套餐类型
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {formData.plan === "basic"
                          ? "基础版"
                          : formData.plan === "premium"
                          ? "高级版"
                          : "企业版"}
                      </dd>
                    </div>
                  </dl>
                </div>

                {/* 提交按钮 */}
                <div className="flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => router.push("/admin/tenants")}
                    className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    取消
                  </button>
                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {isSubmitting ? (
                      <>
                        <svg
                          className="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                          fill="none"
                          viewBox="0 0 24 24"
                        >
                          <circle
                            className="opacity-25"
                            cx="12"
                            cy="12"
                            r="10"
                            stroke="currentColor"
                            strokeWidth="4"
                          ></circle>
                          <path
                            className="opacity-75"
                            fill="currentColor"
                            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                          ></path>
                        </svg>
                        创建中...
                      </>
                    ) : (
                      "创建租户"
                    )}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
