package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private MovieInfoServices movieInfoServiceMock;

    private final String MOVIE_INFO_URL = "/v1/movieinfos";


    @Test
    void findAllMovieInfo() {

        var movieInfos = List.of(
                new MovieInfo(null, "Nguyen Thanh Nhac", 2005, List.of("Year 2011"), LocalDate.parse("2005-06-12")),
                new MovieInfo(null, "Phan Ha Anh", 1997, List.of("Year 2011", "Case 01"), LocalDate.parse("2021-04-03")),
                new MovieInfo(null, "Ly Hao Nhien", 1988, List.of("Year 2015"), LocalDate.parse("2015-07-02")),
                new MovieInfo("okoko", "Le Bao Binh", 1987, List.of("KAKA 01"), LocalDate.parse("2001-10-22"))
        );

        /*
        * mock data fake
        * */
        when(movieInfoServiceMock.findAll()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(4);
    }



    @Test
    void findByMovieInfoId() {
        var movieInfo =  new MovieInfo("okoko", "Le Bao Binh", 1987, List.of("KAKA 01"), LocalDate.parse("2001-10-22"));

        when(movieInfoServiceMock.findById(movieInfo.getMovieInfoId()))
                .thenReturn(Mono.just(movieInfo));


        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/okoko")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedMovieInfo).getYear() == 1987;
                });
    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo("okeoke", "Nguyen Thanh Nhac", 2005, List.of("Year 2011"), LocalDate.parse("2005-06-12"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(Mono.just(movieInfo));

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
                });
    }
    @Test
    void deleteMovieInfoId() {

        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(MOVIE_INFO_URL+"/okoko")
                .exchange()
                .expectStatus()
                .isOk();

    }


    @Test
    void addMovieInfo_Validate() {

        var movieInfo = new MovieInfo("okeoke", "Helo", -2, List.of("Year 2011"), LocalDate.parse("2005-06-12"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(Mono.just(movieInfo));

        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }


}
