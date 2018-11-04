package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/6/8.
 */
public class Column {
    protected String content; //需要打印的内容
    private Alignment alignment = Alignment.LEFT;


    public Column(String content) {
        this.content = content;
    }

    public Column(String content, Alignment alignment) {
        this.content = content;
        this.alignment = alignment;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
}
