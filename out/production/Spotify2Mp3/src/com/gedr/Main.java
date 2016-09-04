package com.gedr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

    public static String success = "";
    public static String extractedJarPath;
    private final HintTextField idField;
    private JButton downloadButton;
    private boolean settingUp = true;
    public static JScrollPane scroll;

    public static int trackNumber = 0;
    private LoadingLabel loadingImg;

    public static void scrollDown() {
        //        JViewport view = scroll.getViewport();
        //        view.setViewPosition(new Point((int) view.getViewPosition().getX(), (int) view.getViewPosition().getY() + 30));
        //        scroll.setViewport(view);

        JScrollBar vscroll = scroll.getVerticalScrollBar();

        if(vscroll.getValue() + vscroll.getVisibleAmount() + +vscroll.getMaximum() / trackNumber < vscroll.getMaximum()) {
            vscroll.setValue(vscroll.getValue() + vscroll.getMaximum() / trackNumber);
        }
    }

    public static void main(String[] args) {

        //javax.swing.UIManager.put("Button.background", new javax.swing.plaf.ColorUIResource(38,38,38));
        //UIManager.put(scroll, Color.RED);

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

    public static Font fontThin;
    public static Font fontNormal;
    public static Font fontBold;

    public Main() {
        try {
            fontNormal = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Montserrat-Light.otf"));
            fontThin = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Montserrat-Hairline.otf"));
            fontBold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Montserrat-Regular.otf"));

        } catch(IOException | FontFormatException e) {
            e.printStackTrace();
        }

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

        } catch(Exception e) {
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
                    if(System.getProperty("os.name").contains("Windows")) { //windows os
                        if(executeCommand("java -d64 -version").toLowerCase().contains("Error: This Java instance does not support a 32-bit JVM.".toLowerCase())) { //jvm is 32 bit
                            ffmpeg = finalDestDirectory + "/exes/win32/ffmpeg.exe";
                            os = "win32";
                        } else { //jvm is 64 bit
                            ffmpeg = finalDestDirectory + "/exes/win64/ffmpeg.exe";
                            os = "win64";
                        }
                    } else if(System.getProperty("os.name").toLowerCase().contains("mac")) { //osx
                        ffmpeg = finalDestDirectory + "/exes/mac/ffmpeg";
                        os = "mac";
                    } //add linux.

                    if(!finalPrefix.endsWith(".jar")) { //for IDE development
                        settingUp = false;
                        return;
                    }
                    jar = new java.util.jar.JarFile(finalPrefix);

                    java.util.Enumeration enums = jar.entries();
                    while(enums.hasMoreElements()) {
                        java.util.jar.JarEntry file = (java.util.jar.JarEntry) enums.nextElement();
                        if(file.getName().contains("exes") && (file.getName().contains(os) || file.getName().contains("youtube-dl"))) {
                            System.out.println(file.getName());
                            java.io.File fi = new java.io.File(finalDestDirectory + java.io.File.separator + file.getName());
                            if(!fi.exists()) {
                                if(file.isDirectory()) { // if its a directory, create it
                                    fi.mkdirs();
                                    continue;
                                }
                                File parent = new File(finalDestDirectory + File.separator + file.getName()).getParentFile();
                                //if(!(parent.exists())) {
                                parent.mkdirs();
                                //}
                                java.io.InputStream is = jar.getInputStream(file); // get the input stream
                                java.io.FileOutputStream fos = new java.io.FileOutputStream(fi);
                                while(is.available() > 0) {  // write contents of 'is' to 'fos'
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
                                Files.setPosixFilePermissions(fi.toPath(), perms);
                            }
                        }
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                }
                settingUp = false;
                if(loadingImg != null) loadingImg.setVisible(false);

            }
        }).start();

        frame = new JFrame("Spotify to MP3");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //handle that later
        frame.setSize(864, 540);
        frame.setLocation((int) screen.getWidth() / 2 - 864 / 2, (int) screen.getHeight() / 2 - 540 / 2);
        frame.setResizable(false);

        pane = (JPanel) frame.getContentPane();
        pane.setOpaque(true);
        pane.setBackground(new Color(28, 28, 28));
        pane.setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridBagLayout());

        formPane = new JPanel(new BorderLayout());
        idField = new HintTextField("enter username");
        idField.setColumns(14);
        JButton loginButton = new JButton("Login");
        loginButton.setFont(fontBold.deriveFont(14f));
        AffineTransform affinetransform = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        int textwidth = (int) (fontBold.deriveFont(14f).getStringBounds("Login", frc).getWidth());
        loginButton.setPreferredSize(new Dimension(textwidth + 20, loginButton.getPreferredSize().height));
        idField.setPreferredSize(new Dimension(loginButton.getPreferredSize().width, loginButton.getPreferredSize().height));

        loginButton.setForeground(Color.gray);
        loginButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setForeground(Color.white);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setForeground(Color.gray);
            }
        });
        loginButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        loginButton.setOpaque(false);

        ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        if(idField.getText().equals("")) return;
                        if(idField.loading) return;
                        idField.loading = true;
                        idField.repaint();
                        idField.revalidate();
                        currentSession = new SpotifySession(idField.getText());
                        ArrayList<Playlist> playlists = currentSession.getPlaylists();
                        if(!playlists.isEmpty()) {
                            setupPlaylistGrid(playlists.toArray(new Playlist[playlists.size()]));

                        } else {
                            idField.red = Color.red;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while(idField.red.getAlpha() != 0) {
                                        long now = System.currentTimeMillis();
                                        while(System.currentTimeMillis() < now + 18) {
                                        }
                                        idField.red = new Color(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), idField.red.getAlpha() - 5);
                                        idField.repaint();
                                        idField.revalidate();
                                    }

                                }
                            }).start();
                        }
                        idField.loading = false;
                        idField.running = false;
                        idField.thread = null;
                    }
                }).start();
            }
        };

        idField.addActionListener(action);
        loginButton.addActionListener(action);

        Image img = null;
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/spotify_icon_128.png"));
            //img = ImageIO.read(new File("src/spotify_icon_128.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        JPanel formWrap = new JPanel(new BorderLayout());
        JLabel icon = new JLabel(new ImageIcon(img));
        formWrap.add(BorderLayout.CENTER, icon);
        JLabel fill = new JLabel(" ");
        fill.setPreferredSize(new Dimension(1, (int) (Main.screen.height * .6 * .1)));

        formPane.add(BorderLayout.NORTH, fill);
        formPane.add(BorderLayout.CENTER, idField);
        formPane.add(BorderLayout.EAST, loginButton);
        formWrap.setOpaque(false);
        formPane.setOpaque(false);
        center.setOpaque(false);

        formWrap.add(BorderLayout.SOUTH, formPane);
        center.add(formWrap);

        pane.add(BorderLayout.CENTER, center);

        //formPane.setPreferredSize(new Dimension(formPane.getPreferredSize().width, idField.getHeight()));

        frame.setVisible(true);
        idField.transferFocus();

    }

    boolean running = true;

    public void setupPlaylistGrid(Playlist[] playlists) {
        pane.removeAll();
        pane.setOpaque(true);
        pane.setBackground(new Color(28, 28, 28));

        grid = new Grid(playlists);
        JPanel wrapp = new JPanel();
        wrapp.setLayout(new BoxLayout(wrapp, BoxLayout.Y_AXIS));
        wrapp.setPreferredSize(new Dimension(Main.frame.getSize().width, (29 + 10) * (playlists.length)));
        wrapp.setOpaque(true);
        //wrapp.setBackground(new Color(38, 38, 38));

        wrapp.add(grid);
        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flow.setOpaque(false);
        flow.setPreferredSize(new Dimension(Main.frame.getSize().width, (29 + 10) * (playlists.length)));

        flow.add(wrapp);
        scroll = new JScrollPane(flow, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(28, 28, 28));
        scroll.setBorder(null);
        //scroll.getVerticalScrollBar().setBackground(new Color(38, 38, 38));
        scroll.getVerticalScrollBar().setBorder(null);
        scroll.getVerticalScrollBar().setUI(new SpotifyScroll());

        pane.add(BorderLayout.CENTER, scroll);
        loadingImg = new LoadingLabel(4);
        loadingImg.setPreferredSize(new Dimension(42, 42));
        JPanel gridBag = new JPanel(new GridBagLayout());
        gridBag.add(loadingImg);
        gridBag.setOpaque(false);
        gridBag.setPreferredSize(new Dimension(70, 50));

        JPanel divider = new JPanel(new BorderLayout());
        divider.add(gridBag, BorderLayout.CENTER);
        divider.setOpaque(false);

        downloadButton = new JButton("Download Selected");
        downloadButton.setFont(Main.fontNormal.deriveFont(14f));
        downloadButton.setForeground(Color.gray);
        downloadButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                downloadButton.setForeground(Color.white);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                downloadButton.setForeground(Color.gray);
            }
        });
        downloadButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));
        downloadButton.setOpaque(false);

        JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flowWrap.setOpaque(false);
        flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 - loadingImg.getPreferredSize().getWidth()), 50));
        JPanel gridWrap = new JPanel(new GridBagLayout());
        gridWrap.add(downloadButton);
        gridWrap.setPreferredSize(new Dimension(downloadButton.getPreferredSize().width, 50));
        gridWrap.setOpaque(false);
        flowWrap.add(gridWrap);
        //wrap.setPreferredSize(new Dimension(-1, 35));
        divider.add(gridBag, BorderLayout.WEST);
        divider.add(flowWrap, BorderLayout.EAST);
        divider.setOpaque(true);
        divider.setBackground(new Color(38, 38, 38));
        pane.add(BorderLayout.NORTH, divider);

        if(!settingUp) {
            loadingImg.setVisible(false);
        } else {
            loadingImg.setVisible(true);
        }

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(settingUp) {
                    return;
                }
                //                JFileChooser fileChooser = new JFileChooser();
                //                fileChooser.setAcceptAllFileFilterUsed(false);
                //                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //                fileChooser.setBounds((int) (Main.screen.getWidth() * .2), (int) (Main.screen.getHeight() * .2), (int) (Main.screen.getWidth() * .6), (int) (Main.screen.getHeight() * .6));
                //                if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                //                    System.out.println("getCurrentDirectory(): " + fileChooser.getCurrentDirectory());
                //                    System.out.println("getSelectedFile() : " + fileChooser.getSelectedFile());
                //                    if(fileChooser.getSelectedFile() == null)
                //                        DownloadManager.output = fileChooser.getCurrentDirectory();
                //                    else DownloadManager.output = fileChooser.getSelectedFile();
                //                }
                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                FileDialog fc = new FileDialog(frame, "Choose directory to download playlist into");
                fc.setVisible(true);
                String fn = fc.getFile();
                if(fn == null) return;
                else DownloadManager.output = new File(fc.getDirectory() + "/" + fn);
                System.out.println("out : " + DownloadManager.output.getAbsolutePath());
                System.setProperty("apple.awt.fileDialogForDirectories", "false");

                loadingImg.setVisible(true);

                if(DownloadManager.output != null) {
                    DownloadManager.temp = new File(DownloadManager.output.getAbsolutePath() + "/_temp");
                    DownloadManager.temp.mkdirs();
                    DownloadManager.temp.deleteOnExit();
                } else {
                    return;
                }

                ArrayList<Playlist> toDownload = new ArrayList<Playlist>();
                for(SpotifyCheckBox box : grid.checkBoxes) {
                    if(box.checked) {
                        for(Playlist p : playlists) {
                            if(p.name.equals(box.getName())) {
                                if(p.total != 0) {
                                    toDownload.add(p);
                                    currentSession.getTracks(p);
                                }
                            }
                        }
                    }
                }

                if(toDownload.isEmpty()) {
                    return;
                }

                int height = 39; //adjust for dividers

                JPanel tracklistWrap = new JPanel();
                tracklistWrap.setLayout(new BoxLayout(tracklistWrap, BoxLayout.Y_AXIS));
                int size = 0;
                int index = 0;
                for(Playlist playlist : toDownload) {
                    if(playlist.tracks.length != 0) {
                        Grid grid = new Grid(playlist.tracks, playlist.name);
                        System.out.println(playlist.name + " - " + playlist.tracks.length);
                        grid.setPreferredSize(new Dimension(Main.frame.getSize().width, height * (playlist.tracks.length + 1) + 5 * playlist.tracks.length + 1));
                        tracklistWrap.add(grid);
                        if(index++ + 1 != toDownload.size()) {
                            Separator sep = new Separator(true);
                            tracklistWrap.add(Box.createVerticalStrut(12));
                            sep.setMinimumSize(new Dimension(Main.frame.getWidth() - 20, 20));
                            tracklistWrap.add(sep);
                            tracklistWrap.add(Box.createVerticalStrut(9));

                        }
                        size += playlist.tracks.length + 1;
                    } else {
                        toDownload.remove(playlist);
                        toDownload.trimToSize();
                    }
                }
                trackNumber = size;
                //System.out.println(toDownload.size());
                tracklistWrap.setPreferredSize(new Dimension(Main.frame.getSize().width, (index) * 28 + (height) * (size) - (toDownload.size() == 1 ? 36 : 0)));

                tracklistWrap.setOpaque(false);
                JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
                flow.setOpaque(false);
                //flow.setPreferredSize(new Dimension(Main.frame.getSize().width, (29 + 8) * (size)));

                flow.add(tracklistWrap);
                scroll = new JScrollPane(flow, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.getVerticalScrollBar().setUnitIncrement(19);
                scroll.getViewport().setOpaque(true);
                scroll.getViewport().setBackground(new Color(28, 28, 28));
                scroll.setBorder(null);
                scroll.getVerticalScrollBar().setBorder(null);

                scroll.getVerticalScrollBar().setUI(new SpotifyScroll());

                pane.removeAll();
                pane.repaint();
                pane.revalidate();
                pane.add(BorderLayout.CENTER, scroll);
                pane.setOpaque(false);
                running = false;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadManager downloader = new DownloadManager(toDownload.toArray(new Playlist[toDownload.size()]));
                    }
                }).start();

            }
        });
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    private static URI getJarURI() throws URISyntaxException {
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

    private static URI getFile(final URI where, final String fileName) throws IOException {
        final File location;
        final URI fileURI;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if(location.isDirectory()) {
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

    private static URI extract(final ZipFile zipFile, final String fileName) throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if(entry == null) {
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

            while((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        } finally {
            close(zipStream);
            close(fileStream);
        }

        return (tempFile.toURI());
    }

    private static void close(final Closeable stream) {
        if(stream != null) {
            try {
                stream.close();
            } catch(final IOException ex) {
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

class HintTextField extends JTextField implements FocusListener {

    private final String hint;
    private boolean showingHint;
    public Thread thread;
    public boolean running;
    private double degree;

    public HintTextField(final String hint) {
        super(hint);
        setFont(Main.fontNormal.deriveFont(13f));
        this.hint = hint;
        this.showingHint = true;
        setForeground(Color.lightGray);
        setOpaque(false);
        int s = shadeWidth + 1 + textSpacing;
        setBorder(BorderFactory.createEmptyBorder(s, s + 5, s, s + 5));
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText("");
            setForeground(Color.black);
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText(hint);
            setForeground(Color.lightGray);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }

    private Shape shape;

    private int round = 13;
    private int shadeWidth = 1;
    private int textSpacing = 3;
    public boolean loading;
    Color green = new Color(9, 166, 73);
    Color red = new Color(255, 0, 0, 0);

    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape border = getBorderShape();

        Stroke os = g2d.getStroke();
        g2d.setStroke(new BasicStroke(shadeWidth * 3));
        g2d.setPaint(Color.BLACK);

        g2d.draw(border);
        g2d.setStroke(os);

        g2d.setPaint(Color.WHITE);
        g2d.fill(border);
        g2d.setPaint(Color.WHITE);

        if(loading) {
            if(!running) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(loading) {
                            long now = System.currentTimeMillis();
                            while(System.currentTimeMillis() < now + 2) {
                            }
                            degree += .2;
                            repaint();
                            revalidate();
                        }
                    }
                });
                thread.start();
                running = true;
            }
            g2d.setStroke(new BasicStroke(2));

            g2d.setColor(green);
            int r = Math.min(getWidth(), getHeight()) - 10;
            Arc2D arc = new Arc2D.Double(getWidth() - r - 6, 5, r, r, (degree % 360), 300, Arc2D.OPEN);
            g2d.draw(arc);
        }

        if(red.getAlpha() > 0) {
            g2d.setColor(red);
            g2d.setFont(Main.fontBold.deriveFont(16f));
            g2d.drawString("!", getWidth() - 17, 20);
        }

        super.paintComponent(g);
    }

    private Shape getBorderShape() {
        JTextComponent component = this;
        if(round > 0) {
            return new RoundRectangle2D.Double(shadeWidth, shadeWidth, component.getWidth() - shadeWidth * 2 - 1, component.getHeight() - shadeWidth * 2 - 1, round * 2, round * 2);
        } else {
            return new Rectangle2D.Double(shadeWidth, shadeWidth, component.getWidth() - shadeWidth * 2 - 1, component.getHeight() - shadeWidth * 2 - 1);
        }
    }

    public boolean contains(int x, int y) {
        if(shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
        return shape.contains(x, y);
    }
}

class SpotifyScroll extends BasicScrollBarUI {

    Color background = new Color(28, 28, 28);

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(background);
        g.fillRect(0, 0, (int) trackBounds.getWidth(), (int) trackBounds.getHeight());
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(new Color(52, 52, 52));
        g2d.translate(thumbBounds.x, thumbBounds.y);
        g2d.fillRoundRect(4, 3, (int) thumbBounds.getWidth() - 7, (int) thumbBounds.getHeight() - 6, 10, 8);
        g2d.translate(-thumbBounds.x, -thumbBounds.y);
        g2d.dispose();

    }
}

class LoadingLabel extends JLabel {
    int degree = 0;

    public LoadingLabel(int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    long now = System.currentTimeMillis();
                    while(System.currentTimeMillis() < now + 10) {
                    }
                    degree++;
                    repaint();
                    revalidate();
                }
            }
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color green = new Color(9, 166, 73);

        g2d.setStroke(new BasicStroke(getHeight() / 12));

        g2d.setColor(green);
        int r = Math.min(getWidth(), getHeight()) - 10;
        Arc2D arc = new Arc2D.Double(5, 5, r, r, (degree % 360), 300, Arc2D.OPEN);
        g2d.draw(arc);
        g2d.dispose();
    }
}