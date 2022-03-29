package com.example.bookworms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminCategoryActivity extends AppCompatActivity {
    private TextView fiction_book,science_book;
    private TextView comics_book,biography_book;
    private Button LogOutBtn, checkOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        fiction_book=(TextView) findViewById(R.id.fiction_book);
        science_book=(TextView) findViewById(R.id.science_book);
        comics_book=(TextView) findViewById(R.id.comics_book);
        biography_book=(TextView) findViewById(R.id.biography_book);

        LogOutBtn = (Button) findViewById(R.id.admin_log_out_btn);
        checkOrderBtn =(Button) findViewById(R.id.check_order_btn);

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminOrderActivity.class);
                startActivity(intent);

            }
        });

        fiction_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AddProductActivity.class);
                intent.putExtra("Category","fiction_book");
                startActivity(intent);
            }
        });
        science_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AddProductActivity.class);
                intent.putExtra("Category","science_book");
                startActivity(intent);
            }
        });
    }
}