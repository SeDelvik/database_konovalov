package mainClasses;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

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
    public void changeData(HashMap<String,String> newValues){
        /*проверка удаленных*/
        ArrayList<String> keySet = new ArrayList<>(data.keySet());
        for(String key : keySet){
            if(!newValues.containsKey(key)){ //если значение было удалено то перемещение значения в старые данные и удаление ключа в массиве с данными
                if(!oldData.containsKey(key)) oldData.put(key, new ArrayList<>());
                oldData.get(key).add(data.remove(key));
            }else if(!newValues.get(key).equals(data.get(key))){
                if(!key.equals("Name")&&!key.equals("Type")&&!key.equals("Main Address")) {
                    if (!oldData.containsKey(key)) oldData.put(key, new ArrayList<>());
                    oldData.get(key).add(data.get(key));
                }
                data.put(key,newValues.get(key));
            }
        }
        for(String key : newValues.keySet()){
            if(!data.containsKey(key)){
                data.put(key,newValues.get(key));
            }
        }
    }

    @Override
    public String toString(){
        return id.get() +" "+data.get("Name");
    }

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
