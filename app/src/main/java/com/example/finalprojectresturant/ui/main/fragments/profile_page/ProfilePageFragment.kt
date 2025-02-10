package com.example.finalprojectresturant.ui.main.fragments.profile_page

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.content.Intent
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalprojectresturant.R
import com.example.finalprojectresturant.data.users.UserModel
import com.example.finalprojectresturant.ui.auth.AuthActivity
import com.example.finalprojectresturant.ui.main.ReviewsViewModel
import com.example.finalprojectresturant.ui.main.fragments.reviews_list.ReviewsAdapter
import com.example.finalprojectresturant.utils.decodeBase64ToImage
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream


class ProfilePageFragment : Fragment() {
    private lateinit var reviewsList: RecyclerView
    private val viewModel: ReviewsViewModel by activityViewModels()
    private var reviewerUid: String = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var imageView: ImageView
    private lateinit var base64Image: String
    private var mainUser: UserModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usernameText = view.findViewById<TextView>(R.id.username_text)
        imageView = view.findViewById<ImageView>(R.id.profile_picture)
        viewModel.getUserById(reviewerUid).observe(viewLifecycleOwner) { user ->
            user?.let {
                mainUser = it
                usernameText.text = it.name ?: "Guest"

                if(it.profile_picture != "") {
                    imageView.setImageBitmap(decodeBase64ToImage(it.profile_picture ?: ""))
                } else {
                    val googleImage = FirebaseAuth.getInstance().currentUser?.photoUrl
                    if(googleImage !== null) {
                        Glide.with(this) // 'this' can be a Fragment or Activity context
                            .load(googleImage)
                            .into(imageView)
                        imageView.setImageURI(googleImage)
                    }
                }




                imageView.setOnClickListener {
                    pickImageFromGallery()
                }
            } ?: run {
                // Handle user not found
                usernameText.text = "Guest"
            }
        }

        val logout = view.findViewById<Button>(R.id.profile_page_logout_button)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }



        reviewsList = view.findViewById(R.id.profile_page_posts_list)
        context?.let { initStudentsList(it) }
        viewModel.getAllReviewsByUserId(reviewerUid).observe(viewLifecycleOwner, {
            it?.let {
                if(it.isEmpty()) viewModel.reFetchReviews()
                (reviewsList.adapter as? ReviewsAdapter)?.updateReviews(it)
            }

        })
    }


    private fun initStudentsList(context: Context) {
        reviewsList.run {
            layoutManager = LinearLayoutManager(context)
            adapter = ReviewsAdapter{ id ->
                val action = ProfilePageFragmentDirections.actionProfilePageFragmentToReviewDetailsFragment(id)
                findNavController().navigate(action)
            }
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
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
                imageView.setImageURI(uri)
                base64Image = convertImageToBase64(uri)

                mainUser?.let {
                    val newUser = UserModel(id = mainUser!!.id, profile_picture = base64Image, name = mainUser!!.name, email = mainUser!!.email)
                    viewModel.updateUser(newUser)
                }


            }
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 480, 480, true)
        val compressedStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, compressedStream) // Lower quality to 30%
        val compressedByteArray = compressedStream.toByteArray()
        return Base64.encodeToString(compressedByteArray, Base64.DEFAULT)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}