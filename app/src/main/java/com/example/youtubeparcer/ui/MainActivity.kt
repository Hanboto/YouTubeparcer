package com.example.youtubeparcer.ui
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubeparcer.R
import com.example.youtubeparcer.adapter.PlaylistAdapter
import com.example.youtubeparcer.model.ItemsItem
import com.example.youtubeparcer.model.PlaylistModel
import com.example.youtubeparcer.ui.detail_playlist.DetailPlaylistActivity
import com.example.youtubeparcer.utils.NetworkUtils
import com.example.youtubeparcer.utils.isShow
import kotlinx.android.synthetic.main.activity_main.*
@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {


    private var viewModel: MainViewModel? = null
    private var adapter: PlaylistAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initAdapter()
        fetchPlaylist()
    }

    private fun initAdapter() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = PlaylistAdapter() {item: ItemsItem -> clickItem(item)}
        recycler_view.adapter = adapter

    }

    private fun clickItem(item: ItemsItem) {
        val intent = Intent(this, DetailPlaylistActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("title", item.snippet.title)
        intent.putExtra("channelTitle", item.snippet.channelId)
        intent.putExtra("etag", item.etag)
        startActivity(intent)
    }

    private fun fetchPlaylist() {
        //TODO check internet
        if ( !NetworkUtils.isOnline(applicationContext)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            showNoConnection(true)
        }
        val data = viewModel?.getPlaylistData()
        data?.observe(this, Observer<PlaylistModel> {
            val model: PlaylistModel? = data.value
            when {
                model != null -> {
                    updateAdapterData(model)
                }
            }
        })
    }
    private fun updateAdapterData(list: PlaylistModel?) {
        val data = list?.items
        adapter?.updateData(data)
    }
    fun restart(view: View) {
        if (! NetworkUtils.isOnline(applicationContext)){
            showNoConnection(true)
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }else{
            if(NetworkUtils.isOnline(applicationContext)){
                fetchPlaylist()
                showNoConnection(false)
            }
        }
    }
    private fun showNoConnection(isShown : Boolean){
        imageTry.isShow(isShown)
        imageNoConnect.isShow(isShown)
    }
}
