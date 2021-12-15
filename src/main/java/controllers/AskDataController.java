package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AskDataController implements Initializable {
    @FXML
    private ChoiceBox<String> category;
    @FXML
    private TextField orgText;
    @FXML
    private Button closeButton;
    @FXML
    private Button applyButton;
    @FXML
    private Label errorMessage;
    @FXML
    private TextField categoryData;

    private HashMap<String,String> data;
    private ArrayList<String> listType;


    public AskDataController(HashMap<String,String> data,ArrayList<String> list){
        this.data = data;
        this.listType = list;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //category.getItems().add("All");
        //listType.add(0,"All");
        /*listType.forEach(x ->{
            category.getItems().add(x);
        });*/
        category.setItems(FXCollections.observableList(listType));
        //category.setValue("All");
        category.getSelectionModel().selectFirst();
    }

    public void cancel() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void applyFindNum() {
        if (Pattern.matches("^.+$", orgText.getText()) ){
            data.put("name", orgText.getText());
            data.put("type data",category.getValue());
            data.put("data",categoryData.getText());
            Stage stage = (Stage) applyButton.getScene().getWindow();
            stage.close();

        }
        else{
            errorMessage.opacityProperty().setValue(1);
        }
    }
    public HashMap<String,String> getData(){
        return data;
    }
}
