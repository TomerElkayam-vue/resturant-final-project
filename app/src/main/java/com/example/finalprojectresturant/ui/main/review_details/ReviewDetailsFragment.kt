package com.example.finalprojectresturant.ui.main.fragments.review_details
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.transition.Visibility
import com.example.finalprojectresturant.R
import com.example.finalprojectresturant.data.reviews.ReviewWithReviewer
import com.example.finalprojectresturant.ui.main.ReviewsViewModel
import com.example.finalprojectresturant.utils.decodeBase64ToImage
import com.google.firebase.auth.FirebaseAuth


class ReviewDetailsFragment : Fragment() {
    private val viewModel: ReviewsViewModel by activityViewModels()
    private var reviewerUid: String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var currentReview : ReviewWithReviewer ?= null
        val reviewId = arguments?.getString("review_id")

        viewModel.getAllReviews().observe(viewLifecycleOwner, {
            if(it.isEmpty()) viewModel.invalidateReviews()
            val reviewsList = it

            reviewId?.let {
                val reviewId = it
                currentReview = reviewsList.find { review -> review.review.id == reviewId}
 
            }
            val resturantName = view.findViewById<TextView>(R.id.details_resturant_name)
            val description = view.findViewById<TextView>(R.id.details_reviewer_description)
            val userId = view.findViewById<TextView>(R.id.details_reviewer_id)
            val rating = view.findViewById<RatingBar>(R.id.details_resturant_rating)
            val image = view.findViewById<ImageView>(R.id.details_review_image)

            val editButton = view.findViewById<Button>(R.id.details_edit_button)

            if(reviewerUid == currentReview?.reviewer?.id) {
                editButton.visibility = View.VISIBLE
            } else {
                editButton.visibility = View.GONE
            }


            resturantName.text = currentReview?.review?.restaurant_name ?: ""
            description.text = currentReview?.review?.id ?: ""
            userId.text = currentReview?.reviewer?.name ?: ""
            rating.rating = currentReview?.review?.rating?.toFloat() ?: 0f

            currentReview?.review?.image?.let {
                val bitmap = decodeBase64ToImage(it)
                image.setImageBitmap(bitmap)
            }


        })
    }
}

