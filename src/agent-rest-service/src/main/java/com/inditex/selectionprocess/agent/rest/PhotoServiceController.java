/**
 * 
 */
package com.inditex.selectionprocess.agent.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.inditex.selectionprocess.grpc.client.CoreServiceClient;
import com.inditex.selectionprocess.grpc.client.GrpcUtilities;
import com.inditex.selectionprocess.grpc.protobuf.ContentChunk;
import com.inditex.selectionprocess.grpc.protobuf.ImageDownloadResponse;
import com.inditex.selectionprocess.grpc.protobuf.Photo;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@RestController
@RequestMapping(value = "/photos", produces = MediaTypes.HAL_JSON_VALUE)
public class PhotoServiceController {
	@Autowired
	private ApplicationContext applicationContext;

	private CoreServiceClient grpcClient;

	@PostConstruct
	public void init() {
		this.grpcClient = this.applicationContext.getBean(CoreServiceClient.class);
	}

	@GetMapping
	public ResponseEntity<?> findAllPhotos() {
		try {
			Collection<PhotoRepresentationModel> results = new LinkedList<PhotoRepresentationModel>();

			Iterator<Photo> grpcResult = this.grpcClient.findAllPhotos();
			grpcResult.forEachRemaining(result -> {
				PhotoRepresentationModel arm = new PhotoRepresentationModel();
				arm.setId(result.getId());
				arm.setName(result.getName());
				arm.setDescription(result.getDescription());
				arm.setType(result.getType());

				arm.add(linkTo(methodOn(PhotoServiceController.class).findPhotoById(result.getId())).withSelfRel());

				results.add(arm);
			});

			CollectionModel<PhotoRepresentationModel> collectionModel = CollectionModel.of(results);
			collectionModel.add(linkTo(methodOn(PhotoServiceController.class).findAllPhotos()).withSelfRel());

			return ResponseEntity.ok(collectionModel);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findPhotoById(@PathVariable("id") String id) {
		try {
			Photo grpcPhoto = this.grpcClient.findPhotoById(id);

			PhotoRepresentationModel arm = new PhotoRepresentationModel();
			arm.setId(grpcPhoto.getId());
			arm.setName(grpcPhoto.getName());
			arm.setDescription(grpcPhoto.getDescription());
			arm.setType(grpcPhoto.getType());

			arm.add(linkTo(methodOn(PhotoServiceController.class).findPhotoById(id)).withSelfRel());

			return ResponseEntity.ok(arm);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@PostMapping
	public ResponseEntity<?> createPhoto(@RequestBody PhotoRepresentationModel request) {
		try {
			Photo.Builder grpcRequestBuilder = Photo.newBuilder().setId(request.getId());
			grpcRequestBuilder.setName(request.getName());
			grpcRequestBuilder.setDescription(request.getDescription());
			grpcRequestBuilder.setType(request.getType());
			Photo grpcRequest = grpcRequestBuilder.build();

			Photo grpcResponse = this.grpcClient.createPhoto(grpcRequest);

			PhotoRepresentationModel arm = new PhotoRepresentationModel();
			arm.setId(grpcResponse.getId());
			arm.setName(grpcResponse.getName());
			arm.setDescription(grpcResponse.getDescription());
			arm.setType(grpcResponse.getType());

			arm.add(linkTo(methodOn(PhotoServiceController.class).findPhotoById(arm.getId())).withSelfRel());

			return ResponseEntity.ok(arm);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updatePhoto(@PathVariable("id") String id, @RequestBody PhotoRepresentationModel request) {
		try {
			Photo.Builder grpcRequestBuilder = Photo.newBuilder().setId(id);
			if (StringUtils.isNotEmpty(request.getName())) {
				grpcRequestBuilder.setName(request.getName());
			}
			if (StringUtils.isNotEmpty(request.getDescription())) {
				grpcRequestBuilder.setDescription(request.getDescription());
			}
			if (StringUtils.isNotEmpty(request.getType())) {
				grpcRequestBuilder.setType(request.getType());
			}
			Photo grpcRequest = grpcRequestBuilder.build();
			Photo grpcResponse = this.grpcClient.updatePhoto(grpcRequest);

			PhotoRepresentationModel arm = new PhotoRepresentationModel();
			arm.setId(grpcResponse.getId());
			arm.setName(grpcResponse.getName());
			arm.setDescription(grpcResponse.getDescription());
			arm.setType(grpcResponse.getType());

			arm.add(linkTo(methodOn(PhotoServiceController.class).findPhotoById(arm.getId())).withSelfRel());

			return ResponseEntity.ok(arm);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePhotoById(@PathVariable("id") String id) {
		try {
			this.grpcClient.deletePhotoById(id);
			return ResponseEntity.noContent().build();
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@PostMapping("/{id}/upload")
	public ResponseEntity<?> uploadPhotoImage(@PathVariable("id") String id, @RequestParam("file") MultipartFile file) {
		try {
			this.grpcClient.uploadPhotoImage(id, file);

			return ResponseEntity.noContent().build();
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		} catch (Throwable e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/{id}/download")
	public ResponseEntity<StreamingResponseBody> downloadPhotoImage(@PathVariable("id") String id) {
		final String[] contentType = { MediaType.IMAGE_PNG_VALUE };

		try {
			Photo photo = this.grpcClient.findPhotoById(id);
			contentType[0] = photo.getType();

			StreamingResponseBody responseBody = response -> {
				Iterator<ImageDownloadResponse> chunks = this.grpcClient.downloadPhotoImage(id);
				chunks.forEachRemaining(chunk -> {
					try {
						if (chunk.hasMetadata()) {
							Photo metadata = chunk.getMetadata();
							contentType[0] = metadata.getType();
						} else {
							ContentChunk content = chunk.getChunk();
							response.write(content.getContent().toByteArray());
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				});
			};

			HttpHeaders headers = new HttpHeaders();
		    headers.add(HttpHeaders.CONTENT_TYPE, contentType[0]);
			return new ResponseEntity<StreamingResponseBody>(responseBody, headers, HttpStatus.OK);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptionsStream(sre);
		}
	}
}
