package cn.acewill.mobile.pos.printer;

/**
 * Created by linmingren on 16/8/3.
 */
public enum PrinterVendor {
    UNKNOWN("unknown"),
    SHANGMI_FIX("shangmi_fix"),
    SHANGMI_MOBILE("shangmi_mobile"),
    IBOX_MOBILE("ibox_mobile"),
    XINDALU_MOBILE("xindalu_mobile"),
    EPSON("epson"), //爱普生
    RONDTA("rongta"), //容大
    SPRT("sprt"),//思普瑞特
    POSIN_FIX("posin_fix"),
    YPT("ypt"); //滢普通

    private String name;

    PrinterVendor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static PrinterVendor fromName(String name) {
        for (PrinterVendor vendor : PrinterVendor.values()) {
            if (vendor.getName().equals(name)) {
                return vendor;
            }
        }

        return PrinterVendor.UNKNOWN;
    }

    public String getString()
    {
        switch (name)
        {
            case "shangmi_fix":
                return "商米POS";
            case "shangmi_mobile":
                return "商米移动Pos";
            case "ibox_mobile":
                return "盒子支付移动Pos";
            case "epson":
                return "爱普生";
            case "rongta":
                return "容大";
            case "ypt":
                return "滢普通";
            case "xindalu_mobile":
                return "新大陆移动POS";
            case "sprt":
                return "思普瑞特";
            case "posin_fix":
                return "POSIN POS";
            default:
                return "未知厂商";
        }
    }
}
