/**
 * 
 */
package com.inditex.selectionprocess.test.grpc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import com.inditex.selectionprocess.grpc.protobuf.Album;
import com.inditex.selectionprocess.grpc.protobuf.AlbumServiceGrpc.AlbumServiceBlockingStub;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
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
public class AlbumServiceUnitTests {
	@GrpcClient("inProcess")
	private AlbumServiceBlockingStub albumService;
	
	private static String existentAlbumId;

	@Test
	@DisplayName("Unit Test: Check the JPA injection capabilities)")
	@Order(1)
	void testJPAInjection(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		assertNotNull(this.albumService);
	}

	@Test
	@DisplayName("Unit Test: Album creation (the happy path)")
	@Order(10)
	void testAlbumCreation(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Album.newBuilder().setId(entityId).setName("This is the name of " + entityId)
					.setDescription("This is the description of " + entityId).build();
			assertNotNull(request);

			var response = this.albumService.create(request);
			assertNotNull(response);
			assertNotEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			
			existentAlbumId = response.getId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Album creation (wrong parameters)")
	@Order(11)
	void testAlbumCreation001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Album.newBuilder().setId(entityId)
					//.setName("This is the name of " + entityId)
					.setDescription("This is the description of " + entityId).build();
			assertNotNull(request);

			var exception = assertThrows(StatusRuntimeException.class, () -> {this.albumService.create(request);});
			assertEquals(exception.getStatus().getCode(), Status.INVALID_ARGUMENT.getCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Album update (the happy path)")
	@Order(20)
	void testAlbumUpdate(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = existentAlbumId;
			var request = Album.newBuilder().setId(entityId).setName("This is the new name of " + entityId)
					.setDescription("This is the new description of " + entityId).build();
			assertNotNull(request);

			var response = this.albumService.update(request);
			assertNotNull(response);
			assertEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Unit Test: Album update (wrong parameters)")
	@Order(21)
	void testAlbumUpdate001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = existentAlbumId;
			var request = Album.newBuilder()
					//.setId(entityId)
					.setName("This is the new name of " + entityId)
					.setDescription("This is the new description of " + entityId).build();
			assertNotNull(request);

			var exception = assertThrows(StatusRuntimeException.class, () -> {this.albumService.update(request);});
			assertEquals(exception.getStatus().getCode(), Status.INVALID_ARGUMENT.getCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Album update (wrong entity)")
	@Order(22)
	void testAlbumUpdate002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = Album.newBuilder()
					.setId(entityId)
					.setName("This is the new name of " + entityId)
					.setDescription("This is the new description of " + entityId).build();
			assertNotNull(request);

			var exception = assertThrows(StatusRuntimeException.class, () -> {this.albumService.update(request);});
			assertEquals(exception.getStatus().getCode(), Status.NOT_FOUND.getCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Album deletion (the happy path)")
	@Order(30)
	void testAlbumDelete(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = StringValue.newBuilder().setValue(entityId).build();
			assertNotNull(request);

			var response = this.albumService.delete(request);
			assertNotNull(response);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Album deletion (wrong parameters)")
	@Order(31)
	void testAlbumDelete001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var request = StringValue.newBuilder().build();
			assertNotNull(request);

			var exception = assertThrows(StatusRuntimeException.class, () -> {this.albumService.delete(request);});
			assertEquals(exception.getStatus().getCode(), Status.INVALID_ARGUMENT.getCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Unit Test: Album finding by primary key (the happy path)")
	@Order(40)
	void testAlbumFindById(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = existentAlbumId;
			var request = StringValue.newBuilder().setValue(entityId).build();
			assertNotNull(request);

			var response = this.albumService.findById(request);
			assertNotNull(response);
			assertNotNull(response.getId());
			assertNotNull(response.getName());
			assertNotNull(response.getDescription());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Unit Test: Album finding by primary key (wrong parameters)")
	@Order(41)
	void testAlbumFindById001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var request = StringValue.newBuilder().build();
			assertNotNull(request);

			var exception = assertThrows(StatusRuntimeException.class, () -> {this.albumService.findById(request);});
			assertEquals(exception.getStatus().getCode(), Status.INVALID_ARGUMENT.getCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Unit Test: Album finding by primary key (not found)")
	@Order(42)
	void testAlbumFindById002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var entityId = UUID.randomUUID().toString();
			var request = StringValue.newBuilder().setValue(entityId).build();
			assertNotNull(request);

			var exception = assertThrows(StatusRuntimeException.class, () -> {this.albumService.findById(request);});
			assertEquals(exception.getStatus().getCode(), Status.NOT_FOUND.getCode());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Unit Test: Finding whole Albums (the happy path)")
	@Order(50)
	void testAlbumFindAll(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		
		try {
			var request = Empty.newBuilder().build();
			assertNotNull(request);

			var response = this.albumService.findAll(request);
			assertNotNull(response);
			
			final int[] entityCounter = {0};
			response.forEachRemaining(entity -> {
				entityCounter[0]++;
				
				assertNotNull(entity.getId());
				assertNotNull(entity.getName());
				assertNotNull(entity.getDescription());
			});
			assertNotEquals(0, entityCounter[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
