package org.lasque.tusdkvideodemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import org.lasque.tusdk.core.decoder.TuSDKMoviePacketReader;
import org.lasque.tusdk.core.decoder.TuSDKVideoTimeEffectController;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.video.editor.TuSDKMovieEditor;
import org.lasque.tusdk.video.editor.TuSDKTimeRange;
import org.lasque.tusdkvideodemo.R;

/**
 * @author ligh
 * @Date: 27/03/2018
 * @Copyright: (c) 2018 tusdk.com. All rights reserved.
 * @Description
 */
public class TimeEffectsLayout extends TuSdkRelativeLayout implements View.OnClickListener
{
    private TuSDKMovieEditor mMovieEditor;

    public TimeEffectsLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setMovieEditor(TuSDKMovieEditor movieEditor)
    {
        this.mMovieEditor = movieEditor;
    }

    /**
     * 加载视图
     */
    @Override
    public void loadView()
    {
        super.loadView();

        findViewById(R.id.lsq_time_effects_reverse_btn).setOnClickListener(this);
        findViewById(R.id.lsq_time_effects_sequence_btn).setOnClickListener(this);
        findViewById(R.id.lsq_time_effects_repeat_btn).setOnClickListener(this);
        findViewById(R.id.lsq_time_effects_slow_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.lsq_time_effects_reverse_btn:
                mMovieEditor.setPlayMode(TuSDKMoviePacketReader.ReadMode.ReverseMode);
                break;
            case R.id.lsq_time_effects_sequence_btn:
                mMovieEditor.setPlayMode(TuSDKMoviePacketReader.ReadMode.SequenceMode);
                break;
            case R.id.lsq_time_effects_repeat_btn:
                long mStartActionTime = Long.valueOf(((EditText)findViewById(R.id.lsq_time_effects_start_time)).getText().toString());
                long mTimeDuration = Long.valueOf(((EditText)findViewById(R.id.lsq_time_effects_duration)).getText().toString());
                int mTimes = Integer.valueOf(((EditText)findViewById(R.id.lsq_time_effects_times)).getText().toString());
                mMovieEditor.setTimeEffectMode(TuSDKVideoTimeEffectController.TimeEffectMode.RepeatMode, TuSDKTimeRange.makeTimeUsRange(mStartActionTime * 1000,(mStartActionTime + mTimeDuration) * 1000),mTimes);
                break;
            case R.id.lsq_time_effects_slow_btn:
                long mStartActionTime_ = Long.valueOf(((EditText)findViewById(R.id.lsq_time_effects_start_time)).getText().toString());
                long mTimeDuration_ = Long.valueOf(((EditText)findViewById(R.id.lsq_time_effects_duration)).getText().toString());
                int mTimes_ = Integer.valueOf(((EditText)findViewById(R.id.lsq_time_effects_times)).getText().toString());
                mMovieEditor.setTimeEffectMode(TuSDKVideoTimeEffectController.TimeEffectMode.SlowMode,TuSDKTimeRange.makeTimeUsRange(mStartActionTime_ * 1000,(mStartActionTime_ + mTimeDuration_) * 1000),mTimes_);
                break;
        }
    }
}
