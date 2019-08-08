package net.mbonnin.photostream

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = emptyList<Item>()

    fun setItems(items: List<Item>) {
        this.items = items

        // XXX: use DiffUtil or some other diff framework
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            TYPE_TEXT -> {
                val textView = TextView(parent.context)
                textView.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                textView.gravity = Gravity.CENTER
                textView.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, textView.resources.displayMetrics).toInt())
                textView
            }
            else -> LayoutInflater.from(parent.context).inflate(R.layout.image, parent, false)
        }

        return object:RecyclerView.ViewHolder(view) {}
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Item.Text -> TYPE_TEXT
            is Item.Image -> TYPE_IMAGE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is Item.Text -> {
                (holder.itemView as TextView).setText(item.text)
            }
            is Item.Image -> {
                Picasso.with(holder.itemView.context)
                    .load(item.url)
                    .placeholder(R.color.placeholder)
                    .into((holder.itemView as ImageView))
            }
        }
    }

    sealed class Item {
        class Text(val text: String) : Item()
        class Image(val url: String) : Item()
    }

    companion object {
        private const val TYPE_TEXT = 0
        private const val TYPE_IMAGE = 1
    }
}