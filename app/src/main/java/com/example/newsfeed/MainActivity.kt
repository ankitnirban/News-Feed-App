package com.example.newsfeed

import android.net.Uri
import android.os.Bundle
import android.widget.Scroller
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.newsfresh.MySingleton
import com.example.newsfresh.R

class MainActivity : AppCompatActivity(), NewsItemClicked {
    private lateinit var mAdapter: NewsListAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<RecyclerView>(R.id.recyclerView).layoutManager=LinearLayoutManager(this)

        fetchData()
        mAdapter = NewsListAdapter(this)

        findViewById<RecyclerView>(R.id.recyclerView).adapter=mAdapter
    }

    private fun fetchData() {
        val url="https://newsdata.io/api/1/news?apikey=pub_3688d3b4e84dea848039e80f2d6406a0f3ed&q=india"
        val jsonObjectRequest= JsonObjectRequest(Request.Method.GET,
            url,
            null,
            Response.Listener {
                val newsJsonArray = it.getJSONArray("results")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("creator"),
                        newsJsonObject.getString("link"),
                        newsJsonObject.getString("image_url")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            Response.ErrorListener {
                Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show()
            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val url = Uri.parse(item.url)
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent:CustomTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, url)
    }
}