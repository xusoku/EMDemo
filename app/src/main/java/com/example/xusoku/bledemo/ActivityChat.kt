package com.example.xusoku.bledemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hyphenate.EMMessageListener

import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

class ActivityChat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity_chat)
       title = "聊一聊"

        sendTextMessage("你好");
    }

    override fun onResume() {
        super.onResume()
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    override fun onStop() {
        super.onStop()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    var msgListener : EMMessageListener = object : com.hyphenate.EMMessageListener {
        override fun onMessageDeliveryAckReceived(p0: MutableList<EMMessage>?) {
            Log.e("onMessageDeliveryAckReceived","onMessageDeliveryAckReceived")
        }

        override fun onMessageReadAckReceived(p0: MutableList<EMMessage>?) {
            Log.e("onMessageReadAckReceived","onMessageReadAckReceived")
        }

        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            for ( message : EMMessage in messages!!) {
                var username: String ="";
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }

                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals("robotone")) {
                    // 声音和震动提示有新消息
                    Log.e("robotone","robotone="+message)
                } else {
                    // 如果消息不是和当前聊天ID的消息
                }
            }
        }

        override fun onMessageChanged(p0: EMMessage?, p1: Any?) {
            Log.e("onMessageChanged","onMessageChanged")
        }

        override fun onCmdMessageReceived(p0: MutableList<EMMessage>?) {
            Log.e("onCmdMessageReceived","onCmdMessageReceived")
        }
    }
    //发送消息方法
    //==========================================================================
    protected fun sendTextMessage(content: String) {
        val message = EMMessage.createTxtSendMessage(content, "robotone")
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
    }

    /**
     * 将应用的会话类型转化为SDK的会话类型
     * @param chatType
     * @return
     */
    fun   getConversationType  (chatType : Int): EMConversation.EMConversationType {
        if (chatType == 1) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == 3) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }
}
