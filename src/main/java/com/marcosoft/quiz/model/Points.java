package com.marcosoft.quiz.model;

import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Component;

import javafx.scene.control.Label;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class Points {

    @Getter
    @Setter
    private int blueTeamPoints = 0, redTeamPoints = 0, purpleTeamPoints = 0, greenTeamPoints = 0;

    public void refreshPoints(Label blue, Label red, Label green, Label purple) {
        blue.setText(String.valueOf(blueTeamPoints));
        red.setText(String.valueOf(redTeamPoints));
        green.setText(String.valueOf(greenTeamPoints));
        purple.setText(String.valueOf(purpleTeamPoints));
    }

}
