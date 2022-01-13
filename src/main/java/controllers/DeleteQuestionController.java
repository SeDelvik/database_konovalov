package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DeleteQuestionController {
    @FXML
    Button closeBut;
    private boolean accepted = false;

    public DeleteQuestionController(){}

    public void close(){
        Stage stage = (Stage) closeBut.getScene().getWindow();
        stage.close();
    }

    public void accept(){
        accepted = true;
        close();
    }

    public boolean getConsent(){
        return accepted;
    }
}
