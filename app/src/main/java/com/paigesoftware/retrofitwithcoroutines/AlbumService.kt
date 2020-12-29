package com.paigesoftware.retrofitwithcoroutines

import retrofit2.Response
import retrofit2.http.*

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