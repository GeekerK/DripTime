package com.geekerk.driptime.utils;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import com.geekerk.driptime.vo.EventBean;
import com.geekerk.driptime.vo.ListBean;
import com.geekerk.driptime.vo.UserBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

/**
 * 解析json数据,生成json数据
 * Created by s21v on 2016/6/17.
 */
public class JsonUtil {
    private static final String TAG = "JsonUtil";

    //生成userBean的json
    public static String getUserBeanJson(UserBean userBean) {
        String result = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, Charset.forName("utf-8")));
        JsonWriter jsonWriter = new JsonWriter(bufferedWriter);
        try {
            jsonWriter.beginObject();
            jsonWriter.name("msgCode").value(1);    //消息码
            jsonWriter.name("msg").value("操作成功");   //消息
            jsonWriter.name("userBean").beginObject();  //UserBean
            jsonWriter.name("userId").value(userBean.getId());
            jsonWriter.name("userName").value(userBean.getName());
            jsonWriter.name("userEmail").value(userBean.getEmail());
            jsonWriter.name("password").value(userBean.getPassword());
            jsonWriter.endObject();
            jsonWriter.endObject();
            jsonWriter.flush();
            //---------------- 测试读取 ------------------
            getUserBeanFromJson(byteArrayOutputStream.toByteArray());
            //---------------- end ----------------------
            result = byteArrayOutputStream.toString("utf-8");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                jsonWriter.close();
                jsonWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //从Json输入流中读取数据，并生成UserBean
    public static UserBean getUserBeanFromJson (byte[] jsonInput) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonInput);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(byteArrayInputStream, Charset.forName("utf-8")));
        int msgCode = 0;
        String msg = "";
        UserBean userBean = null;
        try {
            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
                String elementName = jsonReader.nextName();
                if ("msgCode".equals(elementName))
                    msgCode = jsonReader.nextInt();
                else if ("msg".equals(elementName))
                    msg = jsonReader.nextString();
                else if ("userBean".equals(elementName)) {
                    jsonReader.beginObject();
                    userBean = new UserBean();
                    while(jsonReader.hasNext()) {
                        String elementName1 = jsonReader.nextName();
                        if ("userId".equals(elementName1))
                            userBean.setId(jsonReader.nextInt());
                        else if ("userName".equals(elementName1))
                            userBean.setName(jsonReader.nextString());
                        else if ("userEmail".equals(elementName1))
                            userBean.setEmail(jsonReader.nextString());
                        else if ("password".equals(elementName1))
                            userBean.setPassword(jsonReader.nextString());
                    }
                    jsonReader.endObject();
                }
            }
            jsonReader.endObject();
            Log.i(TAG, "getUserBeanFromJson : msgCode:"+msgCode+" , msg:"+msg+" , userBean:"+userBean.toString());
        } catch (IOException e) {
            Log.i(TAG, "getUserBeanFromJson ：msgCode:"+msgCode+" , msg:"+msg);
            e.printStackTrace();
        } finally {
            try {
                jsonReader.close();
                jsonReader = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userBean;
    }

    //生成单个ListBean的json数据
    public static String getListBeanJson (ListBean listBean) {
        String result = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, Charset.forName("utf-8"))));
        try {
            jsonWriter.beginObject();
            jsonWriter.name("msgCode").value(1);    //消息码
            jsonWriter.name("msg").value("操作成功");   //消息
            jsonWriter.name("listBean").beginObject();  //ListBean
            jsonWriter.name("listId").value(listBean.getId());
            jsonWriter.name("listName").value(listBean.getName());
            jsonWriter.name("isClosed").value(listBean.isClosed());
            jsonWriter.name("userID").value(listBean.getUser().getId());
            jsonWriter.endObject();
            jsonWriter.endObject();
            jsonWriter.flush();
            //------------ 测试读取 ------------
            JsonUtil.getListBeanFromJson(byteArrayOutputStream.toByteArray());
            //------------ end ----------------
            result = byteArrayOutputStream.toString("utf-8");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                jsonWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonWriter = null;
        }
        return result;
    }

    //从Json输入流中解析ListBean
    public static ListBean getListBeanFromJson (byte[] jsonInput) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonInput);
        JsonReader jsonReader = new JsonReader(new InputStreamReader(byteArrayInputStream, Charset.forName("utf-8")));
        int msgCode = 0;
        String msg = "";
        ListBean listBean = null;
        try {
            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
                String elementName = jsonReader.nextName();
                if ("msgCode".equals(elementName))
                    msgCode = jsonReader.nextInt();
                else if ("msg".equals(elementName))
                    msg = jsonReader.nextString();
                else if ("listBean".equals(elementName)) {
                    jsonReader.beginObject();
                    listBean = new ListBean();
                    while(jsonReader.hasNext()) {
                        String elementName1 = jsonReader.nextName();
                        if ("listId".equals(elementName1))
                            listBean.setId(jsonReader.nextInt());
                        else if ("listName".equals(elementName1))
                            listBean.setName(jsonReader.nextString());
                        else if ("isClosed".equals(elementName1))
                            listBean.setClosed(jsonReader.nextBoolean());
                        else if ("userID".equals(elementName1)) {
                            listBean.setUser(new UserBean(jsonReader.nextInt()));
                        }
                    }
                    jsonReader.endObject();
                }
            }
            jsonReader.endObject();
            Log.i(TAG, "getListBeanFromJson : msgCode:"+msgCode+" , msg:"+msg+" , ListBean:"+listBean.toString());
        } catch (IOException e) {
            Log.i(TAG, "getListBeanFromJson ：msgCode:"+msgCode+" , msg:"+msg);
            e.printStackTrace();
        } finally {
            try {
                jsonReader.close();
                jsonReader = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //生成ListBean数组的json
    public static String getListBeanArrayJson (ArrayList<ListBean> lists) {
        String result = null;
        ByteArrayOutputStream byteArrayOutPutStream = new ByteArrayOutputStream(1024);
        JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(byteArrayOutPutStream, Charset.forName("utf-8"))));
        try {
            jsonWriter.beginObject();
            jsonWriter.name("msgCode").value(1);    //消息码
            jsonWriter.name("msg").value("操作成功");   //消息
            jsonWriter.name("listBeanArray").beginArray();  //ListBean数组
            for (ListBean listBean : lists) {
                jsonWriter.beginObject();
                jsonWriter.name("listId").value(listBean.getId());
                jsonWriter.name("listName").value(listBean.getName());
                jsonWriter.name("isClosed").value(listBean.isClosed());
                jsonWriter.name("userID").value(listBean.getUser().getId());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
            jsonWriter.flush();
            // -----------  测试读取 -----------
            JsonUtil.getListBeanArrayFromJson(byteArrayOutPutStream.toByteArray());
            // -----------  end ---------------
            result = byteArrayOutPutStream.toString("utf-8");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                jsonWriter.close();
                jsonWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //从Json输入流中获得ListBean的数组
    public static ArrayList<ListBean> getListBeanArrayFromJson(byte[] jsonInput) {
        int msgCode;
        String msg;
        ArrayList<ListBean> result = null;
        JSONTokener jsonTokener = new JSONTokener(new String(jsonInput, Charset.forName("utf-8")));
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            msgCode = jsonObject.getInt("msgCode");
            msg = jsonObject.getString("msg");
            JSONArray jsonArray = jsonObject.getJSONArray("listBeanArray");
            result = new ArrayList<>();
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject listJson = jsonArray.getJSONObject(i);
                ListBean listbean = new ListBean(listJson.getInt("listId"));
                listbean.setName(listJson.getString("listName"));
                listbean.setClosed(listJson.getBoolean("isClosed"));
                listbean.setUser(new UserBean(listJson.getInt("userID")));
                result.add(listbean);
            }
            Log.i(TAG, "getListBeanFromJson : msgCode:"+msgCode+" , msg:"+msg+" , ListBeanArray:"+result.toString());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //生成EventBean的json
    public static String getEventBeanJson(EventBean eventbean) {
        String result = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msgCode", 1);
            jsonObject.put("msg", "操作成功");
            JSONObject eventJson = new JSONObject();
            eventJson.put("id", eventbean.getId());
            eventJson.put("title", eventbean.getTitle());
            eventJson.put("priorityLevel", eventbean.getPriorityLevel());
            eventJson.put("release_time", eventbean.getReleaseTime().getTime());
            if (eventbean.getReleaseTime() != null)     //截止事件可以为空值
                eventJson.put("deadline", eventbean.getDeadline().getTime());
            if(eventbean.getList() != null)     //所属清单可以为空值
                eventJson.put("listId", eventbean.getList().getId());
            eventJson.put("userId", eventbean.getUser().getId());
            jsonObject.put("eventBean", eventJson);
            result = jsonObject.toString();
            // --------- 测试读取 ----------
            JsonUtil.getEventBeanFromJson(result.getBytes());
            // --------- end ----------
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static EventBean getEventBeanFromJson (byte[] jsonInput) {
        int msgCode;
        String msg;
        EventBean result = null;
        try {
            JSONObject jsonObject = new JSONObject(new String(jsonInput));
            msgCode = jsonObject.getInt("msgCode");
            msg = jsonObject.getString("msg");
            JSONObject eventJson = jsonObject.getJSONObject("eventBean");
            result = new EventBean();
            result.setId(eventJson.getInt("id"));
            result.setTitle(eventJson.getString("title"));
            result.setPriorityLevel(eventJson.getInt("priorityLevel"));
            result.setReleaseTime(eventJson.getLong("release_time"));
            try {
                result.setDeadline(eventJson.getLong("deadline"));
            }catch (JSONException e) {
                result.setDeadline(null);   //默认的截止时间
            }
            try {
                result.setList(new ListBean(eventJson.getInt("listId")));
            }catch (JSONException e) {
                result.setList(null);   //默认的清单列表
            }
            result.setUser(new UserBean(eventJson.getInt("userId")));
            Log.i(TAG, "getListBeanFromJson : msgCode:"+msgCode+" , msg:"+msg+" , EventBean:"+result.toString());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //生成EventBean数组的json
    public static String getEventBeanArrayJson (ArrayList<EventBean> events) {
        String result = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msgCode", 1);
            jsonObject.put("msg", "操作成功");
            JSONArray eventListJson = new JSONArray();
            for(EventBean eventbean : events) {
                JSONObject eventJson = new JSONObject();
                eventJson.put("id", eventbean.getId());
                eventJson.put("title", eventbean.getTitle());
                eventJson.put("priorityLevel", eventbean.getPriorityLevel());
                eventJson.put("release_time", eventbean.getReleaseTime().getTime());
                if (eventbean.getReleaseTime() != null)     //截止事件可以为空值
                    eventJson.put("deadline", eventbean.getDeadline().getTime());
                if(eventbean.getList() != null)
                    eventJson.put("listId", eventbean.getList().getId());
                eventJson.put("userId", eventbean.getUser().getId());
                eventListJson.put(eventJson);
            }
            jsonObject.put("eventBeanArray", eventListJson);
            result = jsonObject.toString();
            //-------------- 测试读取 ------------
            JsonUtil.getEventBeanArrayFromJson(result.getBytes());
            //-------------- end -------------
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    //从Json输入流中解析EventBean数组
    public static ArrayList<EventBean> getEventBeanArrayFromJson (byte[] jsonInput) {
        int msgCode;
        String msg;
        ArrayList<EventBean> result = null;
        JSONTokener jsonTokener = new JSONTokener(new String(jsonInput, Charset.forName("utf-8")));
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            msgCode = jsonObject.getInt("msgCode");
            msg = jsonObject.getString("msg");
            JSONArray jsonArray = jsonObject.getJSONArray("eventBeanArray");
            result = new ArrayList<>();
            for(int i=0; i<jsonArray.length(); i++) {
                EventBean eventbean = new EventBean();
                JSONObject eventObject = (JSONObject) jsonArray.get(i);
                eventbean.setId(eventObject.getInt("id"));
                eventbean.setTitle(eventObject.getString("title"));
                eventbean.setPriorityLevel(eventObject.getInt("priorityLevel"));
                eventbean.setReleaseTime(eventObject.getLong("release_time"));
                try {
                    eventbean.setDeadline(eventObject.getLong("deadline"));
                }catch (JSONException e) {
                    eventbean.setDeadline(null);   //默认的截止时间
                }
                try {
                    eventbean.setList(new ListBean(eventObject.getInt("listId")));
                }catch (JSONException e) {
                    eventbean.setList(null);    //默认的清单
                }
                eventbean.setUser(new UserBean(eventObject.getInt("userId")));
                result.add(eventbean);
            }
            Log.i(TAG, "getEventBeanArrayFromJson : msgCode:"+msgCode+" , msg:"+msg+" , EventBeanArray:"+result.toString());
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
