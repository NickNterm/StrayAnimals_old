package com.iqsoft.strayanimals.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.iqsoft.strayanimals.Constants
import com.iqsoft.strayanimals.R
import com.iqsoft.strayanimals.adapters.MariaDB
import com.iqsoft.strayanimals.adapters.MariaDBInterface
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.upload_fragment.view.*


class UploadFragment(private val con: Context) : Fragment(), MariaDBInterface {
    private var token: String? = null
    private lateinit var mariaDB: MariaDB

    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(Constants.IntentToken)
        }
        mariaDB = MariaDB(con)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lf = requireActivity().layoutInflater
        val v: View = lf.inflate(R.layout.upload_fragment, container, false)
        v.UploadFragmentUploadButton.setOnClickListener {
            if (v.UploadFragmentDescriptionInput.editText?.text.isNullOrEmpty()) {
                v.UploadFragmentDescriptionInput.error = "required!"
            } else {
                uploadImage()
            }
        }
        v.UploadFragmentSelectImageButton.setOnClickListener {
            readExternalStoragePermission()
        }
        return v
    }

    private fun uploadImage() {
        mariaDB.imageUploadToServerFunction(
            bitmap!!,
            view?.UploadFragmentDescriptionInput?.editText?.text.toString(),
            token!!
        )
    }

    private fun readExternalStoragePermission() {
        Dexter.withContext(con)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"),
                        Constants.SelectPhotoCode
                    )
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Log.i("myerror", "denied")
                    showRationDialogForPermissions()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Log.i("myerror", "rationale")
                    showRationDialogForPermissions()
                }
            }).check()
    }

    private fun showRationDialogForPermissions() {
        AlertDialog.Builder(con)
            .setMessage("It looks like you have turned off the permission required for this feature. It can be enabled under the Application settings")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", con.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.SelectPhotoCode) {
            val selectedImageUri: Uri? = data?.data
            if (null != selectedImageUri) {
                bitmap = MediaStore.Images.Media.getBitmap(con.contentResolver, selectedImageUri);
                view?.UploadFragmentUploadButton?.isEnabled = true
                view?.UploadFragmentImageView?.setImageURI(selectedImageUri)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context, token: String?) =
            UploadFragment(context).apply {
                arguments = Bundle().apply {
                    putString(Constants.IntentToken, token)
                }
            }
    }
}