package com.gedr;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.nio.file.Files.setPosixFilePermissions;

public class Main {


    public static String success = "";
    public static String extractedJarPath;
    private JPanel loadingWrap;
    private JButton downloadButton;
    private boolean settingUp = true;

    public static void main(String[] args) {
        new Main();

//        try {
//            VGet v = new VGet(new URL("http://www.youtube.com/watch?v=1JyA1HZmjg8"), new File("/"));
//            v.download();
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }

    public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    public static JFrame frame;
    private JPanel pane;

    protected final static String clientId = "d81b37798a5b435bb1fb6ea1cc9369d6";
    protected final static String secret = "72c92e7ffab04d16992a949efeeaca9f";

    static SpotifySession currentSession;

    public static String youtube_dl;
    public static String ffmpeg;

    static JPanel formPane;
    static Grid grid;

    public Main() {
        String prefix = "";
        String destDirectory = "";
        try {

            prefix = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            if(prefix.endsWith(".jar")) {
                destDirectory = prefix.substring(0, prefix.lastIndexOf('/')) + "/stuff";
                File f = new File(destDirectory);
                f.mkdirs();
                f.deleteOnExit();
            } else {
                destDirectory = prefix;
                settingUp = false;
            }
            System.out.println(prefix);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String finalPrefix = prefix;
        final String finalDestDirectory = destDirectory;
        new Thread(new Runnable() {
            @Override
            public void run() {
                java.util.jar.JarFile jar = null;
                try {

                    youtube_dl = finalDestDirectory + "/exes/youtube-dl";

                    String os = "";
                    if (System.getProperty("os.name").contains("Windows")) { //windows os
                        if (executeCommand("java -d64 -version").toLowerCase().contains("Error: This Java instance does not support a 32-bit JVM.".toLowerCase())) { //jvm is 32 bit
                            ffmpeg = finalDestDirectory + "/exes/win32/ffmpeg.exe";
                            os="win32";
                        } else { //jvm is 64 bit
                            ffmpeg = finalDestDirectory + "/exes/win64/ffmpeg.exe";
                            os="win64";
                        }
                    } else if (System.getProperty("os.name").toLowerCase().contains("mac")) { //osx
                        ffmpeg = finalDestDirectory + "/exes/mac/ffmpeg";
                        os="mac";
                    } //add linux.

                    if(!finalPrefix.endsWith(".jar"))
                        return;
                    jar = new java.util.jar.JarFile(finalPrefix);

                    java.util.Enumeration enums = jar.entries();
                    while (enums.hasMoreElements()) {
                        java.util.jar.JarEntry file = (java.util.jar.JarEntry) enums.nextElement();
                        if(file.getName().contains("exes") && (file.getName().contains(os) || file.getName().contains("youtube-dl"))) {
                            System.out.println(file.getName());
                            java.io.File fi = new java.io.File(finalDestDirectory + java.io.File.separator + file.getName());
                            if(!fi.exists()) {
                                if (file.isDirectory()) { // if its a directory, create it
                                    fi.mkdirs();
                                    continue;
                                }
                                File parent = new File(finalDestDirectory + File.separator + file.getName()).getParentFile();
                                //if(!(parent.exists())) {
                                parent.mkdirs();
                                //}
                                java.io.InputStream is = jar.getInputStream(file); // get the input stream
                                java.io.FileOutputStream fos = new java.io.FileOutputStream(fi);
                                while (is.available() > 0) {  // write contents of 'is' to 'fos'
                                    fos.write(is.read());
                                }
                                fos.close();
                                is.close();
                                Set perms = new HashSet();
                                perms.add(PosixFilePermission.OTHERS_EXECUTE);
                                perms.add(PosixFilePermission.OWNER_READ);
                                perms.add(PosixFilePermission.OWNER_EXECUTE);
                                perms.add(PosixFilePermission.GROUP_EXECUTE);
                                perms.add(PosixFilePermission.GROUP_READ);
                                perms.add(PosixFilePermission.OWNER_WRITE);
                                perms.add(PosixFilePermission.GROUP_WRITE);
                                perms.add(PosixFilePermission.OTHERS_WRITE);
                                perms.add(PosixFilePermission.OTHERS_READ);

                                fi.setExecutable(true, false);
                                fi.setReadable(true, false);
                                fi.setWritable(true, false);

                                //perms.add(PosixFilePermission.OWNER_WRITE);
                                //Files.setPosixFilePermisions(fi.toPath(),
                                 //       EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE));
                                Files.setPosixFilePermissions(fi.toPath(), perms);                            }
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                settingUp = false;
                if(loadingWrap != null)
                    loadingWrap.setVisible(false);

            }
        }).start();


        frame = new JFrame("generic title");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //handle that later
        frame.setBounds((int) (screen.getWidth() * .2), (int) (screen.getHeight() * .2), (int) (screen.getWidth() * .6), (int) (screen.getHeight() * .6));
        frame.setResizable(false);
        pane = (JPanel) frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridBagLayout());

        formPane = new JPanel(new BorderLayout());
        JTextField idField = new JTextField("", 12);
        JButton loginButton = new JButton("Login");
        loginButton.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (idField.getText().equals("")) {
                    return;
                }
                currentSession = new SpotifySession(idField.getText());
                while (success.equals("")) {

                }
                if (success.equals("f")) {
                    return;
                } else {
                    pane.removeAll();
                    ArrayList<Playlist> playlists = currentSession.getPlaylists();
                    grid = new Grid(playlists.toArray(new Playlist[playlists.size()]));
                    JPanel wrapp = new JPanel();
                    wrapp.setLayout(new BoxLayout(wrapp, BoxLayout.Y_AXIS));
                    wrapp.setPreferredSize(new Dimension(Main.frame.getSize().width, 29 * (playlists.size() + 1)));


                    wrapp.add(grid);
                    JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    flow.setPreferredSize(new Dimension(Main.frame.getSize().width, 29 * (playlists.size() + 1)));

                    flow.add(wrapp);
                    pane.add(BorderLayout.CENTER, new JScrollPane(flow, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

                    ImageIcon img = new ImageIcon(getClass().getResource("/loading.gif"));
                    JLabel loadingImg = new JLabel(img);
                    img.setImageObserver(loadingImg);

                    JPanel divider = new JPanel(new BorderLayout());
                    JLabel loadingText = new JLabel(" Setting up... ");
                    loadingText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

                    loadingWrap = new JPanel(new BorderLayout());
                    loadingWrap.add(BorderLayout.CENTER, loadingText);
                    loadingWrap.add(BorderLayout.EAST, loadingImg);


                    JPanel paneel = new JPanel();
                    paneel.add(loadingWrap); //lmao these names

                    downloadButton = new JButton("Download Selected");
                    JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    flowWrap.setPreferredSize(new Dimension((downloadButton.getPreferredSize().width + 20) < 150 ? 250 : downloadButton.getPreferredSize().width + 20, 35));
                    flowWrap.add(downloadButton);
                    //wrap.setPreferredSize(new Dimension(-1, 35));
                    divider.add(paneel, BorderLayout.WEST);
                    divider.add(flowWrap, BorderLayout.EAST);
                    pane.add(BorderLayout.NORTH, divider);

                    if(!settingUp){
                        loadingWrap.setVisible(false);
                    } else {
                        loadingWrap.setVisible(true);
                    }

                    downloadButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (settingUp) {
                                return;
                            }
                            ArrayList<Playlist> toDownload = new ArrayList<Playlist>();
                            for (JCheckBox box : grid.checkBoxes) {
                                if (box.isSelected()) {
                                    for (Playlist p : playlists) {
                                        if (p.name.equals(box.getName())) {
                                            toDownload.add(p);
                                            currentSession.getTracks(p);
                                        }
                                    }
                                }
                            }
                            if (toDownload.isEmpty()) {
                                return;
                            }

                            pane.removeAll();

                            pane.repaint();
                            pane.revalidate();

                            JPanel tracklistWrap = new JPanel();
                            tracklistWrap.setLayout(new BoxLayout(tracklistWrap, BoxLayout.Y_AXIS));
                            int size = 0;
                            for (Playlist playlist : toDownload) {
                                Grid grid = new Grid(playlist.tracks);
                                tracklistWrap.add(grid);
                                size += playlist.tracks.length;
                                System.out.println(size);
                            }
                            tracklistWrap.setPreferredSize(new Dimension(Main.frame.getSize().width, 29 * (size + 1)));
                            JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
                            flow.setPreferredSize(new Dimension(Main.frame.getSize().width, 29 * (size + 1)));

                            flow.add(tracklistWrap);
                            final JScrollPane[] scroll = {null};

                                    scroll[0] = new JScrollPane(flow, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                            while(scroll[0] == null) {}
                            pane.add(BorderLayout.CENTER, scroll[0]);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DownloadManager downloader = new DownloadManager(toDownload.toArray(new Playlist[toDownload.size()]));
                                }
                            }).start();

//                        grid = new Grid(playlists.toArray(new Playlist[playlists.size()]));
//                        JPanel wrapp = new JPanel(new GridLayout());
//                        wrapp.add(grid);

                        }
                    });
                    pane.revalidate();
                }
            }
        };

        loginButton.addActionListener(action);
        idField.addActionListener(action);
        Image img = null;
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/spotify_icon_128.png"));
            //img = ImageIO.read(new File("src/spotify_icon_128.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel formWrap = new JPanel(new BorderLayout());
        JLabel icon = new JLabel(new ImageIcon(img));
        formWrap.add(BorderLayout.CENTER, icon);
        JLabel fill = new JLabel(" ");
        fill.setPreferredSize(new

                Dimension(1, (int)

                (Main.screen.height * .6 * .1)));

        formPane.add(BorderLayout.NORTH, fill);
        formPane.add(BorderLayout.CENTER, idField);
        formPane.add(BorderLayout.EAST, loginButton);
        formWrap.add(BorderLayout.SOUTH, formPane);
        center.add(formWrap);

        pane.add(BorderLayout.CENTER, center);

        frame.setVisible(true);
        idField.grabFocus();

    }

    public void decompress(String zipFilePath, String extractedFilePath) {
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry ze = zipInputStream.getNextEntry();
            while (ze != null) {
                String filename = ze.getName();
                File newFile = new File(zipFilePath + "/" + filename);
                if (!newFile.exists()) {
                    newFile.getParentFile().mkdirs();
                    newFile = new java.io.File(zipFilePath + "/" + filename);
                }
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }


    private static URI getJarURI()
            throws URISyntaxException {
        final ProtectionDomain domain;
        final CodeSource source;
        final URL url;
        final URI uri;

        domain = Main.class.getProtectionDomain();
        source = domain.getCodeSource();
        url = source.getLocation();
        uri = url.toURI();

        return (uri);
    }

    private static URI getFile(final URI where,
                               final String fileName)
            throws ZipException,
            IOException {
        final File location;
        final URI fileURI;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
            } finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    private static URI extract(final ZipFile zipFile,
                               final String fileName)
            throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
        }

        zipStream = zipFile.getInputStream(entry);
        fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(tempFile);
            buf = new byte[1024];
            i = 0;

            while ((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        } finally {
            close(zipStream);
            close(fileStream);
        }

        return (tempFile.toURI());
    }

    private static void close(final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void failureLogin() {
        formPane.setVisible(true);
        JLabel warning = new JLabel("User ID does not exist");
        warning.setForeground(Color.red);
        formPane.add(BorderLayout.NORTH, warning);
    }

}
