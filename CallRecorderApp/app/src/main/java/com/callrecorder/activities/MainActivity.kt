package com.callrecorder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.callrecorder.R
import com.callrecorder.databinding.ActivityMainBinding
import com.callrecorder.utils.PermissionHelper
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkPermissions()
    }

    private fun setupUI() {
        binding.btnGrantPermissions.setOnClickListener {
            checkPermissions()
        }

        binding.btnViewRecordings.setOnClickListener {
            startActivity(Intent(this, RecordingListActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnAccessibilitySettings.setOnClickListener {
            openAccessibilitySettings()
        }
    }

    private fun checkPermissions() {
        if (PermissionHelper.hasAllPermissions(this)) {
            binding.layoutPermissions.visibility = View.GONE
            binding.layoutMainContent.visibility = View.VISIBLE
        } else {
            binding.layoutPermissions.visibility = View.VISIBLE
            binding.layoutMainContent.visibility = View.GONE
            
            PermissionHelper.requestPermissions(this) { allGranted ->
                if (allGranted) {
                    binding.layoutPermissions.visibility = View.GONE
                    binding.layoutMainContent.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }
}
