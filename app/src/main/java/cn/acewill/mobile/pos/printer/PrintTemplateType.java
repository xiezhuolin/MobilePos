package cn.acewill.mobile.pos.printer;

/**
 * Created by Administrator on 2017-02-17.
 */
public enum PrintTemplateType
{
    CUSTOMER(0),//客用单
    ADD_DISH(1),//加菜单
    RETREAT_DISH(2),//退菜单
    PRE_CHECKOUT(3),//预结单
    CHECKOUT(4),//结账单
    LABEL(5),//标签
    KICHEN(6);//厨房单
    private int value;
    private PrintTemplateType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }

    public String getString()
    {
        switch(value)
        {
            case 0:
                return "客用单";
            case 1:
                return "加菜单";
            case 2:
                return "退菜单";
            case 3:
                return "预结单";
            case 4:
                return "结账单";
            case 5:
                return "标签单";
            case 6:
                return "厨房单";
            default:
                return null;
        }
    }

    public String getKeyString()
    {
        switch(value)
        {
            case 0:
                return "CUSTOMER";
            case 1:
                return "ADD_DISH";
            case 2:
                return "RETREAT_DISH";
            case 3:
                return "PRE_CHECKOUT";
            case 4:
                return "CHECKOUT";
            case 5:
                return "LABEL";
            case 6:
                return "KICHEN";
            default:
                return null;
        }
    }

    public static PrintTemplateType valueOf(int value)
    {
        switch(value)
        {
            case 0:
                return CUSTOMER;
            case 1:
                return ADD_DISH;
            case 2:
                return RETREAT_DISH;
            case 3:
                return PRE_CHECKOUT;
            case 4:
                return CHECKOUT;
            case 5:
                return LABEL;
            case 6:
                return KICHEN;
            default:
                return null;
        }
    }
}
