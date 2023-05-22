package com.example.myapplication

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executor

class MainActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        findViewById<View>(R.id.reset).setOnClickListener {
            callback.successes = 0
            showPrompt()
        }

        showPrompt()
    }

    private fun showPrompt() {
        val clickListener = DialogInterface.OnClickListener { _, _ -> }
        val prompt = BiometricPrompt.Builder(this)
            .setTitle("Hello")
            .setNegativeButton("Cancel", MainThreadExecutor, clickListener)
            .setConfirmationRequired(false)
            .build()

        prompt.authenticate(CancellationSignal(), MainThreadExecutor, callback)
    }

    private val callback = object : BiometricPrompt.AuthenticationCallback() {
        var successes = 0

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            successes++
            if (successes < 2) {
                val delay = findViewById<EditText>(R.id.delay)?.text?.toString()?.toLongOrNull()
                if (delay == null || delay <= 0) {
                    showPrompt()
                } else {
                    MainThreadHandler.postDelayed({ showPrompt() }, delay)
                }
            }
        }
    }
}

object MainThreadHandler : Handler(Looper.getMainLooper())

object MainThreadExecutor : Executor {
    override fun execute(command: Runnable) {
        MainThreadHandler.post(command)
    }
}
