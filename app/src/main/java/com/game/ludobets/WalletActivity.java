package com.game.ludobets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {
//    TextView current_balance;
//    EditText amountEt;
//    Button send;
//    final int UPI_PAYMENT = 0;
//    String nameEt,checkbalance=null,userid,useremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wallet);
//        current_balance=findViewById(R.id.current_balance);
//        send = findViewById(R.id.send);
//        amountEt = findViewById(R.id.amount_et);
//        Intent intent = getIntent();
//        nameEt = intent.getStringExtra("current_user_name");
//        useremail=intent.getStringExtra("current_user_email");
//        userid=intent.getStringExtra("current_user_id");
////        upiIdEt = findViewById(R.id.upi_id);
////        current_balance.setText("₹0.00");
//
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Getting the values from the EditTexts
//                String amount = amountEt.getText().toString();
//                String name = nameEt.toString();
//                String upiId = "8295350656@paytm";
//                payUsingUpi(amount,name,upiId);
//            }
//        });
//        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
//        CollectionReference requestRef = fStore.collection("users");
//        requestRef.whereEqualTo("Email", useremail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        checkbalance=(String)document.getString("wallet");
//                        if(checkbalance!=null)
//                        {
//                            current_balance.setText(checkbalance+".00");
//                        }
//                       else
//                        {
//                           current_balance.setText("₹0.00");
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    void payUsingUpi(String amount,String name,String upiId) {
//
//        Uri uri = Uri.parse("upi://pay").buildUpon()
//                .appendQueryParameter("pa", upiId)
//                .appendQueryParameter("pn", name)
//                .appendQueryParameter("am", amount)
//                .appendQueryParameter("cu", "INR")
//                .build();
//
//
//        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
//        upiPayIntent.setData(uri);
//
//        // will always show a dialog to user to choose an app
//        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
//
//        // check if intent resolves
//        if(null != chooser.resolveActivity(getPackageManager())) {
//            startActivityForResult(chooser, UPI_PAYMENT);
//        } else {
//            Toast.makeText(WalletActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case UPI_PAYMENT:
//                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
//                    if (data != null) {
//                        String trxt = data.getStringExtra("response");
//                        Log.d("UPI", "onActivityResult: " + trxt);
//                        ArrayList<String> dataList = new ArrayList<>();
//                        dataList.add(trxt);
//                        upiPaymentDataOperation(dataList);
//                    } else {
//                        Log.d("UPI", "onActivityResult: " + "Return data is null");
//                        ArrayList<String> dataList = new ArrayList<>();
//                        dataList.add("nothing");
//                        upiPaymentDataOperation(dataList);
//                    }
//                } else {
//                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
//                    ArrayList<String> dataList = new ArrayList<>();
//                    dataList.add("nothing");
//                    upiPaymentDataOperation(dataList);
//                }
//                break;
//        }
//    }
//
//    private void upiPaymentDataOperation(ArrayList<String> data) {
//        if (isConnectionAvailable(WalletActivity.this)) {
//            String str = data.get(0);
//            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
//            String paymentCancel = "";
//            if(str == null) str = "discard";
//            String status = "";
//            String approvalRefNo = "";
//            String response[] = str.split("&");
//            for (int i = 0; i < response.length; i++) {
//                String equalStr[] = response[i].split("=");
//                if(equalStr.length >= 2) {
//                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
//                        status = equalStr[1].toLowerCase();
//                    }
//                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
//                        approvalRefNo = equalStr[1];
//                    }
//                }
//                else {
//                    paymentCancel = "Payment cancelled by user.";
//                }
//            }
//
//            if (status.equals("success")) {
//                //Code to handle successful transaction here.
//                Toast.makeText(WalletActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
//                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
//                current_balance.setText("₹"+amountEt.getText().toString());
//                CollectionReference requestRef = fStore.collection("users");
//                requestRef.whereEqualTo("Email", useremail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<Object, String> map = new HashMap<>();
//                                map.put("wallet","₹"+amountEt.getText().toString());
//                                requestRef.document(document.getId()).set(map, SetOptions.merge());
//                            }
//                        }
//                    }
//                });
//                Log.d("UPI", "responseStr: "+approvalRefNo);
//            }
//            else if("Payment cancelled by user.".equals(paymentCancel)) {
//                Toast.makeText(WalletActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(WalletActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(WalletActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public static boolean isConnectionAvailable(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//            if (netInfo != null && netInfo.isConnected()
//                    && netInfo.isConnectedOrConnecting()
//                    && netInfo.isAvailable()) {
//                return true;
//            }
//        }
//        return false;
    }
}
