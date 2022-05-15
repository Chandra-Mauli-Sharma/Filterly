package com.example.filterly

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.filterly.databinding.FragmentFilterBinding
import com.example.filterly.repository.ApiRepository
import com.example.filterly.utils.URIPathHelper
import com.example.filterly.viewmodel.FilterViewModel
import com.example.filterly.viewmodel.FilterViewModelFactory
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.net.URL


class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private lateinit var viewModel: FilterViewModel
    private val binding get() = _binding!!
    lateinit var effect:String
    lateinit var uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        val view = binding.root
        val repository = ApiRepository()
        val viewModelFactory = FilterViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FilterViewModel::class.java)

        binding.viewmodel = viewModel
        binding.imageFilterView.setOnClickListener {
            ImagePicker.with(this).createIntent { filterImageResult.launch(it) }
        }


        binding.postImage.setOnClickListener {
            uploadFile(uri)
        }

        binding.getImage.setOnClickListener {
            viewModel.uploadfilterImage.observe(viewLifecycleOwner) {
                if (it.isSuccessful) {
                    binding.progressBar.visibility = View.INVISIBLE
                    viewModel.getImage(it.body()!!)
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.filterImage.observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                Glide.with(this).load(it.body()).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).placeholder(R.drawable.ic_baseline_image_24)
                    .transform(
                        CenterInside(),
                        RoundedCorners(24)
                    ).into(binding.imageFilterView)
            }
        }
        viewModel.effect.observe(viewLifecycleOwner){
            effect=it
        }
        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            viewModel.effect.postValue(when (checkedId) {
                R.id.button1 -> ("bw")
                R.id.button2 -> ("blur")
                R.id.button3 -> ("threshold")
                else -> ("")
            })
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val filterImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                uri = fileUri
                Glide.with(this).load(uri).placeholder(R.drawable.ic_baseline_image_24).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).transform(
                    CenterInside(),
                    RoundedCorners(24)
                ).into(binding.imageFilterView)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    fun uploadFile(sourceFilePath: String) {
        uploadFile(File(sourceFilePath))
    }

    fun uploadFile(sourceFileUri: Uri) {
        val pathFromUri = URIPathHelper().getPath(requireActivity(), sourceFileUri)
        uploadFile(File(pathFromUri!!))

    }

    fun uploadFile(sourceFile: File) {
        Thread {
            val mimeType = getMimeType(sourceFile);
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread
            }
            val fileName: String = sourceFile.name
            Log.e("debug", sourceFile.path)
            toggleProgressDialog(true)
            try {
                val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), sourceFile)
                val body = MultipartBody.Part.createFormData(fileName, fileName, requestFile)

                viewModel.uploadImage(body, effect, fileName)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                showToast(ex.message.toString())
            }
            toggleProgressDialog(false)
        }.start()
    }

    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun toggleProgressDialog(show: Boolean) {
        activity?.runOnUiThread {
            if (show) {
                binding.progressBar.visibility=View.VISIBLE
            } else {
                binding.progressBar.visibility=View.INVISIBLE
            }
        }
    }

}