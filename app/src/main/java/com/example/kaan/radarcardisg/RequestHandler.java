package com.example.kaan.radarcardisg;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RequestHandler {

    private static RequestHandler requestHandler = new RequestHandler();

    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
    HttpGet httpGetRequest;

    private RequestHandler(){
    }
    public static RequestHandler getRequestHandler(){
        return requestHandler;
    }

    public int userJSonGetRequest(String url){

        String aJsonString= "";
        String result = prepareRequest(url);

        try {
            JSONObject jObject = new JSONObject(result);
            aJsonString = jObject.getString("Json_Check_UserResult");
            Log.d(aJsonString,aJsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(aJsonString=="")
            aJsonString = "0";

        return Integer.parseInt(aJsonString);

    }

    private String prepareRequest(String url){

        InputStream inputStream = null;
        String result = null;

        try{
            httpGetRequest = new HttpGet(url);

            HttpResponse response = httpclient.execute(httpGetRequest);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.d("Exception",e.toString());
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }

        return result;
    }

    private JSONArray getJson(String requestString,String result){
        JSONArray jsonArray ;
        try {
            JSONObject jObject = new JSONObject(result);
            String newResult = jObject.getString(requestString);
            jsonArray = new JSONArray(newResult);


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonArray;
    }

    public int getTasks(String url, ArrayList<TaskProviderElement> pendingTasks){
        prepareRequest(url);

        JSONArray jsonArray;

        String startDate= "";
        String finishDate= "";
        String listId= "";
        String locationExplanation= "";
        String locationName= "";
        String tkId= "";
        String weekNo="";

        String result = prepareRequest(url);

        try {
            jsonArray = getJson("Get_Hse_JobsResult",result);
            pendingTasks.clear();
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject innerJsonObject =  jsonArray.getJSONObject(0);
                startDate = innerJsonObject.getString("start");
                finishDate = innerJsonObject.getString("finish");
                locationExplanation = innerJsonObject.getString("loc_aciklama");
                locationName = innerJsonObject.getString("loc_adi");
                listId = innerJsonObject.getString("list_id");
                tkId = innerJsonObject.getString("tk_id");
                weekNo = innerJsonObject.getString("weekno");
                pendingTasks.add(new TaskProviderElement(locationName,"HSE",startDate,finishDate,listId,tkId));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTableElements(String url,ArrayList<String> taskData){

        JSONArray jsonArray;

        String result = prepareRequest(url);

        try {
            jsonArray = getJson("Get_Hse_TableResult",result);
            taskData.add("");
            for(int i = 0; i<jsonArray.length();i++){
                JSONObject innerJsonObject =  jsonArray.getJSONObject(i);
                if(innerJsonObject.getInt("isgroup") == 1)
                    taskData.add("|"+innerJsonObject.getString("krt_aciklama"));
                else taskData.add(innerJsonObject.getString("krt_aciklama"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int uploadFile(String sourceFileUri, String upLoadServerUri, int serverResponseCode, final Context context, Activity activity) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {


              activity.runOnUiThread(new Runnable() {
                public void run() {

                }
            });

            return 0;

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=file;filename="
                        + fileName + "" + lineEnd);

                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();

                InputStream inputStream = conn.getInputStream();
                String inputString = convertStreamToString(inputStream);
                JSONObject jObject = new JSONObject(inputString);
                String responseStatus = jObject.getString("status");

                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200 && responseStatus == "true") {

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "İşlem Tamamlandı",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

               activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;
        }
    }

    private  String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
