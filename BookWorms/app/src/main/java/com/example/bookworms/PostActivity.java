package com.example.bookworms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.bookworms.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class PostActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog loadinBar;
    private ImageButton selectPostImage;
    private Button UpdatePostBtn;
    private EditText PostDescription;
    private Uri imageUri;
    private static final int Gallery_pick=1;
    private String Description;
    private StorageReference PostImagesRef;
    private DatabaseReference userRef,postRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, postRandomName,downloadUrl,current_user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
//        current_user_id = mAuth.getCurrentUser().getUid();
        PostImagesRef = FirebaseStorage.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        selectPostImage = (ImageButton) findViewById(R.id.image_Button);
        UpdatePostBtn = (Button) findViewById(R.id.post_btn);
        PostDescription= (EditText) findViewById(R.id.post_Text);
        loadinBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.update_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewGallery();
            }
        });
        UpdatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePostInfo();
            }
        });
    }

    private void validatePostInfo() {
         Description =  PostDescription.getText().toString();
        if(imageUri==null)
        {
            Toast.makeText(this, "Please first select post Image", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please Write something", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadinBar.setTitle("Add new post");
            loadinBar.setMessage("Please wait..");
            loadinBar.show();
            loadinBar.setCanceledOnTouchOutside(true);
            storingImageToFirebaseStorage();
        }
    }

    private void storingImageToFirebaseStorage()
    {
        Calendar calendarDate =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendarDate.getTime());

        Calendar calendarTime =  Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
        saveCurrentTime = currentTime.format(calendarTime.getTime());

        postRandomName = saveCurrentDate+saveCurrentTime;



        StorageReference filePath = PostImagesRef.child("Post Images")
                .child(imageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this, "Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                    savingPostInformationDatabase();
                }
                else{
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void savingPostInformationDatabase() {
        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String userFullName= snapshot.child("name").getValue().toString();
                    String userProfileImage = snapshot.child("image").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", downloadUrl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("name", userFullName);
                    postRef.child(current_user_id +postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    sendUserToHome();
                                    Toast.makeText(PostActivity.this, "Post is updated successfully", Toast.LENGTH_SHORT).show();
                                    loadinBar.dismiss();
                                }
                                else{
                                    Toast.makeText(PostActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                    loadinBar.dismiss();
                                }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void viewGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home)
        {
            sendUserToHome();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToHome() {
        Intent mainIntent = new Intent(PostActivity.this, HomeActivity.class);
        startActivity(mainIntent);
    }
}