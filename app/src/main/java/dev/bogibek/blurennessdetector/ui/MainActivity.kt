package dev.bogibek.blurennessdetector.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.bogibek.blurennessdetector.R
import dev.bogibek.blurennessdetector.databinding.ActivityMainBinding
import dev.bogibek.blurennessdetector.repository.BlurrinessFactory
import dev.bogibek.blurennessdetector.repository.BlurrinnessRepository
import dev.bogibek.blurennessdetector.utils.UiStateObject
import dev.bogibek.blurennessdetector.viewmodel.BlurrinessViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: BlurrinessViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupObservers()
        initViews()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.blurred.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        binding.tvResult.text = "Checking please wait!!"
                    }
                    is UiStateObject.SUCCESS -> {
                        if (it.data) binding.tvResult.text = "This image blurred"
                        else binding.tvResult.text = "This image is not blurred"
                    }
                    is UiStateObject.ERROR -> {

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initViews() {
        binding.bCheck.setOnClickListener {
            isBlurred(binding.etUrl.text.toString())
        }
    }

    private fun isBlurred(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.ivImage.setImageBitmap(resource)
                    viewModel.isBlurred(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
    }


    private fun setupViewModel() {

        viewModel = ViewModelProvider(
            this,
            BlurrinessFactory(BlurrinnessRepository())
        ).get(BlurrinessViewModel::class.java)
    }

}