package com.example.livedashboardandroid

import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.livedashboardandroid.databinding.ActivityMainBinding

fun ComponentActivity.viewBinding() = lazy {
    DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
}