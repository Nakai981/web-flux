package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){
        return route()
                .nest(path("/v1/reviews"), builder -> {
                    builder
                    .POST("", reviewHandler::addReview)
                    .GET("", reviewHandler::findAll);
                })
                .GET("/v1/hello", (request -> ServerResponse.ok().bodyValue("hello")))
//                .POST("/v1/reviews", reviewHandler::addReview)
//                .GET("/v1/reviews", reviewHandler::findAll)
                .build();
    }

}
