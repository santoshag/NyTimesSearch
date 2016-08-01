package com.codepath.nytimessearch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleAdapter;
import com.codepath.nytimessearch.decorators.ItemClickSupport;
import com.codepath.nytimessearch.fragments.SearchFilterFragment;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.ArticlesResponse;
import com.codepath.nytimessearch.models.Response;
import com.codepath.nytimessearch.utils.ArticlesService;
import com.codepath.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.utils.Utilities;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {


    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvArticles)
    RecyclerView rvArticles;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    ArrayList<Article> articles;
    ArticleAdapter adapter;
    final static String BASE_URL = "https://api.nytimes.com";
    SharedPreferences filterPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Typeface titleFont = Typeface.createFromAsset(this.getAssets(), "fonts/englishtowne.ttf");
        toolbar_title.setTypeface(titleFont);
        filterPreferences = getSharedPreferences(Utilities.FILTER_PREFERENCES, Context.MODE_PRIVATE);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchArticles(query);
        }else{
            //clear only on new activity launches
            filterPreferences.edit().clear().commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                searchView.clearFocus();
                searchArticles(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!Utilities.checkForInternet()){
            Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            SearchFilterFragment myDialog = new SearchFilterFragment();
            FragmentManager fm = getSupportFragmentManager();
            myDialog.show(fm, "test");
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchArticles(final String searchQuery) {

        Toast.makeText(this, "Find articles related to " + searchQuery, Toast.LENGTH_LONG).show();
        // Lookup the recyclerview in activity layout
        //rvArticles.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

        // Get movies
        articles = new ArrayList<>();

        //search articles on page 0 initially
        fetchArticles(searchQuery, 0);
        // Create adapter passing in articles list
        adapter = new ArticleAdapter(this, articles);
        // Attach the adapter to the recyclerview to populate items
        rvArticles.setAdapter(adapter);
        // Set layout manager to position the items
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view

        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("DEBUG", "page: " + page);
                if (page < ArticlesResponse.MAX_PAGES_TO_RETRIEVE) {
                    fetchArticles(searchQuery, page);
                }
            }
        });

        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Article article = articles.get(position);
                        Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
                        intent.putExtra("article", Parcels.wrap(article));
                        startActivity(intent);
                    }
                }
        );
        rvArticles.setLayoutManager(staggeredGridLayoutManager);
    }


    private void fetchArticles(final String searchQuery, final int page) {

        //clear articles if it is a new search
        if (page == 0) {
            articles.clear();
        }

        final String apiKey = "119d6da8a49945548737cfc94407e7a0";

        //Use retrofit networking library to make API calls
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //Add below interceptor to enable debugging logs
            /*HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            // Can be Level.BASIC, Level.HEADERS, or Level.BODY
            // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.networkInterceptors().add(httpLoggingInterceptor);*/


        builder.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api-key", apiKey)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);

            }
        });


        OkHttpClient httpClient = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Map<String, String> params = new HashMap<>();

        params.put("q", searchQuery);
        params.put("page", String.valueOf(page));

        String sort = filterPreferences.getString("sort", "");
        String begin_date = filterPreferences.getString("begin_date", "");
        String end_date = filterPreferences.getString("end_date", "");
        String news_desk = filterPreferences.getString("news_desk", "");

        Log.d("info", "sort: " + sort + " begin_date:" + begin_date + " end_date:" + end_date + "news_desk:" + news_desk);

        if (!TextUtils.isEmpty(sort)) {
            params.put("sort", sort);
        }
        if (!TextUtils.isEmpty(begin_date)) {
            params.put("begin_date", begin_date);
        }
        if (!TextUtils.isEmpty(end_date)) {
            params.put("end_date", end_date);
        }
        if (!TextUtils.isEmpty(news_desk)) {
            params.put("fq", "news_desk:(" + news_desk + ")");
        }

        ArticlesService articlesService = retrofit.create(ArticlesService.class);
        Call<Response> call = articlesService.listArticles(params);
        call.enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                int statusCode = response.code();
                Response r = response.body();
                articles.addAll(r.articlesResponse.articles);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.i("DEBUG", " Call to network failed! ");
            }
        });


    }

}
