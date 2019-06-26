package adbair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Shell extends Thread {

    private static final String WSL_ERROR = "Something wen wrong. Are you sure Do you have WSL (Windows Subsystem for Linux) installed?";

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
            int exitValue;
            if(!isWindows()) {
                exitValue = unixExec(path);
            }else {
                String wslPath = wslPathConverter(path);

                if(wslPath.equals(WSL_ERROR)) {
                    iShell.onScriptEnd(1, WSL_ERROR);
                    return;
                }

                exitValue = windowsExec(wslPath);

                if (exitValue != 0) {
                    iShell.onScriptEnd(exitValue, WSL_ERROR);
                    return;
                }
            }

            iShell.onScriptEnd(exitValue, lastLine);
        } catch (Exception e) {
            e.printStackTrace();
            iShell.onScriptException(e.getMessage());
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
            throw new RuntimeException("execution of script failed : "
                    + lastLine);
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
            throw new RuntimeException("execution of script failed : "
                + lastLine);
        }

        return exitValue;
    }

    private String wslPathConverter(String path) throws IOException, InterruptedException{

        String command = "bash.exe -c \"wslpath -a " + path + "\"";

        ProcessBuilder processBuilder = new ProcessBuilder(command);
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
           return WSL_ERROR;
        }

        return lastLine;
    }


    private boolean isWindows() {
        return System.getProperty("os.name").contains("win");
    }

    interface IShell {
        void onScriptEnd(int exitValue, String line);
        void onScriptException(String message);
    }
}
