package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var binding: ActivityMainBinding

    private var selectedRepository: Repository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.contentMain.rbGlide.setOnCheckedChangeListener(onCheckedListener)
        binding.contentMain.rbRetrofit.setOnCheckedChangeListener(onCheckedListener)
        binding.contentMain.rbLoadApp.setOnCheckedChangeListener(onCheckedListener)
        binding.contentMain.customButton.setOnClickListener {
            selectedRepository?.let {
                download(it.repoUrl)
            } ?: kotlin.run {
                Toast.makeText(this, getString(R.string.no_repo_selected), Toast.LENGTH_SHORT).show()
            }
        }
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            contentIntent.putExtra(Constants.DOWNLOAD_ID_KEY, id)
            contentIntent.putExtra(Constants.DOWNLOAD_FILE_NAME_KEY, selectedRepository?.name)

            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                Constants.NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            sendNotification(
                applicationContext,
                getString(R.string.notification_title),
                getString(R.string.notification_description),
                pendingIntent
            )
        }
    }

    private val onCheckedListener =
        CompoundButton.OnCheckedChangeListener { compoundButton, selected ->
            if (selected) {
                when (compoundButton) {
                    binding.contentMain.rbGlide -> {
                        selectedRepository = Repository.GLIDE
                    }

                    binding.contentMain.rbRetrofit -> {
                        selectedRepository = Repository.RETROFIT
                    }

                    binding.contentMain.rbLoadApp -> {
                        selectedRepository = Repository.LOAD_APP
                    }
                }
            }
        }

    private fun sendNotification(applicationContext: Context, title: String, message: String, pendingIntent: PendingIntent?) {
        val builder = NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(message)

        pendingIntent?.let {
            builder.setContentIntent(it)
                .addAction(R.drawable.ic_assistant_black_24dp, getString(R.string.see_detail), it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
    }

    private fun download(url: String) {
        val requestURL = Uri.parse(url)
        val request =
            DownloadManager.Request(requestURL)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }
}
