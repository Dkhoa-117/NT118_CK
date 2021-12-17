package com.example.musiclovers;

import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.artistItem;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.models.songItem;
import com.example.musiclovers.models.userItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
/**
 * DONE
 */
public interface PlaceHolder {
    // 👇 GET 👇
    @GET("songs")
    Call<List<songItem>> getSongs();

    @GET("songs/{songId}")
    Call<songItem> getSong (@Path("songId") String songId);

    @GET("albums")
    Call<List<albumItem>> getAlbums();

    @GET("albums/{albumId}")
    Call<albumItem> getAlbum (@Path("albumId") String albumId);

    @GET("albums/artist/{artistId}")
    Call<List<albumItem>> getAlbumsByArtist (@Path("artistId") String artistId);

    @GET("songs/album/{albumId}")
    Call<List<songItem>> getSongsByAlbum (@Path("albumId") String albumId);

    @GET("songs/{category}")
    Call<List<songItem>> getSongsByCategory (@Path("category") String category); //new-music * best-new-songs *

    @GET("albums/{category}")
    Call<List<albumItem>> getAlbumsByCategory (@Path("category") String category); //new-albums * hot-albums *

    @GET("songs/artist/{artistId}")
    Call<List<songItem>> getSongsByArtist (@Path("artistId") String artistId);

    @GET("playlists/{playlistId}")
    Call<playlistItem> getPlaylist (@Path("playlistId") String playlistId);

    @GET("playlists/songs/{playlistId}")
    Call<List<songItem>> getSongsByPlaylist (@Path("playlistId") String playlistId);

    @GET("playlists/user/{userId}")
    Call<List<playlistItem>> getPlaylistsByUser (@Path("userId") String userId);

    @GET("playlists/{userId}/{playlist_number}")
    Call<List<playlistItem>> getPlaylistByUser_PlaylistNum(@Path("userId") String userId, @Path("playlist_number") int playlist_number);

    @GET("artists")
    Call<List<artistItem>> getArtists();

    @GET("artists/{artistId}")
    Call<artistItem> getArtist(@Path("artistId") String artistId);

    @GET("songs/search")
    Call<List<songItem>> searchSongs (@Query("q") String q);

    @GET("albums/search")
    Call<List<albumItem>> searchAlbums (@Query("q") String q);

    @GET("artists/search")
    Call<List<artistItem>> searchArtists (@Query("q") String q);

    // 👇 PATCH 👇

    // 👇 POST 👇
    @FormUrlEncoded
    @POST("users/")
    Call<userItem> register(
            @Field("userName") String userName,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<userItem> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("playlists/")
    Call<playlistItem> createPlaylist(
            @Field("playlistName") String playlistName,
            @Field("userId") String userId
    );

    @FormUrlEncoded
    @POST("playlists/{playlistId}/songs")
    Call<Void> addSongToPlaylist(
            @Path("playlistId") String playlistId,
            @Field("songId") String songId
    );

    @FormUrlEncoded
    @POST("songs/likes")
    Call<Void> likeSong(
            @Field("userId") String userId,
            @Field("songId") String songId
    );

    @FormUrlEncoded
    @POST("songs/recent")
    Call<Void> addSong2RecentList(
            @Field("userId") String userId,
            @Field("songId") String songId
    );

    // 👇 DELETE 👇
    @DELETE("playlists/{playlistId}/songs/{songId}")
    Call<Void> removeSongInPlaylist(
            @Path("playlistId") String playlistId,
            @Path("songId") String songId
    );

    @DELETE("playlists/{playlistId}")
    Call<Void> deletePlaylist(
            @Path("playlistId") String playlistId
    );
}
