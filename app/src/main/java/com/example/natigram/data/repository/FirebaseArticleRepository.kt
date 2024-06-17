import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.natigram.data.listeners.ArticleDeleteListener
import com.example.natigram.data.model.articles.ArticleDao
import com.example.natigram.data.model.articles.ArticleDataResponse
import com.example.natigram.data.repository.ArticleRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class FirebaseArticleRepository(private val context: Context) : ArticleRepository, SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val db = FirebaseFirestore.getInstance()
    private var articlesListener: ListenerRegistration? = null
    private val sqlDb = writableDatabase

    override fun saveArticle(article: ArticleDataResponse, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val articleId = db.collection("articles").document().id
        val articleWithId = article.copy(id = articleId)
        db.collection("articles").document(articleId).set(articleWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    override fun flushAllArticles() {
        db.collection("articles").get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit()
            }
    }

    override fun flushArticle(article: ArticleDataResponse) {
        db.collection("articles")
            .whereEqualTo("title", article.title)
            .whereEqualTo("body", article.body)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit()
            }
    }

    override fun flushArticle(articleId: String) {
        db.collection("articles")
            .document(articleId)
            .delete()
            .addOnSuccessListener {
                Log.d("DeleteArticle", "Successfully deleted article with id $articleId")
                sqlDb.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(articleId))
            }
            .addOnFailureListener { exception ->
                Log.e("DeleteArticle", "Error deleting article with id $articleId", exception)
            }
    }

    override fun flushArticle(articleId: String, userId: String) {
        Log.d("DeleteArticle", "Searching for article with id $articleId and userId $userId")

        db.collection("articles")
            .whereEqualTo("id", articleId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("DeleteArticle", "Successfully deleted article with id $articleId and userId $userId")
                            val articleDao = ArticleDao(context)
                            articleDao.deleteArticle(articleId)
                            (context as? ArticleDeleteListener)?.onArticleDeleted()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("DeleteArticle", "Error deleting article with id $articleId and userId $userId", exception)
                        }
                } else {
                    Log.d("DeleteArticle", "No article found with id $articleId and userId $userId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DeleteArticle", "Error finding article with id $articleId and userId $userId", exception)
            }
    }

    fun fetchArticles(onSuccess: (List<ArticleDataResponse>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("articles").get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val articles = result.toObjects(ArticleDataResponse::class.java)
                onSuccess(articles)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun setupArticlesListener(onArticlesChanged: (List<ArticleDataResponse>) -> Unit) {
        articlesListener = db.collection("articles")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("FirebaseArticleRepo", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    val articles = snapshots.toObjects(ArticleDataResponse::class.java)
                    onArticlesChanged(articles)
                }
            }
    }

    fun removeArticlesListener() {
        articlesListener?.remove()
    }

    companion object {
        const val DATABASE_NAME = "articles.db"
        const val DATABASE_VERSION = 3
        const val TABLE_NAME = "articles"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_BODY = "body"
        const val COLUMN_USER_ID = "userId"

        private const val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "$COLUMN_USER_ID TEXT, " +
                "$COLUMN_ID TEXT PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_BODY TEXT) "
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME RENAME TO temp_$TABLE_NAME;")
                db.execSQL(SQL_CREATE_TABLE)
                db.execSQL("INSERT INTO $TABLE_NAME ($COLUMN_ID, $COLUMN_TITLE, $COLUMN_BODY, $COLUMN_USER_ID) " +
                        "SELECT CAST($COLUMN_ID AS TEXT), $COLUMN_TITLE, $COLUMN_BODY, $COLUMN_USER_ID FROM temp_$TABLE_NAME;")
                db.execSQL("DROP TABLE temp_$TABLE_NAME;")
            } catch (e: Exception) {
                Log.e("ArticleDao", "Error during database upgrade", e)
            }
        }
    }
}
