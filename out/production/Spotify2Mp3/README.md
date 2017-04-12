# Spotify to MP3
A program that downloads your playlists from Spotify.

Uses `youtube-dl` and `ffmpeg`, alongside the `Youtube` and `Spotify` API to get a `Spotify` playlist, extract it's tracklist, find a suitable video for each track, download and convert it to MP3 and save to disk.

It is self-contained and is compatible with Windows and Mac. *only tested on OSX*


APIs Used:
- Spotify Web Java API: https://github.com/thelinmichael/spotify-web-api-java
- youtube-dl: https://github.com/rg3/youtube-dl
- ffmpeg: https://github.com/FFmpeg/FFmpeg
- YouTube API: https://developers.google.com/youtube/v3/
