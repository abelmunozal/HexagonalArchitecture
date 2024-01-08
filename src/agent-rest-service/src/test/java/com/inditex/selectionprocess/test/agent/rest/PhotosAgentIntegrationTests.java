/**
 * 
 */
package com.inditex.selectionprocess.test.agent.rest;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.selectionprocess.agent.rest.PhotoRepresentationModel;

import jakarta.servlet.ServletException;

/**
 * 
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class PhotosAgentIntegrationTests {
	static final private String PHOTOS_AGENT_URL = "/photos";
	static final private String PHOTO_IMAGE_FILENAME = "image001.png";
	static final private String PHOTO_IMAGE_FILETYPE = MediaType.IMAGE_PNG_VALUE;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private static String existentPhotoId;

	@Test
	@DisplayName("Integration Test: Check the injection capabilities")
	@Order(1)
	void testJPAInjection(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		assertNotNull(this.mockMvc);
		assertNotNull(this.objectMapper);
	}

	@Test
	@DisplayName("Integration Test: Photo creation (the happy path)")
	@Order(10)
	void testPhotoCreation(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			var request = new PhotoRepresentationModel();
			request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setType(MediaType.IMAGE_PNG_VALUE);

			ResultActions result = mockMvc.perform(post(PHOTOS_AGENT_URL).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request)));
			assertNotNull(result);

			var responseBody = result.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andReturn().getResponse().getContentAsString();
			assertNotNull(responseBody);

			var response = this.objectMapper.readValue(responseBody, PhotoRepresentationModel.class);
			assertNotEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			assertEquals(request.getType(), response.getType());

			existentPhotoId = response.getId();
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Integration Test: Photo creation (wrong parameters)")
	@Order(11)
	void testPhotoCreation001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var exception = assertThrows(ServletException.class, () -> {
				mockMvc.perform(post(PHOTOS_AGENT_URL).contentType(MediaType.APPLICATION_JSON).content("{}"));
			});
			assertEquals(exception.getCause().getClass(), NullPointerException.class);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo creation (wrong MIME-TYPE)")
	@Order(12)
	void testPhotoCreation002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			var request = new PhotoRepresentationModel();
			request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setType("jpggg");

			mockMvc.perform(post(PHOTOS_AGENT_URL).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Integration Test: Photo update (the happy path)")
	@Order(20)
	void testPhotoUpdate(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = existentPhotoId;
			var request = new PhotoRepresentationModel();
			// request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setType(MediaType.IMAGE_PNG_VALUE);

			ResultActions result = mockMvc.perform(put(PHOTOS_AGENT_URL + "/{id}", entityId)
					.contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(request)));
			assertNotNull(result);

			var responseBody = result.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andReturn().getResponse().getContentAsString();
			assertNotNull(responseBody);

			var response = this.objectMapper.readValue(responseBody, PhotoRepresentationModel.class);
			assertEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
			assertEquals(request.getType(), response.getType());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Integration Test: Photo update (wrong parameters)")
	@Order(21)
	void testPhotoUpdate001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			mockMvc.perform(put(PHOTOS_AGENT_URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
					.andExpect(status().isMethodNotAllowed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo update (wrong entity)")
	@Order(22)
	void testPhotoUpdate002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			var request = new PhotoRepresentationModel();
			request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setType(MediaType.IMAGE_PNG_VALUE);

			mockMvc.perform(put(PHOTOS_AGENT_URL + "/{id}", entityId).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request))).andExpect(status().isNotFound());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo update (wrong MIME-TYPE)")
	@Order(23)
	void testPhotoUpdate003(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = existentPhotoId;
			var request = new PhotoRepresentationModel();
			request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setType("patata");

			mockMvc.perform(put(PHOTOS_AGENT_URL + "/{id}", entityId).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo deletion (the happy path)")
	@Order(30)
	void testPhotoDelete(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			mockMvc.perform(delete(PHOTOS_AGENT_URL + "/{id]", entityId)).andExpect(status().isNoContent());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo deletion (wrong parameters)")
	@Order(31)
	void testPhotoDelete001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			mockMvc.perform(delete(PHOTOS_AGENT_URL)).andExpect(status().isMethodNotAllowed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo finding by primary key (the happy path)")
	@Order(40)
	void testPhotoFindById(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = existentPhotoId;
			ResultActions result = mockMvc.perform(get(PHOTOS_AGENT_URL + "/{id}", entityId));
			assertNotNull(result);

			var responseBody = result.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andReturn().getResponse().getContentAsString();
			assertNotNull(responseBody);

			var response = this.objectMapper.readValue(responseBody, PhotoRepresentationModel.class);
			assertEquals(entityId, response.getId());
			assertNotNull(response.getName());
			assertNotNull(response.getDescription());
			assertNotNull(response.getType());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo finding by primary key (wrong parameters)")
	@Order(41)
	void testPhotoFindById001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			mockMvc.perform(get(PHOTOS_AGENT_URL + "/")).andExpect(status().isNotFound());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Photo finding by primary key (not found)")
	@Order(42)
	void testPhotoFindById002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			mockMvc.perform(get(PHOTOS_AGENT_URL + "/{id}", entityId)).andExpect(status().isNotFound());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Finding whole Photos (the happy path)")
	@Order(50)
	void testPhotoFindAll(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			ResultActions response = mockMvc.perform(get(PHOTOS_AGENT_URL));
			response.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andExpect(jsonPath("$._embedded.photoRepresentationModelList", hasSize(greaterThan(0))));
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Integration Test: Upload a photo image (the happy path)")
	@Order(60)
	void testUploadPhotoImage(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		InputStream fileIS = null;

		try {
			var entityId = existentPhotoId;
			fileIS = getClass().getClassLoader().getResourceAsStream(PHOTO_IMAGE_FILENAME);
			assertNotNull(fileIS);
			MockMultipartFile fileMultipart = new MockMultipartFile("file", PHOTO_IMAGE_FILENAME, PHOTO_IMAGE_FILETYPE,
					fileIS);
			mockMvc.perform(multipart(PHOTOS_AGENT_URL + "/{id}/upload", entityId).file(fileMultipart))
					.andExpect(status().isNoContent());
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
	@DisplayName("Integration Test: Upload a photo image (wrong photo)")
	@Order(61)
	void testUploadPhotoImage001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");
		InputStream fileIS = null;
		var entityId = UUID.randomUUID().toString();

		try {
			fileIS = getClass().getClassLoader().getResourceAsStream(PHOTO_IMAGE_FILENAME);
			assertNotNull(fileIS);
			MockMultipartFile fileMultipart = new MockMultipartFile("file", PHOTO_IMAGE_FILENAME, PHOTO_IMAGE_FILETYPE,
					fileIS);
			mockMvc.perform(multipart(PHOTOS_AGENT_URL + "/{id}/upload", entityId).file(fileMultipart))
					.andExpect(status().isNotFound());
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
	@DisplayName("Integration Test: Download a photo image (the happy path)")
	@Order(70)
	void testDownloadPhotoImage(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = existentPhotoId;
			mockMvc.perform(get(PHOTOS_AGENT_URL + "/{id}/download", entityId)).andExpect(status().isOk())
					.andExpect(content().contentType(PHOTO_IMAGE_FILETYPE));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
