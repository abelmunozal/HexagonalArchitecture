@echo off
cls
echo The gRPC ECHO service is instantiated on port 9091
echo Some of the integration tests will fail becouse it always returns the same that you have sent
rem set BCNC_GRPC_SERVER_ADDRESS=static://localhost:9091

echo The gRPC service that uses H2 is instantiated on port 9090
echo The integration tests will be able to execute without issues
set BCNC_GRPC_SERVER_ADDRESS=static://localhost:9090

timeout /t 3

cd src/core-echo-service
call mvnw clean package
start "core-echo-service" cmd /C "mvnw spring-boot:run"
cd ..\..
cd src/core-service
call mvnw clean package
start cmd /C mvnw spring-boot:run
cd ..\..

timeout /t 3 /nobreak

cd src/agent-rest-service
call mvnw clean package
start cmd /C mvnw spring-boot:run
cd ..\..
echo DONE!!

echo .
echo The gRPC service that uses H2 is listening on port 9090
echo The gRPC ECHO service is listening on port 9091
echo You can test them with Postman (you can use server reflection) or grpcurl
echo .
echo The REST AGENT is listening on port 8080
echo You can get the OpenAPI information accessing 'http://localhost:8080/swagger-ui/index.html'
echo .

