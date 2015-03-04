package com.chrisgcasey.blog;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ListActivity {
    //String[] mBlogPosts2 = {"red", "green", "blue"};
    ProgressBar mProgressBar;

    //String[] mBlogPosts;
    JSONObject mBlogData;
    public static final int NUMBER_OF_POSTS = 20;
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG2 = "ERROR OCCURRED HERE";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPosts);
        //setListAdapter(adapter);

        //get the website from the user
        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
            getBlogPostsTask.execute();
            Log.i(TAG, "its good");
        } else {
            Log.i(TAG2, "network unavailable");
        }

    }


    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean available = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            available = true;
            Log.i(TAG, "hello");
        }
        return available;
    }




    private void handleBlogResponse() {
        mProgressBar.setVisibility(View.INVISIBLE);

        if (mBlogData != null) {

            try {


                JSONArray jsonArray = mBlogData.getJSONArray("posts");
                ArrayList<HashMap<String, String>> blogArray = new ArrayList<HashMap<String, String>>();
                //mBlogPosts = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject blogPosts = jsonArray.getJSONObject(i);
                    String title = blogPosts.getString(KEY_TITLE);
                    title = Html.fromHtml(title).toString();
                    String author = blogPosts.getString(KEY_AUTHOR);
                    author = Html.fromHtml(author).toString();

                    HashMap<String,String> blogHashMap = new HashMap<String, String>();
                    blogHashMap.put(KEY_TITLE, title);
                    blogHashMap.put(KEY_AUTHOR, author);

                    blogArray.add(blogHashMap);


                    //mBlogPosts[i] = title;


                }
                String[] from = {KEY_TITLE, KEY_AUTHOR};
                int[] to = {android.R.id.text1, android.R.id.text2};
                ListAdapter adapter = new SimpleAdapter(this, blogArray, android.R.layout.simple_list_item_2, from, to);
                setListAdapter(adapter);


            } catch (JSONException e) {
                Log.i(TAG, "json error", e);
            }


        } else {
            handleErrors();
        }
    }

    private void handleErrors() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.network_error));
        alert.setMessage(R.string.cannot_retrieve_data);
        alert.setPositiveButton(android.R.string.ok, null);
        alert.create();
        alert.show();
        //build an alertdialog to handle an error getting blog data
        TextView emptyText = (TextView) getListView().getEmptyView();
        //emptyText.setText(getString(R.string.no_data));

        Log.e(TAG, "it is null");
    }


    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {
        int respCode = -1;
        String respCodeString = "";


        @Override
        protected JSONObject doInBackground(Object... result) {

            JSONObject jsonObject = null;
            //JSONObject jsonPost;
            try {
                //isf response code equals http ok constant
                // creat an inputstream object
                // creat a reader object to read in the inputstream object
                // read into a char[] the length of the content
                URL url = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                respCode = connection.getResponseCode();
                respCodeString = Integer.toString(respCode);
                Log.i(TAG, respCodeString);
                if (respCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int contentLength = connection.getContentLength();
                    char[] chars = new char[contentLength];
                    reader.read(chars);
                    String responseData = new String(chars);
                    Log.v(TAG, responseData);
                    jsonObject = new JSONObject(responseData);

                }
            } catch (MalformedURLException e) {

                Log.i(TAG, "MALFORMED", e);

            } catch (IOException e) {
                Log.i(TAG, "IO", e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.i(TAG, "regular exception", e);
            }
            return jsonObject;

        }
        @Override
        protected void onPostExecute(JSONObject result) {
            mBlogData = result;
            handleBlogResponse();
        }




    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        try {
            JSONArray jsonArray = mBlogData.getJSONArray("posts");
            JSONObject blogPosts = jsonArray.getJSONObject(position);
            String blogUrl = blogPosts.getString("url");

            Intent intent = new Intent(this, BlogPostActivity.class);
            intent.setData(Uri.parse(blogUrl));
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();


        }


    }


}

