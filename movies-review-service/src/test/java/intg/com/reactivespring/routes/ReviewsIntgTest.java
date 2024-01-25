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
                new Review(null, 2L, "KOKO Movie", 10.0)
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
}
