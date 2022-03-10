import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        // Задача 1
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);

        //Задача 2
        fileName = "data.xml";
        List<Employee> list1 = parseXML(fileName);
        String json1 = listToJson(list1);
        writeString(json1);

        //Задача 3
        String json2 = readString("data.json");
        List<Employee> list2 = jsonToList(json2);
        for (Employee e : list2) {
            System.out.println(e);
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json) {
        try (FileWriter file = new FileWriter("data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Задача 1
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Задача 2
    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (Node.ELEMENT_NODE == node_.getNodeType()) {
                    Element element = (Element) node_;
                    employees.add(new Employee
                            (Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                                    element.getElementsByTagName("firstName").item(0).getTextContent(),
                                    element.getElementsByTagName("lastName").item(0).getTextContent(),
                                    element.getElementsByTagName("country").item(0).getTextContent(),
                                    Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())));
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return employees;
    }

    //Задача 3
    private static String readString(String json) {
        StringBuilder data = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(json))) {
            String s;
            while ((s = br.readLine()) != null) {
                data.append(s)
                        .append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return data.toString();
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> employees = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            JSONArray staff = (JSONArray) obj;
            for (Object i : staff) {
                employees.add(gson.fromJson(i.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return employees;
    }
}