package com.example.keepalive.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.R
import com.example.keepalive.databinding.FragmentSettingsBinding
import com.example.keepalive.storage.TelegramIdStorage
import com.example.keepalive.utils.Extensions.toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding: FragmentSettingsBinding by viewBinding(FragmentSettingsBinding::bind)
    private val storage by lazy { TelegramIdStorage(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hintUserinfobot.apply {
            text = stringToSpannable(getString(R.string.telegram_id_hint))
            movementMethod = LinkMovementMethod()
        }
        binding.hintKeepAliveBot.apply {
            text = stringToSpannable(getString(R.string.keep_alive_bot_hint))
            movementMethod = LinkMovementMethod()
        }
        observeStatus()
        binding.save.setOnClickListener { save() }
        binding.clearStorage.setOnClickListener { clear() }
        binding.goBack.setOnClickListener { (activity as Host).openPrevious() }
    }

    private fun observeStatus() {
        lifecycleScope.launch {
            storage.observeUserId().collect { userId ->
                Log.i(LOG_TAG, "Collected new user ID: $userId")
                if (userId <= 0) {
                    binding.currentSavedId.text = "[ no saved ID ]"
                } else {
                    binding.currentSavedId.text = "Saved ID: $userId"
                }
            }
        }
    }

    private fun save() {
        val userId = binding.idInput.text.toString().toLongOrNull()
        if (userId == null) {
            toast("Failed to parse user ID")
            return
        }
        Log.i(LOG_TAG, "Save user ID: $userId")
        lifecycleScope.launch { storage.saveUserId(userId) }
    }

    private fun clear() {
        Log.i(LOG_TAG, "Clear storage requested")
        lifecycleScope.launch {
            storage.clear()
            toast("Cleared")
        }
    }

    private fun stringToSpannable(html: String): SpannableString {
        val res = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
        return SpannableString(res)
    }

    companion object {
        private const val LOG_TAG = "SettingsFragment"
    }
}