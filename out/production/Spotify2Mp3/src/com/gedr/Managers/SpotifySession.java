package com.gedr.Managers;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.*;

import java.util.ArrayList;
import java.util.List;

public class SpotifySession {
    String id;
    Api api;

    public SpotifySession(String id) {
        this.id = id;
        startUp();
    }

    public void startUp() {
        api = Api.builder().clientId(Global.clientId).clientSecret(Global.secret).build();

        final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();

        final SettableFuture<ClientCredentials> responseFuture = request.getAsync();

        Futures.addCallback(responseFuture, new FutureCallback<ClientCredentials>() {
            @Override
            public void onSuccess(ClientCredentials clientCredentials) {
                System.out.println("Successfully retrieved an access token! " + clientCredentials.getAccessToken());
                System.out.println("The access token expires in " + clientCredentials.getExpiresIn() + " seconds");
                api.setAccessToken(clientCredentials.getAccessToken());
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }

    public ArrayList<com.gedr.Modules.Playlist> getPlaylists() {
        final UserPlaylistsRequest request = api.getPlaylistsForUser(id).build();
        ArrayList<com.gedr.Modules.Playlist> playlistList = new ArrayList<>();

        try {
            final Page<SimplePlaylist> playlistsPage = request.get();

            for(SimplePlaylist playlist : playlistsPage.getItems()) {
                if(playlist.getOwner().getId().equals(id)) {
                    playlistList.add(new com.gedr.Modules.Playlist(playlist.getName(), playlist.getId(), playlist.getOwner().getId(), playlist.getTracks().getTotal(), null));
                }
            }
        } catch(Exception e) {
            e.getStackTrace();
        }
        return playlistList;
    }

    public com.gedr.Modules.Track[] getTracks(com.gedr.Modules.Playlist playlist) {
        PlaylistTracksRequest request;
        ArrayList<com.gedr.Modules.Track> trackList = new ArrayList<>();

        int offset = 0;
        int increment = 100;
        System.out.println(playlist.total);
        while(offset < playlist.total) {
            request = api.getPlaylistTracks(id, playlist.id).offset(offset).limit(playlist.total - offset > 100 ? 100 : playlist.total - offset).build();
            offset += increment;
            try {
                final Page<PlaylistTrack> page = request.get();

                final List<PlaylistTrack> playlistTracks = page.getItems();
                for(PlaylistTrack playlistTrack : playlistTracks) {
                    com.wrapper.spotify.models.Track track = playlistTrack.getTrack();
                    ArrayList<String> artists = new ArrayList<>();
                    for(SimpleArtist artist : track.getArtists()) {
                        artists.add(artist.getName());
                    }
                    System.out.println(track.getName());
                    trackList.add(new com.gedr.Modules.Track(track.getName(), artists.toArray(new String[artists.size()]), track.getAlbum().getName(), track.isExplicit(), track.getPopularity(), track.getDuration()));
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        playlist.tracks = trackList.toArray(new com.gedr.Modules.Track[trackList.size()]);
        return playlist.tracks;
    }
}
