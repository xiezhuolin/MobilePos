package cn.acewill.mobile.pos.model.event;

/**
 * Created by DHH on 2016/6/17.
 */
public class PrinterContentEvent {
    int action;
    String content;
    public PrinterContentEvent(int action)
    {
        this.action = action;
    }
    public PrinterContentEvent(int action, String content)
    {
        this.action = action;
        this.content = content;
    }
    public int getAction() {
        return action;
    }

    public String getContent() {
        return content;
    }
}
