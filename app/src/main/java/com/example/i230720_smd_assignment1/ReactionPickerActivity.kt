package com.example.i230720_smd_assignment1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReactionPickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reaction_picker)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        Toast.makeText(this, "Select a reaction (Like, Love, Haha, Wow, Sad, Angry)", Toast.LENGTH_SHORT).show()
    }
}
