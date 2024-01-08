/**
 * 
 */
package com.inditex.selectionprocess.grpc.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.ContentChunk;
import com.inditex.selectionprocess.grpc.protobuf.ImageDownloadResponse;
import com.inditex.selectionprocess.grpc.protobuf.ImageUploadRequest;
import com.inditex.selectionprocess.grpc.protobuf.Photo;
import com.inditex.selectionprocess.grpc.protobuf.PhotoServiceGrpc.PhotoServiceImplBase;
import com.inditex.selectionprocess.model.entity.EPhoto;
import com.inditex.selectionprocess.model.repository.EPhotoRepository;

import io.grpc.stub.StreamObserver;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 
 */
@Slf4j
@GrpcService
public class PhotoService extends PhotoServiceImplBase {
	private static final Path IMAGE_STORAGE_PATH = Paths.get(System.getProperty("java.io.tmpdir"));
	private static final MimeTypes MIME_TYPES = MimeTypes.getDefaultMimeTypes();

	@Autowired
	EPhotoRepository repository;

	@Override
	public void create(Photo request, StreamObserver<Photo> responseObserver) {
		try {
			if (StringUtils.isEmpty(request.getName())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the photo name").asException());
				
				return;
			}
			if (StringUtils.isEmpty(request.getType())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the MIME-TYPE of the image").asException());
				
				return;
			}
			
			MimeType mimeType = this.getContentType(request.getType());
			if (mimeType == null) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("Incorrect MIME type '" + request.getType() + "'").asException());
				
				return;
			}
			
			EPhoto entity = new EPhoto();
			entity.setName(request.getName());
			entity.setDescription(request.getDescription());
			entity.setType(mimeType.getName());
			entity = repository.save(entity);

			Photo photo = Photo.newBuilder().setId(entity.getId()).setName(entity.getName())
					.setDescription(entity.getDescription()).setType(entity.getType()).build();

			responseObserver.onNext(photo);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void update(Photo request, StreamObserver<Photo> responseObserver) {
		try {
			if (StringUtils.isEmpty(request.getId())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the photo identifier").asException());
				
				return;
			}
			
			Optional<EPhoto> opt = repository.findById(request.getId());
			if (opt.isPresent()) {
				EPhoto entity = opt.get();
				if (StringUtils.isNotEmpty(request.getName())) {
					entity.setName(request.getName());
				}
				if (StringUtils.isNotEmpty(request.getDescription())) {
					entity.setDescription(request.getDescription());
				}
				if (StringUtils.isNotEmpty(request.getType())) {
					MimeType mimeType = this.getContentType(request.getType());
					if (mimeType == null) {
						responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("Incorrect MIME type '" + request.getType() + "'").asException());
						
						return;
					}
					
					entity.setType(mimeType.getName());
				}
				entity = repository.save(entity);
				
				Photo dto = Photo.newBuilder().setId(entity.getId()).setName(entity.getName())
						.setDescription(entity.getDescription()).setType(entity.getType()).build();

				responseObserver.onNext(dto);
				responseObserver.onCompleted();
			} else {
				responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the photo " + request.getId()).asException());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void delete(StringValue request, StreamObserver<Empty> responseObserver) {
		try {
			if (StringUtils.isEmpty(request.getValue())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the photo identifier").asException());
				
				return;
			}
			
			repository.deleteById(request.getValue());

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
			if (StringUtils.isEmpty(request.getValue())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the album identifier").asException());
				
				return;
			}
			
			Optional<EPhoto> opt = repository.findById(request.getValue());
			if (opt.isPresent()) {
				EPhoto entity = opt.get();
				Photo dto = Photo.newBuilder().setId(entity.getId()).setName(entity.getName())
						.setDescription(entity.getDescription()).setType(entity.getType()).build();

				responseObserver.onNext(dto);
				responseObserver.onCompleted();
			} else {
				responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the photo " + request.getValue()).asException());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void findAll(Empty request, StreamObserver<Photo> responseObserver) {
		try {
			Iterable<EPhoto> entities = repository.findAll();
			entities.forEach(entity -> {
				Photo dto = Photo.newBuilder().setId(entity.getId()).setName(entity.getName())
						.setDescription(entity.getDescription()).setType(entity.getType()).build();

				responseObserver.onNext(dto);
			});
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public StreamObserver<ImageUploadRequest> uploadImage(StreamObserver<Empty> responseObserver) {
		return new StreamObserver<ImageUploadRequest>() {
			OutputStream writer;

			@Override
			public void onNext(ImageUploadRequest request) {
				try {
					if (request.hasMetadata()) {
						var imageMetadata = request.getMetadata();
						/**/
						Optional<EPhoto> opt = repository.findById(imageMetadata.getId());
						if (opt.isPresent()) {
							EPhoto entity = opt.get();
							entity.setType(imageMetadata.getType());
							
							repository.save(entity);
						} else {
							this.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the photo " + imageMetadata.getId()).asException());
						}
						/**/
						this.writer = Files.newOutputStream(getImageFilePath(imageMetadata.getId(), imageMetadata.getType()));
					} else {
						this.writer.write(request.getChunk().getContent().toByteArray());
					}
				} catch (Exception e) {
					this.onError(e);
				}
			}

			@Override
			public void onError(Throwable t) {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (Exception ignored) {
				}

				responseObserver.onError(t);
			}

			@Override
			public void onCompleted() {
				try {
					if (writer != null) {
						writer.flush();
						writer.close();
					}
				} catch (Exception ignored) {
				}

				responseObserver.onNext(Empty.newBuilder().build());
				responseObserver.onCompleted();
			}
		};
	}

	@Override
	public void downloadImage(StringValue request, StreamObserver<ImageDownloadResponse> responseObserver) {
		try {
			Optional<EPhoto> opt = repository.findById(request.getValue());
			if (opt.isPresent()) {
				EPhoto entity = opt.get();
				// Metadata
				var metadata = Photo.newBuilder().setId(entity.getId()).setName(entity.getName()).setDescription(entity.getDescription()).setType(entity.getType());
				var metadataResponse = ImageDownloadResponse.newBuilder().setMetadata(metadata).build();
				responseObserver.onNext(metadataResponse);
				
				Path imagePath = getImageFilePath(entity.getId(), entity.getType());
				InputStream imageIS = Files.newInputStream(imagePath, StandardOpenOption.READ);
				try {
					int bufferSize = 4 * 1024;// 4K
					byte[] buffer = new byte[bufferSize];
					int length;
					while ((length = imageIS.read(buffer, 0, bufferSize)) != -1) {
						// Content
						var content = ContentChunk.newBuilder().setSize(length).setContent(ByteString.copyFrom(buffer, 0, length)).build();
						var contentResponse = ImageDownloadResponse.newBuilder().setChunk(content).build();
						responseObserver.onNext(contentResponse);
					}
				} finally {
					imageIS.close();
				}
				responseObserver.onCompleted();
			} else {
				responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the image " + request.getValue()).asException());
			}
		} catch (Exception e) {
			responseObserver.onError(io.grpc.Status.ABORTED.withDescription("Unable to acquire the image " + request.getValue())
					.withCause(e).asException());
		}
	}
	
	private MimeType getContentType(final String type) {
		MimeType mimeType = null;
		try {
			mimeType = MIME_TYPES.getRegisteredMimeType(type);
		} catch (MimeTypeException ignored) {
		}

		return mimeType;
	}

	private Path getImageFilePath(final String imageId, final String imageMIMEType)
			throws IOException, MimeTypeException {
		MimeType type = MIME_TYPES.forName(imageMIMEType);
		String fileExtension = type.getExtension();
		var fileName = imageId + "." + fileExtension;
		return IMAGE_STORAGE_PATH.resolve(fileName);
	}
}
