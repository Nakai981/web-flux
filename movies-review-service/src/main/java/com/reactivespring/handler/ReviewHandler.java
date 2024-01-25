package com.reactivespring.handler;
import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReviewHandler {

    private final ReviewMongoRepository reviewMongoRepository;
    public Mono<ServerResponse> addReview(ServerRequest request) {
       return  request.bodyToMono(Review.class)
                .flatMap(reviewMongoRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        var reviewFlux = reviewMongoRepository.findAll();
        return ServerResponse.ok().body(reviewFlux, Review.class);
    }
}
