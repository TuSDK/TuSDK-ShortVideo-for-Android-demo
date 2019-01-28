package org.lasque.tusdkvideodemo.views.props.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.json.JsonHelper;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdkvideodemo.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/******************************************************************
 * droid-sdk-video 
 * org.lasque.tusdkvideodemo.views.props.model
 *
 * @author sprint
 * @Date 2018/12/28 11:19 AM
 * @Copyright (c) 2018 tutucloud.com. All rights reserved.
 ******************************************************************/
// 贴纸分类
public class PropsItemStickerCategory extends PropsItemCategory<PropsItemSticker>{

    public PropsItemStickerCategory(List<PropsItemSticker> stickerPropsItems) {
        super(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdKMediaEffectDataTypeSticker,stickerPropsItems);
    }

    /**
     * 获取所有贴纸分类
     *
     * @return List<PropsItemStickerCategory>
     */
    public static List<PropsItemStickerCategory> allCategories() {

        List<PropsItemStickerCategory> categories = new ArrayList<>();

        try {
            InputStream stream = TuSdkContext.context().getResources().openRawResource(R.raw.customstickercategories);

            if (stream == null) return null;

            byte buffer[] = new byte[stream.available()];
            stream.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = JsonHelper.json(json);
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                // 该分类下的所有贴纸道具
                List<PropsItemSticker> propsItems = new ArrayList<PropsItemSticker>();

                JSONArray jsonArrayGroup = item.getJSONArray("stickers");

                for (int j = 0; j < jsonArrayGroup.length(); j++) {

                    JSONObject itemGroup = jsonArrayGroup.getJSONObject(j);
                    StickerGroup group = new StickerGroup();
                    group.groupId = itemGroup.optLong("id");
                    group.previewName = itemGroup.optString("previewImage");
                    group.name = itemGroup.optString("name");

                    PropsItemSticker propsItem = new PropsItemSticker(group);
                    propsItems.add(propsItem);
                }

                // 该贴纸道具分类
                PropsItemStickerCategory category = new PropsItemStickerCategory(propsItems);
                category.setName(item.getString("categoryName"));

                categories.add(category);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;

    }
}

