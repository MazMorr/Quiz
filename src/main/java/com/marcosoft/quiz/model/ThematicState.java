package com.marcosoft.quiz.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ThematicState {

    private final Map<String, Boolean> thematicStatus = new HashMap<>();

    public ThematicState() {
        // Inicializar todas las temáticas como no seleccionadas
        thematicStatus.put("Temática1", false);
        thematicStatus.put("Temática2", false);
        thematicStatus.put("Temática3", false);
        thematicStatus.put("Temática4", false);
    }

    public boolean isThematicSelected(String thematic) {
        return thematicStatus.getOrDefault(thematic, false);
    }

    public void selectThematic(String thematic) {
        thematicStatus.put(thematic, true);
    }

    public boolean allThematicsSelected() {
        return thematicStatus.values().stream().allMatch(selected -> selected);
    }

    public String getLastRemainingThematic() {
        return thematicStatus.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
