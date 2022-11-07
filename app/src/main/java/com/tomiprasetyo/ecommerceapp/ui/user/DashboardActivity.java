package com.tomiprasetyo.ecommerceapp.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomiprasetyo.ecommerceapp.R;
import com.tomiprasetyo.ecommerceapp.databinding.ActivityDashboardBinding;
import com.tomiprasetyo.ecommerceapp.model.Product;
import com.tomiprasetyo.ecommerceapp.preferences.UserPreference;
import com.tomiprasetyo.ecommerceapp.ui.MainActivity;
import com.tomiprasetyo.ecommerceapp.ui.adapter.ProductAdapter;
import com.tomiprasetyo.ecommerceapp.ui.user.product.CategoryActivity;
import com.tomiprasetyo.ecommerceapp.ui.user.product.ListProductActivity;
import com.tomiprasetyo.ecommerceapp.ui.user.product.electronics.ElectronicsProductActivity;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityDashboardBinding binding;
    private UserPreference preference;
    private FirebaseFirestore db;

    private ImageView books, clothes, electronic, other;
    private boolean isLogin = false;
    private RecyclerView rvProduct;
    private List<Product> list = new ArrayList<>();
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        preference = new UserPreference(this);
        if (user != null) {
            isLogin = true;
            binding.username.setText("Halo,\n"+preference.getUserPref().getUsername());
        } else {
            isLogin = false;
            finish();
        }

        books = binding.categoryBooks;
        books.setOnClickListener(this);
        clothes = binding.categoryClothes;
        clothes.setOnClickListener(this);
        electronic = binding.categoryElectronic;
        electronic.setOnClickListener(this);
        other = binding.categoryOther;
        other.setOnClickListener(this);

        rvProduct = binding.rvProduct;

        getData();
        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(new GridLayoutManager(this, 2));

        binding.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.admin_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.logout:
                            FirebaseAuth.getInstance().signOut();
                            preference.deleteUserPref();
                            reload();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.categoryBooks:
                Intent intent = new Intent(DashboardActivity.this, ListProductActivity.class);
                intent.putExtra(ListProductActivity.EXTRA_TYPE, "books");
                startActivity(intent);
                break;
            case R.id.categoryClothes:
                Intent intentClothes = new Intent(DashboardActivity.this, CategoryActivity.class);
                startActivity(intentClothes);
                break;
            case R.id.categoryElectronic:
                Intent intentElectro = new Intent(DashboardActivity.this, ElectronicsProductActivity.class);
                startActivity(intentElectro);
                break;
            case R.id.categoryOther:
                Intent intentOther = new Intent(DashboardActivity.this, ListProductActivity.class);
                intentOther.putExtra(ListProductActivity.EXTRA_TYPE, "other");
                startActivity(intentOther);
                break;
        }
    }

    private void getData() {

        db.collection("produk").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            list.add(product);
                        }
                        Log.d("AdminProduk", String.valueOf(list.size()));
                        productAdapter = new ProductAdapter(DashboardActivity.this, list);
                        productAdapter.notifyDataSetChanged();
                        rvProduct.setAdapter(productAdapter);
                    } else {
                        Log.w("AdminProduk", "loadPost:onCancelled", task.getException());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setMode(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public void setMode(int selectedTab) {
        switch (selectedTab) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                preference.deleteUserPref();
                reload();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isLogin) {
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLogin) {
            reload();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isLogin) {
            reload();
        }
    }

    private void reload() {
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }




}