package cn.acewill.mobile.pos.utils;

/**
 * Created by DHH on 2016/7/18.
 */
public class Symbol {

    public static final String CURRENT_USER = "currentUser";

    public static final String TERMINAL_FILE_FOLDER_PREFIX = "terminal";
    public static final String TERMINAL_LOG_FOLDER_PRIFIX = "terminallog";
    public static final String TERMINAL_OTHER_FILE_FOLDER_PREFIX = "terminalOtherFile";

    public static final String REQUEST_TOKEN_KEY = "token";

    public static final String FILE_SEPERATOR = "/";

    public static final int PAY_TYPE_ZHIFUBAO = 1;
    public static final int PAY_TYPE_WX = 2;

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_LIMIT = 20;

    public static final int SUCCESS = 0;
    public static final int UNKNOWN_ERROR = -1;
    public static final int DUPLICATED_BUSINESS_APPID = 1001;
    public static final int NO_SUCH_APP = 1002;
    public static final int NO_SUCH_APP_STATUS = 1003;
    public static final int WRONG_DATE_FORMAT = 1004;
    public static final int NO_SUCH_BRAND = 1005;
    public static final int NO_SUCH_BRAND_STATUS = 1006;
    public static final int NO_SUCH_STORE = 1007;
    public static final int NO_SUCH_STORE_CATEGORY = 1008;
    public static final int STORE_EXISTS_UNDER_CATEGORY = 1009;

    public static final int DUPLICATED_USERNAME = 1011;
    public static final int UNKNOWN_USER_LEVEL = 1012;
    public static final int UNKNOWN_USER_TYPE = 1013;
    public static final int UNKNOWN_USER_STATUS = 1014;
    public static final int PASSWORD_IS_EMPTY = 1015;
    public static final int APP_IS_DISABLED = 1016;
    public static final int LOGIN_FAILED = 1017;
    public static final int NO_SUCH_USER = 1018;
    public static final int NO_USER_LOGIN = 1019;
    public static final int NOT_ENOUGH_LEVEL = 1020;
    public static final int STORE_EXISTS_UNDER_BRAND = 1021;
    public static final int ERROR_WITH_SUPPORT_BRANDS = 1022;
    public static final int ERROR_WITH_SUPPORT_STORES = 1023;
    public static final int UNFORMAT_APPID = 1024;
    public static final int APPID_TOO_LONG = 1025;
    public static final int APPID_TOO_SHORT = 1026;
    public static final int TERMINAL_LOGIN_FAILED_TERMINAL_DISABLE = 1027;
    public static final int TERMIANL_LOGIN_FAILED_EXCEED_MAX_TERMINAL = 1028;
    public static final int TERMINAL_LOGIN_FAILED_STORE_DISABLE = 1029;
    public static final int NO_SUCH_TERMINAL = 1030;
    public static final int UNKNOWN_TERMINAL_STATUS = 1031;
    public static final int UPLOAD_FILE_IS_EMPTY = 1032;
    public static final int FAILED_CREATE_TERMINAL_FOLDER = 1033;
    public static final int DUPLICATED_TERMINAL_NAME = 1034;
    public static final int API_BAD_TOKEN = 1035;
    public static final int EXCEED_MAX_STORE_UNDER_APP = 1036;
    public static final int TERMIANL_LOGIN_FAILED_EXCEED_MAX_TERMINAL_UNDER_STORE = 1037;
    public static final int DUPLICATED_PRICELEVEL_NAME = 1038;
    public static final int NO_SUCH_PRICELEVEL = 1039;
    public static final int STORE_EXIST_UNDER_PRICE_LEVEL = 1040;
    public static final int DUPLICATED_VERSIONID = 1041;
    public static final int NO_SUCH_VERSION = 1042;
    public static final int NO_SUCH_PAYTYPE_STATUS = 1043;
    public static final int NO_SUCH_PAYTYPE = 1044;
    public static final int BRAND_IS_DISABLED = 1045;
    public static final int STORE_IS_DISABLED = 1046;
    public static final int USER_IS_DISABLED = 1047;
    public static final int USER_ETIME_LARGEER_THAN_BUSINESS = 1048;
    public static final int UNKNOWN_TERMINAL_FILE_TYPE = 1049;
    public static final int UNKNOWN_PRICELEVEL_STATUS = 1050;

    //  唐运忠
    public static final int CONFLICT_SORTMARK = 1107;  // 速记码冲突
    public static final int CONFLICT_ID_OR_NAME = 1108;  // ID或者名称冲突
    public static final int NO_SUCH_COOK = 1109;  // 此做法不存在
    public static final int NO_SUCH_DISH = 1110;  // 此菜品不存在
    public static final int NO_SUCH_TIME = 1111;  // 此时段(菜品)不存在
    public static final int NO_SUCH_KIND = 1112;  // 此分类不存在
    public static final int NO_SUCH_TASTE = 1113;  // 此口味不存在
    public static final int CAN_NOT_REMOVE = 1114;  // 已被使用,不能移除
    public static final int NO_SUCH_UNIT = 1115;  // 此口味不存在
    public static final int NO_SUCH_CHOICE = 1116;  // 此口味不存在
    public static final int NO_SUCH_MARKET = 1117;  // 此营销方案不存在
    public static final int NO_SUCH_MARKET_TIME = 1118;  // 此营销方案的时段不存在

    //美团云存储
    public static final int MSS_CLIENT_NOT_READY = 2000; //客户端没有初始化
    public static final int MSS_BUCKET_ALREADY_EXISTS = 2001; //bucket已经存在
    public static final int MSS_BUCKET_NOT_EMPTY = 2002; //bucket非空
    public static final int MSS_CLIENT_ERROR = 2003; //连不上美团云
    public static final int MSS_OBJECT_ALREADY_EXISTS = 2004; //文件已经存在
    public static final int MSS_CONFIGURATION_DOES_NOT_EXIST = 2005; //美团的相关配置不存在，比如host，access_key等

    //订单消息
    public static final int ORDER_NOT_FOUND = 3000;
    public static final int ORDER_NOT_PAID = 3001;
}
