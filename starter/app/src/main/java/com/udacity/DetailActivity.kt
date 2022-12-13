package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.view.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val id = intent.getLongExtra(Constants.DOWNLOAD_ID_KEY, -1L)
        val fileName = intent.getStringExtra(Constants.DOWNLOAD_FILE_NAME_KEY)
        binding.contentDetail.tvFileNameContent.text = fileName
        binding.contentDetail.tvStatusContent.text =
            if (id == -1L) {
                getString(R.string.status_fail)
            } else
                getString(R.string.status_success)

        binding.contentDetail.btnBack.setOnClickListener {
            finish()
        }
    }
}

