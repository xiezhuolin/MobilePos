package cn.acewill.mobile.pos.printer;

/**
 * 这里是一篇比较清晰的文章： http://www.cnblogs.com/aheizi/p/4792576.html
 * Created by Acewill on 2016/6/8.
 */
public class PrinterCommand {
    //结束本次打印 -- 也就是打印所有缓冲区中的内容并且切纸
    public static byte[] END_PRINT = new byte[]{0x0a, 0x1d, 0x56, 0x42, 0x00};

    /**
     * EPSON帧式打印指令
     * @return
     */
    public static byte[] FONT_SCALE_ZS(int widthScale, int heightScale){
        byte[] command = new byte[]{0x1c, 0x21, 4};
        byte scale = (byte) ((widthScale-1) * 4 + (heightScale-1) * 8);

        command[2] = scale;
        return command;
    }

    /**
     *
     * @param widthScale 宽度倍数， 1- 6
     * @param heightScale 高度倍数， 1-6
     * @return
     */
    public static byte[] FONT_SCALE(int widthScale, int heightScale) {
        byte[] command = new byte[]{0x1d, 0x21, 0x00};

        byte scale = (byte) ((widthScale -1) * 0x10 + (heightScale -1));

        command[2] = scale;
        return command;
    }

    /**
     * 设置字体是否加粗
     * @param bold true时字体加粗
     * @return
     */
    public static byte[] FONT_BOLD(boolean bold) {
        byte[] command = new byte[]{0x1b, 0x45, 0x00};

        byte scale =  (byte)(bold ? 0xF : 0);

        command[2] = scale;
        return command;
    }

    /**
     * 设置字体是否加粗
     * @param revert true时字体加粗
     * @return
     */
    public static byte[] REVERT_MODE(boolean revert) {
        byte[] command = new byte[]{0x1d, 0x42, 0x00};

        byte scale =  (byte)(revert ? 0xF : 0);

        command[2] = scale;
        return command;
    }

    /**
     *
     * @param underline true时字体加粗
     * @return
     */
    public static byte[] SHOW_UNDER_LINE(boolean underline) {
        byte[] command = new byte[]{0x1b, 0x2d, 0x00};

        byte scale =  (byte)(underline ? 0x2 : 0);

        command[2] = scale;
        return command;
    }

    /**
     * 设置字体是否加粗
     * @param font true时字体加粗
     * @return
     */
    public static byte[] FONT(Font font) {
        byte[] command = new byte[]{0x1b, 0x4d, 0x00};

        byte scale =  0x3;
        if (font == Font.REGULAR_ASCII) {
            scale = 0x0;
        } else if (font == Font.SMALL_ASCII) {
            scale = 0x1;
        }

        command[2] = scale;
        return command;
    }

    public static byte[] align(Alignment align) {
        byte[] command = new byte[]{0x1b, 0x61, 0x00};
        if (align == Alignment.CENTER) {
            command[2] = 0x1;
        } else if (align == Alignment.RIGHT) {
            command[2] = 0x2;
        }

        return command;
    }

    public static byte[] ROW_HEIGHT(int height) {
        byte[] command = new byte[]{0x1b, 0x33, (byte)height};


        return command;
    }

    /**
     *
     * @param height
     * @param position
     * @return
     */
    public static byte[] BAR_CODE(int height, BarcodePosition position) {
        //前3个字节开启条形码模式， 中间3个字符设置条形码中数字的位置，后三个字符设置条形码的类型
        byte[] command = new byte[]{0x1d, 0x68, (byte)height,0x1d, 0x48, (byte)position.getStatus(),0x1d, 0x6b, 0x02};


        return command;
    }

    public static byte[] EPSON_BAR_CODE(int height, int width, BarcodePosition position) {
        //前3个字节开启条形码模式， 中间3个字符设置条形码中数字的位置，后三个字符设置条形码的类型

        byte[] command = new byte[]{0x1d, 0x68, (byte)height,0x1d, 0x77, (byte)width,0x1d, 0x48, (byte)position.getStatus(),0x1d, 0x6b, 0x00};

        return command;
    }

    public static byte[] EPSON_BAR_CODE2(int height, int width, BarcodePosition position) {
        //前3个字节开启条形码模式， 中间3个字符设置条形码中数字的位置，后三个字符设置条形码的类型***
         //  at [Source: {"uid":"1542359973035331","payAmount":"5800.0","totalAmount":"5800.0",
        byte[] command = new byte[]{ 0x1b,0x28,0x42,0x03,0x00,0x02,0x00,0x7d,0x00,0x00,0x30,0x31,0x32};

        return command;
    }

    public  static byte[] CHECK_STATUS() {
        byte[] command = new byte[]{0x10, 0x04, 0x02};
        return command;
    }
}
