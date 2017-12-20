package com.ksyun.ts.ShortVideoDemo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ksyun.ts.ShortVideoDemo.model.ErrorModel;
import com.ksyun.ts.ShortVideoDemo.model.User;
import com.ksyun.ts.ShortVideoDemo.request.RequestManager;
import com.ksyun.ts.ShortVideoDemo.request.RequestModel;
import com.ksyun.ts.ShortVideoDemo.ui.DefaultOnClick;
import com.ksyun.ts.ShortVideoDemo.ui.GlideCircleTransform;
import com.ksyun.ts.ShortVideoDemo.ui.SelectWindow;
import com.ksyun.ts.ShortVideoDemo.ui.UserLocalization;
import com.ksyun.ts.ShortVideoDemo.ui.Utils;
import com.ksyun.ts.shortvideo.common.util.KLog;
import com.ksyun.ts.shortvideo.kit.IKSVSShortVideoListener;
import com.ksyun.ts.shortvideo.kit.IKSVSShortVideoUpload;
import com.ksyun.ts.skin.KSVSShortVideoKitManager;
import com.ksyun.ts.skin.common.KSVSDialodManager;
import com.ksyun.ts.skin.common.TitleCommonView;
import com.ksyun.ts.skin.util.ToastUtils;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiaoqiang on 2017/11/21.
 */

public class SettingActivity extends BaseActivity {
    private final static int PERMISSION_REQUEST_CAMERA = 1;
    private final static int PERMISSION_REQUEST_WRITE_EXTERNL = 2;
    private static final int TAKE_PHOTO = 1;
    private static final int CUT_PHOTO = 2;
    private static final int CHOOSE_PHOTO = 3;
    private TitleCommonView mCommonView;
    private EditText mUserName;
    private FrameLayout mUserIconSelect;
    private ImageView mUserIcon;
    private TextView mSexSelect;
    private TextView mLogout;
    private SelectWindow mSelectWindow;
    private User mUser;
    private GlideCircleTransform mCircleTransform;
    private RequestManager mRequestManager;
    private Dialog mLoadingDialog;
    private KSVSShortVideoKitManager mKitManager;
    private File mSrcFile;
    private File mDstFile;

    public static void openSettingActivityForResult(Activity context, User user, int requestCode) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra(USER_PARAMS, user);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mUser = (User) getIntent().getSerializableExtra(USER_PARAMS);
        initView();
    }

    private void initView() {
        mCommonView = findViewById(R.id.title_common_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, getBorderTop(), 0, 0);
        mCommonView.setLayoutParams(params);
        mCommonView.setBackView(R.drawable.kts_black_back);
        mCommonView.setTitle(R.string.setting_title, R.color.setting_title_color);
        mCommonView.setOtherView(R.string.setting_save, R.color.setting_title_color);
        mCommonView.setBackClick(new Consumer<View>() {
            @Override
            public void accept(View view) throws Exception {
                finish();
            }
        });
        mCommonView.setOtherClick(mSaveClick);

        // 头像选择
        mUserIconSelect = findViewById(R.id.fl_user_icon_select);
        mUserName = findViewById(R.id.edit_user_name_change);
        mUserIcon = findViewById(R.id.imgv_user_logo);
        mSexSelect = findViewById(R.id.tv_sex_select);
        mLogout = findViewById(R.id.tv_logout);
        mCircleTransform = new GlideCircleTransform(this);

        mUserName.setHint(mUser.getNickname());
        Glide.with(this)
                .load(mUser.getHeadUrl())
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .dontAnimate()
                .transform(mCircleTransform)
                .into(mUserIcon);

        String sex = (mUser.getGender() == null || !mUser.getGender()) ? "男" : "女";
        mSexSelect.setText(sex);

        mUserIconSelect.setOnClickListener(mOnClick);
        mSexSelect.setOnClickListener(mOnClick);
        mLogout.setOnClickListener(mOnClick);

        mRequestManager = new RequestManager(SettingActivity.this);

        mKitManager = new KSVSShortVideoKitManager(SettingActivity.this);
    }


    private void setUserInfo(String userIcon) {
        mRequestManager.setUserInfo(userIcon, mUser.getNickname(),
                (mUser.getGender() != null) ? mUser.getGender() : false,
                new RequestManager.IKSVSRequestListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        new UserLocalization(SettingActivity.this).
                                saveData(mUser);
                        Intent intent = new Intent();
                        intent.putExtra(USER_PARAMS, mUser);
                        setResult(RESULT_OK, intent);
                        finish();
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onFailed(ErrorModel errorInfo) {
                        if ("NotLoggedIn".equalsIgnoreCase(errorInfo.getCode())) {
                            ToastUtils.showToast(SettingActivity.this, R.string.logout_user);
                            LoginActivity.logout(SettingActivity.this);
                        }
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                    }
                });
    }

    /**
     * 更新用户信息
     */
    private Consumer mSaveClick = new Consumer<View>() {
        @Override
        public void accept(View o) throws Exception {
            String name = mUserName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                name = mUser.getNickname();
            }
            mUser.setNickname(name);

            mLoadingDialog = KSVSDialodManager.showLoadingDialog(SettingActivity.this, null);

            if (mDstFile == null || !mDstFile.exists()) {
                setUserInfo(null);
            } else {
                mKitManager.uploadFile(mUser.getUid(), mDstFile.getPath(),
                        new IKSVSShortVideoListener() {
                            @Override
                            public void onInfo(String type, Bundle data) {
                                if (type.equals(IKSVSShortVideoListener.KSVS_LISTENER_TYPE_UPLOAD)) {
                                    int status = data.getInt(IKSVSShortVideoListener.KSVS_LISTENER_BUNDLE_STATUS_INT);
                                    if (status == IKSVSShortVideoUpload.UPLOAD_INFO_COMPLETE) {
                                        String path = data.getString(IKSVSShortVideoUpload.UPLOAD_INFO_FILE_PATH);
                                        KLog.d(TAG, "获取到的path是：" + path);
                                        mUser.setHeadUrl(path);
                                        //图片上传成功
                                        setUserInfo(path);
                                    }
                                }
                            }

                            @Override
                            public void onError(String type, int error, Bundle data) {
                                KLog.e(TAG, "个人信息修改失败，error=" + error);
                                ToastUtils.showToast(SettingActivity.this, R.string.setting_setuser_error);
                                if (mLoadingDialog != null)
                                    mLoadingDialog.dismiss();
                            }

                            @Override
                            public void onProgress(String type, int params, int progress) {

                            }
                        });

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequestManager != null) {
            mRequestManager.releaseAllReuqest();
        }
        mRequestManager = null;
        deleteFile();
    }

    /**
     * 拍一张效果
     * 6.0以上手机需要动态请求权限
     */
    private void openCamera() {
        int cameraPerm = ActivityCompat.checkSelfPermission(SettingActivity.this,
                Manifest.permission.CAMERA);
        int writePerm = ActivityCompat.checkSelfPermission(SettingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (cameraPerm != PackageManager.PERMISSION_GRANTED ||
                writePerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ToastUtils.showToast(SettingActivity.this, R.string.setting_no_camera_permissions);
                KLog.e(TAG, "No Camera permission,check permission");
            } else {
                String[] permissions = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,};
                ActivityCompat.requestPermissions(SettingActivity.this, permissions,
                        PERMISSION_REQUEST_CAMERA);
            }
        } else {
            openImageCapture();
        }
    }

    /**
     * 开启照相功能，文件保存在临时目录下。名称为user_logo
     * <p>
     * 注意，在7.0及以上的设备，因为权限的问题，所以不能使用URi,必须使用ContentValues包装以下
     */
    private void openImageCapture() {
        mSrcFile = new File(Utils.getTempLocalVideoPath(SettingActivity.this),
                "user_logo.png");
        if (mSrcFile.exists()) {
            mSrcFile.delete();
        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSrcFile));
        } else {
            Uri imgUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                    mSrcFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        }
        startActivityForResult(intent, TAKE_PHOTO);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length >= 2 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openImageCapture();
                } else {
                    ToastUtils.showToast(SettingActivity.this, R.string.setting_no_camera_permissions);
                }
            }
            break;
            case PERMISSION_REQUEST_WRITE_EXTERNL:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageGallery();
                } else {
                    ToastUtils.showToast(SettingActivity.this, R.string.setting_no_write_permissions);
                }
                break;
        }
    }

    /**
     * 打开图库
     */
    private void openGallery() {
        int writePerm = ActivityCompat.checkSelfPermission(SettingActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (writePerm != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ToastUtils.showToast(SettingActivity.this, R.string.setting_no_write_permissions);
                KLog.e(TAG, "No write permission,check permission");
            } else {
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,};
                ActivityCompat.requestPermissions(SettingActivity.this, permissions,
                        PERMISSION_REQUEST_WRITE_EXTERNL);
            }
        } else {
            openImageGallery();
        }

    }

    private void openImageGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 图片剪裁
     */
    private void cutPhoto(Uri srcUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(srcUri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("return-data", false);
        mDstFile = new File(Utils.getTempLocalVideoPath(SettingActivity.this),
                "user_logo_dst.png");
        if (mDstFile.exists()) {
            mDstFile.delete();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mDstFile));
        try {
            startActivityForResult(intent, CUT_PHOTO);// 启动裁剪程序
        } catch (Exception e) {
            ToastUtils.showToast(SettingActivity.this, R.string.setting_no_picture);
        }
    }

    private void deleteFile() {
        File[] files = new File[2];
        files[0] = mDstFile != null ? mDstFile : new File("");
        files[1] = mSrcFile != null ? mSrcFile : new File("");
        Observable.fromArray(files)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TAKE_PHOTO:
                Uri imgUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imgUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider",
                            mSrcFile);
                } else {
                    imgUri = Uri.fromFile(mSrcFile);
                }
                cutPhoto(imgUri);
                break;
            case CHOOSE_PHOTO:
                cutPhoto(data.getData());
                break;
            case CUT_PHOTO:
                if (mDstFile != null && mDstFile.exists()) {
                    Glide.with(SettingActivity.this)
                            .load(mDstFile)
                            .placeholder(R.drawable.user_icon)
                            .error(R.drawable.user_icon)
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .transform(mCircleTransform)
                            .into(mUserIcon);
                }
                break;
        }
    }

    private Consumer mSexSelectClick = new Consumer<View>() {
        @Override
        public void accept(View o) throws Exception {
            int type = (int) o.getTag();
            switch (type) {
                case SelectWindow.ITEM1_CLICK:
                    mSexSelect.setText("男");
                    mUser.setGender(false);
                    break;
                case SelectWindow.ITEM2_CLICK:
                    mSexSelect.setText("女");
                    mUser.setGender(true);
                    break;
            }
        }
    };


    private Consumer mUserIconSelectClick = new Consumer<View>() {
        @Override
        public void accept(View o) throws Exception {
            int type = (int) o.getTag();
            switch (type) {
                case SelectWindow.ITEM1_CLICK:
                    openCamera();
                    break;
                case SelectWindow.ITEM2_CLICK:
                    openGallery();
                    break;
            }
        }
    };


    private DefaultOnClick mOnClick = new DefaultOnClick(null, new Consumer<View>() {
        @Override
        public void accept(View view) throws Exception {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            if (view.getId() == R.id.fl_user_icon_select) {// 头像选择
                if (mSelectWindow != null) {
                    mSelectWindow.dismissWindow();
                }
                mSelectWindow = new SelectWindow(SettingActivity.this);
                mSelectWindow.setViewOnClick(mUserIconSelectClick);
                mSelectWindow.showWindow(findViewById(R.id.ll_root_view));
            } else if (view.getId() == R.id.tv_sex_select) { //性别选择
                if (mSelectWindow != null) {
                    mSelectWindow.dismissWindow();
                }
                mSelectWindow = new SelectWindow(SettingActivity.this, "男", "女");
                mSelectWindow.setViewOnClick(mSexSelectClick);
                mSelectWindow.showWindow(findViewById(R.id.ll_root_view));
            } else if (view.getId() == R.id.tv_logout) {  //退出登录
                KSVSDialodManager.showMessageDialog(
                        SettingActivity.this,
                        R.string.setting_logout_message,
                        R.string.setting_logout_confirm,
                        new Consumer<Dialog>() {

                            @Override
                            public void accept(Dialog dialog) throws Exception {
                                mRequestManager.logout(
                                        new RequestManager.IKSVSRequestListener<RequestModel>() {
                                            @Override
                                            public void onSuccess(RequestModel requestModel) {
                                                KLog.d(TAG, "退出登录接口调用成功");
                                            }

                                            @Override
                                            public void onFailed(ErrorModel errorInfo) {
                                                KLog.e(TAG, "退出登录接口调用失败，失败原因是：" +
                                                        errorInfo.getMessage());
                                            }
                                        });
                                LoginActivity.logout(SettingActivity.this);
                            }
                        },
                        R.string.setting_logout_cancel,
                        null
                );

            }
        }
    });

}
