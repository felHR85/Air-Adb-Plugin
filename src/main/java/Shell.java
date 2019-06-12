import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Shell extends Thread {

    private String path;
    private String line, lastLine;
    private IShell iShell;

    public Shell(String path, IShell iShell) {
        this.path = path;
        this.iShell = iShell;
    }

    @Override
    public void run() {
        try {
            int exitValue = 1;
            if(!isWindows()) {
                exitValue = unixExec(path);
            }else {
                exitValue = windowsExec(path);

                if (exitValue != 0) {
                    //TODO: Do you have WSL installed?
                    //TODO: Reboot
                }
            }

            iShell.onScriptEnd(exitValue, lastLine);
        } catch (Exception e) {
            e.printStackTrace();
            iShell.onScriptException();
        }
    }

    private int unixExec(String path) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(path);
        //Sets the source and destination for subprocess standard I/O to be the same as those of the current Java process.
        //processBuilder.inheritIO();
        Process process = processBuilder.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = in.readLine()) != null) {
            lastLine = line;
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            // check for errors
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("execution of script failed!");
        }

        return exitValue;
    }

    private int windowsExec(String path) throws IOException, InterruptedException{
        ProcessBuilder processBuilder = new ProcessBuilder("bash.exe", path);
        //Sets the source and destination for subprocess standard I/O to be the same as those of the current Java process.
        //processBuilder.inheritIO();
        Process process = processBuilder.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = in.readLine()) != null) {
            lastLine = line;
        }

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            // check for errors
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("execution of script failed!");
        }

        return exitValue;
    }


    private boolean isWindows() {
        return System.getProperty("os.name").contains("win");
    }

    interface IShell {
        void onScriptEnd(int exitValue, String line);
        void onScriptException();
    }
}
