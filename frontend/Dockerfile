FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

ARG BASE_URL
ARG SOCKET_URL
ENV REACT_APP_SOCKET_URL=${SOCKET_URL}
ENV REACT_APP_BASE_URL=${BASE_URL}

RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD [ "nginx", "-g", "daemon off;" ]
