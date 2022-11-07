package com.tomiprasetyo.ecommerceapp.ui.user.product.electronics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tomiprasetyo.ecommerceapp.R;
import com.tomiprasetyo.ecommerceapp.databinding.ActivityElectronicsCategoryListBinding;
import com.tomiprasetyo.ecommerceapp.model.Product;
import com.tomiprasetyo.ecommerceapp.ui.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class ElectronicsCategoryListActivity extends AppCompatActivity {

    private ActivityElectronicsCategoryListBinding binding;
    public static final String EXTRA_CATEGORY = "extra_category";
    private FirebaseFirestore db;
    private RecyclerView rvProduct;
    private List<Product> list = new ArrayList<>();
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityElectronicsCategoryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rvProduct = binding.rvProduct;
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
        }

        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(new GridLayoutManager(this, 2));

        if (category!=null) {
            getData(category);
        }

        getSupportActionBar().setTitle(category);

    }

    private void getData(String category) {

        db.collection("produk")
                .whereEqualTo("tipe", "electronics")
                .whereEqualTo("kategori", category)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            list.add(product);
                        }
                        Log.d("AdminProduk", String.valueOf(list.size()));
                        productAdapter = new ProductAdapter(ElectronicsCategoryListActivity.this, list);
                        productAdapter.notifyDataSetChanged();
                        rvProduct.setAdapter(productAdapter);
                    } else {
                        Log.w("AdminProduk", "loadPost:onCancelled", task.getException());
                    }
                });
    }
}