package cn.acewill.mobile.pos.presenter;

public interface OnWorkListener {
	/**
	 * 成功
	 */
	<T> void onSuccess(T work);
	/**
	 * 失败
	 */
	void onError();

}

