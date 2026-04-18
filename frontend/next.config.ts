import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  // Enables `node server.js` deployment in Docker
  output: 'standalone',

  // Proxy /api/* → Spring Boot so client components avoid CORS
  async rewrites() {
    const backend = process.env.API_URL ?? 'http://localhost:8080';
    return [
      {
        source: '/api/:path*',
        destination: `${backend}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
