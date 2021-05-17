package com.program.moist.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.program.moist.R;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseActivity;
import com.program.moist.databinding.ActivityChatBinding;
import com.program.moist.entity.User;
import com.program.moist.entity.item.Message;
import com.program.moist.socket.NIOClient;
import com.program.moist.utils.ImageLoaderManager;
import com.program.moist.utils.InnerFileUtil;
import com.program.moist.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.program.moist.base.AppConst.TAG;

/**
 * Author: SilentSherlock
 * Date: 2021/5/10
 * Description: describe the class
 */
public class ChatActivity extends BaseActivity {

    private ActivityChatBinding activityChatBinding;
    private User targetUser;
    private String host;
    private ChatRecycleAdapter chatRecycleAdapter;
    private boolean run = true;
    private Handler handler;
    private Message message;
    private static final int refresh = 1;

    //直接将适配器作为内部类实现
    class ChatRecycleAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {

        public ChatRecycleAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, Message message) {
            if (message != null) {
                if (message.getSender().equals("this")) {
                    baseViewHolder.setText(R.id.right_text, message.getContent());
                    ImageLoaderManager.loadImageWeb(App.context,
                            AppConst.Server.oss_address + Objects.requireNonNull(App.getUserInfo()).getUserAvatar(),
                            baseViewHolder.getView(R.id.right_avatar));
                    baseViewHolder.getView(R.id.left_text).setVisibility(View.GONE);
                    baseViewHolder.getView(R.id.left_avatar).setVisibility(View.INVISIBLE);
                } else {
                    baseViewHolder.setText(R.id.left_text, message.getContent());
                    ImageLoaderManager.loadImageWeb(App.context, AppConst.Server.oss_address + targetUser.getUserAvatar(), baseViewHolder.getView(R.id.left_avatar));
                    baseViewHolder.getView(R.id.right_text).setVisibility(View.GONE);
                    baseViewHolder.getView(R.id.right_avatar).setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(LayoutInflater.from(this));
        setContentView(activityChatBinding.getRoot());

        initView();
        eventBind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        run = false;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initView() {
        targetUser = (User) getIntent().getSerializableExtra("user");
        host = (String) getIntent().getExtras().get("host");

        chatRecycleAdapter = new ChatRecycleAdapter(R.layout.item_chat_message);
        activityChatBinding.chatContent.setAdapter(chatRecycleAdapter);
        activityChatBinding.chatContent.setLayoutManager(new LinearLayoutManager(this));
        activityChatBinding.chatDetailToolBar.setTitle(targetUser.getUserName());

        ArrayList<Message> messages = InnerFileUtil.readObjectFromFile(AppConst.Base.chat_dir + host + ".txt");
        if (messages == null || messages.size() == 0) {
            Log.e(TAG, "initView: 已有消息为空，好像不对哦", new Exception());
            finish();
        }
        Log.i(TAG, "initView: message log" + messages.toString());
        chatRecycleAdapter.addData(messages);
        activityChatBinding.chatContent.scrollToPosition(chatRecycleAdapter.getItemCount()-1);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                switch (msg.what) {
                    case refresh:
                        if (message.getContent().equals(AppConst.Base.key_word)) {
                            new MaterialAlertDialogBuilder(ChatActivity.this)
                                    .setMessage("对方请求获取预留联系方式,是否同意？")
                                    .setNegativeButton("取消", (dialog, which) -> {
                                        dialog.dismiss();
                                        NIOClient.connectAndSend(
                                                new Message(null, AppConst.Base.not_key_word, null, String.valueOf(Objects.requireNonNull(App.getUserInfo()).getUserId())),
                                                null
                                        );
                                        ToastUtil.showToastShort("发送成功~");
                                    })
                                    .setPositiveButton("确定", (dialog, which) -> {
                                        dialog.dismiss();
                                        NIOClient.connectAndSend(
                                                new Message(null, App.getUserInfo().getPhoneNumber(), null, String.valueOf(Objects.requireNonNull(App.getUserInfo()).getUserId())),
                                                null
                                        );
                                        ToastUtil.showToastShort("发送成功~");
                                    })
                                    .show();
                        } else if (message.getContent().equals(AppConst.Base.not_key_word)){
                            new MaterialAlertDialogBuilder(ChatActivity.this)
                                    .setMessage("对方已经拒绝进一步联系")
                                    .setPositiveButton("确定", null)
                                    .show();
                        } else {
                            Log.i(TAG, "handleMessage: 子线程更新UI线程消息");
                            chatRecycleAdapter.addData(message);
                            activityChatBinding.chatContent.scrollToPosition(chatRecycleAdapter.getItemCount()-1);
                        }
                        break;
                }
            }
        };
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void eventBind() {
        activityChatBinding.chatDetailToolBar.setNavigationOnClickListener(v -> {
            finish();
        });

        activityChatBinding.chatDetailSendMessage.setOnClickListener(v -> {
            String text = Objects.requireNonNull(activityChatBinding.chatDetailMessage.getText()).toString();
            if (text.equals("")) ToastUtil.showToastShort("消息不能为空哦~");
            else {
                Message local = Message.createByAll("this", text, AppConst.getFormatDate(null) ,String.valueOf(Objects.requireNonNull(App.getUserInfo()).getUserId()));
                activityChatBinding.chatDetailMessage.setText("");
                chatRecycleAdapter.addData(local);
                activityChatBinding.chatContent.scrollToPosition(chatRecycleAdapter.getItemCount()-1);
                InnerFileUtil.saveObjectToFile(local, AppConst.Base.chat_dir, host + ".txt");
                NIOClient.connectAndSend(
                        new Message(null, text, null, String.valueOf(Objects.requireNonNull(App.getUserInfo()).getUserId())),
                        null
                );

            }
        });
        messageListenerRegister();

        activityChatBinding.chatDetailToolBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.chat_menu_send:
                    new MaterialAlertDialogBuilder(this)
                            .setMessage("确定向对方发送请求,交换预留联系方式吗")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", (dialog, which) -> {
                                dialog.dismiss();
                                NIOClient.connectAndSend(
                                        new Message(null, AppConst.Base.key_word, null, String.valueOf(Objects.requireNonNull(App.getUserInfo()).getUserId())),
                                        null
                                );
                                ToastUtil.showToastShort("发送成功~");
                            })
                            .show();
                    break;
                default:
                    return false;
            }
            return true;
        });
    }


    //获取正在聊天的对象发来的消息
    private void messageListenerRegister() {
        new Thread(() -> {
            while (run) {
                try {
                    this.message = App.messageQueue.take();
                    android.os.Message message = new android.os.Message();
                    message.what = refresh;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, host).start();
    }
}
