package com.example.natigram.ui.articles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.natigram.R

private const val ARG_PARAM1 = "userId"
private const val ARG_PARAM2 = "id"
private const val ARG_PARAM3 = "title"
private const val ARG_PARAM4 = "body"
private const val ARG_PARAM5 = "image"

class ArticleFragment : Fragment() {
    private var userId: String? = null
    private var id: String? = null
    private var title: String? = null
    private var body: String? = null
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)
            id = it.getString(ARG_PARAM2)
            title = it.getString(ARG_PARAM3)
            body = it.getString(ARG_PARAM4)
            image = it.getString(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_fragement, container, false)

        val titleTextView: TextView = view.findViewById(R.id.article_title)
        val bodyTextView: TextView = view.findViewById(R.id.article_body)

        titleTextView.text = title
        bodyTextView.text = body

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String, id: String, title: String, body: String, image: String) =
            ArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, userId)
                    putString(ARG_PARAM2, id)
                    putString(ARG_PARAM3, title)
                    putString(ARG_PARAM4, body)
                    putString(ARG_PARAM5, image)
                }
            }
    }
}
