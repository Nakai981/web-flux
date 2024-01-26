package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewMongoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    static String REVIEWS_URL = "/v1/reviews";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewMongoRepository reviewMongoRepository;

    @BeforeEach
    void setUp(){
        var reviewList = List.of(
                new Review(null, 1L, "Doraemon Movie 1", 9.0),
                new Review(null, 1L, "Doraemon Movie 2", 2.5),
                new Review("hello", 2L, "KOKO Movie", 10.0)
        );
        reviewMongoRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown(){
        reviewMongoRepository.deleteAll().block();
    }

    @Test
    void addReview(){
        var review = new Review(null, 1L, "Doraemon Movie 3", 9.0);

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
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    void findAllReview(){

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
        var review = new Review("hello", 1L, "Doraemon Movie 5", 9.0);
        var reviewId = "hello";
        webTestClient
                .put()
                .uri(REVIEWS_URL + "/" + reviewId)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assert updatedReview != null;
                    assert updatedReview.getRating() == 9.0;
                });
    }

    @Test
    void deleteReview(){
        var reviewId = "hello";
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/" + reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void findAllReview_MovieInfo(){
        var uri = UriComponentsBuilder.fromUriString(REVIEWS_URL)
                .queryParam("movieInfoId", 1)
                .buildAndExpand().toUri();
        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }
}
