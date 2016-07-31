package com.codepath.nytimessearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle)
    WebView wvArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Article article = (Article) Parcels.unwrap(getIntent().getParcelableExtra("article"));
        Log.i("DEB", article.headline.mainHeadline);
        wvArticle.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return  true;}
        });
        wvArticle.loadUrl(article.webUrl);
    }

}
