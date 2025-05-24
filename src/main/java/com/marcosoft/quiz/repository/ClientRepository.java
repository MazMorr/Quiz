package com.marcosoft.quiz.repository;

import com.marcosoft.quiz.domain.Client;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClientRepository extends CrudRepository<Client, Integer> {

    @Override
    boolean existsById(Integer integer);

    @Transactional
    @Modifying
    @Query("update Client c set c.windowMode = ?1 where c.id = ?2")
    int updateWindowModeById(int windowMode, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.resolution = ?1 where c.id = ?2")
    int updateResolutionById(String resolution, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.folderPath = ?1 where c.id = ?2")
    int updateFolderPathById(String folderPath, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.isNew = ?1 where c.id = ?2")
    int updateIsNewById(boolean isNew, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.questionNumber = ?1 where c.id = ?2")
    int updateQuestionNumberById(int questionNumber, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.thematicNumber = ?1 where c.id = ?2")
    int updateThematicNumberById(int thematicNumber, int id);
}