package com.example.xyz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.Model.CardShop;
import com.example.xyz.Model.Product;
import com.example.xyz.ProductActivity;
import com.example.xyz.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductAdapter extends FirebaseRecyclerAdapter<Product, ProductAdapter.ViewHolder> {

    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Product> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Product model) {
        try {
            holder.nameProduct.setText(model.getName());
            holder.ratingBar.setRating(model.getRating());

            holder.price.setText(model.getPrice() + "đ");

            holder.addCard.setOnClickListener(v -> {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(firebaseUser.getUid()).child("Orders");
                String ID = myRef.push().getKey();
                myRef.child(ID).setValue(new CardShop(
                        ID,
                        model.getID(),
                        1,
                        model.getPrice()
                ));
                Toast.makeText(holder.addCard.getContext(), "add success", Toast.LENGTH_SHORT).show();
            });

            Glide.with(holder.imageProduct.getContext()).load(model.getImage()).into(holder.imageProduct);
            holder.imageProduct.setOnClickListener(v -> {
                Intent intent = new Intent(holder.imageProduct.getContext(), ProductActivity.class);
                intent.putExtra("productID", model.getID());
                holder.imageProduct.getContext().startActivity(intent);
            });
        }catch (Exception ex) {}
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imageProduct;
        private TextView nameProduct, price;
        private Button addCard;
        private RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.imageProduct);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            price = itemView.findViewById(R.id.price);
            addCard = itemView.findViewById(R.id.addCard);

        }
    }

}
