package com.rogerio.scripts_conversao;

/**
 *
 * @author Rogerio
 */
public class KML_Creator {
    
    private String mKMLheader;
    private StringBuilder mKMLcontent;
        
    public KML_Creator(String fileName) {
        this.mKMLheader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                     + "<kml xmlns=\"http://www.opengis.net/kml/2.2\""
                     + " xmlns:gx=\"http://www.google.com/kml/ext/2.2\""
                     + " xmlns:kml=\"http://www.opengis.net/kml/2.2\""
                +      " xmlns:atom=\"http://www.w3.org/2005/Atom\">"
                + "<Document><name>TAG_NAME</name>"
                + "<Placemark>\n" 
                + "<gx:Track>"
                + "\n"
                + "\n";
        
        this.mKMLheader = this.mKMLheader.replace("TAG_NAME", fileName);
        //TODO: Use Java Wildcards instead
                
        mKMLcontent = new StringBuilder();
        
        mKMLcontent.append(this.mKMLheader);
    }
    
    public void add_PointToKML (String timeStamp, double latDegrees, double lonDegrees, double altMeters) {
        String newLine = "<when>" + timeStamp + "</when>\n" +
                         "<gx:coord>" + lonDegrees +" "+ latDegrees +" "+ altMeters+ "</gx:coord>";
        mKMLcontent.append(newLine);
        mKMLcontent.append("\n"); //TODO Check for the last point!
        mKMLcontent.append("\n"); //TODO Check for the last point!
    }
 
    public void finalizeKMLfile () {
        String lastLine = "</gx:Track>\n" +
                          "</Placemark></Document></kml>";
        mKMLcontent.append(lastLine);
    }
    
    public String getKMLfileContentAsString (){
        return this.mKMLcontent.toString();
    }
    
    public StringBuilder getKMLfileContentAsStringBuilder() {
         return this.mKMLcontent;
    }
}
