package com.kelompokempat.kelasku.ui.history

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.base.BaseActivity
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.data.HistoryList
import com.kelompokempat.kelasku.databinding.ActivityHistoryBinding
import com.kelompokempat.kelasku.databinding.ItemHistoryRecyclerBinding
import com.kelompokempat.kelasku.ui.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding, HistoryViewModel>(R.layout.activity_history) {

    private var history = ArrayList<HistoryList?>()

    private var dataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                val intent = Intent()
                intent.putExtra("data_updated", dataUpdated)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        observe()
        getHistory()

        binding.historyBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("data_updated", dataUpdated)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.historyRecycler.adapter = CoreListAdapter<ItemHistoryRecyclerBinding, HistoryList>(R.layout.item_history_recycler)
            .initItem(history) { _, data ->
            }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.history.collect {
                        history.clear()

                        history.addAll(it)
                        binding.historyRecycler.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getHistory() {
        viewModel.getLikeHistory()
    }

    private val updateDataActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val dataUpdated = data?.getBooleanExtra("data_updated", false) ?: true
            if (dataUpdated) {
                val intent = Intent()
                intent.putExtra("data_updated", dataUpdated)
                setResult(Activity.RESULT_OK, intent)
            }
        }
    }

}