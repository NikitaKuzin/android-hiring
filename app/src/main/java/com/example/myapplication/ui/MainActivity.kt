package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.adapter.SelectableAgeAdapter
import com.example.myapplication.utils.dpToPx
import com.example.myapplication.utils.switchVisibility


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private lateinit var popupWindow: ListPopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )


        binding.framelayoutAge.setOnClickListener {
            viewModel.onAgeSelectorClick()
        }

        binding.buttonNext.setOnClickListener {
            viewModel.onContinueClick(getSelectedAge(), getSelectedGender())
        }

        binding.cardviewMaleNotSelected.setOnClickListener {
            viewModel.onMaleSelect()
            switchVisibility(binding.imageviewMaleSelected, binding.cardviewMaleNotSelected)
            switchVisibility(binding.cardviewFemaleNotSelected, binding.imageviewFemaleSelected)
        }

        binding.cardviewFemaleNotSelected.setOnClickListener {
            viewModel.onFemaleSelect()
            switchVisibility(binding.imageviewFemaleSelected, binding.cardviewFemaleNotSelected)
            switchVisibility(binding.cardviewMaleNotSelected, binding.imageviewMaleSelected)
        }

        viewModel.showSelectedGender.observe(this) {
            if (it == "f") {
                switchVisibility(binding.cardviewMaleNotSelected, binding.imageviewMaleSelected)
                switchVisibility(
                    binding.imageviewFemaleSelected, binding.cardviewFemaleNotSelected
                )
            } else if (it == "m") {
                switchVisibility(binding.imageviewMaleSelected, binding.cardviewMaleNotSelected)
                switchVisibility(
                    binding.cardviewFemaleNotSelected, binding.imageviewFemaleSelected
                )
            } else {
                switchVisibility(binding.cardviewMaleNotSelected, binding.imageviewMaleSelected)
                switchVisibility(binding.cardviewFemaleNotSelected, binding.imageviewFemaleSelected)
            }
        }

        viewModel.showSelectedAge.observe(this) {
            if (it != -1) {
                binding.textviewAge.text = it.toString()
            } else {
                binding.textviewAge.text = "- -"
            }
        }

        viewModel.showConnectionErrorLiveData.observe(this) {
            val error = it.handleAndGet()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.showAllowedInfoLiveData.observe(this) {
            val isAllowed = it.handleAndGet()
            if (isAllowed != null) {
                Toast.makeText(this, "isAllowed: $isAllowed", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.enableButtonLiveData.observe(this) {
            binding.buttonNext.isEnabled = it
        }

        viewModel.showAgesLiveData.observe(this) {
            val ages = it.handleAndGet()

            if (ages != null) {

                initPopup()

                val adapter = SelectableAgeAdapter(this, ages)

                popupWindow.setAdapter(adapter)

                popupWindow.setOnItemClickListener { _, _, position, _ ->
                    viewModel.onAgeSelect(adapter.getItem(position)!!.age)
                    popupWindow.dismiss()
                }

                popupWindow.show()
            }
        }


    }

    private fun initPopup() {
        if (!::popupWindow.isInitialized) {
            popupWindow = ListPopupWindow(this, null, 0, R.style.ListPopupRounded)
            popupWindow.anchorView = binding.framelayoutAge
            popupWindow.isModal = true
            popupWindow.height = 185.dpToPx()
        }
    }

    private fun getSelectedGender(): String? {
        return if (binding.imageviewMaleSelected.visibility == View.VISIBLE) "m"
        else if (binding.imageviewFemaleSelected.visibility == View.VISIBLE) "f"
        else null
    }

    private fun getSelectedAge(): Int? {
        return if (binding.textviewAge.text == "- -") null else binding.textviewAge.text.toString()
            .toInt()
    }

}