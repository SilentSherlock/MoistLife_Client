package com.program.moist.adapters;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.entity.item.CommentUser;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.ToastUtil;
import com.program.moist.widget.IconFontTextView;

import org.jetbrains.annotations.NotNull;

/**
 * Author: SilentSherlock
 * Date: 2021/5/15
 * Description: describe the class
 */
public class CommentAdapter extends BaseQuickAdapter<CommentUser, BaseViewHolder> {

    public CommentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CommentUser commentUser) {
        baseViewHolder.setText(R.id.item_comment_detail, commentUser.getComment().getContent());
        ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + commentUser.getFromUser().getUserAvatar(), baseViewHolder.getView(R.id.item_comment_user_avatar));
        baseViewHolder.setText(R.id.item_comment_thumb_up_account, AppConst.getRandomNumber(null));
        baseViewHolder.setText(R.id.item_comment_hide_user_id, String.valueOf(commentUser.getFromUser().getUserId()));
        baseViewHolder.setText(R.id.item_comment_time, AppConst.getFormatDate(commentUser.getComment().getComTime()));

        if (commentUser.getToUser() == null) {
            baseViewHolder.setText(R.id.item_comment_user_name, commentUser.getFromUser().getUserName());
        } else {
            baseViewHolder.setText(R.id.item_comment_user_name, commentUser.getFromUser().getUserName() + " 回复 " + commentUser.getToUser().getUserName());
        }
        baseViewHolder.setText(R.id.item_comment_hide_user_name, commentUser.getFromUser().getUserName());

        baseViewHolder.getView(R.id.item_comment_thumb_up_icon).setOnClickListener(v -> {
            IconFontTextView iconFontTextView = (IconFontTextView) v;
            TextView account = (TextView) baseViewHolder.getView(R.id.item_comment_thumb_up_account);
            int n = Integer.parseInt(account.getText().toString());
            if ("check".equals((String) iconFontTextView.getTag())) {
                iconFontTextView.setText(getContext().getString(R.string.ic_thumb_up_fill));
                iconFontTextView.setTag("checked");
                account.setText(String.valueOf(++n));
                ToastUtil.showToastShort("点赞成功~");
            } else {
                iconFontTextView.setText(getContext().getString(R.string.ic_thumb_up));
                iconFontTextView.setTag("check");
                account.setText(String.valueOf(--n));
                ToastUtil.showToastShort("点赞取消:-)");
            }
        });
    }

}
