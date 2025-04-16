package lk.webstudio.finedine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

public class AddFoodActivity extends AppCompatActivity {
    private ImageView addFoodImage;
    private Uri selectedImageUri;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dtguoyfdp",
            "api_key", "243595236562616",
            "api_secret", "GHYHeA8BUP7KAQzbp3UUe_XO-zA"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Side Menu
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        FrameLayout frameLayout = findViewById(R.id.frame_layout2);
        NavigationView navigationView = findViewById(R.id.navigation_view2);
        TextView tootlText = findViewById(R.id.toolbarTxt1);
        tootlText.setText("Add Food");

        ImageButton menuBtn = findViewById(R.id.imageButtonmenu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });
// Side Menu
        Spinner spinner = findViewById(R.id.spinner);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                QuerySnapshot querySnapshot = task.getResult();
                                String[] categories = new String[querySnapshot.size() + 1];
                                int i = 0;
                                categories[i] = "Select";
                                i = i + 1;
                                for (QueryDocumentSnapshot qs : querySnapshot) {
                                    categories[i] = qs.getString("name");
                                    i++;
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext()
                                        ,
                                        android.R.layout.simple_spinner_item,
                                        categories
                                );
                                spinner.setAdapter(arrayAdapter);
                            } catch (Exception e) {
                                Log.i("FineDine", String.valueOf(e));
                            }
                        }
                    }
                });




        ImageView addFoodImage = findViewById(R.id.addFoodImage);
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        addFoodImage.setImageURI(selectedImageUri);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        selectedImageUri = selectedImage;
                        addFoodImage.setImageURI(selectedImage);
                    }
                });



        addFoodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] options = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodActivity.this);
                builder.setTitle("Select Image From");
                builder.setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                        selectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                        cameraLauncher.launch(cameraIntent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        galleryLauncher.launch(intent);
                    }
                });
                builder.show();

            }
        });



        Button addFoodBtn = findViewById(R.id.addFoodBtn);
        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText foodName = findViewById(R.id.editTextFoodName1);
                EditText fullPortionPrice = findViewById(R.id.fullPortionPrice1);
                EditText halfPortionPrice = findViewById(R.id.halfPortionPrice1);
                EditText description = findViewById(R.id.description1);
                CheckBox vegetarian = findViewById(R.id.checkBox1);
                String category = (String) spinner.getSelectedItem();
                Date date = new Date();

                if (selectedImageUri != null) {

                    try {

                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        byte[] buffer = new byte[1024];
                        int len;

                        while ((len = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, len);
                        }

                        byte[] imageData = byteArrayOutputStream.toByteArray();

                        new Thread(() -> {
                            try {

                                Map uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
                                String imageUrl = (String) uploadResult.get("url");

                                runOnUiThread(() -> {

                                    saveFoodToFirestore(
                                            foodName.getText().toString(),
                                           Double.parseDouble( fullPortionPrice.getText().toString()),
                                           Double.parseDouble( halfPortionPrice.getText().toString()),
                                            description.getText().toString(),
                                            vegetarian.isChecked(),
                                            category,
                                            date,
                                            imageUrl
                                    );
                                    Log.i("FineDineLog", "Saved Successfully ");
                                    foodName.setText("");
                                    halfPortionPrice.setText("");
                                    fullPortionPrice.setText("");
                                    description.setText("");
                                    ImageView imageView = findViewById(R.id.addFoodImage);
                                    imageView.setImageResource(R.drawable.camera);
                                    vegetarian.setChecked(false);
                                    Spinner spinner = findViewById(R.id.spinner);
                                    if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
                                        spinner.setSelection(0);
                                    }
                                    Toast.makeText(AddFoodActivity.this,"Food Saved Successfully",Toast.LENGTH_LONG).show();
                                });

                            } catch (IOException e) {
                                runOnUiThread(() -> Log.e("FineDineLog", "Image upload failed: " + e.getMessage()));
                            }
                        }).start();

                    } catch (IOException e) {
                        Log.e("FineDineLog", "InputStream Error: " + e.getMessage());
                    }
                }





            }
        });



    }

    private void saveFoodToFirestore(String name, Double fullPrice, Double halfPrice, String desc,
                                     boolean isVeg, String category, Date date, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> foodItem = new java.util.HashMap<>();
        foodItem.put("name", name);
        foodItem.put("fullPortionPrice", fullPrice);
        foodItem.put("halfPortionPrice", halfPrice);
        foodItem.put("description", desc);
        foodItem.put("vegetarian", isVeg);
        foodItem.put("category", category);
        foodItem.put("date", date);
        foodItem.put("imageUrl", imageUrl);

        db.collection("foods")
                .add(foodItem)
                .addOnSuccessListener(documentReference -> Log.d("FineDine", "Food added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("FineDine", "Error adding food", e));
    }




}