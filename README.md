# Dependency

- on application level dependency

```kotlin
// Retrofit
implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
implementation "com.squareup.retrofit2:converter-gson:$retrofit_version"
implementation "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
```

# Create Model Class

- AlbumItem.kt

```kotlin
package com.paigesoftware.retrofitwithcoroutines

import com.google.gson.annotations.SerializedName

data class AlbumItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int
)
```

- Albums.kt

```kotlin
package com.paigesoftware.retrofitwithcoroutines

class Albums : ArrayList<AlbumItem>()
```

# Create Interface

- AlbumService.kt

```kotlin
package com.paigesoftware.retrofitwithcoroutines

import retrofit2.Response
import retrofit2.http.GET

interface AlbumService {

    @GET("/albums") //URL endPoint
    suspend fun getAlbums(): Response<Albums>

}
```

# Create Retrofit Instance

**ℹ️ Companion Object is first called when the object is created**

```kotlin
class RetrofitInstance {

    companion object {
        val BASE_URL: String = "https://jsonplaceholder.typicode.com/"
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }

}
```

# Use it on MainActivity

1. Get Retrofit Instance
2. Convert `Response` to `LiveData`
3. Set Observer

- Code example below.

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get Retrofit Instance
        val retrofitService: AlbumService =
            RetrofitInstance
                    .getRetrofitInstance()
                    .create(AlbumService::class.java)

        // Convert `Response` to `LiveData`
        val responseLiveData: LiveData<Response<Albums>> = liveData {
            val response = retrofitService.getAlbums()
            emit(response)
        }

        // Set Observer
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
}
```

# Add Logging Interceptor

- This is great for debugging

```kotlin
class RetrofitInstance {

    companion object {
        val BASE_URL: String = "https://jsonplaceholder.typicode.com/"

        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }

}
```

# Set TimeOut

```kotlin
  val client = OkHttpClient.Builder().apply {
      this.addInterceptor(interceptor)
              /** TimeOut **/
          .connectTimeout(30, TimeUnit.SECONDS)
          .readTimeout(20, TimeUnit.SECONDS)
          .writeTimeout(25, TimeUnit.SECONDS)

  }.build()
```

```kotlin
class RetrofitInstance {

    companion object {
        val BASE_URL: String = "https://jsonplaceholder.typicode.com/"

        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
                    /** TimeOut **/
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)

        }.build()

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
    }

}
```

# Post Request

```kotlin
@POST("/albums")
suspend fun uploadAlbum(@Body album: AlbumItem) : Response<AlbumItem>
```

```kotlin
private fun uploadAlbum() {
        val album = AlbumItem(0, "My Title", 5)
        val postResponse: LiveData<Response<AlbumItem>> = liveData {
            val response = retrofitService.uploadAlbum(album)
            emit(response)
        }
        postResponse.observe(this, Observer {
            val receivedAlbumItem = it.body()
            val result = " " + "Album id : ${receivedAlbumItem.id}" + "\n" +
                    " " + "Album id : ${receivedAlbumItem.title}" + "\n" +
                    " " + "Album id : ${receivedAlbumItem.userId}"
            Log.i("MYTAG", receivedAlbumItem.title)
            textView.append(result)
        })
    }
```

# Other Interfaces, such as `query` `parameters`

```kotlin
interface AlbumService {

    @GET("/albums") //URL endPoint
    suspend fun getAlbums(): Response<Albums>

    //Query, ?a=b
    @GET("/albums")
    suspend fun getSortedAlbums(@Query("userId") userId: Int): Response<Albums>

    //Path Parameters, /a
    @GET("/albums/{id}")
    suspend fun getAlbum(@Path("id") albumId: Int): Response<AlbumItem>

    @POST("/albums")
    suspend fun uploadAlbum(@Body album: AlbumItem) : Response<AlbumItem>

}
```

# Retrofit on MainActivity

```kotlin
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
```