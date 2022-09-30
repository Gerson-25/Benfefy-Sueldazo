package com.syntepro.appbeneficiosbolivia.utils

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.text.DateFormat
import java.util.*

class Helpers {
    companion object {
        fun isUriRequiresPermissions(uri: Uri, resolver: ContentResolver): Boolean {
            try {
                val stream = resolver.openInputStream(uri)
                stream!!.close()
                return false
            } catch (e: FileNotFoundException) {
                return false
            } catch (e: Exception) {
            }

            return false
        }

        fun getPickImageResultUri(data: Intent?, getImage: File): Uri? {
            var isCamera = true
            if (data != null && data.data != null) {
                val action = data.action
                isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
            }
            return if (isCamera) getCaptureImageOutputUri(getImage) else data!!.data
        }

        private fun getCaptureImageOutputUri(getImage: File?): Uri? {
            var outputFileUri: Uri? = null
            //File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
            }
            return outputFileUri
        }

        fun getPickImageChooserIntent(packageManager: PackageManager, externalCacheDir: File): Intent {
            // Determine Uri of camera image to  save.
            val outputFileUri = getCaptureImageOutputUri(externalCacheDir)

            val allIntents = ArrayList<Intent>()
            //PackageManager packageManager =  getPackageManager();

            // collect all camera intents
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val listCam = packageManager.queryIntentActivities(captureIntent, 0)
            for (res in listCam) {
                val intent = Intent(captureIntent)
                intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.setPackage(res.activityInfo.packageName)
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                }
                allIntents.add(intent)
            }

            // collect all gallery intents
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
            for (res in listGallery) {
                val intent = Intent(galleryIntent)
                intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.setPackage(res.activityInfo.packageName)
                allIntents.add(intent)
            }

            // the main intent is the last in the  list (fucking android) so pickup the useless one
            var mainIntent = allIntents[allIntents.size - 1]
            for (intent in allIntents) {
                if (intent.component!!.className == "com.android.ui.EditProfile") {
                    mainIntent = intent
                    break
                }
            }
            allIntents.remove(mainIntent)

            // Create a chooser from the main  intent
            val chooserIntent = Intent.createChooser(mainIntent, "Select source")

            // Add all other intents
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())

            return chooserIntent
        }


        fun dateToStrReg(date: Date, format: Int): String {
            val df: DateFormat = DateFormat.getDateInstance(format, Locale.getDefault())
            val splittedDate = df.format(date).split("/")
            return "${if (splittedDate[0].length == 1) "0" + splittedDate[0] else splittedDate[0]}/${if (splittedDate[1].length == 1) "0" + splittedDate[1] else splittedDate[1]}/${splittedDate[2]}"
        }

        fun dateToStr(date: Date, format: Int): String {
            val df: DateFormat = DateFormat.getDateInstance(format, Locale.getDefault())
            return df.format(date)
        }
    }
}