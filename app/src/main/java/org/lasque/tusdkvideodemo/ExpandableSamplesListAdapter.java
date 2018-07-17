/**
 * TuSDKVideoDemo 
 * ExpandableSamplesListAdapter.java
 * 
 * @author Bonan
 * @Date 9:22:31 AM
 * @Copright (c) 2017 tusdk.com. All rights reserved.
 * 
 */
package org.lasque.tusdkvideodemo;

import org.lasque.tusdkvideodemo.api.AudioMixerActivity;
import org.lasque.tusdkvideodemo.api.MediaPlayerActivity;
import org.lasque.tusdkvideodemo.api.MutiAudioPlayerActivity;
import org.lasque.tusdkvideodemo.api.AudioRecordActivity;
import org.lasque.tusdkvideodemo.api.MovieCompressActivity;
import org.lasque.tusdkvideodemo.api.MovieCutActivity;
import org.lasque.tusdkvideodemo.api.MovieSplicerActivity;
import org.lasque.tusdkvideodemo.api.MovieMixerActivity;
import org.lasque.tusdkvideodemo.api.MovieThumbActivity;
import org.lasque.tusdkvideodemo.api.MovieTranscodeActivity;
import org.lasque.tusdkvideodemo.component.MovieEditorActivity;
import org.lasque.tusdkvideodemo.component.MovieRecordKeepModeActivity;
import org.lasque.tusdkvideodemo.component.MovieRecordNormalModeActivity;
import org.lasque.tusdkvideodemo.component.MultipleCameraActivity;
import org.lasque.tusdkvideodemo.custom.MoviePreviewAndCutFullScreenActivity;
import org.lasque.tusdkvideodemo.custom.MoviePreviewAndCutRatioActivity;
import org.lasque.tusdkvideodemo.custom.MovieRecordFullScreenActivity;
import org.lasque.tusdkvideodemo.suite.MoviePreviewAndCutActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * 功能列表界面 Adapter
 * 
 * @author Bonan
 */
public class ExpandableSamplesListAdapter extends BaseExpandableListAdapter
{
    private Context mContext;

    private SampleGroup[] sampleGroups = {
    		// 功能组合展示
            SampleGroup.COMPOSITE,
            // 功能单个展示
            SampleGroup.COMMON,
            // 自定义组件示例
            SampleGroup.CUSTOM,
            // 功能 API 展示
            SampleGroup.API
    };

    public ExpandableSamplesListAdapter(Context context) 
    {
        this.mContext = context;
    }

    @Override
    public int getGroupCount() 
    {
        return sampleGroups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) 
    {
        return sampleGroups[groupPosition].samples.length;
    }

    @Override
    public Object getGroup(int groupPosition) 
    {
        return sampleGroups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) 
    {
        return sampleGroups[groupPosition].samples[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) 
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) 
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() 
    {
        return true;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
    {
        final View groupView;

        if (convertView != null) {
            groupView = convertView;
        }
        else {
            groupView = LayoutInflater.from(mContext).inflate(R.layout.sample_list_group, null);
        }

        ((TextView)groupView.findViewById(R.id.itemTitle))
                .setText(mContext.getResources().getString(((SampleGroup)getGroup(groupPosition)).titleId));

        return groupView;
    }

    @SuppressLint("InflateParams")
	@Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) 
    {
        final View childView;

        if (convertView != null) {
            childView = convertView;
        }
        else {
            childView = LayoutInflater.from(mContext).inflate(R.layout.sample_list_item, null);
        }

        ((TextView)childView.findViewById(R.id.itemTitle))
                .setText(mContext.getResources().getString(((SampleItem)this.getChild(groupPosition, childPosition)).titleId));

        return childView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) 
    {
        return true;
    }

    public enum SampleItem
    {
    	// 功能套件展示
    	RECORD_PREVIEW_EDITOR(R.string.lsq_record_video, MovieRecordAndImportEditorActivity.class.getName(), 0, true),
    	VIDEO_PREVIEW_EDITOR(R.string.lsq_video_preview_editor, MoviePreviewAndCutActivity.class.getName(), 1, false),
    	
    	// 常用组件展示
    	NORMAL_RECORD_CAMERA(R.string.lsq_normal_record_camera, MovieRecordNormalModeActivity.class.getName(), 0, true),
    	CAPTURE_RECORD_CAMERA(R.string.lsq_capture_record_camera, MultipleCameraActivity.class.getName(), 0, true),
    	KEEP_RECORD_CAMERA(R.string.lsq_keep_record_camera, MovieRecordKeepModeActivity.class.getName(), 0, true),
    	ALBUM_VIDEO_EDITOR(R.string.lsq_album_video_editor, MovieEditorActivity.class.getName(), 1, false),
    	
    	// 自定义组件示例
    	FULL_SCREEN_RECORD_PREVIEW_EDITOR(R.string.lsq_full_screen_record_preview_editor, MovieRecordFullScreenActivity.class.getName(), 0, true),
    	FULL_SCREEN_RECORD_PREVIEW_RATIO_EDITOR(R.string.lsq_full_screen_ratio_cut_editor,MoviePreviewAndCutRatioActivity.class.getName(), 1, true),
    	FULL_SCREEN_ALBUM_VIDEO_TIMECUT_EDITOR(R.string.lsq_full_screen_album_video_timecut_editor, MoviePreviewAndCutFullScreenActivity.class.getName(), 1, false),
    	
    	// 功能 API 展示
    	AUDIO_MIXED(R.string.lsq_audio_mixed, AudioMixerActivity.class.getName(), 0, false),
    	VIDEO_BGM(R.string.lsq_video_bgm, MovieMixerActivity.class.getName(), 1, false),
    	GAIN_THUMBNAIL(R.string.lsq_gain_thumbnail, MovieThumbActivity.class.getName(), 0, false),
        AUDIO_FILE_RECORDER(R.string.lsq_audio_file_recorder, AudioRecordActivity.class.getName(), 0, false),
        VIDEO_COMPRESS(R.string.lsq_compresser_compress, MovieCompressActivity.class.getName(), 1, false),
        MUTI_AUDIO_PLAYER(R.string.lsq_muti_audio_player, MutiAudioPlayerActivity.class.getName(), 0, false),
        AUDIO_PLAYER(R.string.lsq_media_player, MediaPlayerActivity.class.getName(), 1, false),
        ALBUM_VIDEO_TIMECUT_SAVE(R.string.lsq_album_video_timecut_save, MovieCutActivity.class.getName(), 1, false),
        VIDEO_MIXED(R.string.lsq_video_mixed, MovieSplicerActivity.class.getName(), 2, false),
        VIDEO_TRANSCODE(R.string.lsq_transcode_transcode, MovieTranscodeActivity.class.getName(), 1, false);

        public String className;
        public int titleId;
        public int OpenAlbumForPicNum;
        public boolean needOpenCamera;

        private SampleItem(int titleId, String className, int needPicNum, boolean needOpenCamera)
        {
            this.className = className;
            this.titleId = titleId;
            this.OpenAlbumForPicNum = needPicNum;
            this.needOpenCamera = needOpenCamera;
        }
    }

    public enum SampleGroup
    {
    	/** 功能组合展示 */
    	COMPOSITE(R.string.lsq_composite_components,
                SampleItem.RECORD_PREVIEW_EDITOR, 
                SampleItem.VIDEO_PREVIEW_EDITOR),

        /** 功能单个展示 */
        COMMON(R.string.lsq_common_components,
                SampleItem.NORMAL_RECORD_CAMERA,
                SampleItem.CAPTURE_RECORD_CAMERA,
                SampleItem.KEEP_RECORD_CAMERA,
                SampleItem.ALBUM_VIDEO_EDITOR),
                
        /** 自定义组件示例 */
    	CUSTOM(R.string.lsq_custom_components,
                SampleItem.FULL_SCREEN_RECORD_PREVIEW_EDITOR, 
                SampleItem.FULL_SCREEN_RECORD_PREVIEW_RATIO_EDITOR, 
                SampleItem.FULL_SCREEN_ALBUM_VIDEO_TIMECUT_EDITOR),
                
        /** 功能 API 展示 */
    	API(R.string.lsq_api_usage_example,
                SampleItem.AUDIO_MIXED, 
                SampleItem.VIDEO_BGM, 
                SampleItem.GAIN_THUMBNAIL,
                SampleItem.AUDIO_FILE_RECORDER,
                SampleItem.VIDEO_COMPRESS,
                SampleItem.MUTI_AUDIO_PLAYER,
                SampleItem.AUDIO_PLAYER,
                SampleItem.ALBUM_VIDEO_TIMECUT_SAVE,
                SampleItem.VIDEO_MIXED,
                SampleItem.VIDEO_TRANSCODE);

        public int titleId;
        public SampleItem[] samples;

        private SampleGroup(int titleId, SampleItem ... samples) 
        {
            this.titleId = titleId;
            this.samples = samples;
        }
    }
}
