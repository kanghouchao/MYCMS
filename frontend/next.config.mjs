/** @type {import('next').NextConfig} */
const nextConfig = {
  // Enable static export for production builds
  output: "export",

  // Disable telemetry (use env variable instead)
  // telemetry: false,  // This should be set via NEXT_TELEMETRY_DISABLED=1

  // Trailing slashes for static hosting
  trailingSlash: true,

  // Disable image optimization for static export
  images: {
    unoptimized: true,
  },

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
