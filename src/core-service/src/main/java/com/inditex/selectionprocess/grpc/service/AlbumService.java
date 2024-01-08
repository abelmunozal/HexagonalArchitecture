/**
 * 
 */
package com.inditex.selectionprocess.grpc.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.Album;
import com.inditex.selectionprocess.grpc.protobuf.AlbumServiceGrpc.AlbumServiceImplBase;
import com.inditex.selectionprocess.model.entity.EAlbum;
import com.inditex.selectionprocess.model.entity.EPhoto;
import com.inditex.selectionprocess.model.repository.EAlbumRepository;
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
public class AlbumService extends AlbumServiceImplBase {
	@Autowired
	EAlbumRepository albumsRepository;
	@Autowired
	EPhotoRepository photosRepository;

	@Override
	public void create(Album request, StreamObserver<Album> responseObserver) {
		try {
			if (StringUtils.isEmpty(request.getName())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the album name").asException());
				
				return;
			}
			
			EAlbum entity = new EAlbum();
			entity.setName(request.getName());
			entity.setDescription(request.getDescription());
			if (request.getPhotosCount() > 0) {
				List<EPhoto> photos = new LinkedList<EPhoto>();
				request.getPhotosList().forEach(photoId -> {
					Optional<EPhoto> opt2 = photosRepository.findById(photoId);
					if (opt2.isEmpty()) {
						responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the photo" + photoId).asException());
						
						return;
					}
					photos.add(opt2.get());
				});
				
				entity.setPhotos(photos);
			}
			entity = albumsRepository.save(entity);
			
			Album dto = Album.newBuilder().setId(entity.getId()).setName(entity.getName())
					.setDescription(entity.getDescription()).build();

			responseObserver.onNext(dto);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void update(Album request, StreamObserver<Album> responseObserver) {
		try {
			if (StringUtils.isEmpty(request.getId())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the album identifier").asException());
				
				return;
			}
			
			Optional<EAlbum> opt = albumsRepository.findById(request.getId());
			if (opt.isPresent()) {
				EAlbum entity = opt.get();
				if (StringUtils.isNotEmpty(request.getName())) {
					entity.setName(request.getName());
				}
				if (StringUtils.isNotEmpty(request.getDescription())) {
					entity.setDescription(request.getDescription());
				}
				if (request.getPhotosCount() > 0) {
					List<EPhoto> photos = new LinkedList<EPhoto>();
					request.getPhotosList().forEach(photoId -> {
						Optional<EPhoto> opt2 = photosRepository.findById(photoId);
						if (opt2.isEmpty()) {
							responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the photo" + photoId).asException());
							
							return;
						}
						photos.add(opt2.get());
					});
					
					entity.setPhotos(photos);
				}
				
				entity = albumsRepository.save(entity);
				
				Album.Builder builder = Album.newBuilder();
				builder.setId(entity.getId());
				builder.setName(entity.getName());
				builder.setDescription(entity.getDescription()).build();
				entity.getPhotos().forEach(photo -> {
					builder.addPhotos(photo.getId());
				});
				Album dto = builder.build();

				responseObserver.onNext(dto);
				responseObserver.onCompleted();
			} else {
				responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the album " + request.getId()).asException());
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
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the album identifier").asException());
				
				return;
			}
			
			albumsRepository.deleteById(request.getValue());

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
			if (StringUtils.isEmpty(request.getValue())) {
				responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription("You MUST set the album identifier").asException());
				
				return;
			}
			
			Optional<EAlbum> opt = albumsRepository.findById(request.getValue());
			if (opt.isPresent()) {
				EAlbum entity = opt.get();
				Album.Builder builder = Album.newBuilder();
				builder.setId(entity.getId());
				builder.setName(entity.getName());
				builder.setDescription(entity.getDescription()).build();
				entity.getPhotos().forEach(photo -> {
					builder.addPhotos(photo.getId());
				});
				Album dto = builder.build();

				responseObserver.onNext(dto);
				responseObserver.onCompleted();
			} else {
				responseObserver.onError(io.grpc.Status.NOT_FOUND.withDescription("Unable to find the album " + request.getValue()).asException());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			
			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}

	@Override
	public void findAll(Empty request, StreamObserver<Album> responseObserver) {
		try {
			Iterable<EAlbum> entities = albumsRepository.findAll();
			entities.forEach(entity -> {
				Album.Builder builder = Album.newBuilder();
				builder.setId(entity.getId());
				builder.setName(entity.getName());
				builder.setDescription(entity.getDescription()).build();
				entity.getPhotos().forEach(photo -> {
					builder.addPhotos(photo.getId());
				});
				Album dto = builder.build();

				responseObserver.onNext(dto);
			});
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			responseObserver.onError(io.grpc.Status.fromThrowable(e).asException());
		}
	}
}
