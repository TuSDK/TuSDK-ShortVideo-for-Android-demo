package org.lasque.tusdkvideodemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdkvideodemo.views.StickerGroupCategories;
import org.lasque.tusdkvideodemo.views.StickerRecyclerAdapter;
import org.lasque.tusdkvideodemo.views.TabViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴纸ListView
 * @author xujie
 * @Date 2018/9/21
 */

public class StickerFragment extends Fragment{

    public static final String CATEGORIES = "categories";

    /**贴纸栏视图*/
    private RecyclerView mStickerListView;
    private StickerRecyclerAdapter mStickerRecyclerAdapter;
    public StickerGroupCategories mStickerGroupCategories;

    // 点击反馈
    public OnStickerItemClickListener listener;

    public void setOnStickerItemClickListener(OnStickerItemClickListener listener){
        this.listener = listener;
    }

    public interface OnStickerItemClickListener{
        void onStickerItemClick(StickerGroup itemData);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if(bundle != null){
            mStickerGroupCategories = (StickerGroupCategories) bundle.getSerializable(CATEGORIES);
        }

        View view =inflater.inflate(R.layout.sticker_listview_layout,null);
        getStickerListView(view);
        refetchStickerList();
        return view;
    }

    public static StickerFragment newInstance(StickerGroupCategories categories){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CATEGORIES,categories);
        StickerFragment fragment = new StickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 贴纸组视图
     */
    public RecyclerView getStickerListView(View view) {
        mStickerListView = (RecyclerView) view.findViewById(R.id.lsq_sticker_list_view);
        mStickerRecyclerAdapter = new StickerRecyclerAdapter(getActivity());
        mStickerListView.setAdapter(mStickerRecyclerAdapter);
        mStickerRecyclerAdapter.setItemClickListener(mStickerClickListener);
        mStickerRecyclerAdapter.setItemDeleteListener(mItemDeleteListener);
        mStickerRecyclerAdapter.setStickerGroupList(mStickerGroupCategories.getStickerGroupList());
        GridLayoutManager grid = new GridLayoutManager(getActivity(), 5);
        mStickerListView.setLayoutManager(grid);
        mStickerListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int width = parent.getWidth();
                int w = (width - TuSdkContext.dip2px(45) * 5) / 6;
                outRect.bottom = 28;
                outRect.right = w / 2;
                outRect.left = w / 2;
                outRect.top = 20;
            }
        });
        return mStickerListView;
    }

    /**
     * 刷新本地贴纸列表
     */
    public void refetchStickerList() {
        if (mStickerListView == null) return;

        List<StickerGroup> groups = new ArrayList<>();
        groups.addAll(mStickerGroupCategories.getStickerGroupList());
        if(TabViewPagerAdapter.mStickerGroupId != 0)
            for (int i = 0;i < groups.size(); i++){
                if(groups.get(i).groupId == TabViewPagerAdapter.mStickerGroupId){
                    mStickerRecyclerAdapter.setSelectedPosition(i);
                }
            }
        else
            mStickerRecyclerAdapter.setSelectedPosition(-1);
        mStickerRecyclerAdapter.setStickerGroupList(groups);
    }

    /** 贴纸删除事件 */
    private StickerRecyclerAdapter.ItemDeleteListener mItemDeleteListener = new StickerRecyclerAdapter.ItemDeleteListener() {
        @Override
        public void onItemDelete(int position) {
            if(listener != null && mStickerRecyclerAdapter.getStickerGroupList().get(position).groupId == TabViewPagerAdapter.mStickerGroupId) {
                listener.onStickerItemClick(null);
                mStickerRecyclerAdapter.setSelectedPosition(-1);
            }
        }
    };

    /** 贴纸点击事件 */
    private StickerRecyclerAdapter.ItemClickListener mStickerClickListener = new StickerRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            StickerGroup itemData = mStickerRecyclerAdapter.getStickerGroupList().get(position);
            // 必须重新获取StickerGroup,否则itemData.stickers为null
            itemData = StickerLocalPackage.shared().getStickerGroup(itemData.groupId);
            if(listener != null) listener.onStickerItemClick(itemData);
        }
    };

}
