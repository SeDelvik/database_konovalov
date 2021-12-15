package mainClasses;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DBtoApp {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
    static final String USER = "admin";
    static final String PASS = "admin";
    private Connection connection;
    final private String numberRegex = "(.*телефон.*|.*phone number.*)";

    public DBtoApp(){
        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return this.connection;
    }


    /**
     * Формирование объектов из всех данных
     * */
    public ArrayList<Organisation> getCurrentData(){
        ArrayList<Organisation> list = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT company.company_id, company.\"name\",type_company.type_name, type_info.column_names, info.\"data\" , info.active_or_not  " +
                    "FROM info,company, type_company,type_info " +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id "/*+
                    "and info.active_or_not = true"*/);
            list = resultSetToOrganisation(resultSet);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        /*try {
            Statement statement = connection.createStatement();
            ResultSet allEntry = statement.executeQuery("SELECT * FROM info WHERE active_or_not = true"); //выборка всех записей в основной таблице
            while(allEntry.next()){ //чтение основной таблицы со всеми данными
                int curId=0;
                    try {
                        curId = allEntry.getInt("company_id");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                int finalCurId = curId;
                if (list.stream().filter(organisation -> (organisation.getId()== finalCurId)).count() < 1){ //есть ли такая организация в списке
                    try {
                        Statement statName = connection.createStatement();
                        Statement statType = connection.createStatement();
                        ObservableMap<String, String> hash = new SimpleMapProperty<>();

                        int COMID=allEntry.getInt("company_id");
                        ResultSet comName = statName.executeQuery("SELECT * FROM company WHERE company_id = "+ COMID); //поиск имени в таблице с именами организаций
                        comName.next();
                        ResultSet comType = statType.executeQuery("SELECT * FROM type_company WHERE id = "+comName.getInt("type_id")); //поиск типа в таблице с типами организаций
                        comType.next();
                        String companyName=comName.getString("name"); //получение имени как строки
                        String companyType=comType.getString("type_name");  //получение типа как строки

                        Organisation org = new Organisation(finalCurId);
                        //org.getData().
                        org.getData().put("Type",companyType);
                        org.getData().put("Name",companyName);
                        //hash.put("Type",companyType);
                        //hash.put("Name",companyName);
                        list.add(org*//*new Organisation(finalCurId, hash)*//*);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

                try {
                    Statement statInfo = connection.createStatement();
                    int curIdInfo = allEntry.getInt("type_info_id");
                    ResultSet infoType = statInfo.executeQuery("SELECT * from type_info WHERE id = "+curIdInfo);
                    infoType.next();
                    String nameInfo = infoType.getString("column_names");
                    String info = allEntry.getString("data");
                    list.stream().filter(organisation -> (organisation.getId()== finalCurId)).forEach(organisation -> {
                        organisation.getData().put(nameInfo,info);
                        //organisation.putData(nameInfo,info);
                    });

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }*/
        return list;
    }

    public ArrayList<String> getAllTypeOrganisation(){
        ArrayList<String> output = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet allEntry = statement.executeQuery("SELECT * FROM type_company "); //выборка всех записей в основной таблице
            while(allEntry.next()){
                output.add(allEntry.getString("type_name"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return output;
    }

    /**
     * Получить все возможные виды дополнительной информации + основной тип
     * */
    public ArrayList<String> getAllTypeInformationWithoutPhoneNumber(){
        ArrayList<String> output = new ArrayList<>();
        output.add("type");
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select *\n" +
                    "from type_info\n" +
                    "where column_names !~ '(.*телефон.*|.*phone number.*)'");
            while(resultSet.next()){
                output.add(resultSet.getString(2));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return output;
    }

    /**
     * Поиск номера телефона по названию и типу
     * */
    public ArrayList<Organisation> getFilteredNumber(HashMap<String,String> data) throws SQLException {
        ArrayList<Organisation> output = new ArrayList<>();

        /*if(data.get("all").equals("false")){

        }*/

        Statement statement = connection.createStatement();
        ResultSet resultSet;
        if (data.get("type data").equals("type")){ //тип организации или другое
            resultSet = statement.executeQuery("SELECT company.company_id, company.\"name\",type_company.type_name, type_info.column_names, info.\"data\" , info.active_or_not  \n" +
                    "FROM info,company, type_company,type_info \n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n" +
                    "and type_company.type_name ~ \'.*"+data.get("data")+".*\' and company.\"name\" ~ \'.*"+data.get("name")+".*\'");
        }
        else{
            resultSet = statement.executeQuery("SELECT company.company_id, company.\"name\",type_company.type_name, type_info.column_names, info.\"data\" , info.active_or_not  \n" +
                    "FROM info,company, type_company,type_info \n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n" +
                    "and type_info.column_names ~ \'.*"+data.get("type data")+".*|"+ numberRegex +"\' and info.\"data\" ~ \'.*"+data.get("data")+".*\' and company.\"name\" ~ \'.*"+data.get("name")+".*\'");
        }

        output = resultSetToOrganisation(resultSet);
        /*String type = "";
        if (data.get("type").equals("All")){
            type = ".*";
        }
        else{
            type = data.get("type");
        }
        String phoneType = "\'(.*телефон.*|.*phone number.*)\'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT company.company_id, company.\"name\",type_company.type_name, type_info.column_names, info.\"data\"  " +
                "FROM info,company, type_company,type_info " +
                "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id " +
                "and type_company.type_name ~ \'"+type+"\' and type_info.column_names ~ "+phoneType+" and info.data ~ \'.*"+data.get("number")+".*\'");

        output = resultSetToOrganisation(resultSet);*/
        return output;
    }
    /**
     * Формирует из записей объекты-организации
     * */
    public ArrayList<Organisation> resultSetToOrganisation(ResultSet resultSet) throws SQLException {
        ArrayList<Organisation> output = new ArrayList<>();
        while(resultSet.next()){
            Organisation org;
            int id = resultSet.getInt(1);
            Organisation orgTest = output.stream().filter(x -> x.getId()==id).findFirst().orElse(null);
            if (orgTest==null){
                orgTest = new Organisation(id);
                orgTest.getData().put("Name", resultSet.getString(2));
                orgTest.getData().put("Type",resultSet.getString(3));
                output.add(orgTest);
            }
            String dataName = resultSet.getString(4);
            String data = resultSet.getString(5);
            if(resultSet.getBoolean(6)) {
                output.stream().filter(x -> x.getId() == id).forEach(x -> {
                    x.getData().put(dataName, data);
                });
            }
            else{
                output.stream().filter(x -> x.getId() == id).forEach(x -> {
                    if(!x.getOldData().containsKey(dataName)) x.getOldData().put(dataName,new ArrayList<>());
                    x.getOldData().get(dataName).add(data);
                });
            }

            /*if (output.contains(new Organisation(resultSet.getInt(1)))){
                org = output.get(output.indexOf(new Organisation(resultSet.getInt(1))));
            }
            else{
                org = new Organisation(resultSet.getInt(1));
                org.getData().put("Name", resultSet.getString(2));
                org.getData().put("Type",resultSet.getString(3));
                output.add(org);

            }
            org.getData().put(resultSet.getString(4),resultSet.getString(5));*/

        }
        return output;
    }
    /**
     * Поиск организации по номеру телефона
     * */
    public ArrayList<Organisation> getOrganisationToNum(HashMap<String, String> params) {
        ArrayList<Organisation> output = new ArrayList<>();
        //ResultSet resultSet;
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT company.company_id, company.\"name\",type_company.type_name, type_info.column_names, info.\"data\" , info.active_or_not  \n" +
                    "FROM info,company, type_company,type_info \n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n" +
                    "\tand type_info.column_names ~ \'"+numberRegex+"\' and info.\"data\" ~ \'.*"+params.get("number")+".*\'");
            output = resultSetToOrganisation(resultSet);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //output = resultSetToOrganisation(redultSet);
        return output;
    }

}
