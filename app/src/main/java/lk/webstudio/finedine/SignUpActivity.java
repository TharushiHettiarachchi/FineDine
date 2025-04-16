package lk.webstudio.finedine;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView link2 = findViewById(R.id.link2);
        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        EditText fname = findViewById(R.id.fname);
        EditText lname = findViewById(R.id.lname);
        EditText mobile = findViewById(R.id.mobile);
        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validations validations = new Validations();
                if(fname.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Enter the First Name",Toast.LENGTH_LONG).show();
                }else  if(lname.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Enter the Last Name",Toast.LENGTH_LONG).show();
                }else  if(mobile.getText().toString().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Enter the Mobile Number",Toast.LENGTH_LONG).show();
                }else if (!validations.isValidSriLankanMobile(mobile.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                }else{
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("firstName",fname.getText().toString());
                    user.put("lastName",lname.getText().toString());
                    user.put("mobile",mobile.getText().toString());

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore
                            .collection("user")
                            .add(user)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this,"Successful",Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(SignUpActivity.this,MainActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });
                }
            }
        });




    }
}