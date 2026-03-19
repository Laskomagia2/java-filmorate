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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationFilmControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl = "/films";

    @Order(1)
    @Test
    void shouldAddFilmAndGetFilm() {
        Film testFilm = new Film();
        testFilm.setName("testName");
        testFilm.setDescription("testDescription");
        testFilm.setReleaseDate(LocalDate.of(2000, 11, 1));
        testFilm.setDuration(2);

        ResponseEntity<Film> postEntity = restTemplate.postForEntity(baseUrl, testFilm, Film.class);
        assertEquals(200, postEntity.getStatusCodeValue());
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
    void shouldPutUser() {
        Film testFilm1 = new Film();
        testFilm1.setName("testName1");
        testFilm1.setDescription("testDescription1");
        testFilm1.setReleaseDate(LocalDate.of(2000, 11, 1));
        testFilm1.setDuration(2);

        Film testFilm2 = new Film();
        testFilm2.setName("testName2");
        testFilm2.setDescription("testDescription2");
        testFilm2.setReleaseDate(LocalDate.of(2000, 10, 1));
        testFilm2.setDuration(3);

        ResponseEntity<Film> postEntity = restTemplate.postForEntity(baseUrl, testFilm1, Film.class);
        ResponseEntity<Film> postEntity2 = restTemplate.postForEntity(baseUrl, testFilm2, Film.class);

        Film testFilm2ch = new Film();
        testFilm2ch.setId(postEntity2.getBody().getId());
        testFilm2ch.setName("testName4");
        testFilm2ch.setDescription("testDescription4");
        testFilm2ch.setReleaseDate(LocalDate.of(2010, 9, 1));
        testFilm2ch.setDuration(6);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Film> request = new HttpEntity<>(testFilm2ch, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, request, String.class);

        ResponseEntity<Film[]> getEntity = restTemplate.getForEntity(baseUrl, Film[].class);
        assertEquals(200, getEntity.getStatusCodeValue());
        Film[] films = getEntity.getBody();
        assertNotNull(films);
        assertTrue(films.length > 0);
        assertEquals(testFilm2ch, films[1]);
    }

    @Order(3)
    @Test
    void shouldReturnBadRequestWhenDurationAndDateInvalid() {
        Film testFilm = new Film();
        testFilm.setName("testName");
        testFilm.setDescription("testDescription");
        testFilm.setReleaseDate(LocalDate.of(1800, 11, 1));
        testFilm.setDuration(0);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Film> request = new HttpEntity<>(testFilm, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);

        assertEquals(500, response.getStatusCodeValue());
    }

}
