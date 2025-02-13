package com.example.finalprojectresturant.ui.main.fragments.reviews_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectresturant.R
import com.example.finalprojectresturant.ui.main.ReviewsViewModel


class ReviewsListFragment : Fragment() {
    private lateinit var reviewsList: RecyclerView
    private val viewModel: ReviewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reviews_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        reviewsList = view.findViewById<RecyclerView>(R.id.students_list)
        progressBar.visibility = View.VISIBLE
        reviewsList.visibility = View.GONE
        viewModel.reFetchReviews()
        context?.let { initStudentsList(it) }
        viewModel.getAllReviews().observe(viewLifecycleOwner, {
            progressBar.visibility = View.GONE
            reviewsList.visibility = View.VISIBLE
            if(it.isEmpty()) viewModel.reFetchReviews()
            (reviewsList.adapter as? ReviewsAdapter)?.updateReviews(it)
        })
    }

    private fun initStudentsList(context: Context) {
        reviewsList.run {
            layoutManager = LinearLayoutManager(context)
            adapter = ReviewsAdapter{ id ->
               val action = ReviewsListFragmentDirections.actionReviewsListFragmentToReviewDetailsFragment(id)
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
}