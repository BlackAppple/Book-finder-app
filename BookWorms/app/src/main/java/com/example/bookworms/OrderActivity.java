package com.example.bookworms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookworms.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private EditText nameEditTxt,phoneEditTxt,addressEditTxt,cityEditTxt;
    private Button confirmOrderbtn;

    private String  totalAmount="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        totalAmount = getIntent().getStringExtra("Total price");
        Toast.makeText(this, "Total Price = Tk : " +totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderbtn = (Button) findViewById(R.id.confirm_order);
        nameEditTxt=(EditText) findViewById(R.id.shipment_name);
        phoneEditTxt=(EditText) findViewById(R.id.shipment_phone_number);
        addressEditTxt=(EditText) findViewById(R.id.shipment_address);
        cityEditTxt=(EditText) findViewById(R.id.shipment_city);

        confirmOrderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Check();
            }
        });
    }

    private void Check()
    {
        if(TextUtils.isEmpty(nameEditTxt.getText().toString()))
        {
            Toast.makeText(this,"Please provide your full name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditTxt.getText().toString()))
        {
            Toast.makeText(this,"Please provide your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditTxt.getText().toString()))
        {
            Toast.makeText(this,"Please provide your address",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(nameEditTxt.getText().toString()))
        {
            Toast.makeText(this,"Please provide your city name",Toast.LENGTH_SHORT).show();
        }
        else{
            confirmOrder();
        }
    }

    private void confirmOrder()
    {
        final String saveCurrentTime,saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> ordersMap = new HashMap<>();

        ordersMap.put("TotalAmount",totalAmount);
        ordersMap.put("name",nameEditTxt.getText().toString());
        ordersMap.put("phone",phoneEditTxt.getText().toString());
        ordersMap.put("address",addressEditTxt.getText().toString());
        ordersMap.put("city",cityEditTxt.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful())
                               {
                                   Toast.makeText(OrderActivity.this, "Your Order has been placed Successfully", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(OrderActivity.this,HomeActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(intent);
                               }
                                }
                            });
                }
            }
        });


    }
}