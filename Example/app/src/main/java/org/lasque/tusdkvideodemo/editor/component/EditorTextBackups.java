package org.lasque.tusdkvideodemo.editor.component;

import android.view.View;

import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;

import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/12/19 10:14
 * @Copright (c) 2018 tusdk.com. All rights reserved.
 * <p>
 * 文字特效备份管理
 */
public class EditorTextBackups {
    /** 文字贴纸父视图 **/
    private StickerView mStickerView;
    /** 文字贴纸操作视图 **/
    private EditorTextComponent.EditorTextBottomView mBottomView;
    /** 上一次应用的特效数据 **/
    private List<StickerTextItemView> mMemeoList = new ArrayList<>();
    /** 当前正在编辑的数据 **/
    private List<StickerTextItemView> mEditList = new ArrayList<>();

    public EditorTextBackups(StickerView mStickerView, EditorTextComponent.EditorTextBottomView mBottomView) {
        this.mStickerView = mStickerView;
        this.mBottomView = mBottomView;
    }

    /** 同步组件Attach */
    public void onComponentAttach() {
        mEditList.addAll(mMemeoList);

        //显示备忘
        for (StickerTextItemView itemView : mMemeoList) {
            itemView.setVisibility(View.VISIBLE);
            mStickerView.addView(itemView);
            mStickerView.addSticker(itemView);
            itemView.setTranslation(itemView.getTranslation().x, itemView.getTranslation().y);
        }
    }

    /** 同步组件Detach */
    public void onComponentDetach() {
    }

    /** 添加一个文字贴纸 **/
    public void addStickerText(StickerTextItemView itemView) {
        mEditList.add(itemView);
    }

    /** 移除一个文字贴纸 **/
    public void removeStickerText(StickerTextItemView itemView) {
        mEditList.remove(itemView);
        mStickerView.removeView(itemView);
    }

    /** 应用特效 **/
    public void onApplyEffect() {
        mMemeoList.clear();
        mMemeoList.addAll(mEditList);
        mEditList.clear();
    }

    /** 返回事件 **/
    public void onBackEffect() {
        //移除所有备忘数据
        mEditList.clear();
        mStickerView.cancelAllStickerSelected();
        mStickerView.getStickerItems().clear();
        mStickerView.removeAllViews();

        for (StickerTextItemView itemView : mMemeoList) {
            mStickerView.addView(itemView);
            itemView.setTranslation(itemView.getTranslation().x, itemView.getTranslation().y);
        }

        //应用数据
        if(mMemeoList.size() > 0)
        mBottomView.handleCompleted();
    }

}
