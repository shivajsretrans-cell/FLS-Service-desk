FROM node:22-alpine
WORKDIR /app
COPY server.js index.html FLS-Service-Activity.xlsx ./
ENV NODE_ENV=production
ENV DATA_DIR=/data
ENV PORT=8787
EXPOSE 8787
VOLUME ["/data"]
CMD ["node", "server.js"]
