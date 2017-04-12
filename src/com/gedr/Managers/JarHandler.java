package com.gedr.Managers;

import com.gedr.Main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JarHandler implements Runnable {

    public void run() {
        try {

            String prefix, destDirectory;

            prefix = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            if(prefix.endsWith(".jar")) {
                destDirectory = prefix.substring(0, prefix.lastIndexOf('/')) + "/appdata";
                File f = new File(destDirectory);
                f.mkdirs();
                f.deleteOnExit();
            } else {
                destDirectory = prefix;
                Main.statusLabel.setVisible(false);
                Main.settingUp = false;
            }

            java.util.jar.JarFile jar = null;

            Global.youtube_dl = destDirectory + "/exes/youtube-dl";

            String os = "";
            if(System.getProperty("os.name").contains("Windows")) { //windows os
                if(Global.executeCommand("java -d64 -version").toLowerCase().contains("Error: This Java instance does not support a 32-bit JVM.".toLowerCase())) { //jvm is 32 bit
                    Global.ffmpeg = destDirectory + "/exes/win32/ffmpeg.exe";
                    os = "win32";
                } else { //jvm is 64 bit
                    Global.ffmpeg = destDirectory + "/exes/win64/ffmpeg.exe";
                    os = "win64";
                }
            } else if(System.getProperty("os.name").toLowerCase().contains("mac")) { //osx
                Global.ffmpeg = destDirectory + "/exes/mac/ffmpeg";
                os = "mac";
            } //add linux.

            if(!prefix.endsWith(".jar")) { //for IDE development
                Main.statusLabel.setVisible(false);
                Main.settingUp = false;
                return;
            }
            jar = new java.util.jar.JarFile(prefix);

            java.util.Enumeration enums = jar.entries();

            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

            ses.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String text = Main.statusLabel.getText();
                    if(text.endsWith("..."))
                        text = text.substring(0,text.length()-3);
                    else
                        text += ".";
                    Main.statusLabel.setText(text);                }
            }, 0, 1000, TimeUnit.MILLISECONDS);

            if(!new File(Global.youtube_dl).exists() && new File(Global.ffmpeg).exists()) {
                new File(Global.ffmpeg).delete();
            }
            if(new File(Global.youtube_dl).length() != 1512320) {
                new File(Global.youtube_dl).delete();
            } else {
                System.out.println("Already extracted exes");
                Main.statusLabel.setVisible(false);
                Main.settingUp = false;
                return;
            }
            // when finished

            while(enums.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enums.nextElement();

                if(!file.getName().contains("src") && file.getName().contains("exes") && (file.getName().contains(os) || file.getName().contains("youtube-dl"))) {
                    java.io.File fi = new java.io.File(destDirectory + java.io.File.separator + file.getName());
                    if(!fi.exists()) {
                        if(file.isDirectory()) { // if its a directory, create it
                            fi.mkdirs();
                            continue;
                        }
                        File parent = new File(destDirectory + File.separator + file.getName()).getParentFile();
                        parent.mkdirs();

                        if(file.getName().contains("youtube-dl") || file.getName().contains("ffmpeg")) {
                            Main.statusLabel.setText("Extracting " + file.getName());
                            System.out.println("changing");
                        }
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

                        Files.setPosixFilePermissions(fi.toPath(), perms);
                    }
                }
            }

            jar.close();
            ses.shutdown();
            Main.statusLabel.setVisible(false);
            Main.settingUp = false;

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}


