/**
 * 
 */
package com.inditex.selectionprocess.test.grpc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.ContentChunk;
import com.inditex.selectionprocess.grpc.protobuf.ImageUploadRequest;
import com.inditex.selectionprocess.grpc.protobuf.Photo;
import com.inditex.selectionprocess.grpc.protobuf.PhotoServiceGrpc.PhotoServiceBlockingStub;
import com.inditex.selectionprocess.grpc.protobuf.PhotoServiceGrpc.PhotoServiceStub;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;

/**
 * 
 */
@SpringBootTest(properties = {
        "grpc.server.inProcessName=test",
        "grpc.server.port=-1",
        "grpc.client.inProcess.address=in-process:test"
        })
@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestMethodOrder(OrderAnnotation.class)
public class PhotoServiceUnitTests {
	@GrpcClient("inProcess")
	private PhotoServiceBlockingStub photoService;
	@GrpcClient("inProcess")
	private PhotoServiceStub photoServiceNB;
	
	@Test
	@DisplayName("Unit Test: Check the JPA injection capabilities)")
	@Order(1)
	void testJPAInjection(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		assertNotNull(this.photoService);
		assertNotNull(this.photoServiceNB);
	}

	@Test
	@DisplayName("Unit Test: Photo creation (the happy path)")
	@Order(10)
	void testPhotoCreation(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Photo.newBuilder().setId(entityId).setName("This is the name of " + entityId)
					.setDescription("This is the description of " + entityId).build();
			assertNotNull(request);

			var response = this.photoService.create(request);
			assertNotNull(response);
			assertEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			assertEquals(request.getType(), response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Photo creation (wrong parameters)")
	@Order(11)
	void testPhotoCreation001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Photo.newBuilder().setId(entityId)
					//.setName("This is the name of " + entityId)
					.setDescription("This is the description of " + entityId).build();
			assertNotNull(request);

			var response = this.photoService.create(request);
			assertNotNull(response);
			assertEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			assertEquals(request.getType(), response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Photo update (the happy path)")
	@Order(20)
	void testPhotoUpdate(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Photo.newBuilder().setId(entityId).setName("This is the name of " + entityId)
					.setDescription("This is the description of " + entityId).build();
			assertNotNull(request);

			var response = this.photoService.update(request);
			assertNotNull(response);
			assertEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			assertEquals(request.getType(), response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Photo update (wrong parameters)")
	@Order(21)
	void testPhotoUpdate001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Photo.newBuilder()
					//.setId(entityId)
					.setName("This is the name of " + entityId)
					.setDescription("This is the description of " + entityId).build();
			assertNotNull(request);

			var response = this.photoService.update(request);
			assertNotNull(response);
			assertNotEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			assertEquals(request.getType(), response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Photo deletion (the happy path)")
	@Order(30)
	void testPhotoDelete(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = StringValue.newBuilder().setValue(entityId).build();
			assertNotNull(request);

			var response = this.photoService.delete(request);
			assertNotNull(response);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Photo deletion (wrong parameters)")
	@Order(31)
	void testPhotoDelete001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var request = StringValue.newBuilder().build();
			assertNotNull(request);


			var response = this.photoService.delete(request);
			assertNotNull(response);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Unit Test: Photo finding by primary key (the happy path)")
	@Order(40)
	void testPhotoFindById(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = StringValue.newBuilder().setValue(entityId).build();
			assertNotNull(request);

			var response = this.photoService.findById(request);
			assertNotNull(response);
			assertNotNull(response.getId());
			assertNotNull(response.getName());
			assertNotNull(response.getDescription());
			assertNotNull(response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Photo finding by primary key (wrong parameters)")
	@Order(41)
	void testPhotoFindById001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var request = StringValue.newBuilder().build();
			assertNotNull(request);

			var response = this.photoService.findById(request);
			assertNotNull(response);
			assertNotNull(response.getId());
			assertNotNull(response.getName());
			assertNotNull(response.getDescription());
			assertNotNull(response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Unit Test: Finding whole Photos (the happy path)")
	@Order(50)
	void testPhotoFindAll(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var request = Empty.newBuilder().build();
			assertNotNull(request);

			var response = this.photoService.findAll(request);
			assertNotNull(response);
			
			final int[] entityCounter = {0};
			response.forEachRemaining(entity -> {
				entityCounter[0]++;
				
				assertNotNull(entity.getId());
				assertNotNull(entity.getName());
				assertNotNull(entity.getDescription());
				assertNotNull(entity.getType());
			});
			assertNotEquals(0, entityCounter[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Unit Test: Upload a photo image (the happy path)")
	@Order(60)
	void testUploadPhotoImage(TestInfo testInfo) {
		InputStream fileIS = null;
		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityType = MediaType.IMAGE_PNG_VALUE;
			var entityId = UUID.randomUUID().toString();
			
			StreamObserver<ImageUploadRequest> imageUploadRSO = this.photoServiceNB.uploadImage(new StreamObserver<Empty>(){
				@Override
				public void onNext(Empty response) {
				}

				@Override
				public void onError(Throwable t) {
					fail(t.getMessage());
					
					countDownLatch.countDown();
				}

				@Override
				public void onCompleted() {
					countDownLatch.countDown();
				}
			});
			assertNotNull(imageUploadRSO);
			
			fileIS = getClass().getClassLoader().getResourceAsStream("image001.png");
			assertNotNull(fileIS);
			
			// Send metadata
			var imageMetadata = Photo.newBuilder().setId(entityId).setType(entityType).build();
			var metadataRequest = ImageUploadRequest.newBuilder().setMetadata(imageMetadata).build();
			assertNotNull(metadataRequest);
			imageUploadRSO.onNext(metadataRequest);

			// Send Content
			byte[] chunkBuffer = new byte[4 * 1024];
			int chunkSize = 0;
			int chunkCounter = 0;
			while((chunkSize = fileIS.read(chunkBuffer)) != -1) {
				var imageContent = ContentChunk.newBuilder().setSize(chunkSize).setContent(ByteString.copyFrom(chunkBuffer, 0, chunkSize)).build();
				var request = ImageUploadRequest.newBuilder().setChunk(imageContent).build();
				imageUploadRSO.onNext(request);
				
				chunkCounter++;
			}
			imageUploadRSO.onCompleted();
			countDownLatch.await(5, TimeUnit.SECONDS);
			
			assertNotEquals(0, chunkCounter);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			try {
				fileIS.close();
			} catch (IOException ignored) {
			}
		}
	}

	@Test
	@DisplayName("Unit Test: Download a photo image (the happy path)")
	@Order(70)
	void testDownloadPhotoImage(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = StringValue.newBuilder().setValue(entityId).build();
			assertNotNull(request);

			var response = this.photoService.downloadImage(request);
			assertNotNull(response);
			
			final int[] entityCounter = {0};
			response.forEachRemaining(idr -> {
				entityCounter[0]++;
				
				if (idr.hasMetadata()) {
					var metadata = idr.getMetadata();
					assertNotNull(metadata.getId());
					assertNotNull(metadata.getName());
					assertNotNull(metadata.getDescription());
					assertNotNull(metadata.getType());
				} else {
					var chunk = idr.getChunk();
					assertTrue(chunk.getContent().isEmpty());
					assertEquals(0, chunk.getSize());
				}
			});
			assertEquals(2, entityCounter[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
