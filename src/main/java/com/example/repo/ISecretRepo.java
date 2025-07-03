package com.example.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Secret;

@Repository
public interface ISecretRepo extends ReactiveCrudRepository<Secret, Long> {

}
