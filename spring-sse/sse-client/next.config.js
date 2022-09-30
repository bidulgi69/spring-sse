/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  env: {
    SERVER_ADDRESS: process.env.SERVER_ADDRESS
  }
}

module.exports = nextConfig
