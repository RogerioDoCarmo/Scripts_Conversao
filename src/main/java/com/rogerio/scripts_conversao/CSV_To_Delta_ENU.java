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
public class CSV_To_Delta_ENU {
    
    private String mHeader;
    private ArrayList<String> mOriginalCSVcontent;
    private ArrayList<String> fileContent;
    
    private final String CSV_SEPARATOR = ",";
    
    //UNESP_PP_EP01;
    double refX =  3687632.9057;
    double refY = -4620673.7618;
    double refZ = -2387161.02283;
    
    double[] averageENUmeters;
    double[] stdENUmeters;
    double Error_3D_ENU_epoch;
    double Error_3D_total;
    double RMSE_ENU_total;
    
    public CSV_To_Delta_ENU(ArrayList<String> originalContent) {
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
        
        for (int i = 1; i < mOriginalCSVcontent.size(); i++) {
            String[] lineRead = mOriginalCSVcontent.get(i).split(CSV_SEPARATOR);
            
            String timeStamp  = lineRead[0];
            double latDegrees = Double.valueOf(lineRead[1]);
            double lonDegrees = Double.valueOf(lineRead[2]);
            double altMeters  = Double.valueOf(lineRead[3]);
            
//            System.out.println(mOriginalCSVcontent.get(i));

            //Convert to ECEF
            Ecef2LlaConverter.GeodeticLlaValues llaCoord 
                    = new Ecef2LlaConverter.GeodeticLlaValues(Math.toRadians(latDegrees),
                            Math.toRadians(lonDegrees),
                            altMeters);
            
            double[] ecefValues = Lla2EcefConverter.convertFromLlaToEcefMeters(llaCoord);
            
//          Calculate Delta_ECEF
            double deltaX = refX - ecefValues[0];
            double deltaY = refY - ecefValues[1];
            double deltaZ = refZ - ecefValues[2];
           

//          Calculate Delta_ENU
            // Discrepancies in Local System (E,N,U) coordinates
            EnuValues enuDiscrepancies = Ecef2EnuConverter.
                    convertEcefToEnu(deltaX,
                                     deltaY,
                                     deltaZ,
                                     latDegrees,
                                     lonDegrees);
            
            de_original_values[i] = enuDiscrepancies.enuEast;
            dn_original_values[i] = enuDiscrepancies.enuNorth;
            du_original_values[i] = enuDiscrepancies.enuUP;
            
            averageENUmeters[0] += enuDiscrepancies.enuEast;
            averageENUmeters[1] += enuDiscrepancies.enuNorth;
            averageENUmeters[2] += enuDiscrepancies.enuUP ;
            
            //3D error:
            Error_3D_ENU_epoch =     enuDiscrepancies.enuEast*enuDiscrepancies.enuEast +
                                     enuDiscrepancies.enuNorth*enuDiscrepancies.enuNorth +
                                     enuDiscrepancies.enuUP*enuDiscrepancies.enuUP;
            
            Error_3D_total += Error_3D_ENU_epoch;
            
            Error_3D_ENU_epoch = Math.sqrt(Error_3D_ENU_epoch);
            
            System.out.print  (timeStamp + "; " + deltaX + "; " + deltaY + "; " + deltaZ + "; " );
            System.out.println(enuDiscrepancies.enuEast  + "; " +
                               enuDiscrepancies.enuNorth + "; " +
                               enuDiscrepancies.enuUP    + "; " +
                               Error_3D_ENU_epoch);
        }
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
