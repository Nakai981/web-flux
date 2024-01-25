package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    private final String MOVIE_INFO_URL = "/v1/movieinfos";

    @BeforeEach
    void setUp() {
        var movieInfos = List.of(
                new MovieInfo(null, "Nguyen Thanh Nhac", 2005, List.of("Year 2011"), LocalDate.parse("2005-06-12")),
                new MovieInfo(null, "Phan Ha Anh", 1997, List.of("Year 2011", "Case 01"), LocalDate.parse("2021-04-03")),
                new MovieInfo(null, "Phan Ha Anh", 1988, List.of("Year 2015"), LocalDate.parse("2015-07-02")),
                new MovieInfo("okoko", "Le Bao Binh", 1987, List.of("KAKA 01"), LocalDate.parse("2001-10-22"))
        );
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo(null, "Nguyen Thanh Nhac", 2005, List.of("Year 2011"), LocalDate.parse("2005-06-12"));
        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                });
    }

    @Test
    void findAllMovieInfo() {
        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).size() == 4;
                });
    }

    @Test
    void findByMovieInfoId() {
        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/okoko")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).getMovieInfoId() != null;
                });
    }

    @Test
    void deleteMovieInfoId() {
        webTestClient.delete()
                .uri(MOVIE_INFO_URL+"/okoko")
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    void putMovieInfoId() {
        var movieInfo = new MovieInfo(null, "Nguyen Thanh Nhac", 1999, List.of("Year 2011"), LocalDate.parse("2005-06-12"));

        webTestClient.put()
                .uri(MOVIE_INFO_URL+"/okoko")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).getYear() == 1999;
                });
    }

    @Test
    void findByMovieInfoId_NotFound() {

        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/9")
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void findByMovieInfoId_NotFound_Same() {

        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/9")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
        ;

    }

    @Test
    void findAllMovieInfo_Name() {

        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                        .queryParam("name", "Phan Ha Anh")
                                .buildAndExpand().toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

}