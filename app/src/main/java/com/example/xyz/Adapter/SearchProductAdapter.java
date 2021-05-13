package com.example.xyz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.ManagerProductActivity;
import com.example.xyz.Model.Product;
import com.example.xyz.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {

    private List<Product> productList;

    public SearchProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_manager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Product product = productList.get(position);
            Glide.with(holder.imageProduct.getContext()).load(product.getImage()).into(holder.imageProduct);
            holder.nameProduct.setText(product.getName());
            holder.ratingBar.setRating(product.getRating());

            holder.buttonRemove.setOnClickListener(v -> {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(product.getID());
                databaseReference.removeValue();
            });

            holder.buttonUpdate.setOnClickListener(v -> {
                Intent intent = new Intent(holder.imageProduct.getContext(), ManagerProductActivity.class);
                intent.putExtra("productID", product.getID());
                holder.imageProduct.getContext().startActivity(intent);
            });
        } catch (Exception ex) {
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imageProduct;
        private TextView nameProduct;
        private Button buttonRemove, buttonUpdate;
        private RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.imageProduct);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);

        }
    }

}
