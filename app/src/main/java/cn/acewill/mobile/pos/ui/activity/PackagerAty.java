package cn.acewill.mobile.pos.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.mobile.pos.R;
import cn.acewill.mobile.pos.base.activity.BaseActivity;
import cn.acewill.mobile.pos.model.dish.Cart;
import cn.acewill.mobile.pos.model.dish.Dish;
import cn.acewill.mobile.pos.utils.ToolsUtils;
import cn.acewill.mobile.pos.utils.ViewUtil;


/**
 * 套餐选择
 * Created by DHH on 2016/8/17.
 */
public class PackagerAty extends BaseActivity {

    @BindView( R.id.dish_name )
    TextView dishName;
    @BindView( R.id.dialog_close )
    LinearLayout dialogClose;
    @BindView( R.id.dish_list )
    ListView dishList;
    @BindView( R.id.dialog_cancle )
    TextView dialogCancle;
    @BindView( R.id.dialog_ok )
    TextView dialogOk;

    private Dish mDishModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_packager);
        ButterKnife.bind(this);
        //        int dishId = getIntent().getIntExtra("dishID", 0);
        //        int dishType = getIntent().getIntExtra("dishType", -1);
        //        mDishModel = DishDataController.getDish(dishId, dishType);

        mDishModel = (Dish) getIntent().getSerializableExtra("dish");

        dishName.setText(mDishModel.getDishName());
        dishList.setAdapter(new MyAdapter());

        ViewUtil.setActivityWindow(context, 8, 8);
    }


    @OnClick( {R.id.dialog_close, R.id.dialog_cancle, R.id.dialog_ok} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_close:
                ToolsUtils.writeUserOperationRecords("关闭套餐选择");
                finish();
                break;
            case R.id.dialog_cancle:
                ToolsUtils.writeUserOperationRecords("取消套餐选择");
                finish();
                break;
            case R.id.dialog_ok:
                ToolsUtils.writeUserOperationRecords("确认套餐选择");
                if (mDishModel.packageItems == null) {
                    return;
                }
                for (Dish.PackageItem item : mDishModel.packageItems) {
                    if (item.quantity < item.itemCount) {
                        showToast("套餐项未选满!");
                        return;
                    }
                }
                Cart.getInstance().addItem(mDishModel);
                finish();
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDishModel.packageItems == null ? 0
                    : mDishModel.packageItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflate = View.inflate(PackagerAty.this,R.layout.item_setmeal_group, null);
            ListView lv_sv = (ListView) inflate.findViewById(R.id.lv_sv);
            Dish.PackageItem packageItem = mDishModel.packageItems.get(position);
            lv_sv.setAdapter(new ItemAdapter(PackagerAty.this, packageItem));
            TextView tv = (TextView) inflate.findViewById(R.id.tv);
            String text = "";
            if (packageItem.getIsMust()) {
                text = "必选项";
            } else {

                if (packageItem.options != null) {
                    text += getChiniesForInt(packageItem.options
                            .size());
                }
                text += "选"+ getChiniesForInt(packageItem.itemCount);
            }
            tv.setText(text);
            return inflate;
        }

    }

    class ItemAdapter extends BaseAdapter {

        private Context context;
        private Dish.PackageItem packageItem;

        public ItemAdapter(Context context, Dish.PackageItem packageItem) {
            this.context = context;
            this.packageItem = packageItem;
        }

        @Override
        public int getCount() {
            return packageItem.options.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View
                        .inflate(context, R.layout.item_setmeal, null);
            }
            View iv_add = convertView.findViewById(R.id.iv_add);
            View iv_reduce = convertView.findViewById(R.id.iv_reduce);
            final TextView tv_count = (TextView) convertView
                    .findViewById(R.id.tv_count);

            TextView tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            final Dish.Package package1 = packageItem.options.get(position);

            if (package1.extraCost > 0) {
                tv_name.setText(package1.getDishName() + "  加"
                        + package1.extraCost + "元");
            } else {
                tv_name.setText(package1.getDishName());
            }
            //            if (package1.getAlreadyCount() == 0) {
            //                tv_name.setText(package1.getDishName());
            //            } else {
            //                tv_name.setText(package1.getDishName() + "  加"
            //                        + package1.extraCost + "元");
            //            }
            tv_count.setText(package1.quantity + "");
            if (packageItem.getIsMust()) {//当可选项只有一个的时候 该项为必选
                tv_count.setText(package1.count + "");
                iv_add.setVisibility(View.INVISIBLE);
                iv_reduce.setVisibility(View.INVISIBLE);
                package1.quantity = 1;
                packageItem.quantity = packageItem.itemCount;
            }
            else {

                iv_add.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (packageItem.quantity < packageItem.itemCount) {

                            package1.quantity++;
                            packageItem.quantity++;
                        }
                        tv_count.setText(package1.quantity + "");
                    }
                });
                iv_reduce.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ToolsUtils.writeUserOperationRecords("取消套餐选择");
                        if (package1.quantity > 0) {
                            package1.quantity--;
                            packageItem.quantity--;
                        }
                        tv_count.setText(package1.quantity + "");
                    }
                });
            }
            return convertView;
        }

    }

    class Hodler {
        View iv_add;
        View iv_reduce;
        TextView tv_count;
        TextView tv_name;
    }

    public static String getChiniesForInt(int num) {
        switch (num) {
            case 1:

                return "一";
            case 2:

                return "二";
            case 3:

                return "三";
            case 4:

                return "四";
            case 5:

                return "五";
            case 6:

                return "六";
            case 7:

                return "七";
            case 8:

                return "八";
            case 9:

                return "九";
            case 10:

                return "十";

            default:
                return "";
        }
    }
}
