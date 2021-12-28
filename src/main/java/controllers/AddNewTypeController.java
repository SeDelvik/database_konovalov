package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mainClasses.DBtoApp;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class AddNewTypeController {
    @FXML
    private TextField field;
    @FXML
    private Button CloseBut;
    @FXML
    private Label errorMessage;
    private ArrayList<String> array;
    private DBtoApp connector;

    public  AddNewTypeController(DBtoApp connector,ArrayList<String> array){
        this.array = array;
        this.connector = connector;
    }

    public void close(){
        Stage stage = (Stage) CloseBut.getScene().getWindow();
        stage.close();
    }
    public void add(){
        //что-то
        String text = field.getText().trim();
        if ((Pattern.matches("^[a-zA-Z0-9]+$",text))){
            int flag = 0;
            for(int i=0;i < array.size();i++){
                if(array.get(i).equals(text)){
                    flag++;
                    break;
                }
            }
            if(flag<1){
                connector.addNewOrganisationType(text);
                close();
            }
            else errorMessage.opacityProperty().setValue(1);

        }
        else errorMessage.opacityProperty().setValue(1);
    }


}
