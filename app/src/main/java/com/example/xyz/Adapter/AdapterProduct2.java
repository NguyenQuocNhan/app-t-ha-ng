package com.example.xyz.Adapter;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterProduct2 extends RecyclerView.Adapter<AdapterProduct2.ViewHolder> {

    private List<Product> list;

    public AdapterProduct2(List<Product> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final Product product = list.get(position);

            holder.nameProduct.setText(product.getName());
            holder.ratingBar.setRating(product.getRating());
            holder.price.setText(product.getPrice() + "đ");

            holder.addCard.setOnClickListener(v -> {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(firebaseUser.getUid()).child("Orders");
                String ID = myRef.push().getKey();
                myRef.child(ID).setValue(new CardShop(
                        ID,
                        product.getID(),
                        1,
                        product.getPrice()
                ));
                Toast.makeText(holder.addCard.getContext(), "add success", Toast.LENGTH_SHORT).show();
            });

            Glide.with(holder.imageProduct.getContext()).load(product.getImage()).into(holder.imageProduct);
            holder.imageProduct.setOnClickListener(v -> {
                Intent intent = new Intent(holder.imageProduct.getContext(), ProductActivity.class);
                intent.putExtra("productID", product.getID());
                holder.imageProduct.getContext().startActivity(intent);
            });
        }catch (Exception ex) {}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

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