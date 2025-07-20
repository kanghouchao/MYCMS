
# Oli CMS

Oli CMS is a modern, multi-tenant Content Management System built with a headless architecture. The backend is powered by Laravel and the frontend is a Next.js application.

## Tech Stack

- **Backend**: Laravel (PHP)
- **Frontend**: Next.js (React, TypeScript)
- **Database**: PostgreSQL
- **API Style**: RESTful
- **Local Environment**: Docker, Docker Compose
- **Reverse Proxy**: Traefik
- **Infrastructure as Code**: Terraform
- **Cloud Platform**: AWS

## Getting Started

### Prerequisites

- Docker
- Docker Compose

### Local Development Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd oli-cms
    ```

2.  **Configure Host File:**

    For the local domains to work, add the following lines to your hosts file (`/etc/hosts` on macOS/Linux, `C:\Windows\System32\drivers\etc\hosts` on Windows):

    ```
    127.0.0.1 oli-cms.test
    127.0.0.1 api.oli-cms.test
    ```

3.  **Build and start the services:**

    This command will build the Docker images and start all the services in the background.

    ```bash
    docker-compose up -d --build
    ```

4.  **Install Backend Dependencies:**

    The backend dependencies are managed by Composer. Run the following command to install them inside the Docker container:

    ```bash
    docker-compose exec backend composer install
    ```

5.  **Run Database Migrations:**

    ```bash
    docker-compose exec backend php artisan migrate
    ```

### Accessing the Services

- **Frontend Application**: [http://oli-cms.test](http://oli-cms.test)
- **Backend API**: [http://api.oli-cms.test](http://api.oli-cms.test)
- **Traefik Dashboard**: [http://localhost:8080](http://localhost:8080)

