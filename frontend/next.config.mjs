/** @type {import('next').NextConfig} */
const nextConfig = {
  // 动态多租户需要 Middleware，不能使用 static export
  // output: 'export' 已移除

  images: {
    unoptimized: true,
  },

  // 告诉 Next.js 在反向代理后信任传入的 Host 头 (Traefik 会保留原始域名)
  // Next 13.4+ 支持，防止 hostname 被解析成 container 内部名字或 localhost
  trustHostHeader: true,

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
