package cn.acewill.mobile.pos.model;

import java.io.Serializable;

import cn.acewill.mobile.pos.printer.KdsState;


/**
 * Created by DHH on 2017/3/17.
 */

public class KDS implements Serializable{
    private Integer id;
    private String kdsName;
    private String ip;
    private String port;
    private KdsState kdsState = KdsState.WATING;//先设置成等待检测
    private String errMessage = "";//如果不能使用,记录不能使用的原因

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKdsName() {
        return kdsName;
    }

    public void setKdsName(String kdsName) {
        this.kdsName = kdsName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public KdsState getKdsState() {
        return kdsState;
    }

    public void setKdsState(KdsState kdsState) {
        this.kdsState = kdsState;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
