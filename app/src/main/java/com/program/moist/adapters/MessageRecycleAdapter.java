package com.program.moist.adapters;

import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.program.moist.R;
import com.program.moist.activity.ChatActivity;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.entity.item.Message;
import com.program.moist.entity.item.MessageUser;
import com.program.moist.utils.ImageLoaderManager;

import org.jetbrains.annotations.NotNull;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/13
 * Description: describe the class
 */
public class MessageRecycleAdapter extends BaseQuickAdapter<MessageUser, BaseViewHolder> {

    public MessageRecycleAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MessageUser messageUser) {
        baseViewHolder.setText(R.id.item_message_user_name, messageUser.getUser().getUserName());
        baseViewHolder.setText(R.id.item_message_hint, messageUser.getMessage().getContent());
        String time = messageUser.getMessage().getDate();
        if (time != null) {
            String[] times = time.split(" ");
            time = times[1].substring(0, times[1].lastIndexOf(":"));
        }
        baseViewHolder.setText(R.id.message_time, time);
        ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + messageUser.getUser().getUserAvatar(), baseViewHolder.getView(R.id.item_message_avatar));

        baseViewHolder.itemView.setOnClickListener(v -> {
            Log.i(TAG, "convert: 信息卡片被选中");
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("host", messageUser.getMessage().getSender());
            intent.putExtra("user", messageUser.getUser());
            getContext().startActivity(intent);
        });
    }

}
