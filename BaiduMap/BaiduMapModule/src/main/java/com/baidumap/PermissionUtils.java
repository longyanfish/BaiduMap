package com.baidumap;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类用于检测权限和申请
 */

public class PermissionUtils {

    private Activity mActivity;
    private int mReqCode;
    private CallBack mCallBack;

    public static interface CallBack {
        void grantAll();

        void denied();
    }

    public PermissionUtils(Activity activity) {
        mActivity = activity;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void request(List<String> needPermissions, int reqCode, CallBack callback) {

        if (Build.VERSION.SDK_INT < 23) {//23以前在安装时就默认接受
            callback.grantAll();
            return;
        }

        if (mActivity == null) {
            throw new IllegalArgumentException("activity is null.");
        }

        mReqCode = reqCode;
        mCallBack = callback;

        List<String> reqPermissions = new ArrayList<>();

        for (String permission : needPermissions) {
            //检查应用是否拥有该权限，被授权返回值为PERMISSION_GRANTED，否则返回PERMISSION_DENIED
            //有权限PackageManager.PERMISSION_GRANTED
            //无权限PackageManager.PERMISSION_DENIED
            if (mActivity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                reqPermissions.add(permission);
            }
        }

        // fixed!!!
        if (reqPermissions.isEmpty()) { //是否有元素
            callback.grantAll();
            return;
        }
//将弹出请求授权对话框，这个方法在M之前版本调用，OnRequestPermissionsResultCallback 直接被调用，
// 带着正确的 PERMISSION_GRANTED或者 PERMISSION_DENIED 。
        mActivity.requestPermissions(reqPermissions.toArray(new String[]{}), reqCode);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//grantResults用户点击后返回的申请权限结果
        if (requestCode == mReqCode) {
            boolean grantAll = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    grantAll = false;
                    Toast.makeText(mActivity, permissions[i] + " 未授权", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if (grantAll) {
                mCallBack.grantAll();
            } else {
                mCallBack.denied();
            }
        }

    }

}
