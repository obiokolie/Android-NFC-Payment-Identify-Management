package com.finidata.nfcwriter;

/**
 * Created by OBINNA OKOLIE on 8/16/2017.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
/**
 * Created by prabeesh on 7/14/2015.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {
    AlertDialog alertDialog;
    Context ctx;
    BackgroundTask(Context ctx)
    {
        this.ctx =ctx;
    }
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information....");
    }
    @Override
    protected String doInBackground(String... params) {
        String reg_url = "http://eketem.com/update.php";
        String tx_url = "http://eketem.com/updateTransaction.php";
        String login_url = "http://eketem.com/webapp/login.php";
        String method = params[0];
        if (method.equals("newUser")) {
            String userID = params[1];
            String userFirstName = params[2];
            String userLastName = params[3];
            String userEmail = params[4];
            String userOccupation = params[5];
            String userLocation = params[6];
            String userGender = params[7];
            String userPhone = params[8];
            String userPicture = params[9];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8") + "&" +
                        URLEncoder.encode("userFirstName", "UTF-8") + "=" + URLEncoder.encode(userFirstName, "UTF-8") + "&" +
                        URLEncoder.encode("userLastName", "UTF-8") + "=" + URLEncoder.encode(userLastName, "UTF-8") + "&" +
                        URLEncoder.encode("userEmail", "UTF-8") + "=" + URLEncoder.encode(userEmail, "UTF-8") + "&" +
                        URLEncoder.encode("userOccupation", "UTF-8") + "=" + URLEncoder.encode(userOccupation, "UTF-8") + "&" +
                        URLEncoder.encode("userLocation", "UTF-8") + "=" + URLEncoder.encode(userLocation, "UTF-8") + "&" +
                        URLEncoder.encode("userGender", "UTF-8") + "=" + URLEncoder.encode(userGender, "UTF-8") + "&" +
                        URLEncoder.encode("userPhone", "UTF-8") + "=" + URLEncoder.encode(userPhone, "UTF-8") + "&" +
                        URLEncoder.encode("userPicture", "UTF-8") + "=" + URLEncoder.encode(userPicture, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                //httpURLConnection.connect();
                httpURLConnection.disconnect();
                return "Registration Success...";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("newTx"))
        {
            String txID = params[1];
            String txDate = params[2];
            String txItem = params[3];
            String txQty = params[4];
            String txDevice = params[5];
            String txUserID = params[6];

            try {
                URL url = new URL(tx_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String data = URLEncoder.encode("txID", "UTF-8") + "=" + URLEncoder.encode(txID, "UTF-8") + "&" +
                        URLEncoder.encode("txDate", "UTF-8") + "=" + URLEncoder.encode(txDate, "UTF-8") + "&" +
                        URLEncoder.encode("txItem", "UTF-8") + "=" + URLEncoder.encode(txItem, "UTF-8") + "&" +
                        URLEncoder.encode("txQty", "UTF-8") + "=" + URLEncoder.encode(txQty, "UTF-8") + "&" +
                        URLEncoder.encode("txDevice", "UTF-8") + "=" + URLEncoder.encode(txDevice, "UTF-8") + "&" +
                        URLEncoder.encode("txUserID", "UTF-8") + "=" + URLEncoder.encode(txUserID, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                //httpURLConnection.connect();
                httpURLConnection.disconnect();
                return "Registration Success...";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String result) {
        if(result.equals("Registration Success..."))
        {
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        }
        else
        {
            alertDialog.setMessage(result);
            alertDialog.show();
        }
    }
}