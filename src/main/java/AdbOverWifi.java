import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URL;


public class AdbOverWifi extends AnAction implements Shell.IShell {

    private Notification notification;
    private Project project;
    private Presentation presentation;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        this.project = e.getProject();
        this.presentation = e.getPresentation();

        String scriptContent = getScriptContent();
        boolean ret = ScriptHandler.writeScriptToHome(scriptContent);

        if (!ret) {
            notification = createNotification("Air-Adb: Couldnt write shell script to Home folder");
            return;
        }

        notification = createNotification("Air-Adb: Connecting...");

        presentation.setEnabled(false);

        Shell shell = new Shell(ScriptHandler.getScriptPath(), this);
        shell.start();
    }

    @Override
    public void onScriptEnd(int exitValue, String line) {
        presentation.setEnabled(true);
        if(exitValue == 0) {
            notification.expire();
            createNotification(line);
        }else{
            notification.expire();
            createNotification(line);
        }
    }

    @Override
    public void onScriptException() {
        notification.expire();
        createNotification("Air-Adb: Something went wrong");
    }

    private String getScriptContent() {
        URL url = ResourceUtil.getResource(getClass(), "", "air-adb.sh");
        VirtualFile file = VfsUtil.findFileByURL(url);
        return LoadTextUtil.loadText(file).toString();
    }

    private Notification createNotification(String message) {

        NotificationGroup notificationGroup = new NotificationGroup("myplugin", NotificationDisplayType.BALLOON, true);

        Notification notification = notificationGroup.createNotification("Air-Adb",
                message,
                NotificationType.INFORMATION,
                null);
        notification.notify(project);
        return notification;
    }
}
