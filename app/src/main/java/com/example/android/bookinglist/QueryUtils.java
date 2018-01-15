package com.example.android.bookinglist;

import android.content.Context;
import android.icu.lang.UCharacter;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

public final class QueryUtils {

    private QueryUtils() {

    }

    public static ArrayList<ListBook> extractListBooks(String bURL) {
        ArrayList<ListBook> listBooks = new ArrayList<>();
        ArrayList<String> bookAuthor = new ArrayList<>();
        String sBookAuthor = "";
        try {
            JSONObject root = new JSONObject(bURL);
            JSONArray items = root.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject listBook = items.getJSONObject(i);
                JSONObject volumInfo = listBook.getJSONObject("volumeInfo");
                JSONArray authors = volumInfo.getJSONArray("authors");
                String bookName = volumInfo.getString("title");
                for (int j = 0; j < authors.length(); j++) {
                    bookAuthor.add(authors.getString(j));
                }
                sBookAuthor = bookAuthor.get(i);
                listBooks.add(new ListBook(bookName, sBookAuthor));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBooks;
    }

    private static URL createUrl(String url) throws MalformedInputException {
        URL bURL = null;
        try {
            bURL = new URL(url);
        } catch (MalformedURLException e) {
            Log.e("inquaryutil", "error with url", e);
            return null;
        }
        return bURL;
    }

    public static String makeHttpRequaste(URL burl) throws  IOException {
        String jsonResponse = "";
        if (burl == null) {
            return jsonResponse;
        }
        InputStream inputstream = null;
        HttpURLConnection urlconnection = null;
        try {
            urlconnection = (HttpURLConnection) burl.openConnection();
            urlconnection.setRequestMethod("GET");
            urlconnection.setReadTimeout(10000);
            urlconnection.setConnectTimeout(15000);
            urlconnection.connect();
            if (urlconnection.getResponseCode() == 200) {
                inputstream = urlconnection.getInputStream();
                jsonResponse = readFromStream(inputstream);
            }
        } catch (IOException e) {
            Log.e("inquaryutil", "error with the server", e);
        } finally {
            if (urlconnection != null) {
                urlconnection.disconnect();
            }
            if (inputstream != null) {
                inputstream.close();
            }

        }
        return jsonResponse;
    }

    public static String readFromStream(InputStream inputstream) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputstream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputstream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            Log.e("inquaryutil", "errorfrom file", e);
        }
        return output.toString();
    }

    public static ArrayList<ListBook> fetchListbookData(String requestUrl) throws MalformedInputException {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequaste(url);
        } catch (IOException e) {
            Log.e("query", "Problem with the HTTP request.", e);
        }

        ArrayList<ListBook> listBooks = extractListBooks(jsonResponse);

        return listBooks;
    }
}

