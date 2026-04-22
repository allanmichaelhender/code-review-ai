import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import { resolve } from "path";

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 8000,
    proxy: {
      "/api": {
        target: "http://172.19.0.5:8080",
        changeOrigin: true,
        timeout: 300000, // 5 minutes
        proxyTimeout: 300000,
      },
    },
  },
});
