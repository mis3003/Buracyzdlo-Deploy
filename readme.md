# Buraczydło

![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=white&labelColor=20232A)
![TypeScript](https://img.shields.io/badge/TypeScript-4.9-3178C6?logo=typescript&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Terraform](https://img.shields.io/badge/Terraform-AWS-7B42BC?logo=terraform&logoColor=white)
![Ansible](https://img.shields.io/badge/Ansible-EE0000?logo=ansible&logoColor=white)
![Kubernetes](https://img.shields.io/badge/k3s-Kubernetes-326CE5?logo=kubernetes&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2_RDS-FF9900?logo=amazonaws&logoColor=white)
![Spotify](https://img.shields.io/badge/Spotify-API-1DB954?logo=spotify&logoColor=white)

A full-stack music playlist manager that lets users create playlists and play songs from YouTube and Spotify. Deployed on AWS using Terraform for infrastructure provisioning and Ansible for configuration management, running on Kubernetes (k3s).

## Architecture

The application consists of three services deployed on a single EC2 instance managed by k3s:

**Frontend** — React + TypeScript application served by nginx. Communicates with the backend through an nginx reverse proxy, so all API calls go through a single port.

**Backend** — Spring Boot 3 REST API handling authentication, playlist and song management, and Spotify OAuth integration. Uses session-based authentication with BCrypt password hashing.

**Database** — PostgreSQL hosted on AWS RDS in a private subnet, accessible only from the EC2 instance. Hibernate manages schema migrations automatically on startup.

### AWS Network

All resources live inside a single VPC (`10.0.0.0/16`) in the `eu-north-1` (Stockholm) region. The network is divided into three subnets across two availability zones:

```
AWS eu-north-1
└── VPC  10.0.0.0/16
    │
    ├── Public subnet  10.0.1.0/24  (eu-north-1a)
    │   └── EC2  ←── Internet Gateway ←── User
    │       └── k3s
    │           ├── frontend  NodePort :30000  (nginx + React)
    │           └── backend   ClusterIP :8080  (Spring Boot)
    │
    ├── Private subnet A  10.0.2.0/24  (eu-north-1a)
    │   └── RDS PostgreSQL 16
    │
    └── Private subnet B  10.0.3.0/24  (eu-north-1b)
        └── RDS standby (required by AWS for Multi-AZ subnet group)
```

#### Subnets

The public subnet has a Route Table with a `0.0.0.0/0 → Internet Gateway` rule, which gives the EC2 instance outbound internet access (for pulling Docker images, installing packages) and makes it reachable from outside via its public IP.

The two private subnets have no such route — they cannot initiate or receive connections from the internet. RDS requires a DB Subnet Group spanning at least two availability zones, which is why there are two private subnets even though only one RDS instance is running.

#### Security Groups

Two security groups control traffic at the instance level:

**EC2 Security Group**

| Direction | Port | Source | Purpose |
|---|---|---|---|
| Inbound | 22 | 0.0.0.0/0 | SSH access |
| Inbound | 30000 | 0.0.0.0/0 | Frontend (k3s NodePort) |
| Outbound | all | 0.0.0.0/0 | Outbound traffic (package installs, Docker Hub) |

**RDS Security Group**

| Direction | Port | Source | Purpose |
|---|---|---|---|
| Inbound | 5432 | EC2 Security Group | PostgreSQL — EC2 only |

The RDS security group references the EC2 security group by ID rather than by IP address. This means any EC2 instance belonging to that security group can reach the database, regardless of what IP it has — which matters because EC2 public IPs change on restart.

#### Traffic flow

```
User → EC2:30000 → nginx (frontend) → React app
                 → nginx /api proxy → backend:8080 → RDS:5432
```

All user traffic enters through port 30000 (k3s NodePort). Nginx inside the frontend container serves the React app for regular requests and proxies `/api/*` calls to the backend service. The backend connects to RDS using the internal DNS name of the RDS service — traffic never leaves the VPC.

Terraform provisions the AWS resources. Ansible installs Docker, k3s, and deploys the application using Kubernetes manifests. Docker images are hosted on Docker Hub and pulled by k3s at deployment time.

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 19, TypeScript, Mantine UI |
| Backend | Spring Boot 3.4, Spring Security, Spring Data JPA |
| Database | PostgreSQL 16 (AWS RDS) |
| Auth | Session-based with BCrypt |
| Music playback | Spotify Web Playback SDK, ReactPlayer (YouTube) |
| Infrastructure | Terraform, AWS EC2, RDS, VPC |
| Configuration | Ansible, k3s (lightweight Kubernetes) |
| Container registry | Docker Hub |

## Prerequisites

- AWS CLI configured (`aws sts get-caller-identity` should return your account)
- An existing key pair in AWS EC2
- Terraform installed
- Ansible installed with the `amazon.aws` collection
- Docker installed locally (for building images)
- A Spotify Developer account with an app registered at [developer.spotify.com](https://developer.spotify.com)

## Deployment

### 1. Build and push Docker images

```bash
docker build -t YOUR_DOCKERHUB_USERNAME/buraczydlo-backend:latest ./Buraczyd-o-Backend/
docker build -t YOUR_DOCKERHUB_USERNAME/buraczydlo-frontend:latest ./Buraczyd-o-Frontend/

docker push YOUR_DOCKERHUB_USERNAME/buraczydlo-backend:latest
docker push YOUR_DOCKERHUB_USERNAME/buraczydlo-frontend:latest
```

Update the image names in `aws_deploy/ansible/Deploy/backed-deployment.yaml` and `frontend-deployment.yaml` to match your Docker Hub username.

### 2. Provision infrastructure with Terraform

```bash
cd aws_deploy/terraform/

cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars — set your key pair name, db username and password
```

```bash
terraform init
terraform plan
terraform apply
```

Note the outputs — you will need the EC2 public IP and RDS endpoint.

### 3. Configure secrets

Edit `aws_deploy/ansible/Deploy/db-secret.yaml` and fill in:

```yaml
DB_URL: jdbc:postgresql://YOUR_RDS_ENDPOINT/postgres
DB_USERNAME: your_db_username
DB_PASSWORD: your_db_password
SPOTIFY_CLIENT_ID: your_spotify_client_id
SPOTIFY_CLIENT_SECRET: your_spotify_client_secret
SPOTIFY_REDIRECT_URI: http://EC2_PUBLIC_IP:8080/api/spotify/callback
```

This file is in `.gitignore` and should never be committed.

### 4. Configure Ansible inventory

Edit `aws_deploy/ansible/ansible.cfg` and set your key path:

```ini
private_key_file = /path/to/your/key.pem
```

### 5. Deploy with Ansible

```bash
cd aws_deploy/ansible/

# Install dependencies (first time only)
pip install boto3 botocore ansible
ansible-galaxy collection install amazon.aws

# Verify connection
ansible -i aws_ec2.yaml aws_ec2 -m ping

# Run the playbook
ansible-playbook -i aws_ec2.yaml playbook.yaml
```

The playbook installs Docker and k3s, copies the Kubernetes manifests, applies the secret, and deploys all services.

### 6. Access the application

Once the playbook finishes:

- **Frontend:** `http://EC2_PUBLIC_IP:30000`
- **Backend health check:** `http://EC2_PUBLIC_IP:30000/api/test/all`

Update the Redirect URI in your Spotify Developer Dashboard to match:
```
http://EC2_PUBLIC_IP:8080/api/spotify/callback
```

## Teardown

To avoid ongoing AWS charges, destroy all resources when done:

```bash
cd aws_deploy/terraform/
terraform destroy
```

The RDS instance and EC2 are both billed by the hour.

## Running locally

```bash
cd Compose/
cp .env.example .env
# Fill in your credentials

docker compose up
```

The application will be available at `http://localhost:3000`.

## Project structure

```
├── Buraczyd-o-Backend/     Spring Boot backend
├── Buraczyd-o-Frontend/    React frontend
├── Compose/                Docker Compose for local development
└── aws_deploy/
    ├── terraform/          AWS infrastructure (VPC, EC2, RDS)
    └── ansible/
        ├── playbook.yaml   Server configuration and deployment
        ├── aws_ec2.yaml    Dynamic inventory (AWS EC2 plugin)
        └── Deploy/         Kubernetes manifests
```