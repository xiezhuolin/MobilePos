package cn.acewill.mobile.pos.printer;

/**
 * Created by Acewill on 2016/8/19.
 */
public enum KdsState {
    //连接成功
    SUCCESS(576),
    //等待检测
    WATING(384),
    //错误
    ERROR(100);

    int kdsState;

    KdsState(int kdsState) {
        this.kdsState = kdsState;
    }

    public int getKdsState() {
        return kdsState;
    }

    public void setKdsState(int kdsState) {
        this.kdsState = kdsState;
    }
}
