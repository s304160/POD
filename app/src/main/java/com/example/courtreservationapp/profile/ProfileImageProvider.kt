package com.example.courtreservationapp.profile

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.courtreservationapp.R
import java.io.File

class ProfileImageProvider: FileProvider(R.xml.paths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()

            val file =  File.createTempFile(
                "profileImage",
                ".jpg",
                directory
            )

            val authority = context.packageName

            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}