package pkp.faisal.prawathiyaplaces.Function;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pkp.faisal.prawathiyaplaces.Model.PlacesModel;
import pkp.faisal.prawathiyaplaces.R;

/**
 * Created by Faisal on 12/12/2016.
 */

public class HttpRequest {
    private Context ctx;
    private static final int MY_SOCKET_TIMEOUT_MS = 600000;

    public HttpRequest(Context context) {
        ctx = context;
    }

    private void httpHandler(
            int method,
            String url,
            final Map map,
            final SuccessCallback callback1,
            final ErrorCallback callback2) {

        StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback1.onHttpPostSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback2.onHttpPostError(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

//    public void login(
//            LoginModel login,
//            final SuccessCallback callback1,
//            final ErrorCallback callback2
//    ) {
//        String url = ctx.getString(R.string.loginUrl);
//
//        String user, pass, token, imei;
//        MCrypt mCrypt = new MCrypt();
//
//        try {
//            user = MCrypt.bytesToHex(mCrypt.encrypt(login.username));
//            pass = MCrypt.bytesToHex(mCrypt.encrypt(login.password));
//            imei = MCrypt.bytesToHex(mCrypt.encrypt(login.imei));
//        } catch (Exception e) {
//            user = "";
//            pass = "";
//            imei = "";
//        }
//
//        try {
//            token = MCrypt.bytesToHex(mCrypt.encrypt(login.token));
//        } catch (Exception e) {
//            token = "";
//        }
//
//        Map<String, String> map = new HashMap<>();
//        map.put("username", user);
//        map.put("password", pass);
//        map.put("imei", imei);
//        map.put("token", token);
//        httpHandler(url, map, new SuccessCallback() {
//            @Override
//            public void onHttpPostSuccess(String result) {
//                MCrypt mCrypt = new MCrypt();
//                String decrypted;
//                try {
//                    decrypted = new String(mCrypt.decrypt(result.trim()));
//                } catch (Exception e) {
//                    decrypted = null;
//                    e.printStackTrace();
//                }
//                callback1.onHttpPostSuccess(decrypted);
//            }
//        }, new ErrorCallback() {
//            @Override
//            public void onHttpPostError(VolleyError error) {
//                callback2.onHttpPostError(error);
//            }
//        });
//    }

    public void getTag(
            String type,
            int radius,
            LatLng latLng,
            final SuccessCallback callback1,
            final ErrorCallback callback2
    ) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLng.latitude + "," + latLng.longitude + "&radius=" + radius + "&type=" + type + "&key=AIzaSyBhMituntC6WacfVJ20kWZpa4gfxFD8w9w";

        Map<String, String> map = new HashMap<>();
        httpHandler(Request.Method.GET, url, map, new SuccessCallback() {
            @Override
            public void onHttpPostSuccess(String result) {
                callback1.onHttpPostSuccess(result);
            }
        }, new ErrorCallback() {
            @Override
            public void onHttpPostError(VolleyError error) {
                callback2.onHttpPostError(error);
            }
        });
    }

    public void nearby(
            LatLng latLng,
            int radius,
            final SuccessCallback callback1,
            final ErrorCallback callback2
    ) {
        String url = ctx.getString(R.string.base_url) + "/nearby_places/loc=" + latLng.latitude + "," + latLng.longitude + "/radius=" + radius;

        Map<String, String> map = new HashMap<>();
        httpHandler(Request.Method.GET, url, map, new SuccessCallback() {
            @Override
            public void onHttpPostSuccess(String result) {
                callback1.onHttpPostSuccess(result);
            }
        }, new ErrorCallback() {
            @Override
            public void onHttpPostError(VolleyError error) {
                callback2.onHttpPostError(error);
            }
        });
    }

    public void savePlaces(
            PlacesModel data,
            final SuccessCallback callback1,
            final ErrorCallback callback2
    ) {
        String url = ctx.getString(R.string.base_url) + "/master_places";

        Map<String, String> map = new HashMap<>();
        map.put("google_place_id", data.Uid);
        map.put("name", data.Name);
        map.put("vicinity", data.Vicinity);
        map.put("photo_url", data.PhotoUrl);
        map.put("master_category_id", data.MasterCategoryId);
        map.put("lat", data.Lat);
        map.put("lon", data.Lon);
        httpHandler(Request.Method.POST, url, map, new SuccessCallback() {
            @Override
            public void onHttpPostSuccess(String response) {

                if (response != null && response.length() > 0) {
                    callback1.onHttpPostSuccess(response);
                } else {
                    Log.e("", "");
                }
            }
        }, new ErrorCallback() {
            @Override
            public void onHttpPostError(VolleyError error) {
                callback2.onHttpPostError(error);
            }
        });
    }


    public void getToken() {

    }

    public interface SuccessCallback {
        void onHttpPostSuccess(String result);
    }

    public interface ErrorCallback {
        void onHttpPostError(VolleyError error);
    }


}
