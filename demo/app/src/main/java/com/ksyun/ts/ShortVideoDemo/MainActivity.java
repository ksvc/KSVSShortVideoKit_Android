package com.ksyun.ts.ShortVideoDemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ksyun.ts.ShortVideoDemo.model.ErrorModel;
import com.ksyun.ts.ShortVideoDemo.model.User;
import com.ksyun.ts.ShortVideoDemo.request.RequestManager;
import com.ksyun.ts.ShortVideoDemo.ui.DefaultOnClick;
import com.ksyun.ts.ShortVideoDemo.ui.GlideCircleTransform;
import com.ksyun.ts.ShortVideoDemo.ui.UserLocalization;
import com.ksyun.ts.shortvideo.common.util.KLog;
import com.ksyun.ts.skin.KSVSKitDataBuild;
import com.ksyun.ts.skin.KSVSShortVideoKitManager;
import com.ksyun.ts.skin.util.ToastUtils;

import io.reactivex.functions.Consumer;

/**
 * Created by xiaoqiang on 2017/11/17.
 */

public class MainActivity extends BaseActivity {
    private final static int PERMISSION_REQUEST_CAMERA_AUDIOREC = 1;
    private RelativeLayout mTitle;
    private Fragment mRecommendFragment;
    private User mUser;
    private ImageButton mUserIcon;
    private TextView mUserName;
    private ImageButton mRecordButton;
    private final static int REQUEST_OK = 1;
    private RequestManager mRequestManager;
    private KSVSKitDataBuild mKTSData;


    public static final void openMainActivity(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_PARAMS, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        mTitle = findViewById(R.id.rl_recommend_title);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , getResources().getDimensionPixelOffset(R.dimen.recomment_title_height));
        params.topMargin = getBorderTop();
        mTitle.setLayoutParams(params);
        mUser = (User) getIntent().getSerializableExtra(USER_PARAMS);
        mUserToken = mUser.getToken();
        mUserSecret = mUser.getToken();

        mUserIcon = findViewById(R.id.imgv_user_logo);
        mUserName = findViewById(R.id.tv_user_name);
        mRecordButton = findViewById(R.id.imgv_record);

        initUser();

        mUserIcon.setOnClickListener(mOnClick);
        mUserName.setOnClickListener(mOnClick);
        mRequestManager = new RequestManager(this);
        mKTSData = new KSVSKitDataBuild()
                .bindMaxRecordTime(1 * 60 * 1000)
                .bindMinRecordTime(5 * 1000);
        initView();
    }

    private void initView() {
        mRecordButton.setOnClickListener(mOnClick);

        /*** 开启推荐页 ***/
        mRecommendFragment = KSVSShortVideoKitManager.newRecommendFragment(mUser.getUid());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_recommend, mRecommendFragment);
        transaction.commit();

        mRequestManager.getUserInfo(new RequestManager.IKSVSRequestListener<User>() {
            @Override
            public void onSuccess(User user) {
                mUser.setHeadUrl(user.getHeadUrl());
                mUser.setNickname(user.getNickname());
                mUser.setGender(user.getGender());
                initUser();
                new UserLocalization(MainActivity.this).saveData(mUser);
            }

            @Override
            public void onFailed(ErrorModel errorInfo) {
                if ("NotLoggedIn".equalsIgnoreCase(errorInfo.getCode())) {
                    ToastUtils.showToast(MainActivity.this, R.string.logout_user);
                    LoginActivity.logout(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRequestManager != null) {
            mRequestManager.releaseAllReuqest();
        }
        mRequestManager = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK && resultCode == RESULT_OK) {
            mUser = (User) data.getSerializableExtra(USER_PARAMS);
            initUser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_AUDIOREC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    KSVSShortVideoKitManager.startRecording(MainActivity.this, mUser.getUid(),
                            mKTSData.build());
                } else {
                    ToastUtils.showToast(MainActivity.this, R.string.main_no_camera_permissions);
                    KLog.e(TAG, "No CAMERA or AudioRecord permission");
                }
                break;
            }
        }
    }

    private void startCameraPreviewWithPermCheck() {
        int cameraPerm = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        int audioPerm = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO);
        if (cameraPerm != PackageManager.PERMISSION_GRANTED ||
                audioPerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ToastUtils.showToast(MainActivity.this, R.string.main_no_camera_permissions);
                KLog.e(TAG, "No CAMERA or AudioRecord permission,check permission");
            } else {
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(MainActivity.this, permissions,
                        PERMISSION_REQUEST_CAMERA_AUDIOREC);
            }
        } else {
            KSVSShortVideoKitManager.startRecording(MainActivity.this, mUser.getUid(),
                    mKTSData.build());
        }
    }

    private void initUser() {
        Glide.with(this)
                .load(mUser.getHeadUrl())
                .placeholder(R.drawable.kts_player_user_image)
                .error(R.drawable.kts_player_user_image)
                .transform(new GlideCircleTransform(this))
                .skipMemoryCache(true)
                .into(mUserIcon);
        mUserName.setText(mUser.getNickname());
    }

    private DefaultOnClick mOnClick = new DefaultOnClick(null, new Consumer<View>() {
        @Override
        public void accept(View view) throws Exception {
            if (view.getId() == R.id.imgv_record) {
                // 开启录制页
                startCameraPreviewWithPermCheck();
            } else if (view.getId() == R.id.tv_user_name ||
                    view.getId() == R.id.imgv_user_logo) {
                MineActivity.openMeSettingActivityForResult(MainActivity.this, mUser, REQUEST_OK);
            }
        }
    });
}
