package com.ksyun.ts.ShortVideoDemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ksyun.ts.ShortVideoDemo.model.User;
import com.ksyun.ts.ShortVideoDemo.ui.DefaultOnClick;
import com.ksyun.ts.ShortVideoDemo.ui.GlideCircleTransform;
import com.ksyun.ts.shortvideo.common.model.ColumnInfo;
import com.ksyun.ts.shortvideo.common.model.MediaInfo;
import com.ksyun.ts.shortvideo.common.model.VideoInfos;
import com.ksyun.ts.shortvideo.kit.IKSVSShortVideoData;
import com.ksyun.ts.skin.KSVSShortVideoKitManager;
import com.ksyun.ts.skin.common.KSVSDialodManager;
import com.ksyun.ts.skin.common.TitleCommonView;
import com.ksyun.ts.skin.ui.BlurImageView;
import com.ksyun.ts.skin.ui.SwipeItemLayout;
import com.ksyun.ts.skin.util.Constant;
import com.ksyun.ts.skin.util.ContextUtil;
import com.ksyun.ts.skin.util.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by xiaoqiang on 2017/11/20.
 */

public class MineActivity extends BaseActivity {
    private TitleCommonView mCommonView;
    private final static int REQUESTCODE = 0;
    private User mUser;
    private GlideCircleTransform mCircleTransform;
    private ImageView mUserIcon;
    private TextView mUserName;
    private TextView mUserID;
    private ImageView mSexIcon;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRemoteVideo;
    private RemoteVideoAdapter mRemoteAdapter;
    private KSVSShortVideoKitManager mKitManager;
    private Dialog mLoadingDialog;
    private TextView mUploadNum;
    private SimpleDateFormat mDataFormat;
    private List<MediaInfo> mMediaInfos;

    public final static void openMeSettingActivityForResult(Activity context, User user, int request) {
        Intent intent = new Intent(context, MineActivity.class);
        intent.putExtra(USER_PARAMS, user);
        context.startActivityForResult(intent, request);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        mUser = (User) getIntent().getSerializableExtra(USER_PARAMS);
        mKitManager = new KSVSShortVideoKitManager(this);
        mDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        initView();
    }

    private void initView() {
        mCommonView = findViewById(R.id.title_common_view);
        mCommonView.setPadding(0, getBorderTop(), 0, 0);
        mCommonView.setBackView(R.drawable.kts_white_back);
        mCommonView.setOtherView(R.drawable.me_setting);
        mCircleTransform = new GlideCircleTransform(this);
        mUserIcon = findViewById(R.id.imgv_user_logo);
        mUserName = findViewById(R.id.tv_user_name);
        mUserID = findViewById(R.id.tv_user_uid);
        mSexIcon = findViewById(R.id.imgv_sex_logo);

        mCommonView.setBackClick(new Consumer<View>() {
            @Override
            public void accept(View view) throws Exception {
                onBackClick();
            }
        });
        mCommonView.setOtherClick(new Consumer<View>() {
            @Override
            public void accept(View view) throws Exception {
                SettingActivity.openSettingActivityForResult(MineActivity.this, mUser, REQUESTCODE);
            }
        });
        mUserIcon.setOnClickListener(new DefaultOnClick(null, new Consumer<View>() {
            @Override
            public void accept(View view) throws Exception {
                SettingActivity.openSettingActivityForResult(MineActivity.this, mUser, REQUESTCODE);
            }
        }));
        uploadUser();

        mLayoutManager = new LinearLayoutManager(MineActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRemoteVideo = findViewById(R.id.recv_remote_video);
        mRemoteVideo.setLayoutManager(mLayoutManager);
        mRemoteVideo.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        mUploadNum = findViewById(R.id.tv_upload_video);

        mLoadingDialog = KSVSDialodManager.showLoadingDialog(MineActivity.this, null);
        String message = getResources().getString(R.string.me_upload_video_size);
        message = String.format(message, 0);
        mUploadNum.setText(message);
        /**
         * 作为Demo演示，只是给出前50个上传视频的演示
         */
        mKitManager.getUploadVideos(mUser.getUid(), 1, Constant.MAX_LOAD_NUM,
                new IKSVSShortVideoData.IKSVSShortVideoRequestListener<VideoInfos>() {
                    @Override
                    public void onRequestDate(VideoInfos a) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                        String message = getResources().getString(R.string.me_upload_video_size);
                        if (a != null && a.getVideos() != null) {
                            mMediaInfos = a.getVideos();
                            mRemoteAdapter = new RemoteVideoAdapter(MineActivity.this,
                                    mMediaInfos);
                            mRemoteVideo.setAdapter(mRemoteAdapter);
                            message = String.format(message, a.getVideos().size());
                            mUploadNum.setText(message);
                        } else {
                            ToastUtils.showToast(MineActivity.this,
                                    R.string.me_get_upload_video_null);
                            message = String.format(message, 0);
                        }
                        mUploadNum.setText(message);
                    }

                    @Override
                    public void onRequestFailed(int error, String message) {
                        mLoadingDialog.dismiss();
                        mLoadingDialog = null;
                        ToastUtils.showToast(MineActivity.this, R.string.me_get_upload_error);
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onBackClick();  //覆盖系统返回键进行个性化处理
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onBackClick() {
        Intent intent = new Intent();
        intent.putExtra(USER_PARAMS, mUser);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deleteUploadVideo(final MediaInfo info) {
        mLoadingDialog = KSVSDialodManager.showLoadingDialog(MineActivity.this, null);
        mKitManager.deleteUploadVideos(mUser.getUid(), info.getId(),
                new IKSVSShortVideoData.IKSVSShortVideoRequestListener<Boolean>() {
                    @Override
                    public void onRequestDate(Boolean a) {
                        mMediaInfos.remove(info);
                        mRemoteAdapter.notifyDataSetChanged();
                        String message = getResources().getString(R.string.me_upload_video_size);
                        message = String.format(message, mMediaInfos.size());
                        mUploadNum.setText(message);
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onRequestFailed(int error, String message) {
                        mLoadingDialog.dismiss();
                        ToastUtils.showToast(MineActivity.this, R.string.me_delete_video_error);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            mUser = (User) data.getSerializableExtra(USER_PARAMS);
            uploadUser();
        }
    }


    private void uploadUser() {
        Glide.with(this)
                .load(mUser.getHeadUrl())
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .dontAnimate()
                .transform(mCircleTransform)
                .into(mUserIcon);
        mUserName.setText(mUser.getNickname());
        String uid = String.format(getResources().getString(R.string.me_setting_uid),
                mUser.getUid());
        mUserID.setText(uid);

        if (mUser != null && mUser.getGender() != null && mUser.getGender()) {
            mSexIcon.setImageResource(R.drawable.sex_woman);
        } else {
            mSexIcon.setImageResource(R.drawable.sex_man);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mKitManager.release();
        mKitManager = null;
    }

    class RemoteVideoAdapter extends RecyclerView.Adapter<RemoteVideoAdapter.RemoteHolder> {
        private Context mContext;
        private List<MediaInfo> mVideos;

        public RemoteVideoAdapter(Context context, List<MediaInfo> videos) {
            this.mContext = context;
            this.mVideos = videos;
        }

        @Override
        public RemoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.item_upload_video, null);
            return new RemoteHolder(view);
        }

        @Override
        public void onBindViewHolder(RemoteHolder holder, int position) {
            holder.bindData(mVideos.get(position));
        }

        @Override
        public int getItemCount() {
            return mVideos.size();
        }

        private DefaultOnClick mOnClick = new DefaultOnClick(null, new Consumer<View>() {
            @Override
            public void accept(final View view) throws Exception {
                if (view.getId() == R.id.btn_delete) {
                    KSVSDialodManager.showMessageDialog(mContext,
                            R.string.me_delete_video_dialog_message,
                            R.string.me_delete_video_dialog_confirm,
                            new Consumer<Dialog>() {
                                @Override
                                public void accept(Dialog dialog) throws Exception {
                                    deleteUploadVideo((MediaInfo) view.getTag());
                                }
                            },
                            R.string.me_delete_video_dialog_cancel,
                            null);

                } else {
                    ColumnInfo info = new ColumnInfo();
                    info.setId(1);
                    KSVSShortVideoKitManager.startPlayer((Activity) mContext,
                            (MediaInfo) view.getTag(), null, mUser.getUid());
                }
            }
        });

        class RemoteHolder extends RecyclerView.ViewHolder {
            BlurImageView mVideoCover;
            TextView mVideoName;
            TextView mCreateTimer;
            TextView mDuration;
            Button mDelete;
            View mMain;

            public RemoteHolder(View itemView) {
                super(itemView);
                mVideoCover = itemView.findViewById(R.id.imgv_video_cover);
                mVideoName = itemView.findViewById(R.id.tv_video_name);
                mCreateTimer = itemView.findViewById(R.id.tv_create_timer);
                mDuration = itemView.findViewById(R.id.tv_video_duration);
                mDelete = itemView.findViewById(R.id.btn_delete);
                mDelete.setOnClickListener(mOnClick);
                mMain = itemView.findViewById(R.id.main);
                mMain.setOnClickListener(mOnClick);
            }

            public void bindData(MediaInfo info) {
                Glide.with(mContext)
                        .load(info.getCover())
                        .dontAnimate()
                        .dontTransform()
                        .placeholder(R.drawable.kts_recommend_back_default_1)
                        .error(R.drawable.kts_recommend_back_default_1)
                        .into(mVideoCover);
                mVideoName.setText(info.getName());
                mCreateTimer.setText(mDataFormat.format(info.getTime()));
                mDuration.setText(ContextUtil.formatTime((int) (info.getDuration() / 1000)));
                mDelete.setTag(info);
                mMain.setTag(info);
            }
        }
    }

}
