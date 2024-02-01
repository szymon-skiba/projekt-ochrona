FROM nginx:latest
COPY ./conf/nginx.conf /etc/nginx/conf.d/default.conf
COPY ./ssl /etc/nginx/ssl
COPY ../../frontend /usr/share/nginx/html
