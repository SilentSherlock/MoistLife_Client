package com.program.moist.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.entity.User;
import com.program.moist.utils.ImageLoaderManager;

import org.jetbrains.annotations.NotNull;

/**
 * Author: SilentSherlock
 * Date: 2021/5/5
 * Description: describe the class
 */
public class UserRecycleAdapter extends BaseQuickAdapter<User, BaseViewHolder> implements LoadMoreModule {

    public UserRecycleAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, User user) {
        baseViewHolder.setText(R.id.item_user_name, user.getUserName())
                .setText(R.id.item_user_location, user.getLocation());
        if (user.getUserAvatar() == null || user.getUserAvatar().equals("")) {
            baseViewHolder.setImageResource(R.id.item_user_avatar, R.mipmap.avatar_2);
        } else {
            baseViewHolder.setImageResource(R.id.item_user_avatar, R.mipmap.avatar_2);
        }
    }
}
