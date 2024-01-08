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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inditex.selectionprocess.grpc.client.CoreServiceClient;
import com.inditex.selectionprocess.grpc.client.GrpcUtilities;
import com.inditex.selectionprocess.grpc.protobuf.Album;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;

/**
 * 
 */
@RestController
@RequestMapping(value = "/albums", produces = MediaTypes.HAL_JSON_VALUE)
public class AlbumServiceController {
	@Autowired
	private ApplicationContext applicationContext;

	private CoreServiceClient grpcClient;

	@PostConstruct
	public void init() {
		this.grpcClient = this.applicationContext.getBean(CoreServiceClient.class);
	}

	@GetMapping
	public ResponseEntity<?> findAllAlbums() {
		try {
			Collection<AlbumRepresentationModel> albums = new LinkedList<AlbumRepresentationModel>();

			Iterator<Album> grpcResult = this.grpcClient.findAllAlbums();
			grpcResult.forEachRemaining(album -> {
				AlbumRepresentationModel arm = new AlbumRepresentationModel();
				arm.setId(album.getId());
				arm.setName(album.getName());
				arm.setDescription(album.getDescription());
				arm.setPhotos(album.getPhotosList());

				arm.add(linkTo(methodOn(AlbumServiceController.class).findAlbumById(album.getId())).withSelfRel());

				albums.add(arm);
			});

			CollectionModel<AlbumRepresentationModel> collectionModel = CollectionModel.of(albums);
			collectionModel.add(linkTo(methodOn(AlbumServiceController.class).findAllAlbums()).withSelfRel());

			return ResponseEntity.ok(collectionModel);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> findAlbumById(@PathVariable("id") String id) {
		try {
			Album grpcAlbum = this.grpcClient.findAlbumById(id);

			AlbumRepresentationModel arm = new AlbumRepresentationModel();
			arm.setId(grpcAlbum.getId());
			arm.setName(grpcAlbum.getName());
			arm.setDescription(grpcAlbum.getDescription());
			arm.setPhotos(grpcAlbum.getPhotosList());

			arm.add(linkTo(methodOn(AlbumServiceController.class).findAlbumById(id)).withSelfRel());

			return ResponseEntity.ok(arm);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@PostMapping
	public ResponseEntity<?> createAlbum(@RequestBody AlbumRepresentationModel request) {
		try {
			Album.Builder grpcRequestBuilder = Album.newBuilder().setId(request.getId()).setName(request.getName());
			if (StringUtils.isNotEmpty(request.getDescription())) {
				grpcRequestBuilder.setDescription(request.getDescription());
			}
			if (request.getPhotos() != null) {
				grpcRequestBuilder.addAllPhotos(request.getPhotos());
			}
			Album grpcRequest = grpcRequestBuilder.build();
			Album grpcResponse = this.grpcClient.createAlbum(grpcRequest);

			AlbumRepresentationModel arm = new AlbumRepresentationModel();
			arm.setId(grpcResponse.getId());
			arm.setName(grpcResponse.getName());
			arm.setDescription(grpcResponse.getDescription());
			arm.setPhotos(grpcResponse.getPhotosList());

			arm.add(linkTo(methodOn(AlbumServiceController.class).findAlbumById(arm.getId())).withSelfRel());

			return ResponseEntity.ok(arm);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateAlbum(@PathVariable("id") String id, @RequestBody AlbumRepresentationModel request) {
		try {
			Album.Builder grpcRequestBuilder = Album.newBuilder().setId(id);
			if (StringUtils.isNotEmpty(request.getName())) {
				grpcRequestBuilder.setName(request.getName());
			}
			if (StringUtils.isNotEmpty(request.getDescription())) {
				grpcRequestBuilder.setDescription(request.getDescription());
			}
			if (!request.getPhotos().isEmpty()) {
				grpcRequestBuilder.addAllPhotos(request.getPhotos());
			}
			Album grpcRequest = grpcRequestBuilder.build();
			Album grpcResponse = this.grpcClient.updateAlbum(grpcRequest);

			AlbumRepresentationModel arm = new AlbumRepresentationModel();
			arm.setId(grpcResponse.getId());
			arm.setName(grpcResponse.getName());
			arm.setDescription(grpcResponse.getDescription());
			arm.setPhotos(grpcResponse.getPhotosList());

			arm.add(linkTo(methodOn(AlbumServiceController.class).findAlbumById(arm.getId())).withSelfRel());

			return ResponseEntity.ok(arm);
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAlbumById(@PathVariable("id") String id) {
		try {
			this.grpcClient.deleteAlbumById(id);
			return ResponseEntity.noContent().build();
		} catch (io.grpc.StatusRuntimeException sre) {
			return GrpcUtilities.handleGrpcExceptions(sre);
		}
	}
}
