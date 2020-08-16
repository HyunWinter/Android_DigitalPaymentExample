package com.hyun.digitalpayment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_listitem.view.*

class ProductAdapter(productList : ArrayList<ProductModel>,
                     googleClickListener : GoogleClickListener) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private val mProductList : ArrayList<ProductModel> = productList
    private val mGoogleClickListener = googleClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            ProductAdapter.ViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recyclerview_listitem, parent, false)

        return ViewHolder(view, this.mGoogleClickListener)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ViewHolder, position: Int) {

        val product : ProductModel = this.mProductList[position]

        Picasso.get()
            .load(product.thumbnail)
            .into(holder.mThumbnail)
        holder.mTitle.text = product.title
        val size = "" + product.count + " " + product.sizeFormat +
                " (Pack of " + product.packSize + ")"
        holder.mSize.text = size
        holder.mRating.rating = product.rating.toFloat()
        val price = "$" + String.format("%.2f", product.price) +
                " (\$" + String.format("%.2f", product.price / product.packSize) + "/" + product.sizeFormat + ")"
        holder.mPrice.text = price
    }

    override fun getItemCount(): Int {
        return this.mProductList.size
    }

    /************************************************************************
     * Purpose:         RecyclerView View Holder
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    inner class ViewHolder(itemView : View,
                           clickListener : GoogleClickListener) :
        RecyclerView.ViewHolder(itemView) {

        var mThumbnail : ImageView = itemView.Main_RV_Thumbnail
        var mTitle : TextView = itemView.Main_RV_Title
        var mSize : TextView = itemView.Main_RV_Size
        var mRating : RatingBar = itemView.Main_RV_Rating
        var mPrice : TextView = itemView.Main_RV_Price

        init {
            itemView.Main_RV_Google.setOnClickListener {
                clickListener.onButtonSelected(adapterPosition)
            }
        }
    }

    interface GoogleClickListener {
        fun onButtonSelected(position: Int)
    }
}