package com.gedr.Managers;

import com.gedr.Main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;


public class JarHandler implements Runnable {

    public void run() {
        try {

            String prefix, destDirectory;

            prefix = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
            if(prefix.endsWith(".jar")) {
                destDirectory = prefix.substring(0, prefix.lastIndexOf('/')) + "/stuff";
                File f = new File(destDirectory);
                f.mkdirs();
                f.deleteOnExit();
            } else {
                destDirectory = prefix;
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
                Main.settingUp = false;
                return;
            }
            jar = new java.util.jar.JarFile(prefix);

            java.util.Enumeration enums = jar.entries();
            while(enums.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enums.nextElement();
                if(file.getName().contains("exes") && (file.getName().contains(os) || file.getName().contains("youtube-dl"))) {
                    System.out.println(file.getName());
                    java.io.File fi = new java.io.File(destDirectory + java.io.File.separator + file.getName());
                    if(!fi.exists()) {
                        if(file.isDirectory()) { // if its a directory, create it
                            fi.mkdirs();
                            continue;
                        }
                        File parent = new File(destDirectory + File.separator + file.getName()).getParentFile();
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

                        Files.setPosixFilePermissions(fi.toPath(), perms);
                    }
                }
            }
            jar.close();
            Main.settingUp = false;
            if(Main.loadingImg != null) Main.loadingImg.setVisible(false);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}


