package com.gedr;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gedr on 29/07/2016.
 */
public class SpotifySession {
    String id;
    Api api;

    public SpotifySession(String id) {
        this.id = id;
        startUp();
    }

    public void startUp() {

        api = Api.builder()
                .clientId(Main.clientId)
                .clientSecret(Main.secret)
                .build();

        final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();

        final SettableFuture<ClientCredentials> responseFuture = request.getAsync();

        Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
            @Override
            public void onSuccess(ClientCredentials clientCredentials) {
                System.out.println("Successfully retrieved an access token! " + clientCredentials.getAccessToken());
                System.out.println("The access token expires in " + clientCredentials.getExpiresIn() + " seconds");
                Main.success = "t";
                api.setAccessToken(clientCredentials.getAccessToken());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Main.success = "f";
                System.out.println(throwable);
            }
        });
    }

    public ArrayList<Playlist> getPlaylists() {
        final UserPlaylistsRequest request = api.getPlaylistsForUser(id).build();
        ArrayList<Playlist> playlistList = new ArrayList<>();

        try {
            final Page<SimplePlaylist> playlistsPage = request.get();

            for (SimplePlaylist playlist : playlistsPage.getItems()) {
                if(playlist.getOwner().getId().equals(id)) {
                    playlistList.add(new Playlist(playlist.getName(), playlist.getId(), playlist.getOwner().getId(), null));
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return playlistList;
        //return playlistList.toArray(new Playlist[playlistList.size()]);
    }


    public Track[] getTracks(Playlist playlist) {
        final PlaylistTracksRequest request = api.getPlaylistTracks(id, playlist.id).build();
        ArrayList<Track> trackList = new ArrayList<>();

        try {
            final Page<PlaylistTrack> page = request.get();

            final List<PlaylistTrack> playlistTracks = page.getItems();

            for (PlaylistTrack playlistTrack : playlistTracks) {
                com.wrapper.spotify.models.Track track = playlistTrack.getTrack();
                ArrayList<String> artists = new ArrayList<>();
                for(SimpleArtist artist : track.getArtists()) {
                    artists.add(artist.getName());
                }

                trackList.add(new Track(track.getName(), artists.toArray(new String[artists.size()]), track.getAlbum().getName(), track.isExplicit(), track.getPopularity(), track.getDuration()));
            }

        } catch (Exception e) {
        }
        playlist.tracks = trackList.toArray(new Track[trackList.size()]);
        return playlist.tracks;
    }
}
