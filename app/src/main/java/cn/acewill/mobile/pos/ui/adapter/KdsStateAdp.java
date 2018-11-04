package cn.acewill.mobile.pos.ui.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.adapter.BaseAdapter;
import cn.acewill.mobile.pos.common.PrinterDataController;
import cn.acewill.mobile.pos.model.KDS;
import cn.acewill.mobile.pos.model.KitchenStall;
import cn.acewill.mobile.pos.model.event.PosEvent;
import cn.acewill.mobile.pos.printer.KdsState;
import cn.acewill.mobile.pos.utils.Constant;


/**
 * Created by DHH on 2016/6/17.
 */
public class KdsStateAdp<T> extends BaseAdapter {
    private ArrayMap<String, List<KitchenStall>> kitchenStallPrintMap;
    public KdsStateAdp(Context context) {
        super(context);
        kitchenStallPrintMap = PrinterDataController.getKitchenStallPrintMap();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final KDS kds = (KDS) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_device_state, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_ip = (TextView) convertView.findViewById(R.id.tv_ip);
            holder.tv_wating = (TextView) convertView.findViewById(R.id.tv_wating);
            holder.tv_associatedInfo = (TextView) convertView.findViewById(R.id.tv_associatedInfo);
            holder.tv_do = (TextView) convertView.findViewById(R.id.tv_do);
            holder.img_state = (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText("  "+kds.getKdsName());
        holder.tv_ip.setText(kds.getIp());
        KdsState kdsState = kds.getKdsState();
        if(kdsState == KdsState.SUCCESS)
        {
            holder.img_state.setVisibility(View.VISIBLE);
            holder.tv_wating.setVisibility(View.GONE);
            holder.img_state.setImageResource(R.mipmap.img_state_ok);
        }
        else if(kdsState == KdsState.ERROR){
            holder.img_state.setVisibility(View.VISIBLE);
            holder.tv_wating.setVisibility(View.GONE);
            holder.img_state.setImageResource(R.mipmap.img_state_err);
        }
        else{
            holder.img_state.setVisibility(View.GONE);
            holder.tv_wating.setVisibility(View.VISIBLE);
        }
        if(kitchenStallPrintMap != null && kitchenStallPrintMap.size() >0)
        {
            List<KitchenStall> kitchenStallList = kitchenStallPrintMap.get("K"+kds.getId());
            holder.tv_associatedInfo.setText(getPrintForKitStall(kitchenStallList));
        }
        holder.tv_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PosEvent(Constant.TestPrinterState.TEST_PRINT_KDS, kds));
            }
        });
        return convertView;
    }

    private String getPrintForKitStall(List<KitchenStall> kitchenStallList)
    {
        StringBuffer sb = new StringBuffer();
        if(kitchenStallList != null && kitchenStallList.size() >0)
        {
            for (KitchenStall kitchenStall:kitchenStallList)
            {
                sb.append(kitchenStall.getStallsName()+";");
            }
        }
        return sb.toString();
    }
    class ViewHolder {
        TextView tv_name;
        TextView tv_ip;
        TextView tv_wating;
        ImageView img_state;
        TextView tv_associatedInfo;
        TextView tv_do;
    }

}
