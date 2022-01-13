package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mainClasses.DBtoApp;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddNewInfoTypeController {
    @FXML
    TextField field;
    @FXML
    Button CloseBut;
    @FXML
    Label errorMessage;

    private DBtoApp connector;
    private ArrayList<String> existingInfoTypes;
    private Boolean refresh = false;
    private String value="";



    public AddNewInfoTypeController(DBtoApp connector){
        this.connector = connector;
        existingInfoTypes = connector.getAllInfoType();
    }

    public void close(){
        Stage stage = (Stage) CloseBut.getScene().getWindow();
        stage.close();
    }

    public void add(){
        String text = field.getText().trim();
        if ((Pattern.matches("^[a-zA-Z0-9]+$",text))&&
                !(text.toLowerCase().equals("type"))&&!(text.toLowerCase().equals("main address"))&&
                !(text.toLowerCase().equals("name"))){
            int flag = 0;
            for(int i=0;i < existingInfoTypes.size();i++){
                if(existingInfoTypes.get(i).equals(text)){
                    flag++;
                    break;
                }
            }
            if(flag<1){
                connector.addNewInfoType(text);
                refresh = true;
                value = text;
                close();
            }
            else errorMessage.opacityProperty().setValue(1);

        }
        else errorMessage.opacityProperty().setValue(1);



    }

    public boolean refreshable(){
        return refresh;
    }
    public String getText(){
        return value;
    }

}
