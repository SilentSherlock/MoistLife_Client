package com.program.moist.adapters;

import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.entity.Information;
import com.program.moist.utils.ImageLoaderManager;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

/**
 * Author: SilentSherlock
 * Date: 2021/5/7
 * Description: describe the class
 */
public class BannerImageAdapter extends BaseBannerAdapter<String> {
    @Override
    protected void bindData(BaseViewHolder<String> holder, String data, int position, int pageSize) {
        ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + data, holder.findViewById(R.id.banner_image));
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_banner_image;
    }
}
