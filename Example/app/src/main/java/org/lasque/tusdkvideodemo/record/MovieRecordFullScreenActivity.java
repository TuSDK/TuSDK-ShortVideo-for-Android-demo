package org.lasque.tusdkvideodemo.record;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs;
import org.lasque.tusdk.core.utils.hardware.TuSDKRecordVideoCamera;
import org.lasque.tusdk.core.utils.json.JsonHelper;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdkvideodemo.SimpleCameraActivity;
import org.lasque.tusdkvideodemo.R;
import org.lasque.tusdkvideodemo.StickerFragment;
import org.lasque.tusdkvideodemo.utils.Constants;
import org.lasque.tusdkvideodemo.views.StickerGroupCategories;
import org.lasque.tusdkvideodemo.views.TabViewPagerAdapter;
import org.lasque.tusdkvideodemo.views.record.RecordView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MovieRecordFullScreenActivity extends SimpleCameraActivity implements
        RecordView.TuSDKMovieRecordDelegate, TuSDKRecordVideoCamera.TuSDKRecordVideoCameraDelegate {
    // 录制界面视图
    protected RecordView mRecordView;
    // 贴纸PagerAdapter
    private TabViewPagerAdapter mStickerPagerAdapter;
    // 贴纸类别
    private List<StickerGroupCategories> mStickerGroupCategoriesList;

    protected int getLayoutId()
    {
        return R.layout.activity_new_record_full_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        initCamera();

        initSticker();

        getRecordView();

        // 设置录制界面背景为透明色
//        setRecordViewBackgroundColor(getRecordView());
//        getRecordView().setSquareSticker(false);

//        hideNavigationBar();
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        getRecordView().initRecordProgress();
        // 同步闪关灯状态
        getRecordView().updateFlashMode(CameraConfigs.CameraFlash.Off);
    }

    /**
     * 初始化贴纸
     */
    protected void initSticker()
    {
        TabViewPagerAdapter.mStickerGroupId = 0;
        List<StickerFragment> fragments = new ArrayList<>();
        mStickerGroupCategoriesList = getRawStickGroupList();
        for (StickerGroupCategories categories : mStickerGroupCategoriesList){
            StickerFragment stickerFragment = StickerFragment.newInstance(categories);
            stickerFragment.setOnStickerItemClickListener(onStickerItemClickListener);
            fragments.add(stickerFragment);
        }
        mStickerPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(),fragments);
    }

    /**
     * 贴纸点击事件
     */
    private StickerFragment.OnStickerItemClickListener onStickerItemClickListener = new StickerFragment.OnStickerItemClickListener() {
        @Override
        public void onStickerItemClick(StickerGroup itemData) {
            if(itemData == null)
                mVideoCamera.removeAllLiveSticker();
            else {
                mVideoCamera.showGroupSticker(itemData);
                TabViewPagerAdapter.mStickerGroupId = itemData.groupId;
            }
        }
    };

    /**
     * 获取贴纸json
     * @return
     */
    private List<StickerGroupCategories> getRawStickGroupList() {
        List<StickerGroupCategories> list = new ArrayList<StickerGroupCategories>();
        try {
            InputStream stream = getResources().openRawResource(R.raw.customstickercategories);

            if (stream == null) return null;

            byte buffer[] = new byte[stream.available()];
            stream.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = JsonHelper.json(json);
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                StickerGroupCategories categories = new StickerGroupCategories();
                categories.setCategoryName(item.getString("categoryName"));
                List<StickerGroup> groupList = new ArrayList<StickerGroup>();
                JSONArray jsonArrayGroup = item.getJSONArray("stickers");
                for (int j = 0; j < jsonArrayGroup.length(); j++) {
                    JSONObject itemGroup = jsonArrayGroup.getJSONObject(j);
                    StickerGroup group = new StickerGroup();
                    group.groupId = itemGroup.optLong("id");
                    group.previewName = itemGroup.optString("previewImage");
                    group.name = itemGroup.optString("name");
                    groupList.add(group);
                }
                categories.setStickerGroupList(groupList);
                list.add(categories);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 录制界面视图
     */
    protected RecordView getRecordView()
    {
        if (mRecordView == null)
        {
            mRecordView = (RecordView) findViewById(R.id.lsq_movie_record_view);
            mRecordView.setDelegate(this);
            mRecordView.setUpCamera(this, mVideoCamera);
            mRecordView.setStickerAdapter(mStickerPagerAdapter,mStickerGroupCategoriesList);
        }
        return mRecordView;
    }

    protected void initCamera()
    {
        super.initCamera();

        mVideoCamera.setVideoDelegate(this);
        mVideoCamera.setMinRecordingTime(Constants.MIN_RECORDING_TIME);
        mVideoCamera.setMaxRecordingTime(Constants.MAX_RECORDING_TIME);

        // 设置使用录制相机最小空间限制,开发者可根据需要自行设置（默认：50M）
        mVideoCamera.setMinAvailableSpaceBytes(1024*1024*50l);

        // 录制模式
        mVideoCamera.setRecordMode(TuSDKRecordVideoCamera.RecordMode.Keep);
        // 限制录制尺寸不超过 1280
        mVideoCamera.setPreviewEffectScale(1.0f);
        mVideoCamera.setPreviewMaxSize(1280);
        // 指定为全屏画面比例
        mVideoCamera.setPreviewRatio(0);
        // 开启人脸检测 开启后方可使用人脸贴纸及微整形功能
        mVideoCamera.setEnableFaceDetection(true);

        // 编码配置
        TuSDKVideoEncoderSetting encoderSetting = TuSDKVideoEncoderSetting.getDefaultRecordSetting();
        // 输出全屏尺寸
        encoderSetting.videoSize = TuSdkSize.create(0, 0);
        // 这里可以修改帧率和码率; RECORD_MEDIUM2第一个参数代表帧率，第二参数代表码率;选择VideoQuality参数尽量选用RECORD开头(专门为视频录制设计)
        encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_MEDIUM2;
        // 完全自定义帧率和码率
        // encoderSetting.videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_MEDIUM2.setFps(30).setBitrate(3000 * 1000);

        mVideoCamera.setVideoEncoderSetting(encoderSetting);

    }

    /** ----------- 注意事项：如果视频录制完成后需要跳转到视频编辑页面,需要将录制视频页面销毁掉; 视频编辑跳转视频录制也是如此 ---------------------------*/
    @Override
    public void onMovieRecordComplete(TuSDKVideoResult result)
    {
        mRecordView.updateViewOnMovieRecordComplete(isRecording());
    }

    @Override
    public void onMovieRecordProgressChanged(float progress,
                                             float durationTime)
    {
        mRecordView.updateViewOnMovieRecordProgressChanged(progress, durationTime);
    }

    @Override
    public void onMovieRecordStateChanged(TuSDKRecordVideoCamera.RecordState state)
    {
        mRecordView.updateMovieRecordState(state, isRecording());
    }

    @Override
    public void onMovieRecordFailed(TuSDKRecordVideoCamera.RecordError error)
    {
        TLog.e("RecordError : %s",error);
        mRecordView.updateViewOnMovieRecordFailed(error, isRecording());
    }

    @Override
    public void stopRecording()
    {
        if (mVideoCamera.isRecording())
        {
            mVideoCamera.stopRecording();
        }
//        mRecordView.updateViewOnStopRecording(mVideoCamera.isRecording());
    }

    @Override
    public void pauseRecording()
    {
        if (mVideoCamera.isRecording())
        {
            mVideoCamera.pauseRecording();
        }
    }

    @Override
    public void startRecording()
    {
        if (!mVideoCamera.isRecording())
        {
            mVideoCamera.startRecording();
        }

//        mRecordView.updateViewOnStartRecording(mVideoCamera.isRecording());
    }

    @Override
    public boolean isRecording()
    {
        return mVideoCamera.isRecording();
    }

    @Override
    public void finishRecordActivity()
    {
        this.finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
