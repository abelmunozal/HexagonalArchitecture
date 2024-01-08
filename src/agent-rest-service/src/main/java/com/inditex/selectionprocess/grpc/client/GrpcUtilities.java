/**
 * 
 */
package com.inditex.selectionprocess.grpc.client;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 
 */
public abstract class GrpcUtilities {
	static public ResponseEntity<String> handleGrpcExceptions(io.grpc.StatusRuntimeException sre) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

		if (sre.getStatus().getCode() == io.grpc.Status.NOT_FOUND.getCode()) {
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (sre.getStatus().getCode() == io.grpc.Status.INVALID_ARGUMENT.getCode()) {
			httpStatus = HttpStatus.BAD_REQUEST;
		}

		return ResponseEntity.status(httpStatus).body(sre.getStatus().getDescription());
	}

	static public ResponseEntity<StreamingResponseBody> handleGrpcExceptionsStream(io.grpc.StatusRuntimeException sre) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

		if (sre.getStatus().getCode() == io.grpc.Status.NOT_FOUND.getCode()) {
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (sre.getStatus().getCode() == io.grpc.Status.INVALID_ARGUMENT.getCode()) {
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		
		StreamingResponseBody stream = outputStream -> {
		    outputStream.write(sre.getStatus().getDescription().getBytes(StandardCharsets.UTF_8));
		};

		return ResponseEntity.status(httpStatus).body(stream);
	}
}
