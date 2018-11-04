package cn.acewill.mobile.pos.model.table;

/**
 * Created by hzc on 2017/1/4.
 */

public class UiProperties {
    public int x;//": 537,
    public int y;//": 10,
    public int width;//": 155,
    public int height;//": 155,
    public String type;//": "rect",circle
    public int screen_width;//": 0,
    public int screen_height;//": 0

    public boolean isCircle(){
        return "circle".equals(type);
    }
    public boolean isRect(){
        return "rect".equals(type);
    }
}
