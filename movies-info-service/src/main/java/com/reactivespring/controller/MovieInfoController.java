package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MovieInfoController {


    private final MovieInfoServices movieInfoServices;



    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoServices.addMovieInfo(movieInfo).log();
    }

    @GetMapping("/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> findAllMovieInfo(@RequestParam(required = false, name = "name") String name){
        if (!name.isBlank()){
            return movieInfoServices.findByName(name).log();
        }
        return movieInfoServices.findAll().log();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> findByMovieInfoId(
            @PathVariable String id
    ){
        return movieInfoServices.findById(id).map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log()
                ;
    }
    @PutMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieInfo> putMovieInfoId(
            @PathVariable String id,
            @RequestBody MovieInfo movieInfo
    ){
        return movieInfoServices.putMovieInfo(movieInfo, id).log();
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMovieInfoId(
            @PathVariable String id
    ){
         movieInfoServices.deleteMovieInfo( id);
    }



}
