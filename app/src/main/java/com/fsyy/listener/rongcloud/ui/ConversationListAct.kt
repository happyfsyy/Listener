package com.fsyy.listener.rongcloud.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.leancloud.AVUser
import com.fsyy.listener.R
import io.rong.imkit.RongIM
import io.rong.imkit.fragment.ConversationListFragment
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.UserInfo

class ConversationListAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)
        setUserInfo()
        initParams()
    }
    private fun setUserInfo(){
        RongIM.setUserInfoProvider({
            UserInfo(AVUser.currentUser().objectId,AVUser.currentUser().username,Uri.parse(
                AVUser.currentUser().getString("photoUrl")))
        },true)
    }
    private fun initParams(){
        val uri=Uri.parse("rong://"+
            applicationInfo.packageName).buildUpon()
            .appendPath("conversationlist")
            .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
            .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
            .build()
        val conversationListFrag=ConversationListFragment()
        conversationListFrag.uri=uri
        val manager=supportFragmentManager
        val transaction=manager.beginTransaction()
        transaction.replace(R.id.conversation_list_container, conversationListFrag)
        transaction.commit()
    }
}