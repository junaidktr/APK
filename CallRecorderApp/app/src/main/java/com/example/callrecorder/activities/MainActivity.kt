package com.example.callrecorder.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.callrecorder.databinding.ActivityMainBinding
import com.example.callrecorder.utils.PermissionUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResults(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        checkPermissions()
    }

    private fun setupUI() {
        binding.btnGrantPermissions.setOnClickListener {
            checkAndRequestPermissions()
        }
        
        binding.btnViewRecordings.setOnClickListener {
            startActivity(Intent(this, RecordingListActivity::class.java))
        }
        
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        binding.btnEnableAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }
    }

    private fun checkPermissions() {
        if (PermissionUtils.hasAllPermissions(this)) {
            showMainContent()
        } else {
            showPermissionRequired()
        }
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = PermissionUtils.getRequiredPermissions()
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isEmpty()) {
            showMainContent()
        } else {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun handlePermissionResults(permissions: Map<String, Boolean>) {
        val allGranted = permissions.values.all { it }
        
        if (allGranted) {
            showMainContent()
        } else {
            // Check if any permanently denied
            val shouldShowRationale = permissions.filter { !it.value }.keys.any {
                shouldShowRequestPermissionRationale(it)
            }
            
            if (!shouldShowRationale) {
                // User permanently denied, show settings dialog
                showSettingsDialog()
            }
        }
    }

    private fun showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs all permissions to record calls. Please enable them in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun showPermissionRequired() {
        binding.layoutPermissions.visibility = View.VISIBLE
        binding.layoutMainContent.visibility = View.GONE
    }

    private fun showMainContent() {
        binding.layoutPermissions.visibility = View.GONE
        binding.layoutMainContent.visibility = View.VISIBLE
        
        // Update UI based on current state
        updateAccessibilityStatus()
    }

    private fun updateAccessibilityStatus() {
        // Check if accessibility service is enabled
        val isEnabled = isAccessibilityServiceEnabled()
        binding.tvAccessibilityStatus.text = if (isEnabled) {
            "✓ WhatsApp Call Detection Enabled"
        } else {
            "✗ WhatsApp Call Detection Disabled"
        }
        binding.tvAccessibilityStatus.setTextColor(
            ContextCompat.getColor(this, if (isEnabled) android.R.color.holo_green_dark else android.R.color.holo_red_dark)
        )
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName = "${packageName}/com.example.callrecorder.services.WhatsappAccessibilityService"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        
        return enabledServices.contains(serviceName)
    }

    override fun onResume() {
        super.onResume()
        if (PermissionUtils.hasAllPermissions(this)) {
            updateAccessibilityStatus()
        }
    }
}
