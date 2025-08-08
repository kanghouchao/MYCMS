/** @type {import('next').NextConfig} */
const nextConfig = {
  // 动态多租户需要 Middleware，不能使用 static export
  // output: 'export' 已移除

  images: {
    unoptimized: true,
  },

  // 注意：之前使用的 trustHostHeader 不是有效的 next.config 选项，已移除。
  // 如果需要在 Middleware 中自定义 host 处理，可在 middleware.ts 内自行解析 request.headers.get('host')。

  // Asset prefix for CDN (if needed)
  // assetPrefix: process.env.NODE_ENV === 'production' ? '/static' : '',

  // API routes configuration (disabled for static export)
  // Note: rewrites don't work with static export
  // async rewrites() {
  //   return [
  //     {
  //       source: "/api/:path*",
  //       destination: "http://api.oli-cms.test/:path*",
  //     },
  //   ];
  // },
};

export default nextConfig;
