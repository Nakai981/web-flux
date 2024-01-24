package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieInfoServices {

    private final MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo){
        return movieInfoRepository.save(movieInfo);

    }
    public Flux<MovieInfo> findAll(){
        return movieInfoRepository.findAll();

    }
    public Mono<MovieInfo> findById(String id){
        return movieInfoRepository.findById(id);

    }
    public Mono<MovieInfo> putMovieInfo(MovieInfo movieInfo, String id){
        return movieInfoRepository.findById(id).flatMap(movieInfo1 -> {
            movieInfo1.setName(movieInfo.getName());
            movieInfo1.setCast(movieInfo.getCast());
            movieInfo1.setYear(movieInfo.getYear());
            movieInfo1.setRelease_date(movieInfo.getRelease_date());
            return movieInfoRepository.save(movieInfo1);
        });
    }

    public Mono<Void> deleteMovieInfo(String id){
         return movieInfoRepository.deleteById(id).log();
    }
}
