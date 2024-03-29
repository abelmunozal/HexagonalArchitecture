# Hexagonal Architecture with REST and gRPC Microservices

## Overview
This open-source project demonstrates an advanced implementation of hexagonal architecture, where the agents are RESTful microservices, and the core consists of gRPC microservices.
The project's design allows for seamless integration and transformation of REST requests into gRPC calls to the core services, ensuring a decoupled, scalable, and efficient system.

![Hexagonal Architecture](./resources/images/313px-Hexagonal_Architecture.svg.png)

The project is composed by two gRPC core services and one REST agent:
1. **The Core Echo Service**: This gRPC microservice implements CRUP operations for the albums and photos entities. The essential of this service is that it returns what it receives.
    This microservice offers its functionalities through the address grpc://localhost:9091.
2. **The Core Service**: This gRPC microservice implements CRUP operations for the albums and photos entities. The essential of this service is that it stores the entities in an H2 embedded database in memory.
    This microservice offers its functionalities through the address grpc://localhost:9090.
3. **The RESTful Agent Service**: This RESTful microservice allows CRUP operations through the core services in a RESTful way. This agent has been implemented using the open API features.
    This microservice offers its functionalities through the address `http://localhost:8080`.
    You can access the OpenAPI documentation through `http://localhost:8080/swagger-ui/index.html`

    You could decide which one of the core microservices is going to be used by the agent setting the environment variable `BCNC_GRPC_SERVER_ADDRESS` to:
    - **static://localhost:9091** for the ECHO core service.
    - **static://localhost:9090** for the H2 core service.
    The `BCNC_GRPC_SERVER_ADDRESS` can be updated modifying the [build.cmd](build.cmd) script.

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
In order to install, build, execute and test the project you can follow two flavours:

#### Windows 10/11 scripts
1. **Clone the repository**: `git clone https://github.com/yourproject/hexagonal-architecture.git`
2. **Download the tools**: You must download and deploy the required tools following the guidelines set in the [tools/README.md](./tools/README.md) document.
3. **Set the environment variables**: To do that execute the script [setenv.cmd](./setenv.cmd).
4. **Build the project**: To do that execute the script [build.cmd](./build.cmd).
    This script compiles, performs the unit tests, the integration tests and leaves the microservices running in independent consoles.

    - The gRPC API for the H2 core service is accessible at `grpc://localhost:9090`.
    - The gRPC API for the ECHO core service is accessible at `grpc://localhost:9091`.
    - The RESTful API is accessible at `http://localhost:8080/albums` and `http://localhost:8080/photos`.
    - The OpenAPI documentation is accessible at `http://localhost:8080/swagger-ui/index.html`.

    You could decide which one of the core microservices is going to be used by the agent setting the environment variable `BCNC_GRPC_SERVER_ADDRESS` to:
    - **static://localhost:9091** for the ECHO core service.
    - **static://localhost:9090** for the H2 core service.
    The `BCNC_GRPC_SERVER_ADDRESS` can be updated modifying the [build.cmd](build.cmd) script.

#### Docker Compose
1. **Clone the repository**: `git clone https://github.com/yourproject/hexagonal-architecture.git`
2. **Build the project**: Run `docker-compose up --build`.

    This docker-compose script compiles, performs the unit tests, the integration tests and leaves the microservices running.

    - The gRPC API for the H2 core service is accessible at `grpc://localhost:9090`.
    - The gRPC API for the ECHO core service is accessible at `grpc://localhost:9091`.
    - The RESTful API is accessible at `http://localhost:8080/albums` and `http://localhost:8080/photos`.
    - The OpenAPI documentation is accessible at `http://localhost:8080/swagger-ui/index.html`.

    You could decide which one of the core microservices is going to be used by the agent setting the environment variable `BCNC_GRPC_SERVER_ADDRESS` to:
    - **static://localhost:9091** for the ECHO core service.
    - **static://localhost:9090** for the H2 core service.
    The `BCNC_GRPC_SERVER_ADDRESS` can be updated modifying the [docker-compose.yml](docker-compose.yml) script.

## Usage
You can use several tools to interact with the services.

As guidelines:
- The gRPC API services entrypoints are defined in their [protobuf](src/protobuf/src/inditex.proto) file.
- The RESTful API services entrypoints are define in the Swagger UI address: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### grpcurl

```
    grpcurl --plaintext localhost:9090 describe
```

![grpcurl describe](resources/images/grpcurl-describe.png)

```
    grpcurl --plaintext localhost:9090 inditex.v1.AlbumService/findAll
```

![grpcurl findAll](resources/images/grpcurl-findAll.png)

### Postman

![grpc findAll](resources/images/postman-findAll.png)

![http findAll](resources/images/postman-http-findAll.png)

### curl

```
    curl --location 'http://localhost:8080/albums'
```

```
    curl --location 'http://localhost:8080/albums' \
        --header 'Content-Type: application/json' \
        --data '{"name": "REST Test album 3", "description": "This is a REST test album", "photos": []}'
```

## Acknowledgments
- Special thanks to contributors and the open-source community.
- Inspired by the principles of hexagonal architecture and microservices.
