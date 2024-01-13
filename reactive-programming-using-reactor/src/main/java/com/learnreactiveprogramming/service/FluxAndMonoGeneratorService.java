package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux
                .fromIterable(List.of("Phan", "Huy", "Hoang"))
                .log()
                ;
    }
    public Mono<String> nameMono(){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .log()
                ;
    }

    public Mono<List<String>> nameMonoFlatMap(){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMap(this::splitStringMono)
                .log()
                ;
    }

    public Flux<String> nameMonoFlatMapMany(){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMapMany(this::splitStringDelay)
                .log()
                ;
    }


    public Flux<String> namesFluxTransform(int stringLength) {

       UnaryOperator<Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux
                .fromIterable(List.of("Phan", "Huy", "Hoang"))
                .transform(filterMap)
                .flatMap(this::splitStringDelay)
                .log()
                ;
    }



    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        var randomTime =   new Random().nextInt(1000);
        return Mono.just(charList).delayElement(Duration.ofMillis(randomTime));
    }


    public Flux<String> namesFluxFlatMap(int stringLength) {
        return Flux
                .fromIterable(List.of("Phan", "Huy", "Hoang"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitString)
                .log()
                ;
    }

    public Flux<String> namesFluxFlatMapAndDelay(int stringLength) {
        return Flux
                .fromIterable(List.of("Phan", "Huy", "Hoang"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitStringDelay)
                .log()
                ;
    }

    public Flux<String> namesFluxConcatMapAndDelay(int stringLength) {
        return Flux
                .fromIterable(List.of("Phan", "Huy", "Hoang"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .concatMap(this::splitStringDelay)
                .log()
                ;
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitStringDelay(String name){
        var charArray = name.split("");
        var randomTime =  900;//new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(randomTime))
                ;
    }


    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService
                .namesFlux()
                .subscribe(name -> System.out.println("Name is: "+ name));
        fluxAndMonoGeneratorService
                .nameMono()
                .subscribe(name -> System.out.println("Name is " + name));

    }
}
