package com.ecolix.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberImageBitmap(bytes: ByteArray?): ImageBitmap?
