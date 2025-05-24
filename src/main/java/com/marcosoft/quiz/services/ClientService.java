package com.marcosoft.quiz.services;

import com.marcosoft.quiz.domain.Client;

import java.util.List;

public interface ClientService {
    Client getClientById(int id);
    List<Client> getAllClients();
    void deleteClientById(int id);
    Client save(Client client);
    boolean existsClientByClientId(int id);
    int updateResolucionById(String resolution, int id);
    int updateEsNuevoById(boolean isNew, int id);
    int updateRutaCarpetasById(String folderPath, int id);
    int updateModoPantallaById(int windowMode, int id);
    int updateQuestionNumberById(int questionNumber, int id);
    int updateThematicNumberById(int thematicNumber, int id);
}
