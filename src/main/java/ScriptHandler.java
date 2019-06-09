import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScriptHandler {

    private final static String AIR_ADB_PATH = ".air-adb";

    public static boolean writeScriptToHome(String content) {
        try {
            File adbAirFolder = new File(getHomeDir() + "/" + AIR_ADB_PATH);
            File adbScript = new File(getScriptPath());

            boolean folderExists = Files.exists(adbAirFolder.toPath());
            boolean fileExists = Files.exists(adbScript.toPath());

            if(folderExists) {
                if(!fileExists) {
                    Files.write(Paths.get(adbAirFolder.getPath() + "/" + "air-adb.sh"), content.getBytes());
                }
            }else{
                if(adbAirFolder.mkdir()) {
                    Files.write(Paths.get(adbAirFolder.getPath() + "/" + "air-adb.sh"), content.getBytes());
                }else{
                    return false;
                }
            }

            if (setScriptToExecutable()) {
                return true;
            }else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getScriptPath() {
        return getHomeDir() + "/" + AIR_ADB_PATH + "/" + "air-adb.sh";
    }

    private static String getHomeDir() {
        return System.getProperty("user.home");
    }

    private static boolean needUpdated() {
        //TODO: Check version of Script
        return false;
    }

    private static boolean setScriptToExecutable() {
        File script = new File(getScriptPath());
        return script.setExecutable(true, true);
    }
}
