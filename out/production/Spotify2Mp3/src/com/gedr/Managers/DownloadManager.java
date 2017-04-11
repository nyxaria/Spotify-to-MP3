package com.gedr.Managers;

import com.gedr.Main;
import com.gedr.Modules.Playlist;
import com.gedr.Modules.Track;
import com.gedr.UI.GraphicLabel;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.sound.sampled.AudioFileFormat;
import java.io.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by gedr on 30/07/2016.
 */
public class DownloadManager {

    public enum States {
        QUEUE, RUNNING, DONE, RETRYING, EXTRACTING_DONE, ERROR, DOWNLOADING, EXTRACTING
    }

    Playlist[] playlists;
    Playlist currentPlaylist;
    Track currentTrack;

    public static File output;
    public static File temp;

    int playlistIndex = 0;
    int trackIndex = 0;
    boolean retrying = false;
    int attempt = 1;
    boolean downloading;

    //commandline
    ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    Process p;

    public DownloadManager(Playlist[] playlists) {
        this.playlists = playlists;

        startDownload();
    }

    public void startDownload() {
        if(output != null) {
            ses.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if(System.currentTimeMillis() - lastEvent > 45000) {
                        if(downloading) {
                            retryDownload();
                            p.destroy();
                        }
                    }
                }
            }, 10, 10, TimeUnit.SECONDS);
            nextPlaylist();

        } else finish();

    }
    public void nextPlaylist() {
        if(playlistIndex < playlists.length) {
            currentPlaylist = playlists[playlistIndex++];
            trackIndex = 0;
            File f = new File(output.getAbsolutePath() + "/" + currentPlaylist.name);
            f.mkdirs();
            nextTrack();
        } else {
            finish();
        }
    }


    private void retryDownload() {
        System.out.println("Retrying download...");
        if(retrying) {
            trackIndex--;
            retrying = false;
        }
        nextTrack();
    }

    public void nextTrack() {
        if(trackIndex < currentPlaylist.tracks.length) {
            attempt = 1;
            retrying = false;
            currentTrack = currentPlaylist.tracks[trackIndex++];
            String artist = "";
            for(String s : currentTrack.artists) {
                artist += s + ", ";
            }
            artist = artist.substring(0, artist.length() - 2);
            downloadTrack(currentTrack.name, artist, findYoutubeUri(currentTrack, 1));
        } else {
            nextPlaylist();
        }
    }


    public String findYoutubeUri(Track track, int i) {
        String url = "";
        if(track.id.equals("next_track")) {
            System.out.println("URL not found for \"" + track.name + "\"");
            return "next_track";
        }
        if(!track.id.equals("")) {
            return "https://www.youtube.com/watch?v=" + track.id;
        }
        String keyword = null;
        switch(i) {
            case -1:
                keyword = track.name.replace("[", "").replace("]", "").replace("(", "").replace(")", "").replace("\"", "") + " " + track.artists[0].replace("$", "s").replace("[", " ").replace("]", " ").replace("(", "").replace(")", "").replace(".", "") + " lyrics";
                keyword = keyword.replace(" ", "+");

                String duration = "sp=" + ((track.duration / 60) > 4 ? "medium" : "short") + "&";
                String quality = "";
                //String duration = "";
                url = "https://www.googleapis.com/youtube/v3/search?" + duration + quality + "type=video&part=snippet&maxResults=1&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 1:
                keyword = track.name.replace("[", "").replace("]", "").replace("(", "").replace(")", "").replace(".", "").replace("\"", "") + " " + track.artists[0].replace("$", "s").replace("[", " ").replace("]", " ") + " lyrics" + (track.explicit ? " explicit" : "");
                keyword = keyword.replace(" ", "+");
                //duration = "sp=" + ((track.duration / 60) > 4 ? "medium" : "short") + "&";
                duration = "";
                //quality = "videoDefinition=high&";
                quality = "";
                url = "https://www.googleapis.com/youtube/v3/search?" + duration + quality + "type=video&part=snippet&maxResults=1&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 2:
                keyword = track.name.replace("[", " ").replace("]", " ").replace("(", "").replace(")", "").replace(".", "").replace("\"", "") + " " + track.artists[0].replace("$", "s").replace("[", " ").replace("]", " ") + " official lyrics" + (track.explicit ? " explicit" : "");
                keyword = keyword.replace(" ", "+");
                //duration = "sp=" + ((track.duration / 60) > 4 ? "medium" : "short") + "&";
                duration = "";
                url = "https://www.googleapis.com/youtube/v3/search?" + duration + "type=video&part=snippet&maxResults=1&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 3:
                keyword = track.name.replace("[", " ").replace("]", " ").replace("(", "").replace(")", "").replace(".", "").replace("\"", "") + " " + track.artists[0] + " lyrics";
                keyword = keyword.replace(" ", "+");
                duration = "";

                url = "https://www.googleapis.com/youtube/v3/search?" + duration + "type=video&part=snippet&maxResults=1&order=viewCount&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 4:
                keyword = track.name.replace("[", " ").replace("]", " ").replace("(", "").replace(")", "").replace(".", "").replace("\"", "") + " " + track.artists[0];
                keyword = keyword.replace(" ", "+");
                duration = "&sp=" + ((track.duration / 60) > 4 ? "medium" : "short") + "&";

                url = "https://www.googleapis.com/youtube/v3/search?type=video&part=snippet&maxResults=1&order=viewCount&q=" + keyword + duration + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 5:
                if(track.artists.length > 1)
                    keyword = track.name.replace("[", " ").replace("]", " ").replace("(", "").replace(")", "").replace(".", "").replace("\"", "") + " " + track.artists[1];
                else
                    keyword = track.name.replace("[", " ").replace("]", " ").replace("(", "").replace(")", "").replace(".", "").replace("\"", "") + " " + track.artists[0];

                keyword = keyword.replace(" ", "+");

                url = "https://www.googleapis.com/youtube/v3/search?type=video&part=snippet&maxResults=1&order=viewCount&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 6:
                return findYoutubeUri(currentTrack, -1);

        }
        Document doc = null;

        try {
            doc = Jsoup.connect(url).ignoreContentType(true).timeout(6 * 1000).get();
        } catch(IOException e) {
            if(i != -1) {
                return findYoutubeUri(track, ++attempt);
            } else {
                System.out.println("URL not found for \"" + track.name + "\" { keyword=\"" + keyword + " }");
                track.id = "next_track";
                return "next_track";
            }
        }
        JSONObject ob = ((JSONObject) new JSONTokener(doc.text()).nextValue());

        JSONArray array = ob.getJSONArray("items");

        if(array.isEmpty()) {
            if(i != -1) {
                return findYoutubeUri(track, ++attempt);
            } else {
                System.out.println("URL not found for \"" + track.name + "\" { keyword=\"" + keyword + " }");
                track.id = "next_track";
                return "next_track";
            }
        }
        try {
            track.id = array.getJSONObject(0).getJSONObject("id").getString("videoId");
        } catch(Exception e) {
            if(i != -1) {
                return findYoutubeUri(track, ++attempt);
            } else {
                System.out.println("URL not found for \"" + track.name + "\" { keyword=\"" + keyword + " }");
                track.id = "next_track";
                return "next_track";
            }
        }

        if(new Random().nextInt(100000) == 1) { //rickroll
            //track.id = "dQw4w9WgXcQ";
        }

        if(track.ytDuration == 0) {
            url = "https://www.googleapis.com/youtube/v3/videos?id=" + track.id + "&part=contentDetails&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";

            try {
                doc = Jsoup.connect(url).ignoreContentType(true).timeout(4 * 1000).get();
                array = ((JSONObject) new JSONTokener(doc.text()).nextValue()).getJSONArray("items");
                track.ytDuration = getDuration(array.getJSONObject(0).getJSONObject("contentDetails").getString("duration"));
            } catch(Exception e) {
                e.printStackTrace();
                return "https://www.youtube.com/watch?v=" + track.id;
            }

            url = "https://www.googleapis.com/youtube/v3/videos?id=" + track.id + "&part=snippet&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";

            try {
                doc = Jsoup.connect(url).ignoreContentType(true).timeout(4 * 1000).get();
                array = ((JSONObject) new JSONTokener(doc.text()).nextValue()).getJSONArray("items");
                track.title = array.getJSONObject(0).getJSONObject("snippet").getString("title");
            } catch(Exception e) {
                e.printStackTrace();
                return "https://www.youtube.com/watch?v=" + track.id;
            }
        }
        boolean positive = false;
        for(String query : track.name.replaceAll("[.*?]", "").replaceAll("(.*?)", "").replace("$", "s").split(" ")) {
            if(track.title.replace("$", "s").toLowerCase().contains(query.toLowerCase())) {
                positive = true;
            }
        }
        for(String query : track.name.split(" ")) {
            if(track.title.toLowerCase().contains(query.toLowerCase())) {
                positive = true;
            }
        }
        if(!positive) {
            if(i == -1) {
                System.out.println("URL not found for \"" + track.name + "\" { youtube_title=\"" + track.title + "\", keyword=\"" + keyword + " }");
                return "next_track";
            }
            track.id = "";
            track.ytDuration = 0;
            track.title = "";
            return findYoutubeUri(track, ++i);
        }
        System.out.println("URL found for \"" + track.name + "\" { duration=" + track.ytDuration + "s, youtube_title=\"" + track.title + "\", keyword=\"" + keyword + "\", id=\"" + track.id + "\" }");

        return "https://www.youtube.com/watch?v=" + track.id;
    }

    public int getDuration(String time) {
        time = time.substring(2);
        int duration = 0;
        Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for(int i = 0; i < indexs.length; i++) {
            int index = time.indexOf((String) indexs[i][0]);
            if(index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexs[i][1];
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }

    public void updateUI(Track track, States state, String progress, String speed) {
        GraphicLabel label = track.gui;
        label.speed = speed;
        label.progress = progress;
        label.state = state;
        label.repaint();
        label.revalidate();
    }

    public void downloadTrack(String name, String artist, String url) {
        if(url.endsWith("next_track")) {
            updateUI(currentTrack, States.ERROR, "", "");
            nextTrack();
        } else {
            try {

                boolean complete = false; //implement check if downloaded video but not mp3
                File file = new File(output.getAbsolutePath() + "/" + currentPlaylist.name + "/" + name + " - " + artist + ".mp3");
                if(!complete) {

                    if(file.exists()) {
                        AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
                        Map properties = baseFileFormat.properties();
                        Long duration = (Long) properties.get("duration") / 1000000;
                        if(duration > currentTrack.ytDuration - 10) {
                            complete = true;
                            updateUI(currentTrack, States.DONE, "", "");
                        } else {
                            file.delete();
                            executeCommand(new String[]{Global.youtube_dl, "--audio-quality", "0", "-o", temp.getAbsolutePath() + "/" + name + " - " + artist + ".flv", url});
                            if(retrying)
                                retryDownload();
                            else
                                executeCommand(new String[]{Global.ffmpeg, "-i", temp.getAbsolutePath() + "/" + name + " - " + artist + ".flv", "-ab", "256k", output.getAbsolutePath() + "/" + currentPlaylist.name + "/" + name + " - " + artist + ".mp3"}); // ,"-ac", "2", "-ab", "128k"

                        }
                    } else {
                            executeCommand(new String[]{Global.youtube_dl, "--audio-quality", "0", "-o", temp.getAbsolutePath() + "/" + name + " - " + artist + ".flv", url});
                            if(retrying) retryDownload();
                            else {
                                currentTrack.gui.out = output.getAbsolutePath() + "/" + currentPlaylist.name + "/" + name + " - " + artist + ".mp3";
                                executeCommand(new String[]{Global.ffmpeg, "-i", temp.getAbsolutePath() + "/" + name + " - " + artist + ".flv", "-ab", "256k", output.getAbsolutePath() + "/" + currentPlaylist.name + "/" + name + " - " + artist + ".mp3"}); // ,"-ac", "2", "-ab", "128k"
                            }
                        }
                    }


            } catch(Exception e) {

                e.printStackTrace();
                if(retrying) downloadTrack(name, artist, url);
                else nextTrack();
            }

            nextTrack();
        }
    }

    private String executeCommand(String[] commands) {

        StringBuffer output = new StringBuffer();
        for(String command : commands) {
            System.out.print(command + " ");
        }
        System.out.println();

        try {
            p = Runtime.getRuntime().exec(commands);
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
            errorGobbler.start();
            outputGobbler.start();
            p.waitFor();

            // when finished

        } catch(Exception e) {
            e.printStackTrace();
            return "error";
        }
        ses.shutdown();

        return output.toString();

    }

    long lastEvent;

    private class StreamGobbler extends Thread {
        InputStream is;
        String type;
        int level = 0;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;

                while((line = br.readLine()) != null) {
                    System.out.println(line);
                    lastEvent = System.currentTimeMillis();
                    if(line.contains("Downloading webpage") || line.contains("Downloading video info webpage") || line.contains("Extracting video information") || line.contains("Downloading MPD manifest")) {
                        updateUI(currentTrack, States.EXTRACTING, level++ + "", "");
                    } else if(line.contains("[download]")) {
                        downloading = true;
                        if(line.substring(11, 17).replace(" ", "").replaceAll("[^\\d.]", "").equals("100")) {
                            updateUI(currentTrack, States.DOWNLOADING, "100", "");
                        } else if(!line.substring(12, 17).replace(" ", "").replaceAll("[^\\d.]", "").equals("")) {
                            if(!line.contains("Unknown speed")) {
                                String speed = line.substring(31, 45).replace("", "");
                                while((speed.charAt(0) + "").replaceAll("[^\\d.]", "").equals("")) {
                                    speed = speed.substring(1, speed.length());
                                }
                                if(speed.charAt(speed.length() - 1) != 's')
                                    speed = speed.substring(0, speed.length() - 1);

                                int cut = 0;
                                int kb = 0;
                                if(speed.contains("K")) kb = 1;
                                if(!speed.replaceAll("[^\\d.]", "").equals(""))
                                    speed = speed.substring(cut, speed.indexOf(".") + 3 - kb) + "\n" + speed.substring(speed.indexOf(".") + 3, speed.length());
                                updateUI(currentTrack, States.DOWNLOADING, line.substring(12, 17).replace(" ", ""), speed);

                            } else {
                                updateUI(currentTrack, States.DOWNLOADING, line.substring(12, 17).replace(" ", ""), "");

                            }
                        }
                    }
                    if(line.startsWith("size=")) {
                        downloading = false;
                        String time = line.substring(21, 29).replace(":", "");

                        int h = Integer.parseInt(time.substring(0, 2));
                        int m = Integer.parseInt(time.substring(2, 4));
                        int s = Integer.parseInt(time.substring(4, 6));
                        s += m * 60 + h * 60 * 60;
                        updateUI(currentTrack, States.RETRYING, s + "", "");
                    }
                    if(line.startsWith("video:")) {
                        downloading = false;
                        updateUI(currentTrack, States.DONE, "", "");
                    }

                    if(line.startsWith("ERROR: ")) {

                        downloading = false;
                        retrying = true;
                        updateUI(currentTrack, States.ERROR, "", "");
                    }

                }
            } catch(IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void finish() {
        deleteDirectory(temp);
        System.exit(0);
    }

    public static boolean deleteDirectory(File directory) {
        if(directory == null) {
            return false;
        }
        if(directory.exists()) {
            File[] files = directory.listFiles();
            if(null != files) {
                for(File file : files)
                    if(file.isDirectory()) deleteDirectory(file);
                    else file.delete();
            }
        }

        return (directory.delete());
    }

}