package com.android.test.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.test.R
import com.android.test.models.Bookmark
import kotlinx.android.synthetic.main.layout_location_bm_list_item.view.*


class PlacesBookmarkAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: ItemClickListener? = null
    private var onDeleteClickListener: DeleteClickListener? = null
    private var results: List<Bookmark> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_location_bm_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LocationViewHolder -> {
                holder.bind(results[position])
                holder.itemView.deleteBookMark.setOnClickListener(View.OnClickListener {
                    onDeleteClickListener?.onItemClick(results[position])
                })
                holder.itemView.setOnClickListener { view ->
                    onItemClickListener?.onItemClick(
                        view,
                        position
                    )
                }

            }
        }
    }

    fun setBookMarks(bookmarks: List<Bookmark>){
        results = bookmarks
    }

    fun getBookMarks(): List<Bookmark> {
        return results
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class LocationViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){

        private val locationNameTv = itemView.location_name_tv
        private val locationInfoTv = itemView.location_details_info_tv
        private val deleteIv = itemView.deleteBookMark

        fun bind(location: Bookmark){
            locationNameTv.text = location.name
            locationInfoTv.text = location.address
        }

    }

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    fun setDeleteClickListener(clickListener: DeleteClickListener) {
        onDeleteClickListener = clickListener
    }

}
