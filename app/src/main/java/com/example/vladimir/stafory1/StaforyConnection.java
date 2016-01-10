package com.example.vladimir.stafory1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.net.Uri;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import static android.support.v4.widget.SearchViewCompat.getQuery;

/**
 * Created by Владимир on 09.11.2015.
 */
public class StaforyConnection {

    static  final String COOKIES_HEADER = "Set-Cookie";
    static  CookieManager msCookieManager = new CookieManager();
    static Document html;
    static Document html1;
    static Document html2;
    static Document html3;
    static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

    HttpsURLConnection conn;

    static void runparse(){
        try{
            html = Jsoup.parse(MainActivity.staforyConnection.getContentuser("https://stafory.com/kcabinet/kstandart/marketplace.html"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
    static void runparseall(){
        try{
            html1 = Jsoup.parse(MainActivity.staforyConnection.getContentuser("https://stafory.com/messages.html"));
            html2 = Jsoup.parse(MainActivity.staforyConnection.getContentuser("https://stafory.com/catalog/magents.html"));
            html3 = Jsoup.parse(MainActivity.staforyConnection.getContentuser("https://stafory.com/kcabinet/profile.html?Candidates[filterShowCandidate]=rezervSeekers#templates"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
    static void addBitmap(){
        Elements divs = html2.getElementsByClass("agent-item");
        for (int i = 0; i < divs.size(); i++) {
            bitmaps.add(getBitmapFromURL("https://stafory.com" + divs.get(i).getElementsByTag("img").attr("src")));
            System.out.println(i);
        }
    }

    public String getInputStream2(String urlStr, String user, String password) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        URL url = new URL(urlStr);
        conn = (HttpsURLConnection) url.openConnection();

        // Create the SSL connection
        SSLContext sc;
        sc = SSLContext.getInstance("TLS");
        sc.init(null, null, new java.security.SecureRandom());
        conn.setSSLSocketFactory(sc.getSocketFactory());

        conn.setReadTimeout(17000);
        conn.setConnectTimeout(17000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        Uri.Builder query = new Uri.Builder()
                .appendQueryParameter("LoginForm[rememberMe]", "0")
                .appendQueryParameter("LoginForm[username]", user)
                .appendQueryParameter("LoginForm[password]", password);
        String query2 = query.build().getEncodedQuery();
        OutputStream os2 = conn.getOutputStream();
        BufferedWriter writer2 = new BufferedWriter(
                new OutputStreamWriter(os2, "UTF-8"));
        writer2.write(query2);
        writer2.flush();
        writer2.close();
        os2.close();
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content2 = new StringBuilder();
        String line2;
        while ((line2 = bufferedReader2.readLine()) != null)
        {
            content2.append(line2 + "\n");
        }

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
        if(cookiesHeader != null)
        {
            for (String cookie : cookiesHeader)
            {
                msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
            }
        }
        return content2.toString();
    }
        static String getContentuser (String urlStr) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(17000);
        conn.setConnectTimeout(17000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        if(msCookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie",
                    TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
        }


        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content2 = new StringBuilder();
        String line2;
        while ((line2 = bufferedReader2.readLine()) != null)
        {
            content2.append(line2 + "\n");
        }
        return content2.toString();
    }


    static InputStream getBitmapStram (String urlStr) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(17000);
        conn.setConnectTimeout(17000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        if(msCookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie",
                    TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
        }



        return conn.getInputStream();
    }


    public static Bitmap getBitmapFromURL(String src){
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(StaforyConnection.getBitmapStram(src));
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void sendMesgs (String urlStr, String messages) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        URL url = new URL(urlStr);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(17000);
        conn.setConnectTimeout(17000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        if(msCookieManager.getCookieStore().getCookies().size() > 0) {
            conn.setRequestProperty("Cookie",
                    TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
        }
        Uri.Builder query = new Uri.Builder()
                .appendQueryParameter("Messages[message]", messages)
                .appendQueryParameter("Messages[file]", "");
        String query2 = query.build().getEncodedQuery();
        OutputStream os2 = conn.getOutputStream();
        BufferedWriter writer2 = new BufferedWriter(
                new OutputStreamWriter(os2, "UTF-8"));
        writer2.write(query2);
        writer2.flush();
        writer2.close();
        os2.close();


        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content2 = new StringBuilder();
        String line2;
        while ((line2 = bufferedReader2.readLine()) != null)
        {
            content2.append(line2 + "\n");
        }
    }
}
