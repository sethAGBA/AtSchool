package com.ecolix.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.os.Bundle

@SuppressLint("StaticFieldLeak")
private object FilePickerState {
    var continuation: ((FileData?) -> Unit)? = null
    internal lateinit var applicationContext: Context

    fun init(context: Context) {
        if (!this::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }

    @JvmName("getInitializedContext")
    fun getInitializedContext(): Context {
        if (!this::applicationContext.isInitialized) {
            error("FilePicker.init(context) must be called before picking a file. Call it in your Application's onCreate.")
        }
        return applicationContext
    }
}

actual object FilePicker {
    fun init(context: Context) {
        FilePickerState.init(context)
    }

    actual suspend fun pickFile(): FileData? {
        val context = FilePickerState.getInitializedContext()
        return suspendCancellableCoroutine { continuation ->
            FilePickerState.continuation = {
                continuation.resume(it)
            }
            val intent = Intent(context, FilePickerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

            continuation.invokeOnCancellation {
                FilePickerState.continuation = null
            }
        }
    }
}

internal class FilePickerActivity : ComponentActivity() {

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                contentResolver.openInputStream(it)?.use { inputStream ->
                    val fileName = it.lastPathSegment?.substringAfterLast('/') ?: "file"
                    val fileBytes = inputStream.readBytes()
                    FilePickerState.continuation?.invoke(FileData(fileName, fileBytes))
                } ?: FilePickerState.continuation?.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
                FilePickerState.continuation?.invoke(null)
            }
        } ?: FilePickerState.continuation?.invoke(null)

        FilePickerState.continuation = null
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            getContent.launch("*/*")
        } else {
            finish()
        }
    }
}