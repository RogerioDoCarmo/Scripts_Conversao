package com.rogerio.scripts_conversao;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rogerio
 */
public class Main {
    public static void main (String[] args){
        
        System.out.println("CSV APP TO KML CONVERTER!\n\n\n");
        
        String root_directory = "C:\\Users\\Rogerio\\Desktop\\";
        
        //=============================================================================================
        
        String fileNameKML = "RESULT_vlog.kml";              
                        
        ArrayList<String> sppResult =  readTextFile("C:\\Users\\Rogerio\\Desktop\\spp_inct_fct_SPP_result_2020_09_29_17_18_43.txt");
       
        KML_Creator newKMLfile = new KML_Creator(fileNameKML);
        
        // TODO: Put in KML_Creator class
        for (int i = 0; i < sppResult.size() - 1; i++) {
            String[] currentLine = sppResult.get(i).split(";");

            try {

                String timeStamp  = currentLine[0].replace(" ","T") + "Z";
                double latDegrees = Double.valueOf(currentLine[12]);
                double lonDegrees = Double.valueOf(currentLine[13]);
                double altMeters  = Double.valueOf(currentLine[14]);
                
                newKMLfile.add_PointToKML(timeStamp, latDegrees, lonDegrees, altMeters);
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        // TODO: Put in KML_Creator class

        newKMLfile.finalizeKMLfile();

        String writingFileName = root_directory + "\\" + fileNameKML;
                
        writeTextFile(writingFileName,newKMLfile.getKMLfileContentAsString());
        
        //=============================================================================================        
//        ArrayList<String> CSVfile = readTextFile("C:\\Users\\Rogerio\\Desktop\\20200929171828.csv");
//        
//        CSV_To_Delta_ENU csvLatLonToECEFenuValues = new CSV_To_Delta_ENU(CSVfile);
//        csvLatLonToECEFenuValues.compute_discrepancies();
        //=============================================================================================
        
///
        //////////////////////////////////////////////////////////////////////////////////////////////////
        // FOR THE ARTICLE !!!!!!!!!!!!!!!!!!!!!!
        //////////////////////////////////////////////////////////////////////////////////////////////////
        ///
        ArrayList<String> SPPfile = readTextFile("C:\\Users\\Rogerio\\Desktop\\9.txt");
        SPPresult_to_Delta_ENU sppDiscrepanciesValues = new SPPresult_to_Delta_ENU(SPPfile);
        sppDiscrepanciesValues.compute_discrepancies();
    }
    
    public static boolean writeTextFile(String fileName, String fileContent) {
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(fileContent);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ArrayList readTextFile(String fileName) {
        ArrayList<String> fileContent = new ArrayList<>();

        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                if (data.startsWith("#")) continue;                
                
                fileContent.add(data);
//                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return fileContent;
    }
}
