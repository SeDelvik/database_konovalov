package mainClasses;

import comparators.OrganisationComparator;

import java.sql.*;
import java.util.*;

public class DBtoApp {
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
    static final String USER = "admin";
    static final String PASS = "admin";
    private Connection connection;
    final private String numberRegex = "phone number"; //"(.*телефон.*|.*phone number.*)";

    public DBtoApp(String url, String login,String password){
        try {
            connection = DriverManager
                    .getConnection(url,login,password/*DB_URL, USER, PASS*/);

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
        Set<Organisation> setlist = new TreeSet(new OrganisationComparator());


        try {                  /*проход по всем записям*/
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT company.company_id, company.\"name\" ,type_company.type_name, company.address, type_info.column_names, info.\"data\" , info.active_or_not  \n" +
                    "FROM info,company, type_company,type_info \n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id  ");
            setlist.addAll(resultSetToOrganisation(resultSet));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        /*поиск всех организаций*/

        setlist.addAll(getAllOrganisationWithoutExtraData());
        return new ArrayList<>(setlist)/*list*/;
    }
    /**
     * Формирование объектов без дополнительных данных т.е. без данных из таблица info
     * */
    public ArrayList<Organisation> getAllOrganisationWithoutExtraData(){
        ArrayList<Organisation> output = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select distinct company.company_id, company.\"name\","+
                    " type_company.type_name, company.address \n" +
                    "FROM company, type_company,type_info \n" +
                    "where type_company.id =company.type_id \n");
            while(resultSet.next()){
                Organisation org = new Organisation(resultSet.getInt(1));
                org.getData().put("Name", resultSet.getString(2));
                org.getData().put("Type",resultSet.getString(3));
                org.getData().put("Main Address",resultSet.getString(4));
                output.add(org);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }
    /**
     * Получить все возможные виды дополнительной информации + тип организации + главный адрес
     * */
    public ArrayList<String> getAllTypeInformationWithoutPhoneNumber(){
        ArrayList<String> output = new ArrayList<>();
        output.add("type");
        output.add("main address");
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select *\n" +
                    "from type_info\n" +
                    "where column_names !~ '"+numberRegex+"'");
            while(resultSet.next()){
                output.add(resultSet.getString(2));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return output;
    }
    /**
     * Получить все возможные типы компаний
     * */
    public ArrayList<String> getAllTypeCompany(){
        ArrayList<String> output = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select type_name\n" +
                    "from type_company");
            while(resultSet.next()){
                output.add(resultSet.getString(1));
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
        Statement statement = connection.createStatement();
        ResultSet resultSet;
        String sqlZapr;
        switch (data.get("type data")){
            case("type"):
                sqlZapr = "select distinct company.company_id\n" +
                        "FROM info,company, type_company,type_info \n" +
                        "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id  \n"+
                        "and type_company.type_name ~ ? and company.\"name\" ~ ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlZapr);
                preparedStatement.setString(1,data.get("data"));
                preparedStatement.setString(2,data.get("name"));
                resultSet = preparedStatement.executeQuery();
                break;
            case("main address"): //?
                sqlZapr = "select distinct company.company_id\n" +
                        "FROM info,company, type_company,type_info \n" +
                        "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id  \n"+
                        "and company.address ~ ? and company.\"name\" ~ ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(sqlZapr);
                preparedStatement1.setString(1,data.get("data"));
                preparedStatement1.setString(2,data.get("name"));
                resultSet = preparedStatement1.executeQuery();
                break;
            default:
                sqlZapr = "select distinct company.company_id\n" +
                        "FROM info,company, type_company,type_info \n" +
                        "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n"+
                        "and type_info.column_names ~ ? and info.\"data\" ~ ? and company.\"name\" ~ ?";
                String put = data.get("type data")+"|"+numberRegex;
                PreparedStatement preparedStatement2 = connection.prepareStatement(sqlZapr);
                preparedStatement2.setString(1,put);
                preparedStatement2.setString(2,data.get("data"));
                preparedStatement2.setString(3,data.get("name"));
                resultSet = preparedStatement2.executeQuery();
                break;
        }
        output = getAllData(resultSet);
        return output;
    }
    /**
     * Формирует из записей(ResultSet) объекты-организации
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
                orgTest.getData().put("Main Address",resultSet.getString(4));
                output.add(orgTest);
            }
            String dataName = resultSet.getString(5);
            String data = resultSet.getString(6);
            if(resultSet.getBoolean(7)) {
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

        }
        return output;
    }
    /**
     * Поиск организации по номеру телефона
     * */
    public ArrayList<Organisation> getOrganisationToNum(HashMap<String, String> params) {
        ArrayList<Organisation> output = new ArrayList<>();
        String checkSelection = "";
        switch(params.get("check")){
            case ("selected"):
                checkSelection = " and active_or_not = true";
                break;
            case("unselected"):
                checkSelection = " and active_or_not = false";
                break;
            default:
                break;

        }
        try{
            Statement statement = connection.createStatement();
            String sql = "select distinct company.company_id\n" +
                    "FROM info,company, type_company,type_info \n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n"+
                    "and type_info.column_names ~ \'"+numberRegex+"\' and info.\"data\" ~ \'.*"+params.get("number")+".*\'"+checkSelection;
            ResultSet resultSet = statement.executeQuery(sql);

            output = getAllData(resultSet);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }
    /**
     * получить все данные через ResultSet по id организации
     * */
    public ArrayList<Organisation> getAllData(ResultSet resultSet){
        String id = "(";
        ArrayList<Organisation> output = new ArrayList<>();
        try {
            while (resultSet.next()) {
                id = id+resultSet.getInt(1)+",";
            }
            id = id.substring(0,id.length()-1)+")";
            if(id.length()<2) return output;
            ResultSet subResultSet = connection.createStatement().executeQuery("SELECT company.company_id, company.\"name\",type_company.type_name, company.address , type_info.column_names, info.\"data\", info.active_or_not\n" +
                    "FROM info,company, type_company,type_info, main_address\n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n" +
                    "and info.company_id in"+id);

            output = resultSetToOrganisation(subResultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }
    /**
     * поиск по всей дополнительной информации включая старую (не включает поиск по телефонным номерам)
     * */
    public ArrayList<Organisation> findInAllExtraData(String param){
        ArrayList<Organisation> output = new ArrayList<>();
        try {
            String sql = "select distinct company.company_id\n" +
                    "FROM info,company, type_company,type_info \n" +
                    "where  type_company.id = company.type_id and company.company_id = info.company_id and info.type_info_id = type_info.id \n"+
                    "and type_info.column_names !~ ? and info.\"data\" ~ ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,numberRegex);
            preparedStatement.setString(2,param);
            ResultSet resultSet = preparedStatement.executeQuery();
            output = getAllData(resultSet);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return output;
    }
    /**
     * Добавление нового типа организации в базу данных
     * */
    public void addNewOrganisationType(String value){
        try {
            Statement statement = connection.createStatement();
            int MAX = 0;
            ResultSet resultSet = statement.executeQuery("select max(id)\n" +
                    "from type_company");
            while (resultSet.next()){
                MAX = resultSet.getInt(1)+1;
            }
            statement.executeUpdate("INSERT INTO type_company VALUES ("+MAX+",'"+value+"')");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /**
     * Получить список названий всех типов информации*/
    public ArrayList<String> getAllInfoType(){
        ArrayList<String> array = new ArrayList<>();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select type_info.column_names \n" +
                    "FROM type_info ");
            while(resultSet.next()){
                array.add(resultSet.getString(1));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }
    /**
     * Добавление нового типа информации*/
    public void addNewInfoType(String value){
        try{
            Statement statement = connection.createStatement();
            int MAX = 0;
            ResultSet resultSet = statement.executeQuery("select max(id)\n" +
                    "from type_info");
            while (resultSet.next()){
                MAX = resultSet.getInt(1)+1;
            }
            statement.executeUpdate("INSERT INTO type_info VALUES ("+MAX+",'"+value+"')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /**
     * Получить максимальный свободный на данный момент id в таблице company (содержит все организации с обязательными параметрами)*/
    public int getMaxOrganisationId(){
        int MAX = 0;
        try{
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select max(company_id)\n" +
                    "from company");
            while (resultSet.next()){
                MAX = resultSet.getInt(1)+1;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return MAX;
    }
    /**
     * Получить id типа организации по его названию*/
    public int getTypeOrganisationId(String typeName){
        int id = 0;
        try{
            Statement statement = connection.createStatement();
            String update = String.format("select id from type_company\n" +
                    "where type_name ~ '%s'",typeName);
            ResultSet resultSet = statement.executeQuery(update);
            while(resultSet.next()){
                id = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;

    }
    /**
     * Получить id типа информации по его названию*/
    public int getInfoTypeId(String typeName){
        int id = 0;
        try{
            Statement statement = connection.createStatement();
            String update = String.format("select id from type_info\n" +
                    "where column_names ~ '%s'",typeName);
            ResultSet resultSet = statement.executeQuery(update);
            while(resultSet.next()){
                id = resultSet.getInt(1);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }
    /**
     * Получить максимальный свободный на данный момент id в таблице info (содержит все записи с телефонами и доп информацией)*/
    public int getMaxInfoId(){
        int id = 0;
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select max(info_id)\n" +
                    "from info");
            while (resultSet.next()){
                id = resultSet.getInt(1)+1;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return id;
    }

    /**
    * Добавляет новую организацию в соответствующие таблицы базы данных*/
    public void addNewOrganisation(Organisation organisation){
        try{
            int typeId = getTypeOrganisationId(organisation.getData().get("Type"));
            String update = "insert into company values (?,?,?,?)";
            PreparedStatement preparedStatementCompany = connection.prepareStatement(update);
            preparedStatementCompany.setInt(1,organisation.getId());
            preparedStatementCompany.setString(2,organisation.getData().get("Name"));
            preparedStatementCompany.setInt(3,typeId);
            preparedStatementCompany.setString(4,organisation.getData().get("Main Address"));
            preparedStatementCompany.executeUpdate();

            //statement = connection.createStatement();
            int infoId = getMaxInfoId();
            for(String key : organisation.getData().keySet()){
                if(!key.equals("Name")&&
                        !key.equals("Type")&&
                        !key.equals("Main Address")){
                    /*update = String.format("insert into info values " +
                            "(%d,%d,%d,%s,true)",
                            infoId,organisation.getId(),getInfoTypeId(key),organisation.getData().get(key));*/
                    update = "insert into info values (?,?,?,?,true)";
                    PreparedStatement preparedStatementInfo = connection.prepareStatement(update);
                    preparedStatementInfo.setInt(1,infoId);
                    preparedStatementInfo.setInt(2,organisation.getId());
                    preparedStatementInfo.setInt(3,getInfoTypeId(key));
                    preparedStatementInfo.setString(4,organisation.getData().get(key));
                    preparedStatementInfo.executeUpdate();
                    //statement.executeUpdate(update);
                    infoId++;
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /**
     * редактирование переданной организации*/
    public void editOrganisationData(Organisation organisation,HashMap<String,String> newValues){
        /*удаленные*/
        try{
            /*таблица company*/
            Statement statementGet = connection.createStatement();
            ResultSet resultSetCompany = statementGet.executeQuery(String.format("select company.\"name\",type_company.type_name,company.address " +
                    "from company, type_company where company.type_id =type_company.id and company_id = %d",organisation.getId()));
            while(resultSetCompany.next()){
                if(!organisation.getData().get("Name").equals(resultSetCompany.getString(1))){
                    String sql = "update company set name = ? where company_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, organisation.getData().get("Name"));
                    preparedStatement.setInt(2,organisation.getId());
                    preparedStatement.executeUpdate();
                }
                if(!organisation.getData().get("Type").equals(resultSetCompany.getString(2))){
                    String sql = "update company set type_id = ? where company_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, getTypeOrganisationId(organisation.getData().get("Type")) );
                    preparedStatement.setInt(2,organisation.getId());
                    preparedStatement.executeUpdate();
                }
                if(!organisation.getData().get("Main Address").equals(resultSetCompany.getString(3))){
                    String sql = "update company set address = ? where company_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, organisation.getData().get("Main Address"));
                    preparedStatement.setInt(2,organisation.getId());
                    preparedStatement.executeUpdate();
                }


            }
            /*таблица info*/
            ArrayList<String> keySet = new ArrayList<>(organisation.getData().keySet());
            keySet.remove("Type");
            keySet.remove("Main Address");
            keySet.remove("Name");
            Statement statement = connection.createStatement();
            String sql = String.format("select info.info_id ,info.type_info_id ,type_info.column_names,info.\"data\",info.active_or_not " +
                    "from info,type_info where type_info.id = info.type_info_id and company_id = %d",organisation.getId());
            ResultSet resultSetInfo = statement.executeQuery(sql);
            while (resultSetInfo.next()){
                System.out.println(resultSetInfo.getInt(1)+" "+resultSetInfo.getInt(2)+" "+resultSetInfo.getString(3)+
                        " "+resultSetInfo.getString(4)+" "+resultSetInfo.getBoolean(5));

                if(resultSetInfo.getBoolean(5)){
                    if(!organisation.getData().containsKey(resultSetInfo.getString(3))){ //если информацию удалили
                        String sqlUpdate = String.format("update info set active_or_not = false where info.info_id = %d",resultSetInfo.getInt(1));
                        connection.createStatement().executeUpdate(sqlUpdate);
                    }
                    //добавить новое значение если такого же типа уже существовало ранее
                   else if(!organisation.getData().get(resultSetInfo.getString(3)).equals(resultSetInfo.getString(4))){
                        String sqlUpdate = String.format("update info set active_or_not = false where info.info_id = %d",resultSetInfo.getInt(1));
                        connection.createStatement().executeUpdate(sqlUpdate);
                        sqlUpdate = "insert into info values(?,?,?,?,true)";
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
                        preparedStatement.setInt(1,getMaxInfoId());
                        preparedStatement.setInt(2,organisation.getId());
                        preparedStatement.setInt(3,resultSetInfo.getInt(2));
                        preparedStatement.setString(4,organisation.getData().get(resultSetInfo.getString(3)));
                        keySet.remove(resultSetInfo.getString(3));
                        preparedStatement.executeUpdate();
                    }
                }
            }
            for(String key : keySet){ //добавить в бд то что появилось нового
                String sqlUpdate = "insert into info values(?,?,?,?,true)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate);
                preparedStatement.setInt(1,getMaxInfoId());
                preparedStatement.setInt(2,organisation.getId());
                preparedStatement.setInt(3,getInfoTypeId(key));
                preparedStatement.setString(4,organisation.getData().get(key));
                preparedStatement.executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    /**
     * удаление переданной организации*/
    public void deleteOrganisation(Organisation organisation){
        try{
            String sql = String.format("delete from info where company_id = %d",organisation.getId());
            connection.createStatement().executeUpdate(sql);
            sql = String.format("delete from company where company_id = %d",organisation.getId());
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
