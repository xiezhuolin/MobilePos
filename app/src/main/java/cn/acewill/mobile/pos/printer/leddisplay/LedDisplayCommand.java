package cn.acewill.mobile.pos.printer.leddisplay;

/**
 * Created by Acewill on 2017/1/6.
 */

public class LedDisplayCommand {
    //关闭所有的灯
    public static byte[] TURNOFF_ALL_LIGHTS() {
        byte[] command = new byte[]{0x02, 0x4C, 0x30, 0x30,0x30,0x30};
        return command;
    }

    //显示整数
    public static byte[] SHOW_INTEGER_NUMBER(int number) {
        byte[] command = new byte[]{0x1B, 0x51, 0x41, 0x31,0x31,0x31};
        return command;
    }

    //显示整数
    public static byte[] TURN_ON_LIGHT() {
        byte[] command = new byte[]{0x1B, 0x73, 0x31};
        return command;
    }

    public static byte[] SHOW_FLOAT_NUMBER(float number) {
        byte[] command = new byte[]{0x02, 0x4C, 0x30, 0x30,0x30,0x30};
        return command;
    }

    //清除屏幕显示
    public static byte[] CLEAR_SCREEN() {
        byte[] command = new byte[]{0x0c};
        return command;
    }

    //返回商品的重量
    public static byte[] GOODS_WEIGHT() {
        byte[] command = new byte[]{0x24};
        return command;
    }
}
