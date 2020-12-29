package com.paigesoftware.retrofitwithcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var retrofitService: AlbumService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Get Retrofit Instance
        retrofitService =
            RetrofitInstance
                    .getRetrofitInstance()
                    .create(AlbumService::class.java)


        uploadAlbum()
    }

    private fun getRequest() {
        // 2. Convert `Response` to `LiveData`
        val responseLiveData: LiveData<Response<Albums>> = liveData {
            val response = retrofitService.getAlbums()
            emit(response)
        }

        // 3. Set Observer
        responseLiveData.observe(this, Observer {
            val albumsList = it.body()?.listIterator()
            if(albumsList != null) {
                while(albumsList.hasNext()) {
                    val albumsItem = albumsList.next()
                    val result = " " + "Album id : ${albumsItem.id}" + "\n" +
                            " " + "Album id : ${albumsItem.title}" + "\n" +
                            " " + "Album id : ${albumsItem.userId}"
                    Log.i("MYTAG", albumsItem.title)
                    textView.append(result)
                }
            }
        })
    }

    private fun getRequestWithQuery() {
        val responseLiveData: LiveData<Response<Albums>> = liveData {
            val response = retrofitService.getSortedAlbums(1)
            emit(response)
        }

        responseLiveData.observe(this, Observer {
            val albumsList = it.body()?.listIterator()
            if(albumsList != null) {
                while(albumsList.hasNext()) {
                    val albumsItem = albumsList.next()
                    val result = " " + "Album id : ${albumsItem.id}" + "\n" +
                            " " + "Album id : ${albumsItem.title}" + "\n" +
                            " " + "Album id : ${albumsItem.userId}"
                    Log.i("MYTAG", albumsItem.title)
                    textView.append(result)
                }
            }
        })
    }

    private fun getRequestWithQueryParameters() {
        // Path parameter usage
        val pathResponse: LiveData<Response<AlbumItem>> = liveData {
            val response = retrofitService.getAlbum(3)
            emit(response)
        }

        pathResponse.observe(this, Observer {
            val title = it.body()?.title
            Toast.makeText(applicationContext, title!!, Toast.LENGTH_LONG).show()
        })
    }

    private fun uploadAlbum() {
        val album = AlbumItem(0, "My Title", 5)
        val postResponse: LiveData<Response<AlbumItem>> = liveData {
            val response = retrofitService.uploadAlbum(album)
            emit(response)
        }
        postResponse.observe(this, Observer {
            val receivedAlbumItem = it.body()
            val result = " " + "Album id : ${receivedAlbumItem!!.id}" + "\n" +
                    " " + "Album id : ${receivedAlbumItem.title}" + "\n" +
                    " " + "Album id : ${receivedAlbumItem.userId}"
            Log.i("MYTAG", receivedAlbumItem.title)
            textView.append(result)
        })
    }

}