package com.appbenefy.sueldazo.core.pickers

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList

object ImageChooser {
    const val IMAGE_CHOOSER_REQUEST_CODE = 200
    fun getImageChooserIntent(packageManager: PackageManager, externalCacheDir: File): Intent {

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
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
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

    private fun getCaptureImageOutputUri(getImage: File?): Uri? {
        var outputFileUri: Uri? = null
        //File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

    fun getPickImageResultUri(data: Intent?, getImage: File): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri(getImage) else data!!.data
    }

    fun isUriRequiresPermissions(uri: Uri, resolver: ContentResolver): Boolean {
        try {
            //ContentResolver resolver = getContentResolver();
            val stream = resolver.openInputStream(uri)
            stream!!.close()
            return false
        } catch (e: FileNotFoundException) {
            //if (e.getCause() instanceof ErrnoException) {
            return false
            //}
        } catch (e: Exception) {
        }

        return false
    }
}