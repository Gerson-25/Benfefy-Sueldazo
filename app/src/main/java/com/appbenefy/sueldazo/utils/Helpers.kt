package com.appbenefy.sueldazo.utils

import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.appbenefy.sueldazo.base.BaseActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.DateFormat
import java.util.*

class Helpers {
    companion object {
        const val IMAGE_CHOOSER_REQUEST_CODE = 200
        lateinit var currentPhotoPath: String
        var photoURI:Uri? = null
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

        fun getImageChooserIntent(packageManager: PackageManager, externalCacheDir: File, context: Context? = null): Intent {
            val allIntents = ArrayList<Intent>()
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile(context!!)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            context!!,
                            "com.appbenefy.sueldazo.provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        allIntents.add(takePictureIntent)
                    }
                }
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
                if (intent.component?.className == "com.android.documentsui.DocumentsActivity") {
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

        @Throws(IOException::class)
        private fun createImageFile(context: Context): File {
            // Create an image file name
            val timeStamp: String = "file1" //SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File = (context as BaseActivity).getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
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