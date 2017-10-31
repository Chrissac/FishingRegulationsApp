package prefs;

import java.util.Date;

/**
 * Created by csacripante on 31/10/2017.
 */

public class GeoMappingsList {
    public int GeoId;
    public String GeoName;
    public String GeoType;
    public String GeoException;
    public Date StartDate;
    public Date EndDate;
    public float Latitude;
    public float Longitude;
    public int MappingOrder;


    public GeoMappingsList(int geoId, String geoName, String geoType, String geoException, Date startDate, Date endDate, float latitude, float longitude, int mappingOrder) {
        GeoId = geoId;
        GeoName = geoName;
        GeoType = geoType;
        GeoException = geoException;
        StartDate = startDate;
        EndDate = endDate;
        Latitude = latitude;
        Longitude = longitude;
        MappingOrder = mappingOrder;
    }
}
