FROM node:12

RUN mkdir -p /usr/src/pcmr-frontend
WORKDIR /usr/src/pcmr-frontend

COPY . /usr/src/pcmr-frontend/

ENV NUXT_HOST=0.0.0.0

EXPOSE 3000

CMD [ "npm", "start" ]