package prefs;

import android.graphics.Point;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
/**
 * Created by csacripante on 20/07/2017.
 */

public class CommonFunctions {
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD7bf9O7_kTD1BuNVM9X4Zk5OKTfvAJRq0";
    public static String PassWordMd5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static int validate(String email, String password) {

        String Email = email.toString();
        String Password = password.toString();

        if (Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            return 1;
        }

        if (Password.isEmpty() || password.length() < 4 || Password.length() > 10) {
            return 2;
        }
        return 0;
    }
    public static int validate(String email) {

        String Email = email.toString();

        if (Email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            return 1;
        }
        return 0;
    }
    public static String generatePassword() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        uuid = uuid.substring(0,7);
        return uuid;
    }


    private double area(ArrayList<LatLng> arr) {
        double area=0;
        int nPts = arr.size();
        int j=nPts-1;
        LatLng p1; LatLng p2;
        for (int i=0;i<nPts;j=i++) {

            p1=arr.get(i); p2=arr.get(j);
            area+=p1.latitude*p2.longitude;
            area-=p1.longitude*p2.latitude;
        }
        area/=2;

        return area;
    };

    public static LatLng Centroid (ArrayList<LatLng> pts) {
        int nPts = pts.size();
        double x=0; double y=0;
        double f;
        int j=nPts-1;
        LatLng p1; LatLng p2;

        for (int i=0;i<nPts;j=i++) {
            p1=pts.get((i)); p2=pts.get(j);
            f=p1.latitude*p2.longitude-p2.latitude*p1.longitude;
            x+=(p1.latitude+p2.latitude)*f;
            y+=(p1.longitude+p2.longitude)*f;
        }

        f=area(pts)*6;

        return new LatLng(x/f, y/f);
    };
}
