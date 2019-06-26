package adbair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScriptHandler {

    private final static String AIR_ADB_PATH = ".air-adb";
    private final static String SCRIPT_VERSION = "# Version: 1";

    public static boolean writeScriptToHome(String content) {
        try {
            File adbAirFolder = new File(getHomeDir() + "/" + AIR_ADB_PATH);
            File adbScript = new File(getScriptPath());

            boolean folderExists = Files.exists(adbAirFolder.toPath());
            boolean fileExists = Files.exists(adbScript.toPath());

            if(folderExists) {
                if(!fileExists) {
                    Files.write(Paths.get(adbAirFolder.getPath() + "/" + "air-adb.sh"), content.getBytes());
                }else if(needUpdated()) {
                    Files.write(Paths.get(adbAirFolder.getPath() + "/" + "air-adb.sh"), content.getBytes());
                }
            }else{
                if(adbAirFolder.mkdir()) {
                    Files.write(Paths.get(adbAirFolder.getPath() + "/" + "air-adb.sh"), content.getBytes());
                }else{
                    return false;
                }
            }

            return setScriptToExecutable();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getScriptPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) {
            return getHomeDir() + "/" + AIR_ADB_PATH + "/" + "air-adb.sh";
        }else{
            String winPath = getHomeDir() + "\\" + AIR_ADB_PATH + "\\" + "air-adb.sh";
            winPath = winPath.replace("\\", "\\\\\\\\");
            return winPath;
        }

    }

    private static String getHomeDir() {
        return System.getProperty("user.home");
    }

    private static boolean needUpdated() {
        try {
            File adbAirFolder = new File(getHomeDir() + "/" + AIR_ADB_PATH);
            return ! Files.readAllLines(Paths.get(adbAirFolder.getPath() + "/" + "air-adb.sh"))
                    .contains(SCRIPT_VERSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean setScriptToExecutable() {
        File script = new File(getScriptPath());
        return script.setExecutable(true, true);
    }
}
