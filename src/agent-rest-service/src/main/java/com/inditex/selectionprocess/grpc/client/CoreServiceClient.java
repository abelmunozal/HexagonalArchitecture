/**
 * 
 */
package com.inditex.selectionprocess.grpc.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.Album;
import com.inditex.selectionprocess.grpc.protobuf.AlbumServiceGrpc.AlbumServiceBlockingStub;
import com.inditex.selectionprocess.grpc.protobuf.ContentChunk;
import com.inditex.selectionprocess.grpc.protobuf.ImageDownloadResponse;
import com.inditex.selectionprocess.grpc.protobuf.ImageUploadRequest;
import com.inditex.selectionprocess.grpc.protobuf.Photo;
import com.inditex.selectionprocess.grpc.protobuf.PhotoServiceGrpc.PhotoServiceBlockingStub;
import com.inditex.selectionprocess.grpc.protobuf.PhotoServiceGrpc.PhotoServiceStub;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;

/**
 * 
 */
@Service
public class CoreServiceClient {
	@GrpcClient("grpc-server")
	private AlbumServiceBlockingStub albumServiceStub;
	
	@GrpcClient("grpc-server")
	private PhotoServiceBlockingStub photoServiceStub;
	
	@GrpcClient("grpc-server")
	private PhotoServiceStub photoServiceNBStub;
	
	public Iterator<Album> findAllAlbums() {
		Empty emptyRequest = Empty.newBuilder().build();
		return this.albumServiceStub.findAll(emptyRequest);
	}

	public Album findAlbumById(final String albumId) {
		StringValue valueRequest = StringValue.newBuilder().setValue(albumId).build();
		return this.albumServiceStub.findById(valueRequest);
	}
	
	public Album createAlbum(final Album request) {
		return this.albumServiceStub.create(request);
	}

	public Album updateAlbum(final Album request) {
		return this.albumServiceStub.update(request);
	}

	public void deleteAlbumById(final String albumId) {
		StringValue valueRequest = StringValue.newBuilder().setValue(albumId).build();
		this.albumServiceStub.delete(valueRequest);
	}

	public Iterator<Photo> findAllPhotos() {
		Empty emptyRequest = Empty.newBuilder().build();
		return this.photoServiceStub.findAll(emptyRequest);
	}

	public Photo findPhotoById(final String photoId) {
		StringValue valueRequest = StringValue.newBuilder().setValue(photoId).build();
		return this.photoServiceStub.findById(valueRequest);
	}
	
	public Photo createPhoto(final Photo request) {
		Photo result = this.photoServiceStub.create(request);
		
		return result;
	}

	public Photo updatePhoto(final Photo request) {
		return this.photoServiceStub.update(request);
	}

	public void deletePhotoById(final String photoId) {
		StringValue valueRequest = StringValue.newBuilder().setValue(photoId).build();
		this.photoServiceStub.delete(valueRequest);
	}
	
	public void uploadPhotoImage(final String photoId, final MultipartFile multipartFile) throws Throwable {
		String fileType = null;
		InputStream fileIS = null;
		CountDownLatch countDownLatch = new CountDownLatch(1);
		final UploadStatus[] result = {new UploadStatus(io.grpc.Status.OK)};
		
		fileType = multipartFile.getContentType();
		fileIS = multipartFile.getInputStream();
		
		StreamObserver<ImageUploadRequest> imageUploadRSO = this.photoServiceNBStub.uploadImage(new StreamObserver<Empty>(){
			@Override
			public void onNext(Empty response) {
			}

			@Override
			public void onError(Throwable t) {
				result[0].status = io.grpc.Status.ABORTED;
				result[0].throwable = t;
				
				countDownLatch.countDown();
			}

			@Override
			public void onCompleted() {
				countDownLatch.countDown();
			}
		});
		
		try {
			// Send metadata
			var imageMetadata = Photo.newBuilder().setId(photoId).setType(fileType).build();
			var metadataRequest = ImageUploadRequest.newBuilder().setMetadata(imageMetadata).build();
			imageUploadRSO.onNext(metadataRequest);
			
			// Send Content
			byte[] chunkBuffer = new byte[4 * 1024];
			int chunkSize;
			while((chunkSize = fileIS.read(chunkBuffer)) != -1) {
				var imageContent = ContentChunk.newBuilder().setSize(chunkSize).setContent(ByteString.copyFrom(chunkBuffer, 0, chunkSize)).build();
				var request = ImageUploadRequest.newBuilder().setChunk(imageContent).build();
				imageUploadRSO.onNext(request);
			}
			imageUploadRSO.onCompleted();
			countDownLatch.await();
		} finally {
			try {
				fileIS.close();
			} catch (IOException ignored) {
			}
		}
		
		if (result[0].status != io.grpc.Status.OK) {
			if (result[0].throwable != null) {
				throw result[0].throwable;
			} else {
				throw new Exception("Unknown error during the file upload");
			}
		}
	}

	public Iterator<ImageDownloadResponse> downloadPhotoImage(final String photoId) {
		StringValue valueRequest = StringValue.newBuilder().setValue(photoId).build();
		return this.photoServiceStub.downloadImage(valueRequest);
	}
	
	private class UploadStatus {
		public io.grpc.Status status;
		public Throwable throwable;
		/**
		 * @param status
		 */
		public UploadStatus(io.grpc.Status status) {
			this.status = status;
		}
	}
}
