package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux(){
        //give

        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFlux();

        //then

        StepVerifier.create(nameFlux)
//                .expectNext("Phan", "Huy", "Hoang")
                .expectNextCount(3)
                .verifyComplete();


    }

    @Test
    void namesFluxFlatMap() {
        //give
        int stringLength = 3;
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength);

        //then

        StepVerifier.create(nameFlux)
                .expectNext("P", "H", "A", "N", "H", "O", "A", "N", "G")
//                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void namesFluxFlatMapAndDelay() {
        //give
        int stringLength = 3;
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAndDelay(stringLength);

        //then

        StepVerifier.create(nameFlux)
//                .expectNext("P", "H", "A", "N", "H", "O", "A", "N", "G")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMapAndDelay() {
        //give
        int stringLength = 3;
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxConcatMapAndDelay(stringLength);

        //then

        StepVerifier.create(nameFlux)
//                .expectNext("P", "H", "A", "N", "H", "O", "A", "N", "G")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void nameMonoFlatMap() {
        var nameMono = fluxAndMonoGeneratorService.nameMonoFlatMap();

        //then

        StepVerifier.create(nameMono)
                .expectNext(List.of("A", "L", "E", "X"))
//                .expectNextCount(4)
                .verifyComplete();

    }

    @Test
    void nameMonoFlatMapMany() {
        //then
        var nameMono = fluxAndMonoGeneratorService.nameMonoFlatMapMany();
        StepVerifier.create(nameMono)
                .expectNext("A", "L", "E", "X")
//                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {  //give
        int stringLength = 3;
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then

        StepVerifier.create(nameFlux)
//                .expectNext("P", "H", "A", "N", "H", "O", "A", "N", "G")
                .expectNextCount(9)
                .verifyComplete();

    }

    @Test
    void namesFluxTransformDefault() {  //give
        int stringLength = 10;
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        //then

        StepVerifier.create(nameFlux)
//                .expectNext("P", "H", "A", "N", "H", "O", "A", "N", "G")
//                .expectNextCount(9)
                .expectNext("default")
                .verifyComplete();

    }

    @Test
    void testNamesFluxTransformDefault() {
        int stringLength = 5;
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxTransformDefault(stringLength);

        //then

        StepVerifier.create(nameFlux)
//                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void namesFluxConcat() {
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxConcat() ;

        //then

        StepVerifier.create(nameFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void namesFluxMerge() {
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesFluxMerge() ;

        //then

        StepVerifier.create(nameFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void namesMonoMerge() {
        //when

        var nameFlux = fluxAndMonoGeneratorService.namesMonoMerge() ;

        //then

        StepVerifier.create(nameFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFluxZip() {
        var nameFlux = fluxAndMonoGeneratorService.namesFluxZip() ;

        //then

        StepVerifier.create(nameFlux)
                .expectNextCount(2)
                .verifyComplete();
    }
}