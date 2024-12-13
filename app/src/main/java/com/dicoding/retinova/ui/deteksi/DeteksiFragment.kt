package com.dicoding.retinova.ui.deteksi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.retinova.databinding.FragmentDeteksiBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class DeteksiFragment : Fragment() {

    private var _binding: FragmentDeteksiBinding? = null
    private val binding get() = _binding!!
    private lateinit var deteksiViewModel: DeteksiViewModel

    private lateinit var cameraImageUri: Uri
    private lateinit var croppedImageUri: Uri

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            startCropActivity(cameraImageUri)
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    private val fileStorageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flags)

                startCropActivity(uri)
            }
        }
    }

    private val cropActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            UCrop.getOutput(result.data!!)?.let { resultUri ->
                croppedImageUri = resultUri
                deteksiViewModel.setSelectedImageUri(resultUri)

                binding.ivImagePreview.setImageURI(null)
                binding.ivImagePreview.setImageURI(croppedImageUri)
            } ?: run {
                Toast.makeText(requireContext(), "Failed to get cropped image URI", Toast.LENGTH_SHORT).show()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Toast.makeText(requireContext(), "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        deteksiViewModel = ViewModelProvider(this)[DeteksiViewModel::class.java]
        _binding = FragmentDeteksiBinding.inflate(inflater, container, false)

        setupImageSelection()

        deteksiViewModel.selectedImageUri.observe(viewLifecycleOwner) { uri ->
            binding.ivImagePreview.setImageURI(null)
            uri?.let {
                binding.ivImagePreview.setImageURI(it)
            }
        }

        return binding.root
    }

    private fun setupImageSelection() {
        binding.ivImagePreview.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnClassify.setOnClickListener {
            if (::croppedImageUri.isInitialized) {
                val intent = Intent(requireContext(), ResultActivity::class.java)
                intent.putExtra("IMAGE_URI", croppedImageUri.toString())
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Please select and crop an image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "File Storage")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> openFileStorage()
                }
            }
            .show()
    }

    private fun openFileStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        fileStorageLauncher.launch(intent)
    }

    private fun takePicture() {
        val imageFile = File(requireContext().cacheDir, "camera_image.jpg")
        cameraImageUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", imageFile)
        cameraLauncher.launch(cameraImageUri)
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().externalCacheDir, "cropped_image.jpg"))
        val cropOptions = UCrop.Options().apply {
            setCompressionQuality(80)
            setToolbarTitle("Edit Image")
            setShowCropGrid(true)
            setShowCropFrame(true)
        }

        val intent = UCrop.of(sourceUri, destinationUri)
            .withOptions(cropOptions)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .getIntent(requireContext())

        cropActivityResultLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
