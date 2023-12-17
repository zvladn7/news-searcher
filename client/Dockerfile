#pull official base image
FROM node:12

# set working directory
WORKDIR /app

# add '/app/node_modules/.bin to $PATH
ENV PATH /app/node_modules/.bin:$PATH

# install app dependencies
COPY package.json ./
COPY package-lock.json ./
COPY yarn.lock ./
RUN npm cache clean --force
RUN yarn install --frozen-lockfile
#RUN yarn build

# add app
COPY . ./

# start app
CMD ["yarn", "start"]

