package cn.acewill.mobile.pos.model.printer;

/**
 * Created by DHH on 2016/7/27.
 */
public class PrinterStyle {
    /**
     * 模块名
     */
    public String moduleName;
    /**
     * 模块类型
     */
    public int modeStyle;
    /**
     * 是否显示(选中显示)
     */
    public EnableStatus enableStatus;
    /**
     * 显示的位置  左中右
     */
    public ShowLocation showLocation;
    /**
     * 字体样式  加粗 还是 正常
     */
    public TextStyle textStyle;

    /**
     * 基础字体大小 20  几倍字体大小在这个基础上*2 *3 ...以此类推
     */
    public static int baseTextSize = 20;
    /**
     * 字体大小
     */
    public TextSize textSize;

    public static enum EnableStatus {
        UNSELECTED(1), //未选中
        SELECTED(2), ;//默认选中

        private int status;
        private EnableStatus(int status) {
            this.status = status;
        }
    }

    public static enum ShowLocation {
        Left(1), //左对齐
        Center(2), //居中
        Right(3), ;//右对齐

        private int status;
        private ShowLocation(int status) {
            this.status = status;
        }
    }

    public static enum TextStyle {
        Normal(1), //正常字体
        Bold(2), ;//加粗

        private int status;
        private TextStyle(int status) {
            this.status = status;
        }
    }

    public static enum TextSize {
        NormalSize(baseTextSize), //     1倍  正常字体大小
        TwoXSize(baseTextSize*2), //     2倍  正常字体大小
        ThereXSize(baseTextSize*3), ;//  3倍  正常字体大小

        private int status;
        private TextSize(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }

    }
}
