package com.fsyy.listener.rongcloud.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fsyy.listener.R
import io.rong.imkit.fragment.ConversationFragment

class ConversationAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        val conversationFragment=ConversationFragment()
        val manager=supportFragmentManager
        val transaction=manager.beginTransaction()
        transaction.replace(R.id.conversation_container,conversationFragment)
        transaction.commit()
    }
}