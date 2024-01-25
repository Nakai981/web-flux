package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    @Autowired MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setup(){
        var movieInfos = List.of(
                new MovieInfo(null, "Nguyen Thanh Nhac", 2005, List.of("Year 2011"), LocalDate.parse("2005-06-12")),
                new MovieInfo(null, "Phan Ha Anh", 1997, List.of("Year 2011", "Case 01"), LocalDate.parse("2021-04-03")),
                new MovieInfo(null, "Phan Ha Anh", 1988, List.of("Year 2015"), LocalDate.parse("2015-07-02")),
                new MovieInfo("okoko", "Le Bao Binh", 1987, List.of("KAKA 01"), LocalDate.parse("2001-10-22"))
        );
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown(){
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll(){

        var moviesFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesFlux)
                .expectNextCount(4)
                .verifyComplete();
    }
    @Test
    void findById(){
        var moviesMono = movieInfoRepository.findById("okoko");

        StepVerifier.create(moviesMono)
//                .expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertEquals("Le Bao Binh", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo(){
        var movieInfo =  new MovieInfo(null, "Nguyen Thanh Nhac", 2005, List.of("Year 2011"), LocalDate.parse("2005-06-12"));

        var movieInfoSave = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoSave)
//                .expectNextCount(1)
                .assertNext(movieInfo1 -> {
                    assertEquals("Nguyen Thanh Nhac", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo(){


        var moviesMono = movieInfoRepository.findById("okoko").block();
        moviesMono.setYear(2022);

        var movieInfoSave = movieInfoRepository.save(moviesMono).log();


        StepVerifier.create(movieInfoSave)
//                .expectNextCount(1)
                .assertNext(movieInfo1 -> {
                    assertEquals(2022, movieInfo1.getYear());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo(){

        movieInfoRepository.deleteById("okoko").block();

        var movieInfos = movieInfoRepository.findAll().log();


        StepVerifier.create(movieInfos)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findByName(){
        var moviesMono = movieInfoRepository.findByName("Phan Ha Anh");

        StepVerifier.create(moviesMono)
                .expectNextCount(2)
                .verifyComplete();
    }
}