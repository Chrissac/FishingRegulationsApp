package prefs;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import prefs.Utils.*;

public class PHPRequests extends StringRequest {
    private static final String RECOVERY_REQUEST_URL = "http://fishingregulationdb.000webhostapp.com/ForgotPasswordRecoveryCode.php";
    private Map<String, String> params;

    public PHPRequests(String email, String recoverycode,String password, String REQUEST_URL, Response.Listener<String> listener) {
        super(Method.POST, REQUEST_URL, listener, null);
        params = new HashMap<>();
        switch(REQUEST_URL) {
           case Utils.RECOVERY_REQUEST_URL :
               params.put("email", email);
               params.put("recoverycode", recoverycode);
               break;
            case Utils.PASSWORD_UPDATE_REQUEST_URL :
                params.put("email", email);
                params.put("recoverycode", recoverycode);
                params.put("password", password);
                break;
        }
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }


}
