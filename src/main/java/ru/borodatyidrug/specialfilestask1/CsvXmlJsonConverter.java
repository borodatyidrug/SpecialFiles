package ru.borodatyidrug.specialfilestask1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CsvXmlJsonConverter {
    
    private static <T> void toJson(List<T> list, FileWriter writer) throws IOException {
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        writer.write(gson.toJson(list, list.getClass()) + "\n");
        writer.flush();
    }
    
    public static void convertCsvToJson(String csvPath, String jsonPath) {
        
        try (CSVReader reader = new CSVReader(new FileReader(csvPath));
                FileWriter writer = new FileWriter(jsonPath, true)) {
            
            ColumnPositionMappingStrategy<Employee> strategy
                = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            
            List<Employee> list = new ArrayList<>();
            list = csv.parse();
            
            toJson(list, writer);
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void parseXML(Node node, List<Employee> list) {
        
        if (node.getNodeType() == Node.ELEMENT_NODE 
                && node.getNodeName().equals("employee")
                && node.hasChildNodes()) {
            
            long id = 0;
            String firstName = "";
            String lastName = "";
            String country = "";
            int age = 0;
            
            NodeList fields = node.getChildNodes();
            
            for (int j = 0; j < fields.getLength(); j++) {

                Node currField = fields.item(j);

                if (currField.getNodeType() == Node.ELEMENT_NODE) {

                    switch (currField.getNodeName()) {
                        case "id" -> id = Long.parseLong(currField.getTextContent());
                        case "firstName" -> firstName = currField.getTextContent();
                        case "lastName" -> lastName = currField.getTextContent();
                        case "country" -> country = currField.getTextContent();
                        case "age" -> age = Integer.parseInt(currField.getTextContent());
                        default -> {
                        }
                    }
                }
            }
            
            list.add(new Employee(id, firstName, lastName, country, age));
            
        } else {

            if (node.hasChildNodes()) {
                
                NodeList nodeList = node.getChildNodes();
                
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node currNode = nodeList.item(i);
                    parseXML(currNode, list);
                }
            }
        }
    }
    
    public static void convertXmlToJson(String xmlPath, String jsonPath) {
        
        try (FileWriter writer = new FileWriter(jsonPath, true)) {
            
            File source = new File(xmlPath);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(source);
            
            List<Employee> list = new ArrayList<>();
            
            Node root = document.getDocumentElement();
            parseXML(root, list);
            
            toJson(list, writer);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static List<Employee> parseJsonToEmployee(String jsonPath) {
        
        JSONParser parser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        
        try (FileReader reader = new FileReader(jsonPath)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            jsonArray.stream()
                    .forEach(jsonItem -> {
                        JSONObject jsonObject = (JSONObject) jsonItem;
                        list.add(new Employee(
                                Long.parseLong(jsonObject.get("id").toString()),
                                jsonObject.get("firstName").toString(),
                                jsonObject.get("lastName").toString(),
                                jsonObject.get("country").toString(),
                                Integer.parseInt(jsonObject.get("age").toString())
                        ));
                    });
            list.stream().forEach(x -> {
                System.out.println(x.toString());
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
