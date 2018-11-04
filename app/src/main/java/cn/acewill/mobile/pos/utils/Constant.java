package cn.acewill.mobile.pos.utils;

public class Constant {
	public static final String POS = "0";//pos
	public static final String MOBILE_POS = "1";//移动pos
	public static final String ORDER_MACHINE = "2";//点餐机

	public static final int MIN_CLICK_DELAY_TIME = 2000;//防止重复点击造成的重复下单

	public static final String FILE_DIR = "posDish";

	public static final String CHECK_TERMINAL_ID = "MobilePos";
	public static final String NETWORK_NOT_CONNECT = "网络未连接!";
	public static final String SERVICE_TETURN_ERROR = "服务器未连接!";

	public static final String LANGUAGE_CHINESE = "zh";
	public static final String LANGUAGE_ENGLISH = "en";

	/**
	 * 图片存放的路径
	 */
	public static final String PHOTO_PATH = "/"+FILE_DIR+"/"+"Image/";

	public static final String SERVER_ADDRESS_URL = "http://";
	/*
	 * 设置 key
	 */
	public static final String SETTING = "Setting";// 配置文件名字
	public static final String SETTING_SERVERIP = "ServerIp";// 服务器IP
	public static final String SERVERTIMESTAMP = "ServerTimestamp";// 服务器时间戳
	public static final String SETTING_PROTOCOL = "Protocol";// ip协议(http 或
																// https)
	public static final String SETTING_ALIPOS = "Alipos";// 支付宝付账

	public static final String SETTING_HAND = "Hand"; // 选择操作习惯
	public static final String SETTING_SIGNUP = "SignUp";// 注册
	public static final String MID = "Mid";// 菜谱Id

	public static final String SIDS = "Sids";

	public static final String DB_TIMESTAMP = "db_timestamp";

	/* 选择习惯是左手还是右手 */
	public static int hand = 1;

	/* 数据库名 */
	public static final String DATABASE_NAME = "cxjDish.db";
	public static final int DATABASE_VERSION = 1;// 数据库版本
	public static final String DATABASE_PATH = "/data/data/cn.acewill.pos.next/databases";// 手机点餐宝地址

	public static final String HOTELNAME = "北京奥琦玮信息科技";
	public static final String LICENSE = "license";

	// 指定设备类型
	public static final boolean DEBUG = true;
	public static final String PLATFORMMODEL = "HUAWEI C8650+";

	// 获取图片和查款的baseUrl
	public static final String BASE_URL = "http://192.168.1.14";
	// 获取二维码图片的路径
	public static final String IMAGE_URL = "/alipay/requestforward.php?service=create_qrcode";
	// 查款：是否支付成功
	public static final String CHECK_CASH_URL = "/alipay/requestforward.php?service=query_trade_success";

	public static final String ERROR_JSON_MSG = "网络连接失败！";
	public static String PICTURE_ERRORMSG = ERROR_JSON_MSG;// 获取二维码图片失败
	public static String CHECK_CASH_ERRORMSG = ERROR_JSON_MSG;// 查款失败
	
    public static boolean isDemoShow = false;
    
    /**
     * 显示请求网络dialog标识
     */
    public static boolean DIALOG_SHOW = true;
    /**
     * 隐藏请求网络dialog标识
     */
    public static boolean DIALOG_GONE = false;

	public static String FILE_DOWNLOAD_ERROR = "文件下载失败!";
//	public static String SERVICE_TETURN_ERROR = "服务器返回失败!";
	public static String MUST_IMPLE_ONMAINLISTENER = " must implementOnMainListener";
	/**
	 * OrderMain跳转到网上订单界面的回调标识
	 */
	public static final int MAIN_TIPS_TOAST_RESULT = 0X666;

	/**
	 * 有部分打印机会存在发送一次打印指令打印两次的情况出现
	 */
	public static boolean IS_PRINT = true;

	public static final int SINGLE = 666;//单个
	public static final int MULTIPLE = 667;//多个
	public static final int CLOSEORDER = 668;//整单

	/*桌台*/
	public static final int TABLE_TURN = 20;//转台
	public static final int TABLE_ADD = 21;//加台
	public static final int TABLE_JOIN = 22;//并台
	public static final int TABLE_CLEAR = 23;//清台
	public static final int TABLE_RUSH = 25;//催菜
	public static final int TABLE_REFUN = 26;//退菜
	public static final int TABLE_REFUN_ORDER = 56;//退单

	//订单操作
	public static final int ORDER_CLEAN = 27;//清空
	public static final int ORDER_REMARK = 28;//备注
	public static final int ORDER_ALL_DISCOUNT = 29;//全单打折
	public static final int ORDER_FREE = 31;//免单
	public static final int ORDER_HANG = 32;//挂单
	public static final int ORDER_GET = 33;//取单
	public static final int ORDER_ID_REFRESH = 34;//刷新订单号
	public static final int ORDER_MEMBER_CLEAN = 35;//清空微生活会员对象
	public static final int ORDER_CARDNUMBER_MODE = 36;//餐牌模式
	public static final int ORDER_CUSTOMER_NUMBER = 30;//输入就餐人数


	//反结账标识
	public static final int REVERSE_CHECKOUT = 24;

	public static final int JUMP_LOGIN = 10086;


	/**
	 * js与android通信标识类
	 */
	public static class JsToAndroid
	{
		//js调android方法标签
		/**
		 *  js_android 开台
		 */
		public static final int JS_A_OPENTABLE = 1;
		/**
		 *  js_android 加菜
		 */
		public static final int JS_A_ADDDISH = 2;
		/**
		 *  js_android 退菜
		 */
		public static final int JS_A_RETREATDISH = 3;
		/**
		 *  js_android 保存桌台信息 （布局位置,桌台ID信息)
		 */
		public static final int JS_A_SAVE_TABLEINFO = 4;
		/**
		 *  js_android 转台
		 */
		public static final int JS_A_SHIFTTABLE = 5;
		/**
		 *  js_android 结账
		 */
		public static final int JS_A_CHECKOUT  = 6;
		/**
		 * 搭台
		 */
		public static final int JS_A_SPLITTABLE = 7;
		/**
		 * 加台
		 */
		public static final int JS_A_ADDTABLE = 8;
		/**
		 * 选中桌台标识
		 */
		public static final int JS_A_CHECK_TABLE = 10;


		//android调js方法标签
		/**
		 *  android_js 开台
		 */
		public static final int A_JS_OPENTABLE = 11;
		/**
		 *  android_js 加台
		 */
		public static final int A_JS_ADDTABLE = 12;
		/**
		 *  android_js 搭台
		 */
		public static final int A_JS_SPLITTABLE   = 13;
		/**
		 *  android_js 并台
		 */
		public static final int A_JS_COMBINETABLE   = 14;
		/**
		 *  android_js 转台
		 */
		public static final int A_JS_SHIFTTABLE   = 15;

	}

	public static class EventState
	{
		/**
		 * 选择桌台区域回调<tt>tag</tt>
		 */
		public static final int SELECT_AREA = 0x11;
		/**
		 * 切换fragment下单界面
		 */
		public static final int SELECT_FRAGMTNT_ORDER = 0x12;
		/**
		 * 切换fragment桌台界面
		 */
		public static final int SELECT_FRAGMTNT_TABLE = 0x13;
		/**
		 * 切换fragment退菜界面
		 */
		public static final int SELECT_FRAGMTNT_RETREAT = 0x33;
		/**
		 * 切换到桌台结账界面
		 */
		public static final int SELECT_CHECK_OUT = 0x34;
		/**
		 * 主界面下拉回调
		 */
		public static final int SELECT_MAIN_DROP_LIST = 0x14;
		/**
		 * 清除购物车事件
		 */
		public static final int CLEAN_CART = 0x18;
		/**
		 * 跳转到结账界面参数
		 */
		public static final int SOURCE_CREAT_ORDER = 0;//从创建订单界面过来
		public static final int SOURCE_ORDER_DAY = 1;//从当日订单界面过来
		public static final int SOURCE_TABLE_ORDER = 2;//从桌台界面过来
		public static final int SOURCE_RE_CHECKOUT = 3;//从当日订单界面过来
		/**
		 *  小票类型Dialog回调
		 */
		public static final int DIALOG_RECEIPT_CALLBACK = 0x15;
		/**
		 * 服务器处于在线状态
		 */
		public static final int SERVER_STATUS_UP = 0X16;
		/**
		 * 服务器处于离线状态
		 */
		public static final int SERVER_STATUS_DOWN = 0X17;
		/**
		 * 获取到网络订单状态标识
		 */
		public static final int PUT_NET_ORDER = 0X19;
		/**
		 * Token过期的标识
		 */
		public static final int TOKEN_TIME_OUT = 0X20;
		/**
		 * 下单打印的标识
		 */
		public static final int PRINTER_ORDER = 0X233;
		/**
		 * 当前时段没有菜品显示的标识
		 */
		public static final int CURRENT_TIME_DISH_NULL = 0X24;
		/**
		 * 下单打印厨房小票的标识
		 */
		public static final int PRINTER_KITCHEN_ORDER = 0X25;
		/**
		 * 下单打印总单厨房小票的标识
		 */
		public static final int PRINTER_KITCHEN_SUMMARY_ORDER = 0X255;
		/**
		 * 服务器请求超时
		 */
		public static final int SERVER_REQUEST_TIMEOUT = 0X26;
		/**
		 * 打印交接班报表
		 */
		public static final int PRINTER_WORKSHIFT_REPORT = 0X27;
		/**
		 * 退菜打印的标识
		 */
		public static final int PRINTER_RETREAT_DISH = 0X28;
		/**
		 * 退单个菜品打印的标识
		 */
		public static final int PRINTER_RETREAT_ITEM_DISH = 0X288;
		/**
		 * 催菜后台厨房打印标识
		 */
		public static final int PRINTER_RUSH_DISH = 0X29;
		/**
		 * 补打小票标识
		 */
		public static final int PRINTER_EXTRA_RECEIPT = 0X30;
		/**
		 * 补打厨房小票标识
		 */
		public static final int PRINTER_EXTRA_KITCHEN_RECEIPT = 0X33;
		/**
		 * 打印订单小票出错
		 */
		public static final int ERR_PRINT_ORDER = 0X60;
		/**
		 * 打印厨房小票出错
		 */
		public static final int ERR_PRINT_KITCHEN_ORDER = 0X61;
		/**
		 * 打印总单小票出错
		 */
		public static final int ERR_PRINT_KITCHEN_SUMMARY_ORDER = 0X70;
		/**
		 * 打印退单出错
		 */
		public static final int ERR_PRINT_REFUND_ORDER = 0X68;
		/**
		 * 打印客用订单小票出错
		 */
		public static final int ERR_PRINT_GUEST_ORDER = 0X69;
		/**
		 * 收银打印机配置出错
		 */
		public static final int ERR_PRINT_CASH = 0X72;
		/**
		 * 收银打印机打印交班记录Id
		 */
		public static final int PRINT_WORKSHIFT = 0X73;
		/**
		 * 注销
		 */
		public static final int LOGOUT = 0X74;
		/**
		 * 打印结账单
		 */
		public static final int PRINT_CHECKOUT = 0X622;
		/**
		 * 退单打印的标识
		 */
		public static final int PRINTER_RETREAT_ORDER = 0X63;

		/**
		 *
		 */
		public static final int SET_ORDER_ID = 0X64;
		/**
		 * 退菜客用打印的标识
		 */
		public static final int PRINTER_RETREAT_DISH_GUEST = 0X656;

		/**
		 * 打开钱箱
		 */
		public static final int PRINTER_OPEN_MONEYBOX = 0X66;
		/**
		 * 厨房退单打印的标识
		 */
		public static final int PRINTER_RETREAT_KITCHEN_ORDER = 0X67;
		/**
		 * 预结小票标识
		 */
		public static final int ORDER_TYPE_ADVANCE = 0x68;
		/**
		 * 打印外卖结账单
		 */
		public static final int PRINT_WAIMAI = 0X69;
		/**
		 * 通过交接班历史打印交接班报表
		 */
		public static final int PRINT_WORKE_SHIFT_HISTORY = 0X80;
		/**
		 * 确认打印日结报表
		 */
		public static final int PRINT_CONFIRM_DALIY = 0X81;
		/**
		 * orderItem重试补打的标识
		 */
		public static final int PRINT_RETRY_REPRINT = 0X82;
		/**
		 * 发送数据到KDS 下单
		 */
		public static final int SEND_INFO_KDS = 0X83;
		/**
		 * 发送数据到KDS 退单
		 */
		public static final int SEND_INFO_KDS_REFUND_ORDER = 0X84;
		/**
		 * 发送数据到KDS 退单
		 */
		public static final int SEND_INFO_KDS_REFUND_DISH = 0X85;
		/**
		 * 发送数据到KDS 换台
		 */
		public static final int SEND_INFO_KDS_CHANGE_TABLE = 0X86;
		/**
		 * 菜品列表发生变化
		 */
		public static final int DISH_ITEM_CHANGE = 0X87;
		/**
		 * 会员信息发现变化要在副屏上展示的时候
		 */
		public static final int MEMBER_INFO_CHANGE = 0X88;
		/**
		 * 刷新订单号
		 */
		public static final int REFRESH_ORDERID = 0X89;
		/**
		 * 收银打印机是USB
		 */
		public static final int CASH_PRINTER_USB = 0X90;
		/**
		 * 生成订单ID失败的错误提示标签
		 */
		public static final int ERR_CREATE_ORDERID_FILURE = 0X91;
		/**
		 * 获取线上支付状态失败提示标签
		 */
		public static final int ERR_GET_ONLINE_PAY_STATE_FAILURE = 0X92;
		/**
		 * 获取威富通支付状态失败提示标签
		 */
		public static final int ERR_GET_WFT_PAY_STATE = 0X93;
		/**
		 * 获取支付状态失败提示标签
		 */
		public static final int ERR_GET_PAY_STATE_FAILURE = 0X94;
		/**
		 * 网上接单开始
		 */
		public static final int NETORDER_ON = 0X95;
		/**
		 * 网上接单关闭
		 */
		public static final int NETORDER_OFF = 0X96;

	}

	public static class TestPrinterState
	{
		//测试打印
		public static final int TEST_PRINT = 0X996;
		//测试打印KDS
		public static final int TEST_PRINT_KDS = 0X995;
	}

	public static class ShowTableStyle
	{
		/**
		 * 以list的形式展示桌台（GridView）
		 */
		public static final int TABLE_LIST = 0x21;
		/**
		 * 以店内实景形式展示桌台
		 */
		public static final int TABLE_LIVE_SCENER = 0x22;
	}

	public static class PrinterContentStyle
	{
		/**
		 * 添加新的模板样式
		 */
		public static final int ADD_PRINTER_CONTENT = 0x31;

	}
	public static class CheckOutEventStyle
	{
		/**
		 * 下单刷新回调
		 */
		public static final int CHECK_OUT_CHANGE = 0x41;

	}
	public static class DialogStyle
	{
		/**
		 * 添加打印机
		 */
		public static final int ADD_PRINTER = 0x51;
		/**
		 * 修改打印机
		 */
		public static final int MODIFY_PRINTER = 0x52;
		/**
		 * 删除打印机
		 */
		public static final int DELETE_PRINTER = 0x53;
		/**
		 * 测试打印机
		 */
		public static final int TEST_PRINTER = 0x54;
	}

	public static class DishMenu
	{
		/**
		 * 菜品数量
		 */
		public static final int DISH_COUNT = 1;
		/**
		 * 定制项
		 */
		public static final int DISH_SKU = 2;
		/**
		 * 打折方案
		 */
		public static final int DISH_DISCOUNT = 3;
		/**
		 * 退菜
		 */
		public static final int REFUND_DISH = 4;
		/**
		 * 转菜
		 */
		public static final int TURN_DISH = 5;
		/**
		 * 催菜
		 */
		public static final int EXPEDITE_DISH = 6;
		/**
		 * 打包费用
		 */
		public static final int TAKEOUT_DISH = 7;
		/**
		 * 菜品规格
		 */
		public static final int SPECIFICATIONS_DISH = 8;
		/**
		 * 堂食
		 */
		public static final int EAT_IN = 9;
		/**
		 * 外卖
		 */
		public static final int SALE_OUT = 10;
		/**
		 * 外带
		 */
		public static final int TAKE_OUT = 11;

	}


	public static class MainSelctAction
	{
		public static final int LOGIN_OUT =1;
		public static final int UP_LOAD_LOG =2;
		public static final int APP_UPDATA =3;
		public static final int WORK_SHIFT =4;
		public static final int DISH_COUNT =5;
		public static final int ABOUT_APP =6;
		public static final int ABOUT_STORE =7;
		public static final int MORE =8;
		public static final int ADD_DISH =9;
		public static final int TABLE_STATE =10;
	}

}
