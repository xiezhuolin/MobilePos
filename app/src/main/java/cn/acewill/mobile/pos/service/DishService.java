package cn.acewill.mobile.pos.service;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.mobile.pos.common.StoreInfor;
import cn.acewill.mobile.pos.dao.cache.CachedDao;
import cn.acewill.mobile.pos.exception.ErrorCode;
import cn.acewill.mobile.pos.exception.PosServiceException;
import cn.acewill.mobile.pos.factory.RetrofitFactory;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.model.dish.DishCount;
import cn.acewill.mobile.pos.model.dish.DishSummary;
import cn.acewill.mobile.pos.model.dish.DishTime;
import cn.acewill.mobile.pos.model.dish.DishType;
import cn.acewill.mobile.pos.model.dish.Menu;
import cn.acewill.mobile.pos.model.dish.Unit;
import cn.acewill.mobile.pos.model.order.MarketingActivity;
import cn.acewill.mobile.pos.model.order.OrderItem;
import cn.acewill.mobile.pos.model.payment.Payment;
import cn.acewill.mobile.pos.service.retrofit.RetrofitTerminalService;
import cn.acewill.mobile.pos.service.retrofit.response.DishResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PaymentTypesResponse;
import cn.acewill.mobile.pos.service.retrofit.response.PosResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Acewill on 2016/6/2.
 */
public class DishService {
	RetrofitTerminalService internalService;
	private static DishService dishService;
	// private DishDao dishTypeDao;
	private        CachedDao   cachedDishDao;

	public static DishService getInstance() throws PosServiceException {
		if (dishService == null) {
			RetrofitTerminalService internalService = RetrofitFactory
					.buildService(RetrofitTerminalService.class);
			dishService = new DishService(internalService);
		}

		return dishService;
	}

	public DishService(RetrofitTerminalService internalService) {
		this.internalService = internalService;
		//   dishTypeDao = new DishDao();
		cachedDishDao = new CachedDao();
	}

	public void getKindDataInfo(final ResultCallback<List<DishType>> resultCallback) {
		//如果无法连接服务器，则从本地数据库获取信息
		if (!ServerStatus.getInstance().isRunning()) {
			resultCallback.onResult(cachedDishDao.getAllDish());
			return;
		}

		PosInfo posInfo = PosInfo.getInstance();
		Observable<List<DishType>> ob = internalService
				.getKindDataInfo(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<DishResponse, List<DishType>>() {
					@Override
					public List<DishType> call(DishResponse response) {
						if (response.getResult() == 0) {
							return response.getDishKindData();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io());


		ob.observeOn(Schedulers.io())
				.subscribe(new ResultSubscriber(new ResultCallback<List<DishType>>() {
					@Override
					public void onResult(List<DishType> result) {
						//从服务器成功获取后更新到本地数据库
						cachedDishDao.saveDishType(result);
					}

					@Override
					public void onError(PosServiceException e) {

					}
				}));
		//给界面使用
		ob.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<DishType>>(resultCallback));
	}

	public void getDishList(final ResultCallback<List<Menu>> resultCallback) {
		//如果无法连接服务器，则从本地数据库菜单信息
		if (!ServerStatus.getInstance().isRunning()) {
			resultCallback.onResult(cachedDishDao.getAllMenu());
			return;
		}

		PosInfo posInfo = PosInfo.getInstance();
		Observable<List<Menu>> ob = internalService
				.getDishList(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<DishResponse, List<Menu>>() {
					@Override
					public List<Menu> call(DishResponse response) {
						if (response.getResult() == 0) {
							return response.getMenuData();
						}

						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io());

		ob.observeOn(Schedulers.io())
				.subscribe(new ResultSubscriber(new ResultCallback<List<Menu>>() {
					@Override
					public void onResult(List<Menu> result) {
						cachedDishDao.saveMenu(result);
					}

					@Override
					public void onError(PosServiceException e) {

					}
				}));
		ob.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Menu>>(resultCallback));
	}

	public void getAllDishList(final ResultCallback<List<Menu>> resultCallback) {
		//如果无法连接服务器，则从本地数据库菜单信息
		if (!ServerStatus.getInstance().isRunning()) {
			resultCallback.onResult(cachedDishDao.getAllDishMenu());
			return;
		}

		PosInfo posInfo = PosInfo.getInstance();
		Observable<List<Menu>> ob = internalService
				.getAllDishmenu(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<DishResponse, List<Menu>>() {
					@Override
					public List<Menu> call(DishResponse response) {
						if (response.getResult() == 0) {
							return response.getMenuData();
						}

						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io());

		ob.observeOn(Schedulers.io())
				.subscribe(new ResultSubscriber(new ResultCallback<List<Menu>>() {
					@Override
					public void onResult(List<Menu> result) {
						cachedDishDao.saveAllMenu(result);
					}

					@Override
					public void onError(PosServiceException e) {

					}
				}));
		ob.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Menu>>(resultCallback));
	}

   /* private void saveMenu(List<Menu> menuList) {
        //从服务器成功获取后更新到本地数据库
        dishTypeDao.saveMenu(menuList);
        for (Menu menu : menuList) {
            for (Dish dish : menu.getDishData()) {
                dishTypeDao.saveDish(dish);
                dishTypeDao.saveDishMenuMap(new DishMenu(dish.getDishId(), menu.getId()));
            }
        }
    }*/

   /* private List<Menu> readMenu() {
        //从本地数据库读取菜单
        List<Menu> menuList = dishTypeDao.getAllMenu();
        List<Dish> dishList = dishTypeDao.getAllDish();
        List<DishMenu> dishMenuList = dishTypeDao.getDishMenuMap();

        Map<Integer, Set<Integer>> menu2DishMap = new HashMap<>();
        Map<Integer, Dish> id2DishMap = new HashMap<>();

        //构造菜单和里面的菜品id列表的关联
        for(DishMenu dm : dishMenuList) {
            Set<Integer> dishIdList = menu2DishMap.get(dm.getMenuId());
            if (dishIdList == null) {
                dishIdList = new HashSet<>();
                menu2DishMap.put(dm.getMenuId(), dishIdList);
            }

            dishIdList.add(dm.getDishId());
        }

        for(Dish dm : dishList) {
            id2DishMap.put(dm.getDishId(), dm);
        }

        //构造出菜单里的菜品列表
        for (Menu menu : menuList) {
            List<Dish> dishInMenu = new ArrayList<>();

            //找到该菜单的所有菜品
            Set<Integer> set = menu2DishMap.get(menu.getId());
            if (set != null) {
                for (Integer dishId : set) {
                    dishInMenu.add(id2DishMap.get(dishId));
                }
            }


            menu.setDishData(dishInMenu);
        }

        return menuList;
    }*/

	/**
	 * 在这里过滤了一些没用的支付方式
	 *
	 * @param resultCallback
	 */
	public void getPaytypeList(final ResultCallback<List<Payment>> resultCallback) {
		//如果无法连接服务器，则从本地数据库菜单信息
		if (!ServerStatus.getInstance().isRunning()) {
			StoreInfor.setPaymentList(cachedDishDao.getAllPaymentType());
			resultCallback.onResult(cachedDishDao.getAllPaymentType());
			return;
		}

		final PosInfo posInfo = PosInfo.getInstance();
		Observable<List<Payment>> ob = internalService
				.getPaymentTypeList(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<PaymentTypesResponse, List<Payment>>() {
					@Override
					public List<Payment> call(PaymentTypesResponse response) {
						if (response.getResult() == 0) {
							List<Payment> paymentList = new ArrayList<Payment>();
							for (Payment p : response.getPaymentTypes()) {
								if (p.getStatus() == 1 && p.getId() != 3 && p.getId() != 4&& p.getId() != 5 && p
										.getId() != -33 && p.getId() != -31
										&& p.getId() != -30) {
									paymentList.add(p);
								}
								//如果不是会员，那么不显示会员的支付方式
								if (p.getId() == 5 && posInfo.getAccountMember() != null) {
										paymentList.add(p);
								}
							}
							StoreInfor.setPaymentList(paymentList);
							return paymentList;
						}

						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io());

		ob.observeOn(Schedulers.io())
				.subscribe(new ResultSubscriber(new ResultCallback<List<Payment>>() {
					@Override
					public void onResult(List<Payment> result) {
						cachedDishDao.savePaymentType(result);
					}

					@Override
					public void onError(PosServiceException e) {

					}
				}));

		ob.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Payment>>(resultCallback));
	}

	/**
	 * 在下单前调用这个函数检查菜品剩余数量是否足够
	 *
	 * @param dishCountList
	 * @param resultCallback
	 */
	public void checkDishCount(List<DishCount> dishCountList, final ResultCallback<List<DishCount>> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		if (!ServerStatus.getInstance().isRunning()) {
			resultCallback.onResult(new ArrayList<DishCount>());
			//TODO -- 需要检查估清状态
			return;
		}

		Gson   gson       = new Gson();
		String jsonString = gson.toJson(dishCountList);
		internalService.checkDishCount(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), jsonString).map(new Func1<DishResponse, List<DishCount>>() {
			@Override
			public List<DishCount> call(DishResponse response) {
				if (response.getResult() == 0) {
					return response.getSoldOutDishIdList();
				}

				PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<DishCount>>(resultCallback));
	}


	public void updataDishCount(final List<Integer> dishIdList, final int dishCount, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		if (!ServerStatus.getInstance().isRunning()) {
			cachedDishDao.modifyDishCount(dishIdList, dishCount);
			resultCallback.onResult(0);
			return;
		}

		Observable ov = internalService
				.updataDishCount(posInfo.getAppId(), posInfo.getBrandId(), posInfo
						.getStoreId(), dishIdList, dishCount)
				.map(new Func1<PosResponse, Integer>() {
					@Override
					public Integer call(PosResponse response) {
						if (response.getResult() == 0) {
							return 0;
						}

						PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io());

		ov.observeOn(Schedulers.io()).subscribe(new ResultSubscriber(new ResultCallback<Integer>() {
			@Override
			public void onResult(Integer result) {
				//从服务器成功获取后更新到本地数据库
				cachedDishDao.modifyDishCount(dishIdList, dishCount);
			}

			@Override
			public void onError(PosServiceException e) {
				Log.e("LOGIN", "" + e.getMessage());
			}
		}));

		ov.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}


	/**
	 * 获取营销方案列表
	 *
	 * @param resultCallback
	 */
	public void getMarketingActivityList(final ResultCallback<List<MarketingActivity>> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();

		internalService.getMarketingActivityList(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId())
				.map(new Func1<PosResponse<List<MarketingActivity>>, List<MarketingActivity>>() {
					@Override
					public List<MarketingActivity> call(PosResponse<List<MarketingActivity>> response) {
						if (response.getResult() == 0) {
							return response.getContent();
						}

						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<MarketingActivity>>(resultCallback));
	}


	//获取茶品的概要信息
	private List<DishSummary> getDishSummary() {
		List<DishSummary> dishList = new ArrayList<>();

		return dishList;
	}

	//获取某个菜品类型下面的所有的菜品的概要信息
	private List<DishSummary> getDishSummary(long dishTypeId) {
		List<DishSummary> dishList = new ArrayList<>();

		return dishList;
	}

	/**
	 * @param items
	 * @param rejectReason 拒绝的原因
	 */
	public void rejectDish(List<OrderItem> items, int rejectReason) {

	}

	/**
	 * 催单
	 *
	 * @param items
	 */
	public void hastenish(List<OrderItem> items) {

	}


	/**
	 * 添加菜品
	 *
	 * @param dish
	 * @param resultCallback
	 */
	public void addDish(Dish dish, final ResultCallback<PosResponse> resultCallback) {
		internalService.addDish(dish).map(new Func1<PosResponse, PosResponse>() {
			@Override
			public PosResponse call(PosResponse response) {
				if (response.isSuccessful()) {
					return response;
				}

				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<PosResponse>(resultCallback));
	}


	/**
	 * 获取菜品单位
	 *
	 * @param resultCallback
	 */
	public void getDishUnit(final ResultCallback<List<Unit>> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();

		internalService.getDishUnit(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<PosResponse<List<Unit>>, List<Unit>>() {
					@Override
					public List<Unit> call(PosResponse<List<Unit>> response) {
						if (response.getResult() == 0) {
							return response.getContent();
						}

						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Unit>>(resultCallback));
	}

	/**
	 * 获取菜品时段
	 *
	 * @param resultCallback
	 */
	public void getDishTime(final ResultCallback<List<DishTime>> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.getDishTime(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<PosResponse<List<DishTime>>, List<DishTime>>() {
					@Override
					public List<DishTime> call(PosResponse<List<DishTime>> response) {
						if (response.getResult() == 0) {
							return response.getContent();
						}

						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<DishTime>>(resultCallback));
	}


}
