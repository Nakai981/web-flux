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
                .defaultIfEmpty("default")
                .log()
                ;
    }

    public Flux<String> namesFluxTransformDefault(int stringLength) {

        UnaryOperator<Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringDelay);

        var defaultFlux = Flux
                .just("default")
                .transform(filterMap);

        return Flux
                .fromIterable(List.of("Phan", "Huy", "Hoang"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log()
                ;
    }

    public Flux<String> namesFluxZip() {

        /* sequence*/
        var aArray =  Flux.just("A", "B").delayElements(Duration.ofMillis(900));
        var bArray =  Flux.just("E", "C").delayElements(Duration.ofMillis(90));

        return Flux.zip(aArray, bArray).map(t2 -> t2.getT1() + t2.getT2() )
                .log()
                ;
    }
    public Flux<String> namesFluxConcat() {
        /*sequence*/

        var aArray =  Flux.just("A", "B").delayElements(Duration.ofMillis(100));
        var bArray =  Flux.just("A", "C").delayElements(Duration.ofMillis(90));

        return Flux.concat(aArray, bArray)
                .log()
                ;
    }


    public Flux<String> namesFluxMerge() {
        /*no sequence*/
        var aArray =  Flux.just("A", "B").delayElements(Duration.ofMillis(900));
        var bArray =  Flux.just("E", "C").delayElements(Duration.ofMillis(90));

        return Flux.merge(aArray, bArray)
                .log()
                ;
    }

    public Flux<String> namesMonoMerge() {
        /*no sequence*/
        var aArray =  Mono.just("A").delayElement(Duration.ofMillis(100));
        var bArray =  Mono.just("B");

        return aArray.concatWith(bArray)
                .log();
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
        var randomTime =  new Random().nextInt(1000);
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
