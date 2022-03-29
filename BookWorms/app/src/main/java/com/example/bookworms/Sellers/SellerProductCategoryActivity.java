package com.example.bookworms.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bookworms.R;

public class SellerProductCategoryActivity extends AppCompatActivity {
    private TextView fiction_book,science_book;
    private TextView comics_book,biography_book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);

        fiction_book=(TextView) findViewById(R.id.fiction_book);
        science_book=(TextView) findViewById(R.id.science_book);
        comics_book=(TextView) findViewById(R.id.comics_book);
        biography_book=(TextView) findViewById(R.id.biography_book);

        /*
*/
        fiction_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","fiction_book");
                startActivity(intent);
            }
        });
        science_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("Category","science_book");
                startActivity(intent);
            }
        });
    }
}