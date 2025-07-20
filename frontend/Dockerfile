
# frontend/Dockerfile
# Dockerfile for the Next.js frontend service.

# Use official Node.js 20 Alpine image as a base
FROM node:20-alpine

# Set working directory
WORKDIR /app

# Install dependencies
# Copy package.json and lock files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Expose port 3000 for the Next.js dev server
EXPOSE 3000

# Command to run the development server
CMD ["npm", "run", "dev"]
