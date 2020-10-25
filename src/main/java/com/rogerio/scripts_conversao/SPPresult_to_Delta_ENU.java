package com.rogerio.scripts_conversao;

import GnssLogger.Ecef2EnuConverter;
import GnssLogger.Ecef2LlaConverter;
import GnssLogger.Lla2EcefConverter;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rogerio
 */
public class SPPresult_to_Delta_ENU {
    
    private String mHeader;
    private ArrayList<String> mOriginalCSVcontent;
    private ArrayList<String> fileContent;
    
    private final String CSV_SEPARATOR = ";";
    
    //UNESP_PP_EP01;
    double refX =  3687632.9057;
    double refY = -4620673.7618;
    double refZ = -2387161.02283;
    
    double[] averageENUmeters;
    double[] stdENUmeters;
    double Error_3D_ENU_epoch;
    double Error_3D_total;
    double RMSE_ENU_total;
    
    public SPPresult_to_Delta_ENU(ArrayList<String> originalContent) {
        this.mHeader = "# Epoch (GPS time); DX(m); DY(m); DZ(m); DN(m); DE(m); DU(m)" +
                        "\n#\n";
        
        this.mOriginalCSVcontent = originalContent;
        this.fileContent = new ArrayList<>();
        this.fileContent.add(this.mHeader);
    }
    
        // Vector to calculte the standard deviations
        double[] de_original_values;
        double[] dn_original_values;
        double[] du_original_values;
    
    public void compute_discrepancies() {
        
        System.out.println("# Epoch (GPS time); Delta_X; Delta_Y; Delta_Z; Delta_E; Delta_N; Delta_U; Error_3D_ENU");
        
        averageENUmeters = new double[3];
        stdENUmeters     = new double[3];
        
        Error_3D_ENU_epoch   = 0;
        Error_3D_total = 0;
        RMSE_ENU_total = 0;
        
        de_original_values = new double[mOriginalCSVcontent.size()];
        dn_original_values = new double[mOriginalCSVcontent.size()];
        du_original_values = new double[mOriginalCSVcontent.size()];
        
        
        int contLineReadingErrors = 0;
        for (int i = 0; i < mOriginalCSVcontent.size(); i++) {
            String[] lineRead = mOriginalCSVcontent.get(i).split(CSV_SEPARATOR);
            
            String timeStamp  = lineRead[0];

            
//          Calculate Delta_ECEF
            double deltaX = refX - Double.parseDouble(lineRead[4]);
            double deltaY = refY - Double.parseDouble(lineRead[5]);
            double deltaZ = refZ - Double.parseDouble(lineRead[6]);
            
            try {
            
            de_original_values[i] = Double.parseDouble(lineRead[15]);
            dn_original_values[i] = Double.parseDouble(lineRead[16]);
            du_original_values[i] = Double.parseDouble(lineRead[17]);
            
            averageENUmeters[0] += Double.parseDouble(lineRead[15]);
            averageENUmeters[1] += Double.parseDouble(lineRead[16]);
            averageENUmeters[2] += Double.parseDouble(lineRead[17]);
            
            }catch (java.lang.ArrayIndexOutOfBoundsException err) {
                contLineReadingErrors++;
                continue;
            }
            
            //3D error:
            Error_3D_ENU_epoch =    de_original_values[i] * de_original_values[i] +
                                    dn_original_values[i] * dn_original_values[i] +
                                    du_original_values[i] * du_original_values[i];
            
            Error_3D_total += Error_3D_ENU_epoch;
           
            Error_3D_ENU_epoch = Math.sqrt(Error_3D_ENU_epoch);
            
            System.out.print  (timeStamp + "; " + deltaX + "; " + deltaY + "; " + deltaZ + "; " );
            System.out.println(de_original_values[i] + "; " +
                               dn_original_values[i] + "; " +
                               du_original_values[i] + "; " +
                               Error_3D_ENU_epoch);
            
        }
        
        System.out.println("\nErrors:" + contLineReadingErrors);

        
        averageENUmeters[0] = averageENUmeters[0] / mOriginalCSVcontent.size();
        averageENUmeters[1] = averageENUmeters[1] / mOriginalCSVcontent.size();
        averageENUmeters[2] = averageENUmeters[2] / mOriginalCSVcontent.size();
        
        System.out.println("\n");
        System.out.println("Average_DE; Average_DN; Average_DU" );
        System.out.println(averageENUmeters[0] + "; " + averageENUmeters[1] + "; " + averageENUmeters[2]);
        
        // Computing the STDs        
        for (int i = 0; i < de_original_values.length; i++) {
            stdENUmeters[0] += (de_original_values[i] - averageENUmeters[0]) * (de_original_values[i] - averageENUmeters[0]);
            stdENUmeters[1] += (dn_original_values[i] - averageENUmeters[1]) * (dn_original_values[i] - averageENUmeters[1]);
            stdENUmeters[2] += (du_original_values[i] - averageENUmeters[2]) * (du_original_values[i] - averageENUmeters[2]);
        }
        
        stdENUmeters[0] = Math.sqrt(stdENUmeters[0] / stdENUmeters.length - 1);
        stdENUmeters[1] = Math.sqrt(stdENUmeters[1] / stdENUmeters.length - 1);
        stdENUmeters[2] = Math.sqrt(stdENUmeters[2] / stdENUmeters.length - 1);
        
        System.out.println("");
        
        System.out.println("STD_DE; STD_DN; STD_DU" );
        System.out.println(stdENUmeters[0] + "; " + stdENUmeters[1] + "; " + stdENUmeters[2]);
                
        // Computing the RMSE
        RMSE_ENU_total = Math.sqrt( (Error_3D_total/mOriginalCSVcontent.size()) /
                                           mOriginalCSVcontent.size()
                         );
        
        System.out.println("\n");
        System.out.println("RMSE_ENU_meters");
        System.out.println(RMSE_ENU_total);
                
    }
    
}
