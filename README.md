# Hexagonal Architecture with REST and gRPC Microservices

## Overview
This open-source project demonstrates an advanced implementation of hexagonal architecture, where the agents are RESTful microservices, and the core consists of gRPC microservices.
The project's design allows for seamless integration and transformation of REST requests into gRPC calls to the core services, ensuring a decoupled, scalable, and efficient system.

## Architecture
### Hexagonal Architecture
- **Agents (Adapters)**: RESTful microservices acting as the point of contact for external requests.
- **Core (Ports)**: gRPC microservices handling the business logic and data processing.
- **Inter-Service Communication**: REST to gRPC transformation ensuring effective communication within the architecture.

## Features
- RESTful API endpoints for user interaction.
- High-performance gRPC services for core business logic.
- Efficient communication protocol between different architectural layers.
- Scalable and maintainable codebase adhering to SOLID principles.

## Getting Started
### Prerequisites
- Docker
- Kubernetes (for deployment)
- gRPC toolkit
- Any REST client (Postman, curl, etc.)

### Installation and Setup
1. **Clone the repository**: `git clone https://github.com/yourproject/hexagonal-architecture.git`
2. **Build the project**: Navigate to the project directory and run `docker-compose up --build`
3. **Access the REST API**: The API is accessible at `http://localhost:8080`

## Usage
### Making Requests
- Use any REST client to interact with the RESTful services.
- Example request: `curl -X GET http://localhost:8080/api/v1/resource`

## Contributing
Contributions to this project are welcome. Please follow these steps:
1. Fork the repository.
2. Create a new feature branch (`git checkout -b feature/yourFeature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/yourFeature`).
5. Create a new Pull Request.

## Code of Conduct
Please refer to the `CODE_OF_CONDUCT.md` file for guidelines on community interaction.

## License
This project is licensed under the MIT License - see the `LICENSE.md` file for details.

## Contact
For support or queries, please email us at `support@yourproject.com`.

## Acknowledgments
- Special thanks to contributors and the open-source community.
- Inspired by the principles of hexagonal architecture and microservices.
