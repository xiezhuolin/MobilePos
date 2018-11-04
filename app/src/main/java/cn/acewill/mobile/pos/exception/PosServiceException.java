package cn.acewill.mobile.pos.exception;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.utils.Constant;

/**
 * POS后台异常
 * Created by Acewill on 2016/6/7.
 */
public class PosServiceException extends Exception {
    private ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR; //具体的错误码
    private String message = "";

    //错误信息
    public PosServiceException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.message = detailMessage;
        if(!TextUtils.isEmpty(detailMessage))
        {
            if(detailMessage.equals("Token错误"))
            {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.TOKEN_TIME_OUT));
            }
        }
    }

    public PosServiceException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public PosServiceException(ErrorCode errorCode) {
        super("");
        this.errorCode = errorCode;
    }

    public PosServiceException(String detailMessage) {
        super(detailMessage);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage()
    {
        if (message != null && !message.isEmpty()) {
            return message;
        }

        switch (errorCode) {
            case NETWORK_ERROR:
                return "网络连接失败";
        }

        return "系统内部异常";
    }

}
