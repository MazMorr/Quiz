package com.marcosoft.quiz.services.impl;

import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.repository.ClientRepository;
import com.marcosoft.quiz.services.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository){
        this.clientRepository= clientRepository;
    }

    @Override
    public Client getClientById(int id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> getAllClients() {
        return (List<Client>) clientRepository.findAll();
    }

    @Override
    public void deleteClientById(int id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public boolean existsClientByClientId(int id) {
        return clientRepository.existsById(id);
    }

    @Override
    public int updateResolucionById(String resolucion, int id) {
        return clientRepository.updateResolucionById(resolucion, id);
    }

    @Override
    public int updateEsNuevoById(boolean esNuevo, int id) {
        return clientRepository.updateEsNuevoById(esNuevo, id);
    }

    @Override
    public int updateRutaCarpetasById(String rutaCarpetas, int id) {
        return clientRepository.updateRutaCarpetasById(rutaCarpetas, id);
    }

    @Override
    public int updateModoPantallaById(int modoPantalla, int id) {
        return clientRepository.updateModoPantallaById(modoPantalla,id);
    }
}
