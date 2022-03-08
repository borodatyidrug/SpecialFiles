package ru.borodatyidrug.specialfilestask1;

public class SpecialFilesTask1 {
    
    static final String DIR_PREFIX = "/media/aurumbeats/home/documents/Проекты Java/SpecialFilesTask1";
    
    public static void main(String[] args) {
        CsvXmlJsonConverter.convertCsvToJson(DIR_PREFIX + "/data.csv", 
                DIR_PREFIX + "/data.json");
        CsvXmlJsonConverter.convertXmlToJson(DIR_PREFIX + "/data.xml", 
                DIR_PREFIX + "/data2.json");
        CsvXmlJsonConverter.parseJsonToEmployee(DIR_PREFIX + "/data.json");
    }
}
