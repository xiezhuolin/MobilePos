package cn.acewill.mobile.pos.service;

import com.google.gson.Gson;

import java.util.List;

import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.factory.RetrofitFactory;
import cn.acewill.mobile.pos.model.wsh.Account;
import cn.acewill.mobile.pos.model.wsh.WshCreateDeal;
import cn.acewill.mobile.pos.model.wsh.WshDealPreview;
import cn.acewill.mobile.pos.service.retrofit.RetrofitWshService;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import cn.acewill.mobile.pos.utils.FileLog;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 微生活
 * Created by aqw on 2016/10/19.
 */
public class WshService {

	private       RetrofitWshService retrofitWshService;
	public static WshService         wshService;

	public static WshService getInstance() throws PosServiceException {
		if (wshService == null) {
			RetrofitWshService rewshService = RetrofitFactory
					.buildService(RetrofitWshService.class);
			wshService = new WshService(rewshService);
		}

		return wshService;
	}

	private WshService(RetrofitWshService retrofitWshService) {
		this.retrofitWshService = retrofitWshService;
	}

	/**
	 * 获取会员信息, 有返回则说明是会员
	 *
	 * @param cardNo         会员卡号/手机号
	 * @param resultCallback
	 */
	public void getMemberInfo(String cardNo, final ResultCallback<List<Account>> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		retrofitWshService.getMemberInfo(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), cardNo).map(new Func1<PosResponse<List<Account>>, List<Account>>() {
			@Override
			public List<Account> call(PosResponse<List<Account>> accountPosResponse) {
				if (accountPosResponse.getResult() == 0) {
					return accountPosResponse.getContent();
				}
				PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, accountPosResponse
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Account>>(resultCallback));
	}

	/**
	 * 创建交易
	 *
	 * @param resultCallback
	 */
	public void createDeal(final WshCreateDeal.Request request, final ResultCallback<WshDealPreview> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		FileLog.log("微生活预览的参数>" + new Gson().toJson(request));
		retrofitWshService
				.createDeal(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(), request)
				.map(new Func1<PosResponse<WshDealPreview>, WshDealPreview>() {
					@Override
					public WshDealPreview call(PosResponse<WshDealPreview> accountPosResponse) {
						if (accountPosResponse.getResult() == 0) {
							return accountPosResponse.getContent();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, accountPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<WshDealPreview>(resultCallback));
	}

	/**
	 * 提交交易
	 *
	 * @param verifySms
	 * @param resultCallback
	 */
	public void commitDeal(final String bizId, final String verifySms, final ResultCallback<PosResponse> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		retrofitWshService.commitDeal(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), bizId, verifySms)
				.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<PosResponse>(resultCallback));

	}


}
