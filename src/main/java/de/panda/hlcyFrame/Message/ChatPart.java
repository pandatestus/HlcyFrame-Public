package de.panda.hlcyFrame.Message;

import net.kyori.adventure.text.Component;

public class ChatPart {

    private Component part;
    private boolean cmd;
    private boolean hover;
    private boolean setInChat;
    private String cmdString;
    private String hoverString;
    private String setInChatString;

    public ChatPart(Component part, boolean cmd, boolean hover, boolean setInChat) {
        this.part = part;
        this.cmd = cmd;
        this.hover = hover;
        this.setInChat = setInChat;
    }

    public void setCmd(String cmdString) {
        this.cmdString = cmdString;
    }

    public void setHoverString(String hoverString) {
        this.hoverString = hoverString;
    }

    public void setSetInChatString(String setInChatString) {
        this.setInChatString = setInChatString;
    }

    public boolean isCmd() {
        return cmd;
    }

    public boolean isHover() {
        return hover;
    }

    public boolean isSetInChat() {
        return setInChat;
    }

    public String getCmdString() {
        return cmdString;
    }

    public String getHoverString() {
        return hoverString;
    }

    public String getSetInChatString() {
        return setInChatString;
    }

    public Component getText() {
        return part;
    }
}