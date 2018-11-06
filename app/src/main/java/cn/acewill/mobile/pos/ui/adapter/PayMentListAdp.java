package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.model.PaymentList;
import cn.acewill.mobile.pos.utils.ToolsUtils;

import static cn.acewill.mobile.pos.R.id.tv_dishName;

/**
 * Created by DHH on 2016/6/17.
 */
public class PayMentListAdp<T> extends BaseAdapter {
	public PayMentListAdp(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder        holder      = null;
		final PaymentList paymentList = (PaymentList) getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_payment, null);
			holder.tv_dishName = (TextView) convertView.findViewById(tv_dishName);
			holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//        int paymentTypeId = paymentList.getPaymentTypeId();
		//        Payment payment = StoreInfor.getPaymentById(paymentTypeId);
		//        String payName = "";
		//        if (payment != null) {
		//            payName = payment.getName();
		//        }
		if (!TextUtils.isEmpty(paymentList.getPayName()))
			holder.tv_dishName.setText(paymentList.getPayName());
		else
			holder.tv_dishName.setText("");
		if (!TextUtils.isEmpty(paymentList.getPaymentNo())) {
			holder.tv_number.setVisibility(View.VISIBLE);
			holder.tv_number
					.setText(ToolsUtils.returnXMLStr("serial_number") + paymentList.getPaymentNo());
		} else {
			holder.tv_number.setVisibility(View.INVISIBLE);
		}
		String price = paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN).toString();
		holder.tv_price.setText("￥ " + price);

		return convertView;
	}

	class ViewHolder {
		TextView tv_dishName;
		TextView tv_number;
		TextView tv_price;
	}

}
