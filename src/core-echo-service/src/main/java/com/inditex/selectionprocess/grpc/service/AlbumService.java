/**
 * 
 */
package com.inditex.selectionprocess.grpc.service;

import java.util.UUID;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.Album;
import com.inditex.selectionprocess.grpc.protobuf.AlbumServiceGrpc.AlbumServiceImplBase;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 
 */
@Slf4j
@GrpcService
public class AlbumService extends AlbumServiceImplBase {

	@Override
	public void create(Album request, StreamObserver<Album> responseObserver) {
		try {
			responseObserver.onNext(request);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void update(Album request, StreamObserver<Album> responseObserver) {
		try {
			responseObserver.onNext(request);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void delete(StringValue request, StreamObserver<Empty> responseObserver) {
		try {
			responseObserver.onNext(Empty.newBuilder().build());
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void findById(StringValue request, StreamObserver<Album> responseObserver) {
		try {
			var uuid = request.getValue();
			
			Album dto = Album.newBuilder().setId(uuid).setName("The name of " + uuid)
					.setDescription("The description of " + uuid).build();

			responseObserver.onNext(dto);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void findAll(Empty request, StreamObserver<Album> responseObserver) {
		try {
			var uuid = UUID.randomUUID().toString();
			
			Album dto = Album.newBuilder().setId(uuid).setName("The name of " + uuid)
					.setDescription("The description of " + uuid).build();

			responseObserver.onNext(dto);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}
}
