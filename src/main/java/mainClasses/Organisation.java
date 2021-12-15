package mainClasses;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Organisation {
    private IntegerProperty id;
    private MapProperty<String,String> data;
    private MapProperty<String, ArrayList<String>> oldData;

    public IntegerProperty idIntegerProperty() {
        if (id == null) {
           id = new SimpleIntegerProperty();
        }
        return id;
    }

    public void setId(int id){
        idIntegerProperty().set(id);
    }

    public int getId(){
        return this.id.get();
    }

    /*public void putData(String key,String volume){
        data.put(new SimpleStringProperty(key),new SimpleStringProperty(volume));
    }*/

    /*public String getVolume(String key){
        return data.get(new SimpleStringProperty(key)).get();
    }*/
    public MapProperty<String,String> dataMapProperty() {
        if (data == null) {
            data = new SimpleMapProperty<>(FXCollections
                    .observableMap(new HashMap<String, String>()));
        }
        return data;
    }
    public MapProperty<String,String> getData(){
        return data;
    }

    public Organisation(int id){
        setId(id);
        data = dataMapProperty();
        oldData = oldDataMapProperty();
    }

    private MapProperty<String, ArrayList<String>> oldDataMapProperty() {
        if (oldData == null) {
            oldData = new SimpleMapProperty<>(FXCollections
                    .observableMap(new HashMap<String, ArrayList<String>>()));
        }
        return oldData;
    }
    public MapProperty<String,ArrayList<String>> getOldData(){
        return oldData;
    }

    @Override
    public String toString(){
        return Integer.toString(id.get())+" "+data.get("Name");
    }
/*    public ObservableMap<String,String> MapProperty() {
        if (data == null) {
            data = new SimpleMapProperty<>();
        }
        return data;
    }

    public void setDataset(ObservableMap<String,String> hash){
        //MapProperty().set(hash);
        this.data=hash;
    }

    public ObservableMap<String,String> getData(){
        return this.data;
    }

    public Organisation (Integer id, ObservableMap<String,String> data){
        setDataset(data);
        setId(id);
        this.getData().put("Name","");
        this.getData().put("Type","");
        this.getData().put("Phone Number1","");
        this.getData().put("Address","");
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organisation that = (Organisation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }
}
