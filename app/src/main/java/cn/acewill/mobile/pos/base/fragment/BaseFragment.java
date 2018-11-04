package cn.acewill.mobile.pos.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.acewill.mobile.pos.config.MyApplication;
import cn.acewill.mobile.pos.widget.ProgressDialogF;


/**
 * BaseFragment
 * Created by DHH on 2016/1/28.
 */
public  class BaseFragment extends Fragment {
    public Context mContext;
    public Activity aty;
    public Resources resources;
    public MyApplication myApplication;
    protected boolean isVisible;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.resources = getResources();
        this.aty = getActivity();
        this.myApplication  = MyApplication.getInstance();
        progressDialogF = new ProgressDialogF(aty);
    }

    public String getStringById(int resId){
        String str = "";
        if (resources != null){
           str = resources.getString(resId);
        }
        return str;
    }

    public void showToast(String sth)
    {
        MyApplication.getInstance().ShowToast(sth);
    }

//    CatLoadingView mView;
    ProgressDialogF progressDialogF;
    public void showProgress()
    {
        progressDialogF.showLoading("");
//        mView = new CatLoadingView();
//        mView.show(getActivity().getSupportFragmentManager(), "");
    }
    public void showProgress(String str)
    {
        progressDialogF.showLoading(str);
//        mView = new CatLoadingView();
//        if(!TextUtils.isEmpty(str))
//        {
//            mView.setText(str);
//        }
//        mView.show(getActivity().getSupportFragmentManager(), "");
    }

    public void dissmiss() {
        progressDialogF.disLoading();
//        if(mView != null)
//        {
//            mView.setText(resources.getString(R.string.sth_loading));
//            mView.dismiss();
//        }
    }


}
