package prefs;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class GetGeoLocations extends StringRequest {
    private static final String GeoRequest_REQUEST_URL = "http://fishingregulationdb.000webhostapp.com/GetGeoLocations.php";
    private Map<String, String> params;

    public GetGeoLocations( Response.Listener<String> listener) {
        super(Method.POST, GeoRequest_REQUEST_URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
