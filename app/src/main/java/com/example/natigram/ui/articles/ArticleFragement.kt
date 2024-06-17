package com.example.natigram.ui.articles

import FirebaseArticleRepository
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.natigram.R
import com.example.natigram.data.LoginDataSource
import com.example.natigram.data.listeners.ArticleDeleteListener

private const val ARG_PARAM1 = "userId"
private const val ARG_PARAM2 = "id"
private const val ARG_PARAM3 = "title"
private const val ARG_PARAM4 = "body"
private const val ARG_PARAM5 = "image"
private const val ARG_PARAM6 = "currentUserId"

class ArticleFragment : Fragment() {
    private var userId: String? = null
    private var id: String? = null
    private var title: String? = null
    private var body: String? = null
    private var image: String? = null
    private var currentUserId: String? = null

    private var isDescriptionShort = true
    private lateinit var articleRepository: FirebaseArticleRepository
    private var deleteListener: ArticleDeleteListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ArticleDeleteListener) {
            deleteListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        deleteListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)
            id = it.getString(ARG_PARAM2)
            title = it.getString(ARG_PARAM3)
            body = it.getString(ARG_PARAM4)
            image = it.getString(ARG_PARAM5)
            currentUserId = it.getString(ARG_PARAM6)
        }

        context?.let {
            articleRepository = FirebaseArticleRepository(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_fragement, container, false)

        val bodyTextView: TextView = view.findViewById(R.id.article_body)
        val userIdTextView: TextView = view.findViewById(R.id.article_userId)
        val displayName = LoginDataSource.getDisplayName(userId ?: "")
        val imageView: ImageView = view.findViewById(R.id.article_image)
        val toggleButton: TextView = view.findViewById(R.id.toggle_button)
        val deleteButton: ImageButton = view.findViewById(R.id.button_delete_article)

        userIdTextView.text = displayName ?: "Unknown User"

        image?.let {
            Glide.with(this)
                .load(it)
                .into(imageView)
        }

        body?.let {
            if (it.length > 90) {
                bodyTextView.text = buildString {
                    append(it.take(90))
                    append(" ...")
                }

                toggleButton.setOnClickListener {
                    if (isDescriptionShort) {
                        bodyTextView.text = body
                        toggleButton.text = getString(R.string.show_less)
                    } else {
                        bodyTextView.text = buildString {
                            append(body?.take(90))
                            append(" ...")
                        }
                        toggleButton.text = getString(R.string.show_more)
                    }
                    isDescriptionShort = !isDescriptionShort
                }
            } else {
                bodyTextView.text = it
            }
        }

        if (userId == currentUserId) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                id?.let { articleId ->
                    userId?.let { it1 ->
                        articleRepository.flushArticle(articleId, it1)
                        deleteListener?.onArticleDeleted()
                    }
                }
            }
        } else {
            deleteButton.visibility = View.GONE
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(
            userId: String,
            id: String,
            title: String,
            body: String,
            image: String,
            currentUserId: String
        ): ArticleFragment {
            return ArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, userId)
                    putString(ARG_PARAM2, id)
                    putString(ARG_PARAM3, title)
                    putString(ARG_PARAM4, body)
                    putString(ARG_PARAM5, image)
                    putString(ARG_PARAM6, currentUserId)
                }
            }
        }
    }
}
