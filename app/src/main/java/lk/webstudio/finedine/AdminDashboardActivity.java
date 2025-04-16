package lk.webstudio.finedine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
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
        tootlText.setText("Dashboard");

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
                Log.i("ElecLog", item.toString());

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (item.toString().equals("Home")) {
                    drawerLayout.close();

                }else  if (item.toString().equals("Add Food")) {
                    Intent i = new Intent(AdminDashboardActivity.this,AddFoodActivity.class);
                    startActivity(i);
                }

//                tootlText.setText(item.toString());
//
//                Toast.makeText(HomeActivity.this, item.toString(), Toast.LENGTH_LONG).show();
                drawerLayout.close();

                return true;
            }
        });



// Side Menu




    }
}