package lk.webstudio.finedine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//Side Menu
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout1);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        FrameLayout frameLayout = findViewById(R.id.frame_layout1);
        NavigationView navigationView = findViewById(R.id.navigation_view1);
        TextView tootlText = findViewById(R.id.toolbarTxt);
        tootlText.setText("Home");

        ImageButton menuBtn = findViewById(R.id.imageButtonmenu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("FineDineLog", item.toString());

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (item.toString().equals("Home")) {
                    drawerLayout.close();

                } else if (item.toString().equals("Add Food")) {
                    Intent i = new Intent(HomeActivity.this, AddFoodActivity.class);
                    startActivity(i);
                }


                drawerLayout.close();

                return true;
            }
        });


// Side Menu

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Foods> foodsList = new ArrayList<>();
                        for (QueryDocumentSnapshot qs : task.getResult()) {
                            try {
                                foodsList.add(new Foods(
                                        qs.getString("name"),
                                        Double.parseDouble(String.valueOf(qs.getLong("halfPortionPrice"))),
                                        Double.parseDouble(String.valueOf(qs.getLong("fullPortionPrice"))),
                                        qs.getString("description"),
                                        qs.getString("category"),
                                        qs.getDate("date"),
                                        qs.getString("imageUrl"),
                                        qs.getBoolean("vegetarian"),
                                        qs.getId()
                                ));
                            } catch (Exception e) {
                                Log.e("FineDineLog", String.valueOf(e));
                            }
                        }

                        firestore.collection("category")
                                .get()
                                .addOnCompleteListener(catTask -> {
                                    if (catTask.isSuccessful()) {
                                        ArrayList<String> categories = new ArrayList<>();
                                        for (QueryDocumentSnapshot qs : catTask.getResult()) {
                                            categories.add(qs.getString("name"));
                                        }

                                        // Group foods by category
                                        HashMap<String, List<Foods>> categoryFoodMap = new HashMap<>();
                                        for (String cat : categories) {
                                            List<Foods> filtered = new ArrayList<>();
                                            for (Foods food : foodsList) {
                                                if (food.getCategory().equals(cat)) {
                                                    filtered.add(food);
                                                }
                                            }
                                            categoryFoodMap.put(cat, filtered);
                                        }

                                        try {
                                            RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
                                            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                                            CategoryAdapter categoryAdapter = new CategoryAdapter(categories, categoryFoodMap);
                                            categoryRecyclerView.setAdapter(categoryAdapter);

                                        } catch (Exception e) {
                                            Log.e("FineDineLog", String.valueOf(e));
                                        }
                                    }
                                });
                    }
                });


    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<String> categoryList;
    private final HashMap<String, List<Foods>> categoryFoodMap;

    public CategoryAdapter(List<String> categoryList, HashMap<String, List<Foods>> categoryFoodMap) {
        this.categoryList = categoryList;
        this.categoryFoodMap = categoryFoodMap;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryHeading;
        public RecyclerView productRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryHeading = itemView.findViewById(R.id.categoryHeading);
            productRecyclerView = itemView.findViewById(R.id.productRecyclerView);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.categoryHeading.setText(category);

        List<Foods> foodsInCategory = categoryFoodMap.get(category);

        // Disable nested scrolling for smoother horizontal scrolling
        holder.productRecyclerView.setNestedScrollingEnabled(false);

        // Set horizontal LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,  // Horizontal orientation
                false  // Reverse layout (false = normal order)
        );
        holder.productRecyclerView.setLayoutManager(layoutManager);

        Log.d("FineDineLog", "Setting adapter for category: " + category + ", items: " + (foodsInCategory != null ? foodsInCategory.size() : "null"));
        holder.productRecyclerView.setAdapter(new ProductAdapter(foodsInCategory));
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Foods> foodsList;

    public ProductAdapter(List<Foods> foodsList) {
        this.foodsList = foodsList != null ? foodsList : new ArrayList<>();
        Log.d("FineDineLog", "ProductAdapter created with " + this.foodsList.size() + " items");
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, halfPrice, fullPrice, vegText;
        ImageView foodImage, vegImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                Log.e("FineDineLog", "Error6");
                foodName = itemView.findViewById(R.id.foodName);
                halfPrice = itemView.findViewById(R.id.halfPrice);
                fullPrice = itemView.findViewById(R.id.fullPrice);
                vegText = itemView.findViewById(R.id.vegText);
                foodImage = itemView.findViewById(R.id.foodImage);
                vegImage = itemView.findViewById(R.id.vegImage);
            } catch (Exception e) {
                Log.e("FineDineLog", String.valueOf(e));
            }
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (foodsList == null || foodsList.isEmpty() || position >= foodsList.size()) {
            return;
        }

        Foods food = foodsList.get(position);
        if (food != null) {
            holder.foodName.setText(food.getFoodName() != null ? food.getFoodName() : "");
            holder.halfPrice.setText("Rs. " + (food.getHalfPrice() != null ? food.getHalfPrice() : "0"));
            holder.fullPrice.setText("Rs. " + (food.getFullPrice() != null ? food.getFullPrice() : "0"));
            if (food.getVegetarian()) {
                holder.vegImage.setVisibility(View.VISIBLE);
                holder.vegText.setText("Veg");
            } else {
                holder.vegImage.setVisibility(View.INVISIBLE);
                holder.vegText.setText("");
            }
            String imageUrl = food.getImgUrl();
            if (imageUrl != null && imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            Glide.with(holder.foodImage.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.sampleproduct)
                    .error(R.drawable.sampleproduct)
                    .into(holder.foodImage);

        }
    }

    @Override
    public int getItemCount() {
        return foodsList.size();
    }
}


