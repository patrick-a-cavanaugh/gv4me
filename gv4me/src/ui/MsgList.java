/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import gvME.gvME;
import gvME.gvMsgList;
import gvME.parseMsgs;
import gvME.textConvo;
import gvME.textMsg;
import gvME.tools;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;

/**
 *
 * @author matt
 */
public class MsgList extends List implements CommandListener {

    private int itemLength = 20;
    private static Vector MsgListToItemMap = new Vector(10);
    private Command viewMsgCmd;
    private Command replyCmd;
    private Command backCmd;
    private Command fwdCmd;
    private Form readMsg;
    private Form msgProps;
    private Command msgPropsCmd;

    public MsgList(String title, Vector msgs) throws IOException
    {
        super(title, List.IMPLICIT);
        addCommand(getViewMsgCmd());
        addCommand(getReplyCmd());
        addCommand(getFwdCmd());
        addCommand(getBackCmd());
        setCommandListener(this);
        setFitPolicy(Choice.TEXT_WRAP_OFF);
        setSelectCommand(getViewMsgCmd());

        addItemsToMsgList(msgs);     
    }
    
    public void addItemsToMsgList(Vector msgs)
    {
        deleteAll();
        Enumeration msgListEnum = gvMsgList.getMsgList(msgs).elements();
        
        String msgListItem;
        while(msgListEnum.hasMoreElements())
        {
            msgListItem = (String) msgListEnum.nextElement();
            append(msgListItem, null);
        }
    }

    public Vector getMsgListToItemMap()
    {
        return MsgList.MsgListToItemMap;
    }
    
    public static void setMsgListToItemMap(Vector msgListToItemMap)
    {
        MsgList.MsgListToItemMap = msgListToItemMap;
    }

    private Form getReadMsg(String title)
    {
        if(readMsg == null)
        {
            readMsg = new Form("From: "+title);
            readMsg.addCommand(replyCmd);
            readMsg.addCommand(fwdCmd);
            readMsg.addCommand(getMsgPropsCmd());
            readMsg.addCommand(backCmd);
            readMsg.setCommandListener(this);
        }
        else
        {
            readMsg.deleteAll();
        }
        return readMsg;
    }

    private Form getMsgProps(textConvo msg)
    {
        msgProps = new Form("Properties");
        msgProps.addCommand(getBackCmd());
        StringItem sender = new StringItem("Sender: ", msg.getSender());
        msgProps.append(sender);
        StringItem replyNum = new StringItem("Phone Number: ", msg.getReplyNum());
        msgProps.append(replyNum);
        StringItem date = new StringItem("Sent: ", msg.getMsg().getTimeReceived()+" "+msg.getDate());
        msgProps.append(date);
        msgProps.setCommandListener(this);
        return msgProps;
    }

    public void commandAction(Command command, Displayable displayable) {
        int selIndex = this.getSelectedIndex();
        System.out.println(String.valueOf(selIndex));
        textMsg original = null;
        textConvo origConvo = null;
        if(!MsgListToItemMap.isEmpty())
        {
            original = (textMsg) MsgListToItemMap.elementAt(selIndex);
            origConvo = (textConvo) parseMsgs.getStoredConvosHash().get(original.getMsgID());
        }
        if(displayable == this)
        {
            if(command == backCmd)
            {
                try {
                    gvME.dispMan.switchDisplayable(null, gvME.getInbox());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(command == viewMsgCmd)
            {
                gvME.dispMan.switchDisplayable(null, getReadMsg(origConvo.getSender()));
                readMsg.append(tools.decodeString(original.getMessage())); //decodes string from utf8
            }
            else if(command == fwdCmd)
            {
                WriteMsg wm = new WriteMsg("Forward", origConvo);
                gvME.dispMan.switchDisplayable(null, wm);
            }
            else if(command == replyCmd)
            {
                WriteMsg wm = new WriteMsg("Reply", origConvo);
                gvME.dispMan.switchDisplayable(null, wm);
            }
        }
        else if(displayable == readMsg)
        {
            if(command == backCmd)
            {
                gvME.dispMan.switchDisplayable(null, this);
            }
            else if(command == msgPropsCmd)
            {
                textConvo propsConvo = origConvo;
                propsConvo.setLastMsg(original);
                gvME.dispMan.switchDisplayable(null, getMsgProps(propsConvo));
            }
            else if(command == fwdCmd)
            {
                WriteMsg wm = new WriteMsg("Forward", origConvo);
                gvME.dispMan.switchDisplayable(null, wm);
            }
            else if(command == replyCmd)
            {
                WriteMsg wm = new WriteMsg("Reply", origConvo);
                gvME.dispMan.switchDisplayable(null, wm);
            }
        }
        else if(displayable == msgProps)
        {
            if(command == backCmd)
            {
                gvME.dispMan.switchDisplayable(null, this);
            }
        }
    }

    private Command getViewMsgCmd() {
        if (viewMsgCmd == null) {
            viewMsgCmd = new Command("Read", Command.OK, 1);
        }
        return viewMsgCmd;
    }

    private Command getBackCmd() {
        if (backCmd == null) {
            backCmd = new Command("Back", Command.BACK, 2);
        }
        return backCmd;
    }

    private Command getMsgPropsCmd() {
        if (msgPropsCmd == null) {
            msgPropsCmd = new Command("Properties", Command.ITEM, 4);
        }
        return msgPropsCmd;
    }

    private Command getReplyCmd() {
        if (replyCmd == null) {
            replyCmd = new Command("Reply", Command.ITEM, 3);
        }
        return replyCmd;
    }

    private Command getFwdCmd() {
        if (fwdCmd == null) {
            fwdCmd = new Command("Forward", Command.ITEM, 6);
        }
        return fwdCmd;
    }
}
