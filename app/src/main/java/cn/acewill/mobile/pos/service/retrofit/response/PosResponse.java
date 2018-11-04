package cn.acewill.mobile.pos.service.retrofit.response;

/**
 * 服务器返回的消息有统一的格式如下：
 * {
 *     result: 状态码，0为成功，其他为错误
 *     errmsg: 具体的错误信息 -- 不应该把这个信息显示到界面，记录日志里就行
 *     content: 具体的内容 -- 比如获取桌台列表，那么content的类型就是List<Table>
 * }
 * Created by Acewill on 2016/6/17.
 */
public class PosResponse<T> {
    private int result;
    private String errmsg;
    private T content;
    private T data;

    public boolean isSuccessful() {
        return result == 0;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
