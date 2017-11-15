package prefs;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://www.catchandrelease.xyz/Register.php";
    private Map<String, String> params;

    public RegisterRequest(String username, String email, String password,String loginType, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        params.put("password", password);
        params.put("loginType", loginType);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}