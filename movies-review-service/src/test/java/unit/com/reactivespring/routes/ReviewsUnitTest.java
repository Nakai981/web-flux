package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.exceptionHandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewMongoRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;


@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {
    static String REVIEWS_URL = "/v1/reviews";

    @MockBean
    private ReviewMongoRepository reviewMongoRepository;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void addReview(){
        var review = new Review(null, 1L, "Doraemon Movie 3", 9.0);

        when(reviewMongoRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));


        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(savedReview).getRating() == 9.0;
                });
    }


    @Test
    void findAllReview(){

        var reviewList = List.of(
                new Review(null, 1L, "Doraemon Movie 1", 9.0),
                new Review(null, 1L, "Doraemon Movie 2", 2.5),
                new Review("hello", 2L, "KOKO Movie", 10.0)
        );

        when(reviewMongoRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewList));


        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void putReview(){

        /*
         *  new Review("hello", 2L, "KOKO Movie", 10.0)
         *  pattern
         * */
        var review = new Review("hello", 1L, "Doraemon Movie 5", 8.0);
        var reviewId = "hello";
        when(reviewMongoRepository.findById(isA(String.class))).thenReturn(Mono.just(review));
        when(reviewMongoRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));


        webTestClient
                .put()
                .uri(REVIEWS_URL + "/" + reviewId)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    void putReview_NOTDOUND(){

        /*
         *  new Review("hello", 2L, "KOKO Movie", 10.0)
         *  pattern
         * */
        var review = new Review("hello", 1L, "Doraemon Movie 5", 8.0);
        var reviewId = "hello";
        when(reviewMongoRepository.findById(isA(String.class))).thenReturn(Mono.just(review));
        when(reviewMongoRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));


        webTestClient
                .put()
                .uri(REVIEWS_URL + "/" + reviewId)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    void deleteReview(){
        var reviewId = "hello";
        var review = new Review("hello", 1L, "Doraemon Movie 5", 8.0);
        when(reviewMongoRepository.findById(isA(String.class))).thenReturn(Mono.just(review));
        when(reviewMongoRepository.delete(isA(Review.class))).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/" + reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void findAllReview_MovieInfo(){
        var reviewList = List.of(
                new Review(null, 1L, "Doraemon Movie 1", 9.0),
                new Review(null, 1L, "Doraemon Movie 2", 2.5)
                );
        var uri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
                .queryParam("movieInfoId", 1)
                .buildAndExpand().toUri();
        when(reviewMongoRepository.findAllByMovieInfoId(isA(Long.class)))
                .thenReturn(Flux.fromIterable(reviewList));
        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }


    @Test
    void addReview__validate(){
        var review = new Review(null, 1L, "Doraemon Movie 3", -9.0);

        when(reviewMongoRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));


        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }


}
