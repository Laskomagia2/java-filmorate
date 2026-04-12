package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationFilmControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl = "/films";
    private final String usersUrl = "/users";

    @Order(1)
    @Test
    void shouldAddFilmAndGetFilm() {
        Film testFilm = Film.builder()
                .name("testName")
                .description("testDescription")
                .releaseDate(LocalDate.of(2000, 11, 1))
                .duration(2)
                .build();

        ResponseEntity<Film> postEntity = restTemplate.postForEntity(baseUrl, testFilm, Film.class);
        assertEquals(201, postEntity.getStatusCodeValue());
        Film completeFilm = postEntity.getBody();
        assertNotNull(completeFilm);
        assertEquals("testDescription", completeFilm.getDescription());
        assertEquals("testName", completeFilm.getName());
        assertNotNull(completeFilm.getId());

        ResponseEntity<Film[]> getEntity = restTemplate.getForEntity(baseUrl, Film[].class);
        assertEquals(200, getEntity.getStatusCodeValue());
        Film[] films = getEntity.getBody();
        assertNotNull(films);
        assertTrue(films.length > 0);
        assertEquals(completeFilm.getId(), films[0].getId());
    }

    @Order(2)
    @Test
    void shouldReturnBadRequestWhenDurationAndDateInvalid() {
        Film testFilm = Film.builder()
                .name("testName1")
                .description("testDescription")
                .releaseDate(LocalDate.of(1800, 11, 1))
                .duration(0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Film> request = new HttpEntity<>(testFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void shouldOrderPopularFilmsByLikesCount() {
        User userOne = User.builder()
                .name("likeUser1")
                .login("likeLogin1")
                .email("like1@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User userTwo = User.builder()
                .name("likeUser2")
                .login("likeLogin2")
                .email("like2@mail.ru")
                .birthday(LocalDate.of(1990, 2, 2))
                .build();
        Integer userId1 = restTemplate.postForEntity(usersUrl, userOne, User.class).getBody().getId();
        Integer userId2 = restTemplate.postForEntity(usersUrl, userTwo, User.class).getBody().getId();

        Film filmLessLikes = Film.builder()
                .name("filmLess")
                .description("d1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .build();
        Film filmMoreLikes = Film.builder()
                .name("filmMore")
                .description("d2")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        Integer filmId1 = restTemplate.postForEntity(baseUrl, filmLessLikes, Film.class).getBody().getId();
        Integer filmId2 = restTemplate.postForEntity(baseUrl, filmMoreLikes, Film.class).getBody().getId();

        restTemplate.exchange(baseUrl + "/" + filmId1 + "/like/" + userId1, HttpMethod.PUT, null, Void.class);
        restTemplate.exchange(baseUrl + "/" + filmId2 + "/like/" + userId1, HttpMethod.PUT, null, Void.class);
        restTemplate.exchange(baseUrl + "/" + filmId2 + "/like/" + userId2, HttpMethod.PUT, null, Void.class);

        ResponseEntity<Film[]> popular = restTemplate.getForEntity(baseUrl + "/popular", Film[].class);
        assertEquals(200, popular.getStatusCodeValue());
        Film[] popularFilms = popular.getBody();
        assertNotNull(popularFilms);
        assertEquals(2, popularFilms.length);
        assertEquals(filmId2, popularFilms[0].getId());
        assertEquals(filmId1, popularFilms[1].getId());
    }
}
