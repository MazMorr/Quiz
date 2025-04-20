package com.marcosoft.quiz.repository;

import com.marcosoft.quiz.domain.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Integer> {

    @Override
    boolean existsById(Integer integer);
}