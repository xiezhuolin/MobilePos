//package cn.acewill.mobile.pos.service;
//
//import android.annotation.TargetApi;
//import android.app.Service;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Build;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//
//import java.util.List;
//
//import cn.acewill.mobile.pos.common.TimerTaskController;
//import cn.acewill.mobile.pos.interfices.NetOrderinfoCallBack;
//import cn.acewill.mobile.pos.model.dish.Dish;
//import cn.acewill.mobile.pos.model.dish.DishCount;
//import cn.acewill.mobile.pos.model.order.Order;
//
//
///**
// * Created by DHH on 2018/1/4.
// */
//
//public class NetOrderService extends Service {
//    private static TimerTaskController timerTaskController;
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return new NetOrderBinder();
//    }
//
//    public class NetOrderBinder extends Binder {
//        /**
//         * 获取当前Service的实例
//         *
//         * @return
//         */
//        public NetOrderService getService() {
//            return NetOrderService.this;
//        }
//    }
//
//    @TargetApi( Build.VERSION_CODES.KITKAT )
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        timerTaskController = TimerTaskController.getInstance();
//    }
//
//
//    @TargetApi( Build.VERSION_CODES.KITKAT )
//    public synchronized void setReceiveOrderList(List<Order> orderList) {
//        timerTaskController.setReceiveOrderList(orderList);
//    }
//
//    /**
//     * 通过网上订单ID修改订单接收状态
//     * 0:为未接收
//     * 1:为已接收并已下单
//     * 2:为已接收但下单出错
//     *
//     * @param netOrderId
//     * @param netOrderState
//     */
//    @TargetApi( Build.VERSION_CODES.KITKAT )
//    public synchronized static void modifyOrderType(Long netOrderId, int netOrderState) {
//        timerTaskController.modifyOrderType(netOrderId,netOrderState);
//    }
//
//    /**
//     * 网上订单是否已经被接收
//     *
//     * @param netOrderId
//     * @return
//     */
//    @TargetApi( Build.VERSION_CODES.KITKAT )
//    public synchronized static boolean isReceiveNetOrder(Long netOrderId) {
//        return timerTaskController.isReceiveNetOrder(netOrderId);
//    }
//
//    /**
//     * 判断所要查询的订单状态是否与stateType一致
//     *
//     * @param netOrderId
//     * @param stateType  网上订单的接收状态  0为未接收  1为已接收并已下单  2为已接收但下单出错  3正面处理当前订单(正在下单处理的过程中)
//     * @return
//     */
//    @TargetApi( Build.VERSION_CODES.KITKAT )
//    public synchronized static boolean netOrderState(Long netOrderId, int stateType) {
//        return timerTaskController.netOrderState(netOrderId,stateType);
//    }
//
//
//    private boolean isStopSyncNetOrder = true;//是否要停止网上订单轮训接口
//
//    public boolean isStopSyncNetOrder() {
//        return isStopSyncNetOrder;
//    }
//
//
//    public synchronized void setStopSyncNetOrder(boolean stopSyncNetOrder) {
//        timerTaskController.setStopSyncNetOrder(stopSyncNetOrder);
//    }
//
//
//    public void setNetOrderinfoCallBack(NetOrderinfoCallBack callback) {
//        timerTaskController.setNetOrderinfoCallBack(callback);
//    }
//
//
//    public void SyncNetOrder() {
//        timerTaskController.SyncNetOrder();
//    }
//
//    /**
//     * 判断外卖单是否接收
//     *
//     * @return
//     */
//    private boolean logicWaiMaiOrderReceive(Order order) {
//        return timerTaskController.logicWaiMaiOrderReceive(order);
//    }
//
//
//    public synchronized void restoreSyncNetOrder(long time, final List<Order> orderList) {
//        timerTaskController.restoreSyncNetOrder(time,orderList);
//    }
//
//    public void cancleTimter(boolean isColsePrintOrder) {
//        timerTaskController.cancleTimter(isColsePrintOrder);
//    }
//
//
//    public synchronized void cancleStartTimer() {
//        timerTaskController.cancleStartTimer();
//    }
//
//    @TargetApi( Build.VERSION_CODES.KITKAT )
//    public void cleanNetOrderMap() {
//        timerTaskController.cleanNetOrderMap();
//    }
//
//    private boolean isCleanOrderList = false;//是否要重置netOrderList
//    private synchronized void getNetOrderInfo() {
//        timerTaskController.getNetOrderInfo();
//    }
//
//    private synchronized void autoMaticNetOrder(List<Order> orderList) {
//        timerTaskController.autoMaticNetOrder(orderList);
//    }
//
//    /**
//     * 检测菜品库存并下单
//     *
//     * @param order
//     */
//    public synchronized void checkDishCount(final Order order) {
//        timerTaskController.checkDishCount(order);
//    }
//
//    private synchronized void createOrder(List<Dish> dishs, final Order netOrder, Long orderid) {
//        timerTaskController.createOrder(dishs,netOrder,orderid);
//    }
//
//    private synchronized void orderPrint(final Order result, long orderId) {
//        timerTaskController.orderPrint(result,orderId);
//    }
//
//    private synchronized void createOrderJyj(final Order order, final long orderId) {
//        timerTaskController.createOrderJyj(order,orderId);
//    }
//
//    public synchronized void getOrderId(final List<Dish> dishs, final Order order) {
//        timerTaskController.getOrderId(dishs,order);
//    }
//
//    /**
//     * 显示沽清提示
//     *
//     * @param result
//     */
//    public void refreshDish(List<DishCount> result, List<Dish> dishs) {
//        timerTaskController.refreshDish(result,dishs);
//    }
//
//    private void refrushUi(long netOrderId) {
//        timerTaskController.refrushUi(netOrderId);
//    }
//
//
//    /**
//     * 确认接收网上的订单
//     *
//     * @param resultOrder
//     */
//    private synchronized void conFirmNetOrder(final Order resultOrder) {
//        timerTaskController.conFirmNetOrder(resultOrder);
//    }
//
//    /**
//     * 根据订单Id获得订单详情
//     */
//    private void getOrderInfo(final Order order) {
//        timerTaskController.getOrderInfo(order);
//    }
//
//    private void kdsChangeOrderTable(final long netOrderId, final String newTableName) {
//        timerTaskController.kdsChangeOrderTable(netOrderId,newTableName);
//    }
//
//    /**
//     * KDS连接失败的操作
//     */
//    private void KDSOrderFailure(final long netOrderId, final String newTableName) {
//        timerTaskController.KDSOrderFailure(netOrderId,newTableName);
//    }
//
//    /**
//     * 休息三秒
//     */
//    private void sleep(long time) {
//        timerTaskController.sleep(time);
//    }
//
//
//}
