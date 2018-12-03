package org.lasque.tusdkvideodemo.views;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.lasque.tusdkvideodemo.StickerFragment;

import java.util.List;

/**
 * 贴纸ViewPager滑动页
 * @author xujie
 * @Date 2018/9/21
 */

public class TabViewPagerAdapter extends FragmentPagerAdapter{

    // 默认0 其他为贴纸ID
    public static long mStickerGroupId;

    private List<StickerFragment> mFragments;

    public TabViewPagerAdapter(FragmentManager fm,List<StickerFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof StickerFragment){
            ((StickerFragment) object).refetchStickerList();
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public StickerFragment getItem(int i) {
        return mFragments.get(i);
    }
}
