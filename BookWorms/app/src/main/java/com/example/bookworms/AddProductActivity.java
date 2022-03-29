package com.example.bookworms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class AddProductActivity extends AppCompatActivity {
    private String CategoryName,Description,Price,ProductName, saveCurrentDate, saveCurrentTime;
    private ImageView InputProductImage;
    private Button AddProductButton,logOut;
    private EditText InputProductName,InputProductDescription,InputProductPrice;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private String ProductRandomKey,downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        CategoryName= getIntent().getExtras().get("Category").toString();
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Book Images");
        AddProductButton=(Button) findViewById(R.id.add_new_product);
        logOut =(Button) findViewById(R.id.log_out);
        InputProductImage=(ImageView) findViewById(R.id.select_product_image);
        InputProductName=(EditText) findViewById(R.id.product_name);
        InputProductDescription=(EditText) findViewById(R.id.product_description);
        InputProductPrice=(EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 OpenGallery();
            }
        });
            AddProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ValidateProductData();
                }
            });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                Intent intent = new Intent(AddProductActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }
    private void ValidateProductData()
    {
        Description= InputProductDescription.getText().toString();
        Price= InputProductPrice.getText().toString();
        ProductName=InputProductName.getText().toString();

        if (ImageUri==null)
        {
            Toast.makeText(this,"Book image is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"Please Write Book description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this,"Please Write Book Price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ProductName))
        {
            Toast.makeText(this,"Please Write Book Name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreBookInformation();
        }
    }

    private void StoreBookInformation()
    {
        loadingBar.setTitle("Add New Book");
        loadingBar.setMessage("Please wait, while we adding new book ");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime  = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        ProductRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment()+ ProductRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 String message = e.toString();
                 Toast.makeText(AddProductActivity.this,"Error: "+ message,Toast.LENGTH_SHORT).show();
                 loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProductActivity.this,"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw  task.getException();

                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl =task.getResult().toString();
                            Toast.makeText(AddProductActivity.this,"Book Image save to DATABASE",Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });


    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("pid", ProductRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("Description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("Category", CategoryName);
        productMap.put("price", Price);
        productMap.put("bookname", ProductName);
        ProductRef.child(ProductRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Intent intent = new Intent(AddProductActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(AddProductActivity.this,"Book is added successfully!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AddProductActivity.this,"Error:" + message,Toast.LENGTH_SHORT ).show();

                }
            }
        });


    }


}