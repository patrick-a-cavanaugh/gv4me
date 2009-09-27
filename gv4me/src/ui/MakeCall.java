/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.*;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.HttpsConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 *
 * @author matt
 */
public class MakeCall extends WaitScreen implements CommandListener, interCom {
    private String contacting = "";
    private Alert noCallFromAlert;
    private static final String callURL = "https://www.google.com/voice/call/connect/";
    private Vector reqProps;
    private String rnr;
 //   private Image image;

    public MakeCall()
    {
        super(gvME.dispMan.getDisplay());
        setTitle("Making Call");
        setCommandListener(this);
     //   setImage(getImage());
        if(gvME.userSettings.getCallFrom() == null || gvME.userSettings.getCallFrom().equals(""))
        {
            gvME.dispMan.switchDisplayable(getNoCallFromAlert(), gvME.getChangeSettingsMenu());
        }
        setText("Making Call...");
        setTask(getSimpleCancellableTask());
        this.reqProps = parseMsgs.getReqProps();
        this.rnr = gvME.getRNR();
    }

    public SimpleCancellableTask getSimpleCancellableTask() {
        SimpleCancellableTask task = new SimpleCancellableTask();
        task.setExecutable(new org.netbeans.microedition.util.Executable() {
            public void execute() throws Exception {
                makeCall(contacting);
            }
        });
        return task;
    }

    private void makeCall(String contacting) throws IOException, Exception
    {
        String[] strings = {"outgoingNumber=+1", contacting, "&forwardingNumber=+1", gvME.userSettings.getCallFrom(), "&subscriberNumber=undefined&remember=0&_rnr_se=", rnr};
        String postData = tools.combineStrings(strings);
        String[] contentLen = {"Content-Length", String.valueOf(postData.length())};
        reqProps.insertElementAt(contentLen, 2);
        HttpsConnection c = createConnection.open(callURL, "POST", reqProps, postData);
        createConnection.close(c);
    }
    
    private Alert getNoCallFromAlert()
    {
        if(noCallFromAlert == null)
        {
            noCallFromAlert = new Alert("Number Not Found", "Enter Your Number", null, AlertType.WARNING);
            noCallFromAlert.setTimeout(2000);
        }
        return noCallFromAlert;
    }

    public void setContacting(String num) {
        this.contacting = num;
    }

    public void commandAction(Command command, Displayable display) {
            if (command == WaitScreen.FAILURE_COMMAND) {
//TODO:         switchDisplayable(getAlert(), getCallWaitScreen());
                System.out.println("Failed");
            } else if (command == WaitScreen.SUCCESS_COMMAND) {
                gvME.dispMan.showMenu();
            }
    }
}
