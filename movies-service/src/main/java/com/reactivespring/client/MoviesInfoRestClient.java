package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class MoviesInfoRestClient {

    private final WebClient webClient;

    @Value("${restClient.movieInfoUrl}")
    private String moviesInfoUrl;

    public Mono<MovieInfo> retrieveMovieInfo(String movieId){
        var url = moviesInfoUrl.concat("/{id}");
        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return Mono.error(new MoviesInfoClientException(
                            "There is no MovieInfo Avaiable for the passed in Id: " + movieId
                        , clientResponse.statusCode().value())
                        );
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new MoviesInfoClientException(
                                    s, clientResponse.statusCode().value()
                            )));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                     clientResponse.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new MoviesInfoClientException(
                                    s, clientResponse.statusCode().value()
                            )))
                )
                .bodyToMono(MovieInfo.class)
                .log();
    }

}
