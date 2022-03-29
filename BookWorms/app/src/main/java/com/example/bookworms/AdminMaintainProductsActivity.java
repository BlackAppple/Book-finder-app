package com.example.bookworms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookworms.Sellers.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button ApplyChangeBtn,deleteBtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productId="";

    private DatabaseReference productsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);


        productId = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);
        ApplyChangeBtn = findViewById(R.id.Apply_changes_btn);
        name = findViewById(R.id.book_name_maintain);
        price = findViewById(R.id.book_price_maintain);
        description = findViewById(R.id.book_description_maintain);
        imageView = findViewById(R.id.book_image_maintain);
        deleteBtn = findViewById(R.id.delete_book_btn);

        displayProductInfo();
        ApplyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisBook();
            }
        });

    }

    private void deleteThisBook() {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMaintainProductsActivity.this, "Changes apply successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                finish();
                Toast.makeText(AdminMaintainProductsActivity.this, "This Product is deleted Successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChanges() {
        String bName = name.getText().toString();
        String bPrice = price.getText().toString();
        String bDescription = description.getText().toString();

        if (bName.equals(""))
        {
            Toast.makeText(this, "Please Write down Book Name.", Toast.LENGTH_SHORT).show();
        }
        else if (bPrice.equals(""))
        {
            Toast.makeText(this, "Please Write down Book Price. ", Toast.LENGTH_SHORT).show();
        }
        else if (bDescription.equals(""))
        {
            Toast.makeText(this, "Please Write Book Description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("pid", productId);
            productMap.put("Description", bDescription);
            productMap.put("price", bPrice);
            productMap.put("bookname", bName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes apply successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerProductCategoryActivity.class);
                        finish();
                    }
                }
            });
        }

    }

    private void displayProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String bookName = snapshot.child("bookname").getValue().toString();
                    String bookPrice = snapshot.child("price").getValue().toString();
                    String bookDescription = snapshot.child("Description").getValue().toString();
                    String bookImage = snapshot.child("image").getValue().toString();


                    name.setText(bookName);
                    price.setText(bookPrice);
                    description.setText(bookDescription);
                    Picasso.get().load(bookImage).into(imageView);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}