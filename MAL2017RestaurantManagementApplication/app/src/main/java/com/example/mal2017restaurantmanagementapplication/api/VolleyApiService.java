package com.example.mal2017restaurantmanagementapplication.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyApiService {
    private static final String TAG = "VolleyApiService";
    private static VolleyApiService instance;
    private RequestQueue requestQueue;
    private Context context;

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public interface UsersCallback {
        void onSuccess(JSONArray users);
        void onError(String error);
    }

    private VolleyApiService(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(this.context);
    }

    public static synchronized VolleyApiService getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyApiService(context);
        }
        return instance;
    }

    // 用户登录
    public void login(String email, String password, final ApiCallback callback) {
        // 首先获取所有用户
        getAllUsers(new UsersCallback() {
            @Override
            public void onSuccess(JSONArray users) {
                try {
                    // 查找匹配的用户
                    JSONObject matchedUser = null;
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        String userEmail = user.optString("email", "");
                        String userPassword = user.optString("password", "");
                        String userType = user.optString("usertype", "").toLowerCase();

                        if (userEmail.equalsIgnoreCase(email) && userPassword.equals(password)) {
                            matchedUser = user;

                            // 创建登录响应
                            JSONObject response = new JSONObject();
                            response.put("user_id", user.optString("username", ""));
                            response.put("name", user.optString("firstname", "") + " " + user.optString("lastname", ""));
                            response.put("email", email);
                            response.put("role", userType);

                            callback.onSuccess(response);
                            return;
                        }
                    }

                    // 如果没有找到匹配的用户
                    callback.onError("Invalid email or password");

                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    callback.onError("Login failed");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Connection failed: " + error);
            }
        });
    }

    // 获取所有用户
    public void getAllUsers(final UsersCallback callback) {
        String url = ApiConfig.BASE_URL + "/read_all_users/" + ApiConfig.STUDENT_ID;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("users")) {
                                JSONArray users = response.getJSONArray("users");
                                callback.onSuccess(users);
                            } else {
                                callback.onError("No users found");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            callback.onError("Failed to parse response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 添加这行，然后在 Logcat 搜索 "VolleyError"
                        Log.e("VolleyError", "Error: " + error.toString());
                        callback.onError("Network error: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // 设置重试策略
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10秒超时
                0, // 不重试
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // 创建用户
    public void createUser(JSONObject userData, final ApiCallback callback) {
        String url = ApiConfig.BASE_URL + "/create_user/" + ApiConfig.STUDENT_ID;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                userData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Create user failed";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMessage = new String(error.networkResponse.data);
                        }
                        callback.onError(errorMessage);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    // 创建学生数据库（如果还没创建）
    public void createStudentDatabase(final ApiCallback callback) {
        String url = ApiConfig.BASE_URL + "/create_student/" + ApiConfig.STUDENT_ID;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 如果数据库已经存在，可能返回错误，但我们不视为失败
                        callback.onSuccess(new JSONObject());
                    }
                }
        );

        requestQueue.add(request);
    }
}