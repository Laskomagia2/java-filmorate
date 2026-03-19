package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationUserControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;
	private final String baseUrl = "/users";

	@Test
	void shouldAddUserAndGetUsers() {
		User testUser = new User();
		testUser.setName("testName");
		testUser.setLogin("testLogin");
		testUser.setEmail("test@Mail");
		testUser.setBirthday(LocalDate.of(2000, 11, 1));

		ResponseEntity<User> postEntity = restTemplate.postForEntity(baseUrl, testUser, User.class);
		assertEquals(200, postEntity.getStatusCodeValue());
		User completeUser = postEntity.getBody();
		assertNotNull(completeUser);
		assertEquals("testLogin", completeUser.getLogin());
		assertEquals("testName", completeUser.getName());
		assertNotNull(completeUser.getId());

		ResponseEntity<User[]> getEntity = restTemplate.getForEntity(baseUrl, User[].class);
		assertEquals(200, getEntity.getStatusCodeValue());
		User[] users = getEntity.getBody();
		assertNotNull(users);
		assertTrue(users.length > 0);
		assertEquals(completeUser.getId(), users[0].getId());
	}

	@Test
	void shouldPutUser() {
		User testUser1 = new User();
		testUser1.setName("testName");
		testUser1.setLogin("testLogin");
		testUser1.setEmail("test@Mail");
		testUser1.setBirthday(LocalDate.of(2000, 11, 1));

		User testUser2 = new User();
		testUser2.setName("testName");
		testUser2.setLogin("testLogin");
		testUser2.setEmail("test@Mail");
		testUser2.setBirthday(LocalDate.of(2000, 11, 1));

		ResponseEntity<User> postEntity = restTemplate.postForEntity(baseUrl, testUser1, User.class);
		ResponseEntity<User> postEntity2 = restTemplate.postForEntity(baseUrl, testUser2, User.class);

		User testUser2ch = new User();
		testUser2ch.setId(postEntity2.getBody().getId());
		testUser2ch.setName("testName");
		testUser2ch.setLogin("testLogin");
		testUser2ch.setEmail("test@Mail");
		testUser2ch.setBirthday(LocalDate.of(2000, 11, 1));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<User> request = new HttpEntity<>(testUser2ch, headers);

		ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, request, String.class);

		ResponseEntity<User[]> getEntity = restTemplate.getForEntity(baseUrl, User[].class);
		assertEquals(200, getEntity.getStatusCodeValue());
		User[] users = getEntity.getBody();
		assertNotNull(users);
		assertTrue(users.length > 0);
		assertEquals(testUser2ch, users[1]);
	}

	@Test
	void shouldReturnBadRequestWhenEmailAndDateInvalid() {
		User user = new User();
		user.setEmail("invalidEmail"); // без "@"
		user.setLogin("login");
		user.setName("Name");
		user.setBirthday(LocalDate.of(2027, 1, 1));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<User> request = new HttpEntity<>(user, headers);

		ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);

		assertEquals(500, response.getStatusCodeValue());
	}

}
