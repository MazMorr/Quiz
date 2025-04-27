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
    @Query("update Client c set c.modoPantalla = ?1 where c.id = ?2")
    int updateModoPantallaById(int modoPantalla, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.resolucion = ?1 where c.id = ?2")
    int updateResolucionById(String resolucion, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.rutaCarpetas = ?1 where c.id = ?2")
    int updateRutaCarpetasById(String rutaCarpetas, int id);

    @Transactional
    @Modifying
    @Query("update Client c set c.esNuevo = ?1 where c.id = ?2")
    int updateEsNuevoById(boolean esNuevo, int id);
}