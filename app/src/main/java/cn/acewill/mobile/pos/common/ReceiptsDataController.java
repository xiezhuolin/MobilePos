package cn.acewill.mobile.pos.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.acewill.mobile.pos.model.Receipt;
import cn.acewill.mobile.pos.model.ReceiptType;
import cn.acewill.mobile.pos.printer.Printer;
import cn.acewill.mobile.pos.utils.ToolsUtils;


/**
 * Created by DHH on 2016/8/23.
 * 小票类型控制类
 */
public class ReceiptsDataController {
    /**
     * 小票列表
     */
    public static List<Receipt> receiptList = new ArrayList<>();
    public static HashMap<ReceiptType, Receipt> receiptMap = new HashMap<>();

    /**
     * 选中的小票
     */
    public static HashMap<Integer, Receipt> selectReceiptMap = new HashMap<>();

    /**
     * 将获取到的小票列表以小票类型为键用hashmap存储起来
     *
     * @param receiptList
     */
    public static void receiptList2Map(List<Receipt> receiptList) {
        if (!ToolsUtils.isList(receiptList)) {
            int size = receiptList.size();
            for (int i = 0; i < size; i++) {
                Receipt receipt = receiptList.get(i);
                receiptMap.put(receipt.getType(), receipt);
            }
        }
    }

    /**
     * 小票类型列表转小票类型字符串
     *
     * @param printer
     * @return
     */
    public static String receiptType2sth(Printer printer) {
        StringBuffer sb = new StringBuffer();
        if (printer != null) {
            List<ReceiptType> receiptTypeList = printer.getReceiptTypeList();
            cleanSelectReceiptMap();
            if (!ToolsUtils.isList(receiptTypeList)) {
                int size = receiptTypeList.size();
                for (int i = 0; i < size; i++) {
                    ReceiptType receiptType = receiptTypeList.get(i);
                    if(receiptMap != null && receiptMap.size() >0)
                    {
                        Receipt receipt =  receiptMap.get(receiptType);
                        String name = receipt.getName();
                        selectReceiptMap.put(receipt.getId(),receipt);
                        sb.append(name + ",");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 该小票类型是否已经被选中
     *
     * @param receipt
     * @return
     */
    public static boolean isHaveReceipt(Receipt receipt) {
        boolean isHave = false;
        if (selectReceiptMap.containsKey(receipt.getId())) {
            isHave = true;
            return isHave;
        }
        return isHave;
    }

    /**
     * 设置选中类型map集合
     *
     * @param selectReceiptMaps
     */
    public static void setSelectReceiptMap(HashMap<Integer, Receipt> selectReceiptMaps) {
        if (selectReceiptMap != null) {
            selectReceiptMap = selectReceiptMaps;
        }
    }

    /**
     * 获得已选择的小票类型
     * @return
     */
    public static List<ReceiptType> selectMap2ReceiptTypeList() {
        List<ReceiptType> receiptList = new ArrayList<ReceiptType>();
        Iterator iter = selectReceiptMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            receiptList.add(selectReceiptMap.get(key).getType());
        }
        return receiptList;
    }

    public static void cleanSelectReceiptMap() {
        if (selectReceiptMap != null) {
            selectReceiptMap.clear();
        }
    }

    /**
     * 选中小票MAP转sth字符串
     *
     * @return
     */
    public static String selectMap2Sth() {
        StringBuffer sb = new StringBuffer();
        Iterator iter = selectReceiptMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            sb.append(selectReceiptMap.get(key).getName() + ",");
        }
        return sb.toString();
    }

}
