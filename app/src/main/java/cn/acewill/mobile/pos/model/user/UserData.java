package cn.acewill.mobile.pos.model.user;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 创建日期：2015年7月24日<br>
 * 版权所有 XXX公司。 保留所有权利。<br>
 * 项目名：XXX项目 - Android客户端<br>
 * 描述：用户账户数据
 * 
 * @author HJK
 */
public class UserData {
    private static UserData data;
    private static Context mContext;
    private SharedPreferences spfPreferences;
    private SharedPreferences.Editor edit;
    private String userName;
    private String pwd;
	private String realname;
    private boolean saveState;
    private boolean isNotFirstLogin;
    private int printPage;//打印张数
    private String printTitle;//小票标题
    private boolean isPrint;//是否打印小票
    private boolean isScanz;//是否正扫
    private boolean isDebug;//是否开启debug:debug支付金额为0.01元
    private int wxPtid;
    private int aliPtid;
    private int wxCouPtid;
    private int aliCouPtid;
	private boolean isWorkShifts = false;//是否已经开班
    

    private UserData() {
        if (mContext == null)
            return;
        spfPreferences = mContext.getSharedPreferences("userInfo", 0);
        edit = spfPreferences.edit();
    }

    public static UserData getInstance(Context context) {
        mContext = context;
        if (data == null) {
            Lock lock = new ReentrantLock();
            lock.lock();
            if (data == null) {
                data = new UserData();

            }
            lock.unlock();
        }
        return data;
    }

    public String getUserName() {
        // if(TextUtils.isEmpty(userName))
        userName = spfPreferences.getString("userName", "");
        return userName;
    }

    public void setUserName(String userName) {
        edit.putString("userName", userName);
        edit.commit();
    }

    public void setWorkShifts(boolean WorkShifts) {
        edit.putBoolean("WorkShifts", WorkShifts);
        edit.commit();
    }

	public boolean getWorkShifts() {
		isWorkShifts = spfPreferences.getBoolean("WorkShifts", false);
		return isWorkShifts;
	}

    public String getRealName() {
        // if (TextUtils.isEmpty(pwd))
		realname = spfPreferences.getString("realname", "");
        return realname;
    }

    public void setRealName(String realname) {
        edit.putString("realname", realname);
        edit.commit();
    }

	public String getPwd() {
		// if (TextUtils.isEmpty(pwd))
		pwd = spfPreferences.getString("pwd", "");
		return pwd;
	}

	public void setPwd(String pwd) {
		edit.putString("pwd", pwd);
		edit.commit();
	}

    public boolean getSaveState() {
        if (!saveState)
            saveState = spfPreferences.getBoolean("saveState", false);
        return saveState;
    }

    public void setSaveStated(boolean saveState) {
        edit.putBoolean("saveState", saveState);
        edit.commit();
    }

    public boolean isNotFirstLogin() {
        if (!isNotFirstLogin)
            isNotFirstLogin = spfPreferences.getBoolean("isNotFirstLogin", false);
        return isNotFirstLogin;
    }

    public void setNotFirstLogin(boolean isNotFirstLogin) {
        edit.putBoolean("isNotFirstLogin", isNotFirstLogin);
        edit.commit();
    }

	public int getPrintPage() {
		printPage = spfPreferences.getInt("pages", 1);
		return printPage;
	}

	public void setPrintPage(int printPage) {
		edit.putInt("pages", printPage);
		edit.commit();
	}

	public String getPrintTitle() {
		printTitle = spfPreferences.getString("printTitle", "小票打印");
		return printTitle;
	}

	public void setPrintTitle(String printTitle) {
		edit.putString("printTitle", printTitle);
		edit.commit();
	}

	public boolean isPrint() {
		isPrint = spfPreferences.getBoolean("isPrint", true);
		return isPrint;
	}

	public void setPrint(boolean isPrint) {
		edit.putBoolean("isPrint", isPrint);
		edit.commit();
	}

	public boolean isScanz() {
		isScanz = spfPreferences.getBoolean("isScanz", false);
		return isScanz;
	}

	public void setScanz(boolean isScanz) {
		edit.putBoolean("isScanz", isScanz);
		edit.commit();
	}

	public boolean isDebug() {
		isDebug = spfPreferences.getBoolean("isDebug", false);
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		edit.putBoolean("isDebug", isDebug);
		edit.commit();
	}

	public int getWxPtid() {
		wxPtid = spfPreferences.getInt("wxPtid", -24);
		return wxPtid;
	}

	public void setWxPtid(int wxPtid) {
		edit.putInt("wxPtid", wxPtid);
		edit.commit();
	}

	public int getAliPtid() {
		aliPtid = spfPreferences.getInt("aliPtid", 29);
		return aliPtid;
	}

	public void setAliPtid(int aliPtid) {
		edit.putInt("aliPtid", aliPtid);
		edit.commit();
	}

	public int getWxCouPtid() {
		wxCouPtid = spfPreferences.getInt("wxCouPtid", -25);
		return wxCouPtid;
	}

	public void setWxCouPtid(int wxCouPtid) {
		edit.putInt("wxCouPtid", wxCouPtid);
		edit.commit();
	}

	public int getAliCouPtid() {
		aliCouPtid  = spfPreferences.getInt("aliCouPtid", -26);
		return aliCouPtid;
	}

	public void setAliCouPtid(int aliCouPtid) {
		edit.putInt("aliCouPtid", aliCouPtid);
		edit.commit();
	}
	
    
}
