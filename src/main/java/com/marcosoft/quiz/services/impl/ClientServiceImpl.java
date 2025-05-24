package com.marcosoft.quiz.services.impl;

import com.marcosoft.quiz.domain.Client;
import com.marcosoft.quiz.repository.ClientRepository;
import com.marcosoft.quiz.services.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
    public int updateResolucionById(String resolution, int id) {
        return clientRepository.updateResolutionById(resolution, id);
    }

    @Override
    public int updateEsNuevoById(boolean isNew, int id) {
        return clientRepository.updateIsNewById(isNew, id);
    }

    @Override
    public int updateRutaCarpetasById(String folderPath, int id) {
        return clientRepository.updateFolderPathById(folderPath, id);
    }

    @Override
    public int updateModoPantallaById(int windowMode, int id) {
        return clientRepository.updateWindowModeById(windowMode, id);
    }

    @Override
    public int updateQuestionNumberById(int questionNumber, int id) {
        return clientRepository.updateQuestionNumberById(questionNumber, id);
    }

    @Override
    public int updateThematicNumberById(int thematicNumber, int id) {
        return clientRepository.updateThematicNumberById(thematicNumber, id);
    }
}
