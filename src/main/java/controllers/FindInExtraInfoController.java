package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class FindInExtraInfoController implements Initializable {
    @FXML
    private TextField dataText;
    @FXML
    private Button closeButton;
    @FXML
    private Button applyButton;
    @FXML
    private Label errorMessage;

    private HashMap<String,String> params;

    public FindInExtraInfoController(HashMap<String,String> params){
        this.params = params;
    }

    public void cancel() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void applyFindData() {
        if (Pattern.matches("^.+$", dataText.getText()) ){
            params.put("dataAll", dataText.getText());
            Stage stage = (Stage) applyButton.getScene().getWindow();
            stage.close();

        }
        else{
            errorMessage.opacityProperty().setValue(1);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
