package com.program.moist.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.program.moist.R;
import com.program.moist.activity.InfoDetailActivity;
import com.program.moist.entity.Information;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/6
 * Description: describe the class
 */
public class InfoLeftAdapter extends BaseQuickAdapter<Information, BaseViewHolder> implements LoadMoreModule {
    public InfoLeftAdapter(int resourceId) {
        super(resourceId);
    }

    @SuppressLint({"NonConstantResourceId", "SimpleDateFormat"})
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Information information) {

        baseViewHolder.setText(R.id.info_title, information.getInfoTitle())
                .setText(R.id.info_time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(information.getUpdateTime()))
                .setText(R.id.info_location, "".equals(information.getArea()) ? "london" : information.getArea())
                .setText(R.id.info_price, "￥" + information.getPrice());
        baseViewHolder.itemView.setOnClickListener(v -> {
            Log.i(TAG, "onClick: 卡片被选中");
            Intent intent = new Intent(getContext(), InfoDetailActivity.class);
            intent.putExtra("information", information);
            getContext().startActivity(intent);
        });
    }
}
