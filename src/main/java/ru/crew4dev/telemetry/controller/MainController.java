package ru.crew4dev.telemetry.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class MainController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                button.setText("Thanks!");
//            }
//        });
    }

    @FXML
    public void Dagrooster(ActionEvent event) {
        System.out.println("lollolol");
    }

    @FXML
    private Button button;

    @FXML
    public void onClickMethod(){
        //button.setText("Thanks!");
        System.out.println("Thanks!");
    }
}
