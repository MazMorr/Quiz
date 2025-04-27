package com.marcosoft.quiz.services;

import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.repository.ClientRepository;

import java.util.List;

public interface ClientService {
    Client getClientById(int id);
    List<Client> getAllClients();
    void deleteClientById(int id);
    Client save(Client client);
    boolean existsClientByClientId(int id);
    int updateResolucionById(String resolucion, int id);
    int updateEsNuevoById(boolean esNuevo, int id);
    int updateRutaCarpetasById(String rutaCarpetas, int id);
    int updateModoPantallaById(int modoPantalla, int id);
}
