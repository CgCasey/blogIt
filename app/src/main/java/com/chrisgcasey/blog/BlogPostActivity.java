package com.chrisgcasey.blog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Chris on 11/25/2014.
 */
public class BlogPostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_post_activity);

        Intent intent = getIntent();
        Uri blogUri = intent.getData();

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(blogUri.toString());

    }
}
