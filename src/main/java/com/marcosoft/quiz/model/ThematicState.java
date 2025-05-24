package com.marcosoft.quiz.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ThematicState {

    @Getter
    private String actualThematic;

    @Getter
    @Setter
    private int thematicsSelectedCounter = 0;

    // Nuevo: conjunto de temáticas seleccionadas
    @Getter
    private Set<String> selectedThematics = new HashSet<>();

    public void setActualThematic(String thematicName) {
        actualThematic = "/" + thematicName;
    }

    public void selectThematic(String thematicName) {
        selectedThematics.add(thematicName);
        thematicsSelectedCounter = selectedThematics.size();
    }

    public boolean isThematicSelected(String thematicName) {
        return selectedThematics.contains(thematicName);
    }

    public void reset() {
        selectedThematics.clear();
        thematicsSelectedCounter = 0;
        actualThematic = null;
    }
}
