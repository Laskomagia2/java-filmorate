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
		User testUser = User.builder()
				.name("testName")
				.login("testLogin")
				.email("test@Mail")
				.birthday(LocalDate.of(2000, 11, 1))
				.build();

		ResponseEntity<User> postEntity = restTemplate.postForEntity(baseUrl, testUser, User.class);
		assertEquals(201, postEntity.getStatusCodeValue());
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
		User testUser1 = User.builder()
				.name("testName")
				.login("testLogin")
				.email("test@Mail")
				.birthday(LocalDate.of(2000, 11, 1))
				.build();

		User testUser2 = User.builder()
				.name("testName2")
				.login("testLogin2")
				.email("test@Mail2")
				.birthday(LocalDate.of(2000, 11, 2))
				.build();

		ResponseEntity<User> postEntity = restTemplate.postForEntity(baseUrl, testUser1, User.class);
		ResponseEntity<User> postEntity2 = restTemplate.postForEntity(baseUrl, testUser2, User.class);

		User testUser2ch = User.builder()
				.id(2)
				.name("testName")
				.login("testLogin")
				.email("test@Mail2")
				.birthday(LocalDate.of(2000, 11, 1))
				.build();

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
		User user = User.builder()
				.name("testName")
				.login("testLogin")
				.email("invalidEmail")
				.birthday(LocalDate.of(2027, 11, 1))
				.build();

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<User> request = new HttpEntity<>(user, headers);

		ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);

		assertEquals(400, response.getStatusCodeValue());
	}

	@Test
	void shouldAddFriendAndListFriends() {
		User userOne = User.builder()
				.name("friendA")
				.login("loginFriendA")
				.email("friendA@mail.ru")
				.birthday(LocalDate.of(1991, 2, 2))
				.build();
		User userTwo = User.builder()
				.name("friendB")
				.login("loginFriendB")
				.email("friendB@mail.ru")
				.birthday(LocalDate.of(1992, 3, 3))
				.build();

		ResponseEntity<User> post1 = restTemplate.postForEntity(baseUrl, userOne, User.class);
		ResponseEntity<User> post2 = restTemplate.postForEntity(baseUrl, userTwo, User.class);
		assertEquals(201, post1.getStatusCodeValue());
		assertEquals(201, post2.getStatusCodeValue());
		User created1 = post1.getBody();
		User created2 = post2.getBody();
		assertNotNull(created1);
		assertNotNull(created2);

		ResponseEntity<Void> putFriend = restTemplate.exchange(
				baseUrl + "/" + created1.getId() + "/friends/" + created2.getId(),
				HttpMethod.PUT, null, Void.class);
		assertEquals(200, putFriend.getStatusCodeValue());

		ResponseEntity<User[]> friendsOfFirst = restTemplate.getForEntity(
				baseUrl + "/" + created1.getId() + "/friends", User[].class);
		assertEquals(200, friendsOfFirst.getStatusCodeValue());
		User[] list1 = friendsOfFirst.getBody();
		assertNotNull(list1);
		assertEquals(1, list1.length);
		assertEquals(created2.getId(), list1[0].getId());

		ResponseEntity<User[]> friendsOfSecond = restTemplate.getForEntity(
				baseUrl + "/" + created2.getId() + "/friends", User[].class);
		assertEquals(200, friendsOfSecond.getStatusCodeValue());
		User[] list2 = friendsOfSecond.getBody();
		assertNotNull(list2);
		assertEquals(1, list2.length);
		assertEquals(created1.getId(), list2[0].getId());
	}

	@Test
	void shouldRemoveFriend() {
		User userOne = User.builder()
				.name("rmA")
				.login("loginRmA")
				.email("rmA@mail.ru")
				.birthday(LocalDate.of(1988, 1, 1))
				.build();
		User userTwo = User.builder()
				.name("rmB")
				.login("loginRmB")
				.email("rmB@mail.ru")
				.birthday(LocalDate.of(1989, 2, 2))
				.build();

		User created1 = restTemplate.postForEntity(baseUrl, userOne, User.class).getBody();
		User created2 = restTemplate.postForEntity(baseUrl, userTwo, User.class).getBody();
		assertNotNull(created1);
		assertNotNull(created2);

		restTemplate.exchange(
				baseUrl + "/" + created1.getId() + "/friends/" + created2.getId(),
				HttpMethod.PUT, null, Void.class);
		restTemplate.exchange(
				baseUrl + "/" + created1.getId() + "/friends/" + created2.getId(),
				HttpMethod.DELETE, null, Void.class);

		ResponseEntity<User[]> friends = restTemplate.getForEntity(
				baseUrl + "/" + created1.getId() + "/friends", User[].class);
		assertEquals(200, friends.getStatusCodeValue());
		assertNotNull(friends.getBody());
		assertEquals(0, friends.getBody().length);
	}

	@Test
	void shouldReturnMutualFriends() {
		User u1 = User.builder()
				.name("mut1")
				.login("loginMut1")
				.email("mut1@mail.ru")
				.birthday(LocalDate.of(1995, 1, 1))
				.build();
		User u2 = User.builder()
				.name("mut2")
				.login("loginMut2")
				.email("mut2@mail.ru")
				.birthday(LocalDate.of(1995, 2, 2))
				.build();
		User u3 = User.builder()
				.name("mut3")
				.login("loginMut3")
				.email("mut3@mail.ru")
				.birthday(LocalDate.of(1995, 3, 3))
				.build();

		Integer id1 = restTemplate.postForEntity(baseUrl, u1, User.class).getBody().getId();
		Integer id2 = restTemplate.postForEntity(baseUrl, u2, User.class).getBody().getId();
		Integer id3 = restTemplate.postForEntity(baseUrl, u3, User.class).getBody().getId();

		restTemplate.exchange(baseUrl + "/" + id1 + "/friends/" + id2, HttpMethod.PUT, null, Void.class);
		restTemplate.exchange(baseUrl + "/" + id1 + "/friends/" + id3, HttpMethod.PUT, null, Void.class);
		restTemplate.exchange(baseUrl + "/" + id2 + "/friends/" + id3, HttpMethod.PUT, null, Void.class);

		ResponseEntity<User[]> mutual = restTemplate.getForEntity(
				baseUrl + "/" + id1 + "/friends/common/" + id2, User[].class);
		assertEquals(200, mutual.getStatusCodeValue());
		User[] common = mutual.getBody();
		assertNotNull(common);
		assertEquals(1, common.length);
		assertEquals(id3, common[0].getId());
	}

}
