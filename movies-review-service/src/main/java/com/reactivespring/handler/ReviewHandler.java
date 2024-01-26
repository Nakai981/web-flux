package com.reactivespring.handler;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewHandler {

    @Autowired private Validator validator;
    private final ReviewMongoRepository reviewMongoRepository;
    public Mono<ServerResponse> addReview(ServerRequest request) {
       return  request.bodyToMono(Review.class)
               .doOnNext(this::validate)
                .flatMap(reviewMongoRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var  constraintViolations = validator.validate(review);
        log.info("constraintViolations: {}", constraintViolations);
        if (!constraintViolations.isEmpty()){
           var errorMessage =   constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new ReviewDataException(errorMessage);
        }


    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        var movieInfoId = request.queryParam("movieInfoId");
        return movieInfoId.map(s -> ServerResponse.ok().body(reviewMongoRepository.findAllByMovieInfoId(Long.parseLong(s)), Review.class)).orElseGet(() -> ServerResponse.ok().body(reviewMongoRepository.findAll(), Review.class));
    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {

        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewMongoRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given review id")))
                ;

        return existingReview
                .flatMap(review -> serverRequest.bodyToMono(Review.class)
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(reviewMongoRepository::save)
                .flatMap(ServerResponse.ok()::bodyValue));
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {

        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewMongoRepository.findById(reviewId);

        return existingReview
                        .flatMap(reviewMongoRepository::delete)
                        .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> findByReviewId(ServerRequest serverRequest) {

        var reviewId = serverRequest.pathVariable("id");
        var existingReview = reviewMongoRepository.findById(reviewId);

        return existingReview
                        .flatMap(ServerResponse.ok()::bodyValue);
    }


}
