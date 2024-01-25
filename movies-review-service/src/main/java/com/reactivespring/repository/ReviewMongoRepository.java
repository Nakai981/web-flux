package com.reactivespring.repository;
import com.reactivespring.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReviewMongoRepository extends ReactiveMongoRepository<Review, String> {
}
