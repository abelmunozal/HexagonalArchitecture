# Hexagonal Architecture with REST and gRPC Microservices

## Overview
This open-source project demonstrates an advanced implementation of hexagonal architecture, where the agents are RESTful microservices, and the core consists of gRPC microservices.
The project's design allows for seamless integration and transformation of REST requests into gRPC calls to the core services, ensuring a decoupled, scalable, and efficient system.

The project is composed by two gRPC core services and one REST agent:
1. **The Core Echo Service**: This gRPC microservice implements CRUP operations for the albums and photos entities. The essential of this service is that it returns what it receives.
    This microservice offers its functionalities through the address grpc://localhost:9091.
2. **The Core Service**: This gRPC microservice implements CRUP operations for the albums and photos entities. The essential of this service is that it stores the entities in an H2 embedded database in memory.
    This microservice offers its functionalities through the address grpc://localhost:9090.
3. **The RESTful Agent Service**: This RESTful microservice allows CRUP operations through the core services in a RESTful way. This agent has been implemented using the open API features.
    This microservice offers its functionalities through the address http://localhost:8081.
    You can access the OpenAPI documentation through http://localhost:8080/swagger-ui/index.html

    You could decide which one of the core microservices is going to be used by the agent setting the environment variable BCNC_GRPC_SERVER_ADDRESS to:
    - **static://localhost:9091** for the ECHO core service.
    - **static://localhost:9090** for the H2 core service.
    The BCNC_GRPC_SERVER_ADDRESS can be updated modifing the **build.cmd** script.

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
- Extensive use of streams to improve interoperability performance among users, agents and the core microservices.
- Accomplishment of the HATEOAS principles in RESTful message bodies.

## Getting Started
### Prerequisites
- Windows 10/11
- Any gRPC client (Postman, grpcurl, etc.)
- Any REST client (Postman, curl, etc.)

#### Optional
- Docker Desktop 

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
