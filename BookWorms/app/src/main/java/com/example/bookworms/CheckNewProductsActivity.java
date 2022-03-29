package com.example.bookworms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookworms.Interface.ItemClickListener;
import com.example.bookworms.Model.Products;
import com.example.bookworms.ViewHolder.BookViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CheckNewProductsActivity extends AppCompatActivity {
        private RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;
        private DatabaseReference unverifiedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_products);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = findViewById(R.id.admin_check_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("productState").
                        equalTo("Not Approved"), Products.class).build();

        FirebaseRecyclerAdapter<Products, BookViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, BookViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getBookname());
                        holder.txtProductDescription.setText("Description: "+model.getDescription());
                        holder.txtProductPrice.setText("Price: "+ model.getPrice()+ "Taka");
                        Picasso.get().load(model.getImage()).into(holder.imageview);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String productID = model.getPid();
                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CheckNewProductsActivity.this);
                                builder.setTitle("Do You Want to approve this product?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i==0){
                                            ChangeProductState(productID);
                                        }
                                        if (i==1){

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        BookViewHolder holder = new BookViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void ChangeProductState(String productID) {
        unverifiedProductsRef.child(productID).child("productState").setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CheckNewProductsActivity.this, "Item has been approved, Now it is available for sale ", Toast.LENGTH_SHORT).show();
                    }
                }); 
    }
}