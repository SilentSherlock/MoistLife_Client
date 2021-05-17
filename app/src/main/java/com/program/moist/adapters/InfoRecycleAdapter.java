package com.program.moist.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.program.moist.R;
import com.program.moist.activity.InfoDetailActivity;
import com.program.moist.entity.Information;
import com.program.moist.entity.item.InfoUser;

import org.jetbrains.annotations.NotNull;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/3
 * Description: 根据适配recycle view
 */
public class InfoRecycleAdapter extends BaseQuickAdapter<InfoUser, BaseViewHolder> implements LoadMoreModule {

    public InfoRecycleAdapter(int resourceId) {
        super(resourceId);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, InfoUser infoUser) {

        baseViewHolder.setText(R.id.info_title, infoUser.getInfoTitle())
                .setText(R.id.info_time, infoUser.getUserName())
                .setText(R.id.info_location, "".equals(infoUser.getLocation()) ? "london" : infoUser.getLocation())
                .setText(R.id.info_price, "￥" + infoUser.getPrice())
                .setText(R.id.item_info_hide_id, String.valueOf(infoUser.getInfoId()));
//        baseViewHolder.itemView.setOnClickListener(v -> {
//            Log.i(TAG, "onClick: 卡片被选中");
//            Intent intent = new Intent(getContext(), InfoDetailActivity.class);
//            intent.getExtras().putInt();
//            getContext().startActivity(intent);
//        });
    }

}
