package com.example.xusoku.bledemo

import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import com.example.xusoku.bledemo.util.CommonManager
import com.example.xusoku.bledemo.util.parseJson
import com.hyphenate.EMMessageListener

import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_activity_chat.*
import org.jetbrains.anko.toast

class ActivityChat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity_chat)
       title = "提问你想问的"





        commentEdit.setOnFocusChangeListener(View.OnFocusChangeListener { view, b ->
            if (b) {
                CommonManager.showSoftInputMethod(this)
            } else {
                CommonManager.dismissSoftInputMethod(this, view.windowToken)
            }
        })
        controlKeyboardLayout(overlay, commentInputLayout)


        chat_btn.setOnClickListener{
           var aa : String = commentEdit.text.toString()

            if(aa!=null&&!aa.equals("")){
//                chat_tv.setText("")
                sendTextMessage(aa)
                commentEdit.text.clear()
                CommonManager.dismissSoftInputMethod(this, chat_btn.getWindowToken())
            } else{
                Snackbar.make(chat_btn,"请输入内容",Snackbar.LENGTH_SHORT).show()
            }
        }

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
//                    toast(parseJson.getString(message))
                    Log.e("vvvv",""+parseJson.getString(message))
                    chat_tv.post(object : Runnable {
                        override fun run() {
                        chat_tv.setText(parseJson.getString(message))
                    }
                    })

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
        message.setAttribute("em_robot_message",true)
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


    /**
     * @param root         最外层布局，需要调整的布局
     * *
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private fun controlKeyboardLayout(root: View, scrollToView: View) {
        root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            internal var mLastHeight = 0
            internal var mLastBottom = -1

            override fun onGlobalLayout() {
                val rect = Rect()
                root.getWindowVisibleDisplayFrame(rect)

                if (mLastBottom == -1) {
                    mLastBottom = rect.bottom
                    return
                }
                val nb = rect.bottom
                val ob = mLastBottom

                if (nb < ob) {
                    // 键盘显示了， 滑上去
                    val location = IntArray(2)
                    scrollToView.getLocationInWindow(location)
                    val scrollHeight = location[1] + scrollToView.height - nb

                    root.scrollTo(0, scrollHeight)
                    mLastHeight = scrollHeight
                } else if (nb > ob) {
                    // 键盘隐藏了, 滑下来
                    root.scrollTo(0, 0)
                }
                if (nb != ob) {
                    mLastBottom = nb
                }
            }
        })
    }

}
