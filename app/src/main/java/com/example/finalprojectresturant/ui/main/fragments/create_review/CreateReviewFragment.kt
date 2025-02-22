package com.example.finalprojectresturant.ui.main.fragments.create_review

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.finalprojectresturant.R
import com.example.finalprojectresturant.data.reviews.ReviewModel
import com.example.finalprojectresturant.ui.main.ReviewsViewModel
import com.example.finalprojectresturant.utils.getCountries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


private val listener: AdapterView.OnItemSelectedListener =
    object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }


class CreateReviewFragment : Fragment() {
    private val viewModel: ReviewsViewModel by activityViewModels()
    private var reviewerUid: String = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var imageView: ImageView
    private lateinit var base64Image: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val resturantName = view.findViewById<TextView>(R.id.create_resturant_name)
        val description = view.findViewById<TextView>(R.id.create_reviewer_description)
        val rating = view.findViewById<RatingBar>(R.id.create_resturant_rating)
        val countriesSelect = view.findViewById<Spinner>(R.id.create_country_select)
        lifecycleScope.launch {
            val countries = getCountries()


            if(countries.size > 0) {
                populateSpinner(countriesSelect, countries)
            }
        }

        imageView = view.findViewById<ImageView>(R.id.create_review_image)

        imageView.setOnClickListener {
            pickImageFromGallery()
        }

        val submitButton = view.findViewById<Button>(R.id.create_reviewer_button)

        submitButton.setOnClickListener {
            val newReview = ReviewModel(restaurant_name = resturantName.text.toString(), reviewer_id = reviewerUid, review = description.text.toString() , country = countriesSelect.selectedItem.toString(), rating = rating.rating.toInt(), image = base64Image)
            viewModel.addReview(newReview)
            val action = CreateReviewFragmentDirections.actionCreateReviewFragmentToReviewsListFragment()
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            uri?.let {
                // Display the image
                imageView.setImageURI(uri)

                // Convert image to Base64
                base64Image = convertImageToBase64(uri)
            }
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Reduce the dimensions of the image
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 480, 480, true) // Adjust width/height

        // Compress the Bitmap
        val compressedStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, compressedStream) // Lower quality to 30%
        val compressedByteArray = compressedStream.toByteArray()

        // Convert to Base64
        return Base64.encodeToString(compressedByteArray, Base64.DEFAULT)
    }

    private fun populateSpinner(spinner: Spinner, countries: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setOnItemSelectedListener(listener)
        spinner.setSelection(countries.indexOf("Israel"))
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}



