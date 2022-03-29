package com.example.bookworms.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworms.Interface.ItemClickListener;
import com.example.bookworms.R;

public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView imageview;
    public ItemClickListener listner;
    public BookViewHolder(@NonNull View itemView) {
        super(itemView);

        imageview = (ImageView) itemView.findViewById(R.id.book_image);
        txtProductName = (TextView) itemView.findViewById(R.id.book_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.book_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.book_price);
    }
    public void setItemClickListener(ItemClickListener listner)
    {
        this.listner=listner;
    }
    @Override
    public void onClick(View view)
    {
        listner.onClick(view,getAdapterPosition(),false);
    }
}
