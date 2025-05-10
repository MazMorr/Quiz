package com.marcosoft.quiz.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class ThematicState {

    private final Map<String, Boolean> thematicStatus = new HashMap<>();
    @Getter
    private String actualThematic;
    @Setter
    @Getter
    private int x = 0;
    @Getter
    @Setter
    private boolean isThematic1selected = false, isThematic2selected = false, isThematic3selected = false, isThematic4selected = false;

    public void actualThematic(String thematicName) {
        actualThematic = "/" + thematicName;
    }
}
