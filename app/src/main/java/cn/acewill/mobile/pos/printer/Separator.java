package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/8/22.
 */
public class Separator implements Row {
    private String content;
    //
    public Separator(String seperator) {
        this.content = seperator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getScaleWidth() {
        return 1;
    }
}
