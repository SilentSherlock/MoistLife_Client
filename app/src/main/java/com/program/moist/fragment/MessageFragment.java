package com.program.moist.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.program.moist.R;
import com.program.moist.adapters.MessageRecycleAdapter;
import com.program.moist.base.App;
import com.program.moist.base.AppConst;
import com.program.moist.base.BaseFragment;
import com.program.moist.databinding.FragmentMessageBinding;
import com.program.moist.entity.User;
import com.program.moist.entity.item.Message;
import com.program.moist.entity.item.MessageUser;
import com.program.moist.socket.NIOClient;
import com.program.moist.utils.GsonUtil;
import com.program.moist.utils.Result;
import com.program.moist.utils.ResultCallback;
import com.program.moist.utils.Status;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends BaseFragment {

    private static final String TAG = "fuck";
    private FragmentMessageBinding fragmentMessageBinding;
    private MessageRecycleAdapter messageRecycleAdapter;
    private static boolean run = true;
    private HashMap<String, MessageUser> map;//sendId->messageUser
    private static final int refresh = 1;
    private Handler handler;
    private Message message;


    public MessageFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        fragmentMessageBinding = FragmentMessageBinding.inflate(inflater, container, false);
        initView();
        eventBind();
        return fragmentMessageBinding.getRoot();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void initView() {
        map = new HashMap<>();
        messageRecycleAdapter = new MessageRecycleAdapter(R.layout.item_message_card);
        fragmentMessageBinding.messageContent.setAdapter(messageRecycleAdapter);
        fragmentMessageBinding.messageContent.setLayoutManager(new LinearLayoutManager(getContext()));

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case refresh:
                        MessageUser messageUser = map.get(message.senderId);
                        int pos = messageRecycleAdapter.getItemPosition(messageUser);
                        messageUser.setMessage(message);
                        //messageRecycleAdapter.removeAt(pos);
                        messageRecycleAdapter.setData(pos, messageUser);
                        map.put(message.senderId, messageUser);
                        break;
                }
            }
        };
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void eventBind() {
        messageListenerRegister();

        fragmentMessageBinding.messageToolBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.message_menu_delete:
                    NIOClient.connectAndSend(new Message(null, "来自客户端" + new Date().toString(), null, String.valueOf(9)), null);
                    break;
                default:
                    return false;
            }
            return true;
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        run = false;
    }

    private void refreshMessage(Message message) {
        this.message = message;
    }
    //
    private void messageListenerRegister() {
        new Thread(() -> {
            while (run) {
                try {
                    Message message = App.receiveQueue.take();//使用take才会阻塞等待
                    if (!map.isEmpty() && map.containsKey(message.senderId)) {
                        refreshMessage(message);
                        //发送更新请求
                        android.os.Message message1 = new android.os.Message();
                        message1.what = refresh;
                        handler.sendMessage(message1);
                    } else {
                        getUser(Integer.parseInt(message.getSenderId()), message);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getUser(Integer userId, Message message) {
        OkGo.<Result>post(AppConst.User.getUserById)
                .params("userId", userId)
                .execute(new ResultCallback() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Result result = response.body();
                        if (result.getStatus() == Status.SUCCESS) {
                            User user = GsonUtil.fromJson(
                                    GsonUtil.toJson(result.getResultMap().get(AppConst.User.user)),
                                    new TypeToken<User>(){}.getType()
                            );
                            MessageUser messageUser = MessageUser.createByAll(message, user);
                            map.put(message.senderId, messageUser);
                            messageRecycleAdapter.addData(messageUser);
                        } else {
                            Log.i(TAG, "onSuccess: 未获得用户");
                        }
                    }
                });
    }
}