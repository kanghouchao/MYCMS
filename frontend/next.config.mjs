/** @type {import('next').NextConfig} */
const nextConfig = {
  logging: {
    fetches: {
      fullUrl: true,
    },
  },
  images: {
    unoptimized: true,
  },
  output: 'standalone',
  experimental: {
    instrumentationHook: true,
  },
  async rewrites() {
    return [];
  },
};

export default nextConfig;
