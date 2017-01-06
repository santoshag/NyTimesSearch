package com.santoshag.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.santoshag.nytimessearch.R;
import com.santoshag.nytimessearch.models.Article;

import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    TextToSpeech tts;
    @BindView(R.id.wvArticle)
    WebView wvArticle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ShareActionProvider mShareActionProvider;
    private Boolean isPlaying = false;
    private Article article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        article = (Article) Parcels.unwrap(getIntent().getParcelableExtra("article"));
        getSupportActionBar().setTitle(article.headline.mainHeadline.substring(0, 20) + "...");

        wvArticle.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return  true;}
        });
        wvArticle.loadUrl(article.webUrl);

        tts = new TextToSpeech(this, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article_detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // get reference to WebView
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

        miShare.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void playAudioSnippet(){
        if(isPlaying){
            //stop if playing
            tts.stop();
        }else {
            tts.speak(article.headline.mainHeadline, TextToSpeech.QUEUE_FLUSH, null);
            tts.speak(article.snippet, TextToSpeech.QUEUE_ADD, null);
            isPlaying = true;
        }
    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_share) {
            return true;
        } else if (id == R.id.play_audio) {
            playAudioSnippet();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                }else{
                    tts.setPitch(Float.valueOf("1.0"));
                    tts.setSpeechRate(Float.valueOf("0.80"));
                }

            } else {
                Log.e("TTS", "Initilization Failed!");
            }

        }



}
