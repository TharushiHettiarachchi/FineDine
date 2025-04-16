package lk.webstudio.finedine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    public static String userLogId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView link1 = findViewById(R.id.link1);
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        FloatingActionButton adminBtn = findViewById(R.id.floatingActionButton);
adminBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i = new Intent(MainActivity.this,AdminSignInActivity.class);
        startActivity(i);
    }
});


        EditText mobileNumber = findViewById(R.id.mobile1);
        Button signInBtn = findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validations validations = new Validations();
                if(mobileNumber.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Enter the Mobile Number",Toast.LENGTH_LONG).show();
                }else if (!validations.isValidSriLankanMobile(mobileNumber.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore
                            .collection("user")
                            .whereEqualTo("mobile",mobileNumber.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();

                                        if (querySnapshot != null) {
                                            for (QueryDocumentSnapshot qs : querySnapshot) {
                                                String fname = qs.getString("firstName");
                                                String lname = qs.getString("lastName");
                                                String mobile = qs.getString("mobile");

                                                SharedPreferences sharedPreferences = getSharedPreferences("lk.webstudio.finedine.userlist", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("firstName", fname);
                                                editor.putString("lastName", lname);
                                                editor.putString("mobile", mobile);
                                                editor.putString("id", qs.getId().toString());
                                                editor.apply();
                                                userLogId = qs.getId();
                                                Log.i("FineDine", "User Login Successful");
                                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                                mobileNumber.setText("");
                                                Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                                startActivity(i);

                                            }
                                        } else {
                                            Log.i("FineDine", "Mobile Number Not Found");
                                            Toast.makeText(MainActivity.this, "Mobile Number Not Found", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }
                            });
                }
            }
        });

    }
}