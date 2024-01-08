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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.selectionprocess.agent.rest.AlbumRepresentationModel;

import jakarta.servlet.ServletException;

/**
 * 
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class AlbumsAgentIntegrationTests {
	static final private String ALBUMS_AGENT_URL = "/albums";
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private static String existentAlbumId;

	@Test
	@DisplayName("Integration Test: Check the injection capabilities")
	@Order(1)
	void testJPAInjection(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		assertNotNull(this.mockMvc);
		assertNotNull(this.objectMapper);
	}

	@Test
	@DisplayName("Integration Test: Album creation (the happy path)")
	@Order(10)
	void testAlbumCreation(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			var request = new AlbumRepresentationModel();
			request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setPhotos(new LinkedList<String>());

			ResultActions result = mockMvc.perform(post(ALBUMS_AGENT_URL).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request)));
			assertNotNull(result);

			var responseBody = result.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andReturn().getResponse().getContentAsString();
			assertNotNull(responseBody);

			var response = this.objectMapper.readValue(responseBody, AlbumRepresentationModel.class);
			assertNotEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());

			existentAlbumId = response.getId();
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Integration Test: Album creation (wrong parameters)")
	@Order(11)
	void testAlbumCreation001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var exception = assertThrows(ServletException.class, () -> {
				mockMvc.perform(post(ALBUMS_AGENT_URL).contentType(MediaType.APPLICATION_JSON).content("{}"));
			});
			assertEquals(exception.getCause().getClass(), NullPointerException.class);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album update (the happy path)")
	@Order(20)
	void testAlbumUpdate(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = existentAlbumId;
			var request = new AlbumRepresentationModel();
			//request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setPhotos(new LinkedList<String>());

			ResultActions result = mockMvc.perform(put(ALBUMS_AGENT_URL + "/{id}", entityId).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request)));
			assertNotNull(result);

			var responseBody = result.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andReturn().getResponse().getContentAsString();
			assertNotNull(responseBody);

			var response = this.objectMapper.readValue(responseBody, AlbumRepresentationModel.class);
			assertEquals(entityId, response.getId());
			assertEquals(request.getName(), response.getName());
			assertEquals(request.getDescription(), response.getDescription());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

	@Test
	@DisplayName("Integration Test: Album update (wrong parameters)")
	@Order(21)
	void testAlbumUpdate001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			mockMvc.perform(put(ALBUMS_AGENT_URL).contentType(MediaType.APPLICATION_JSON)
					.content("{}")).andExpect(status().isMethodNotAllowed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album update (wrong entity)")
	@Order(22)
	void testAlbumUpdate002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			var request = new AlbumRepresentationModel();
			request.setId(entityId);
			request.setName("This is the name of " + entityId);
			request.setDescription("This is the description of " + entityId);
			request.setPhotos(new LinkedList<String>());

			mockMvc.perform(put(ALBUMS_AGENT_URL + "/{id}", entityId).contentType(MediaType.APPLICATION_JSON)
					.content(this.objectMapper.writeValueAsString(request))).andExpect(status().isNotFound());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album deletion (the happy path)")
	@Order(30)
	void testAlbumDelete(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			mockMvc.perform(delete(ALBUMS_AGENT_URL + "/{id]", entityId)).andExpect(status().isNoContent());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album deletion (wrong parameters)")
	@Order(31)
	void testAlbumDelete001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			mockMvc.perform(delete(ALBUMS_AGENT_URL)).andExpect(status().isMethodNotAllowed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album finding by primary key (the happy path)")
	@Order(40)
	void testAlbumFindById(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = existentAlbumId;
			ResultActions result = mockMvc.perform(get(ALBUMS_AGENT_URL + "/{id}", entityId));
			assertNotNull(result);

			var responseBody = result.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andReturn().getResponse().getContentAsString();
			assertNotNull(responseBody);

			var response = this.objectMapper.readValue(responseBody, AlbumRepresentationModel.class);
			assertEquals(entityId, response.getId());
			assertNotNull(response.getName());
			assertNotNull(response.getDescription());
			assertNotNull(response.getPhotos());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album finding by primary key (wrong parameters)")
	@Order(41)
	void testAlbumFindById001(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			mockMvc.perform(get(ALBUMS_AGENT_URL + "/")).andExpect(status().isNotFound());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Album finding by primary key (not found)")
	@Order(42)
	void testAlbumFindById002(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			var entityId = UUID.randomUUID().toString();
			mockMvc.perform(get(ALBUMS_AGENT_URL + "/{id}", entityId)).andExpect(status().isNotFound());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Integration Test: Finding whole Albums (the happy path)")
	@Order(50)
	void testAlbumFindAll(TestInfo testInfo) {
		System.out.println("Executing test '" + testInfo.getDisplayName() + "' ...");

		try {
			ResultActions response = mockMvc.perform(get(ALBUMS_AGENT_URL));
			response.andExpect(status().isOk()).andExpect(content().contentType(MediaTypes.HAL_JSON))
					.andExpect(jsonPath("$._embedded.albumRepresentationModelList", hasSize(greaterThan(0))));
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}
}
