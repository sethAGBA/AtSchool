package com.ecolix.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

@Composable
actual fun rememberImageBitmap(bytes: ByteArray?): ImageBitmap? {
    return remember(bytes) {
        if (bytes != null) {
            try {
                Image.makeFromEncoded(bytes).toComposeImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}
