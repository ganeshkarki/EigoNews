package com.paradox.eigonews;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private int newsCount = 1;

    MyAdapter mAdapter;
    VerticalViewPager mPager;

    private static final String TAG = "MainActivity";

    private JSONArray articleJson;
    String url = "https://newsapi.org/v2/top-headlines?country=jp&apiKey=".concat(BuildConfig.GOOGLE_NEWS_API_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // defaulttextView = findViewById(R.id.default_text);

        initializNewsDataFromApi();
        mAdapter = new MyAdapter(getSupportFragmentManager(), this.newsCount, this.articleJson);
        mPager = findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
    }


    protected void initializNewsDataFromApi() {
        queue = Volley.newRequestQueue(this);

        Log.d(TAG, "GOOGLE API KEY:" + BuildConfig.GOOGLE_NEWS_API_KEY ); // TODO: Remove this LOG

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    newsCount = response.getInt("totalResults");
                    articleJson = response.getJSONArray("articles");

                    mAdapter.update(newsCount, articleJson);
                    Toast.makeText(MainActivity.this, "Data fetched", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "news count fetched: " + newsCount + articleJson.toString()); // TODO: Remove this LOG
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Something went wrong " + e.getMessage() ); // TODO: Remove this LOG
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e(TAG, url);
                Log.e(TAG, error.getMessage());
            }
        });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    public static class MyAdapter extends SmartFragmentStatePagerAdapter {

        Integer newsCount;

        JSONArray articleJson;
        MyAdapter(FragmentManager fm, int articleCount, JSONArray articleJson) {
            super(fm);
            this.newsCount = articleCount;
            this.articleJson = articleJson;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: " + this.newsCount );
            return this.newsCount;
        }

        @Override
        public Fragment getItem(int position) {
            String url = "";
            String title = "";
            JSONObject article;


            if (this.articleJson != null) {
                try {
                    article = articleJson.getJSONObject(position);
                    url = article.getString("urlToImage");
                    title = article.getString("title");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }

            return NewsCardFragment.newInstance("NEWS 0", Color.WHITE, url, title);
//            if (position <= getCount() && articleJson != null) {
//                return NewsCardFragment.newInstance("NEWS 0", Color.WHITE);
//            }

//            Log.e(TAG, "Trying to get wrong position: " + position);
//            return null;


//            switch (position) {
//                case 0:
//                    return NewsCardFragment.newInstance("NEWS 0", Color.WHITE);
//                case 1:
//                    // return a different Fragment class here
//                    // if you want want a completely different layout
//                    return NewsCardFragment.newInstance("NEWS 1", Color.CYAN);
//                default:
//                    return null;
//            }
        }

        public void update(Integer newsCount, JSONArray articleJson) {
//            Log.d(TAG, "upadated: " + articleJson.toString() ); // TODO: Remove this LOG

            this.articleJson = articleJson;
            this.newsCount = newsCount;
            notifyDataSetChanged();
        }


        public int getItemPosition(@NonNull Object item) {
//            NewsCardFragment fragment = (NewsCardFragment) item;
//            String title = fragment.getTitle();
//            int position = titles.indexOf(title);
//
//            if (position >= 0) {
//                return position;
//            } else {
                return POSITION_NONE;
//            }
        }
    }

    public static class NewsCardFragment extends Fragment {

        private static final String MY_NUM_KEY = "num";
        private static final String MY_COLOR_KEY = "color";

        private String mNum;
        private int mColor;

        // You can modify the parameters to pass in whatever you want
        static NewsCardFragment newInstance(String num, int color, String url, String title) {

//            Log.d(TAG, "Here: "+ oj.getClass() + oj.toString());
            NewsCardFragment f = new NewsCardFragment();
            Bundle args = new Bundle();
            args.putString(MY_NUM_KEY, title);
            args.putInt(MY_COLOR_KEY, color);
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getString(MY_NUM_KEY) : "NONE";
            mColor = getArguments() != null ? getArguments().getInt(MY_COLOR_KEY) : Color.BLACK;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.news_card, container, false);
            v.setBackgroundColor(mColor);
            TextView textView = v.findViewById(R.id.descriptionText);
            textView.setText(mNum);
            return v;
        }
    }

}
