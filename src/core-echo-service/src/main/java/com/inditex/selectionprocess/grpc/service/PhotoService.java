/**
 * 
 */
package com.inditex.selectionprocess.grpc.service;

import java.util.UUID;

import org.springframework.http.MediaType;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.ContentChunk;
import com.inditex.selectionprocess.grpc.protobuf.ImageDownloadResponse;
import com.inditex.selectionprocess.grpc.protobuf.ImageUploadRequest;
import com.inditex.selectionprocess.grpc.protobuf.Photo;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 
 */
@Slf4j
@GrpcService
public class PhotoService extends com.inditex.selectionprocess.grpc.protobuf.PhotoServiceGrpc.PhotoServiceImplBase {
	@Override
	public void create(Photo request, StreamObserver<Photo> responseObserver) {
		try {
			responseObserver.onNext(request);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void update(Photo request, StreamObserver<Photo> responseObserver) {
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
	public void findById(StringValue request, StreamObserver<Photo> responseObserver) {
		try {
			var uuid = request.getValue();
			Photo dto = Photo.newBuilder().setId(uuid).setName("The name of " + uuid)
					.setDescription("The description of " + uuid).setType("the type of " + uuid).build();

			responseObserver.onNext(dto);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void findAll(Empty request, StreamObserver<Photo> responseObserver) {
		try {
			var uuid = UUID.randomUUID().toString();
			Photo dto = Photo.newBuilder().setId(uuid).setName("The name of " + uuid)
					.setDescription("The description of " + uuid).setType("the type of " + uuid).build();

			responseObserver.onNext(dto);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public StreamObserver<ImageUploadRequest> uploadImage(StreamObserver<Empty> responseObserver) {
		return new StreamObserver<ImageUploadRequest>() {
			@Override
			public void onNext(ImageUploadRequest request) {
			}

			@Override
			public void onError(Throwable t) {
				this.onCompleted();
			}

			@Override
			public void onCompleted() {
				responseObserver.onNext(Empty.newBuilder().build());
				responseObserver.onCompleted();
			}
		};
	}

	@Override
	public void downloadImage(StringValue request, StreamObserver<ImageDownloadResponse> responseObserver) {
		try {
			// Metadata
			var metadata = Photo.newBuilder().setId(UUID.randomUUID().toString()).setType(MediaType.IMAGE_PNG_VALUE).build();
			var metadataResponse = ImageDownloadResponse.newBuilder().setMetadata(metadata).build();
			responseObserver.onNext(metadataResponse);
			
			// Content
			var content = ContentChunk.newBuilder().setSize(0).setContent(ByteString.EMPTY).build();
			var contentResponse = ImageDownloadResponse.newBuilder().setChunk(content).build();
			responseObserver.onNext(contentResponse);
			
			responseObserver.onCompleted();
		} catch (Exception e) {
			responseObserver.onError(io.grpc.Status.ABORTED
					.withDescription("Unable to acquire the image " + request.getValue()).withCause(e).asException());
		}
	}
	
}
