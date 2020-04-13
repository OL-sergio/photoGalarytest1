package com.example.photogalary

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import codestart.info.photogalary.AppConstants
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            buttonGallery.setOnClickListener {
                pickPhotoFromGallery()
            }

            buttonTakePhoto.setOnClickListener {
                askCameraPermission()

            }

        }

        private fun pickPhotoFromGallery() {
            val pickImageIntent = Intent (Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(pickImageIntent, AppConstants.PICK_PHOTO_REQUEST)
        }

        private fun launchCamera() {
            val values = ContentValues(1)
            values.put(MediaStore.Images.Media.MIME_TYPE, "Image/jpg")
            fileUri = contentResolver
                .insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                startActivityForResult(intent, AppConstants.TAKE_PHOTO_REQUEST)
            }
        }


        fun askCameraPermission() {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report.areAllPermissionsGranted()) {
                            launchCamera()
                        } else {
                            Toast.makeText(this@MainActivity,
                                "Todas as permissões precisam ser concedidas para tirar uma foto", Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>?, token: PermissionToken?
                    ) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Erro de Permição!")

                            .setMessage("Plrease allow permissions to take photo with camera")

                            .setNegativeButton(
                                android.R.string.cancel
                            ) { dialog, _ -> dialog.dismiss()
                                token?.cancelPermissionRequest()
                            }

                            .setPositiveButton(android.R.string.ok
                            ) { dialog, _ -> dialog.dismiss()
                                token?.continuePermissionRequest()
                            }

                            .setOnDismissListener {
                                token?.cancelPermissionRequest() }
                            .show()
                    }
                }).check()

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK
                && requestCode == AppConstants.TAKE_PHOTO_REQUEST) {
                imageView.setImageURI(fileUri)
            } else if (resultCode == Activity.RESULT_OK
                && requestCode == AppConstants.PICK_PHOTO_REQUEST) {

                fileUri = data?.data
                imageView.setImageURI(fileUri)
            } else {
                super.onActivityResult(requestCode, resultCode, data)

            }
        }
    }
