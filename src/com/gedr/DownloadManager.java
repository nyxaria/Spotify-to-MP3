package com.gedr;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Random;

/**
 * Created by gedr on 30/07/2016.
 */
public class DownloadManager {

    public enum States {
        QUEUE, RUNNING, DONE, RETRYING, EXTRACTING_DONE, ERROR, DOWNLOADING, EXTRACTING;
    }

    Playlist[] playlists;
    File output;

    int playlistIndex = 0;
    int trackIndex = 0;

    Playlist currentPlaylist;
    Track currentTrack;

    File temp;

    public DownloadManager(Playlist[] playlists) {
        this.playlists = playlists;

        startDownload();
    }

    public void startDownload() {
        chooseOutput();
        if (output != null)
            nextPlaylist();
        else
            finish();
    }

    public void chooseOutput() {

        if (System.getProperty("os.name").contains("Windows")) {
            JFileChooser fileChooser;
            int returnValue;
            do {
                fileChooser = new JFileChooser();
                fileChooser.setBounds((int) (Main.screen.getWidth() * .2), (int) (Main.screen.getHeight() * .2), (int) (Main.screen.getWidth() * .6), (int) (Main.screen.getHeight() * .6));
                returnValue = fileChooser.showOpenDialog(null);
            } while (returnValue != JFileChooser.APPROVE_OPTION);
            output = fileChooser.getSelectedFile();
        } else {//unix
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            FileDialog fd = new FileDialog(Main.frame);
            fd.setBounds((int) (Main.screen.getWidth() * .2), (int) (Main.screen.getHeight() * .2), (int) (Main.screen.getWidth() * .6), (int) (Main.screen.getHeight() * .6));
            fd.setBounds(Main.frame.getBounds());
            fd.setVisible(true);
            if (fd.getFile() != null) {
                output = new File(fd.getDirectory() + "/" + fd.getFile());
                System.setProperty("apple.awt.fileDialogForDirectories", "false");
            }
        }

        if (output != null) {
            temp = new File(output.getAbsolutePath() + "/_temp");
            temp.mkdirs();
            temp.deleteOnExit();
        }
        //}
    }

    public void nextPlaylist() {
        if (playlistIndex < playlists.length) {
            currentPlaylist = playlists[playlistIndex++];
            trackIndex = 0;
            File f = new File(output.getAbsolutePath() + "/" + currentPlaylist.name);
            f.mkdirs();
            nextTrack();
        } else {
            finish();
        }
    }

    public void nextTrack() {
        if (trackIndex < currentPlaylist.tracks.length) {
            attempt = 1;
            retry = 0;
            currentTrack = currentPlaylist.tracks[trackIndex++];
            String artist = "";
            for (String s : currentTrack.artists) {
                artist += s + ", ";
            }
            artist = artist.substring(0, artist.length() - 2);
            downloadTrack(currentTrack.name, artist, findYoutubeUri(currentTrack, 2));
        } else {
            nextPlaylist();
        }
    }

    int attempt = 1;
    public String findYoutubeUri(Track track, int i) {
        String url = "";
        switch(i) {
            case 1:
                String keyword = track.name + " " + track.artists[0].replace("$", "s").replace("[", " ").replace("]", " ") + " official";
                keyword = keyword.replace(" ", "+");
                String duration = "sp=" + ((track.duration / 60) > 4 ? "medium" : "short");
                url = "https://www.googleapis.com/youtube/v3/search?" + duration + "&part=snippet&maxResults=1&order=viewCount&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 2:
                keyword = track.name + " " + track.artists[0] + (track.explicit ? " explicit" : "") + "lyrics";
                keyword = keyword.replace(" ", "+");

                url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&order=viewCount&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 3:
                if (track.artists.length > 1)
                    keyword = track.name + " " + track.artists[1];
                else
                    keyword = track.name + " " + track.artists[0];

                keyword = keyword.replace(" ", "+");

                url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&order=viewCount&q=" + keyword + "&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
                break;
            case 4:
                return "next_track";
        }
            //videoDuration=short/medium
            Document doc = null;

            try {
                doc = Jsoup.connect(url).ignoreContentType(true).timeout(6 * 1000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject ob = ((JSONObject) new JSONTokener(doc.text()).nextValue());

            JSONArray array = ob.getJSONArray("items");


        if(array.isEmpty()) {
            findYoutubeUri(track, ++attempt);
        }
        String id = "";
        try {
            id = array.getJSONObject(0).getJSONObject("id").getString("videoId");
        } catch (net.sf.json.JSONException e) {
            findYoutubeUri(track, ++attempt);
        }

        if(new Random().nextInt(10000) == 1) {
            id = "dQw4w9WgXcQ";
        }

        url = "https://www.googleapis.com/youtube/v3/videos?id="+id+"&part=contentDetails&key=AIzaSyDMUtaSnR0hadvSt4jPCCoPJeRh5LbiU5w";
        doc = null;

        try {
            doc = Jsoup.connect(url).ignoreContentType(true).timeout(4 * 1000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        array = ((JSONObject) new JSONTokener(doc.text()).nextValue()).getJSONArray("items");
        track.ytDuration = getDuration(array.getJSONObject(0).getJSONObject("contentDetails").getString("duration"));
        return "https://www.youtube.com/watch?v=" + id;
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
        JLabel label = track.gui;
        Graphics2D g2 = (Graphics2D) label.getGraphics();
        g2.clearRect(0, 0, label.getWidth(), label.getHeight());
        switch (state) {
            case QUEUE:
                g2.setColor(Color.lightGray.darker().darker());
                g2.fillRect(0, 0, label.getPreferredSize().width, label.getPreferredSize().height);
                break;
            case EXTRACTING:
                g2.setColor(new Color(Color.lightGray.getRed() + 10*Integer.parseInt(progress),
                        Color.lightGray.getGreen() + 10*Integer.parseInt(progress),
                        Color.lightGray.getBlue() + 10*Integer.parseInt(progress)));
                g2.fillRect(0, 0, label.getPreferredSize().width, label.getPreferredSize().height);
                break;
            case RETRYING:
                g2.setColor(new Color(50, Color.green.getGreen(), 50));
                g2.fillRect(0, 0, label.getPreferredSize().width, label.getPreferredSize().height);
                g2.setColor(new Color(0,191,255));
                //g2.setColor(Color.red);
                double prog = (double) Integer.parseInt(progress);

                double percentage = (prog/track.ytDuration);
                if(percentage > 1) {
                    track.ytDuration = track.duration;
                    percentage = 1;
                }
                g2.fillRect(0, label.getSize().height - 3, (int) (label.getSize().width*(prog/track.ytDuration)), 3);
                break;
            case EXTRACTING_DONE:
            case DONE:
                g2.setColor(new Color(50, Color.green.getGreen(), 50));
                g2.fillRect(0, 0, label.getPreferredSize().width, label.getPreferredSize().height);
                break;
            case ERROR:
                g2.setColor(Color.orange);
                g2.fillRect(0, 0, label.getPreferredSize().width, label.getPreferredSize().height);
                break;
            case DOWNLOADING:
                g2.setColor(new Color(50, Color.green.getGreen(), 50));
                g2.fillRect(0, 0, (int) (label.getPreferredSize().width * (Double.parseDouble(progress.replaceAll("[^\\d.]", "")) / 100)), label.getPreferredSize().height);
                g2.setColor(Color.black);

                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
                int y = 3;
                for (String line : speed.split("\n"))
                    g2.drawString(line, 3, y += g2.getFontMetrics().getHeight());
                break;
        }

        g2.dispose();
        label.repaint();
        label.revalidate();
    }

    int retry = 0;
    public void downloadTrack(String name, String artist, String url) {
        if(url.equals("next_track")) {
            nextTrack();
        } else {
            try {
                executeCommand(new String[]{Main.youtube_dl, "--audio-quality", "0", "-o", temp.getAbsolutePath() + "/" + name + " - " + artist + ".flv", url});
                executeCommand(new String[]{Main.ffmpeg, "-i", temp.getAbsolutePath() + "/" + name + " - " + artist + ".flv", "-ab", "256k", output.getAbsolutePath() + "/" + currentPlaylist.name + "/" + name + " - " + artist + ".mp3"}); // ,"-ac", "2", "-ab", "128k"

            } catch (Exception e) {
                if(retry++ <= 3)
                    downloadTrack(name, artist, url);
                else
                    nextTrack();
            }

            nextTrack();
        }
    }

    private String executeCommand(String[] commands) {

        StringBuffer output = new StringBuffer();
        for (String command : commands) {
            System.out.print(command + " ");
        }
        System.out.println();

        Process p;
        try {
            p = Runtime.getRuntime().exec(commands);
            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
            errorGobbler.start();
            outputGobbler.start();
            p.waitFor();


        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return output.toString();

    }

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

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("Downloading webpage") || line.contains("Downloading video info webpage") || line.contains("Extracting video information") || line.contains("Downloading MPD manifest")) {
                        updateUI(currentTrack, States.EXTRACTING, level++ + "", "");
                    }
                    if (line.contains("[download]")) {
                        if (line.substring(11, 17).replace(" ", "").replaceAll("[^\\d.]", "").equals("100")) {
                            updateUI(currentTrack, States.DONE, "", "");
                        } else if (!line.substring(12, 17).replace(" ", "").replaceAll("[^\\d.]", "").equals("")) {
                            if(!line.contains("Unknown speed")) {
                                String speed = line.substring(32, 44).replace("", "");
//                                if (speed.charAt(speed.length()) == ' ')
//                                    speed = speed.substring(0, speed.length() - 1);
                                int cut = 1;
                                int kb = 0;
                                if (speed.contains("K"))
                                    kb = 1;
                                if (!speed.replaceAll("[^\\d.]", "").equals(""))
                                    speed = speed.substring(cut, speed.indexOf(".") + 3 - kb) + "\n" + speed.substring(speed.indexOf(".") + 3, speed.length());
                                updateUI(currentTrack, States.DOWNLOADING, line.substring(12, 17).replace(" ", ""), speed);

                            } else {
                                updateUI(currentTrack, States.DOWNLOADING, line.substring(12, 17).replace(" ", ""), "");

                            }
                        }
                    }
                    if(line.startsWith("size=")) {
                        String time = line.substring(21, 29).replace(":","");

                        int h = Integer.parseInt(time.substring(0, 2));
                        int m = Integer.parseInt(time.substring(2,4));
                        int s = Integer.parseInt(time.substring(4,6));
                        s += m*60 + h*60*60;
                        updateUI(currentTrack, States.RETRYING, s + "", "");
                    }
                    if(line.startsWith("video:")) {
                        updateUI(currentTrack, States.DONE, "", "");
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void finish() {
        deleteDirectory(temp);
        System.exit(0);
    }

    public static boolean deleteDirectory(File directory) {
        if (directory == null) {
            return false;
        }
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (File file : files)
                    if (file.isDirectory())
                        deleteDirectory(file);
                    else
                        file.delete();
            }
        }

        return (directory.delete());
    }

}

//            if(System.getProperty("os.name").contains("Windows")) { //windows os
//                if (executeCommand("java -d64 -version").toLowerCase().contains("Error: This Java instance does not support a 32-bit JVM.".toLowerCase())) { //jvm is 32 bit
//                    File curl = new File(temp.getAbsolutePath() + "/curl_win32.zip");
//                    downloadZip("https://dl.uxnr.de/build/curl/curl_winssl_cross_x86/curl-7.50.1/curl-7.50.1.zip", curl.getAbsolutePath());
//
//                } else { //jvm is 64 bit
//                    File curl = new File(temp.getAbsolutePath() + "/curl_win64.zip");
//                    downloadZip("https://dl.uxnr.de/build/curl/curl_winssl_cross_x64/curl-7.50.1/curl-7.50.1.zip", curl.getAbsolutePath());
//                }
//            } else { //unix
//
//            }


//                executeCommand(new String[]{"/bin/bash", "-c", "echo password |sudo curl -L https://yt-dl.org/downloads/latest/youtube-dl -o /usr/local/youtube-dl"});
//                executeCommand(new String[]{"/bin/bash", "-c", "echo password |sudo chmod a+rx /usr/local/youtube-dl"/});
