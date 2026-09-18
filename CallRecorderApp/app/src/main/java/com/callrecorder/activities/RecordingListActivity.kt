package com.callrecorder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.callrecorder.databinding.ActivityRecordingListBinding

class RecordingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadRecordings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Call Recordings"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadRecordings() {
        // TODO: Load recordings from database and display in RecyclerView
        binding.tvEmptyMessage.visibility = View.VISIBLE
    }
}
