package com.example.xyz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.Model.CardShop;
import com.example.xyz.Model.Product;
import com.example.xyz.R;
import com.example.xyz.SeeImageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdapterCardShop extends FirebaseRecyclerAdapter<CardShop, AdapterCardShop.ViewHolder> {

    public AdapterCardShop(@NonNull FirebaseRecyclerOptions<CardShop> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull CardShop model) {
        try{
            model.getProductID();
        }catch (Exception ex) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(model.getProductID());
            databaseReference.removeValue();
        }

        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase
                    .getInstance()
                    .getReference("Users")
                    .child(firebaseUser.getUid())
                    .child("Orders")
                    .child(model.getID());

            holder.remove.setOnClickListener(v -> reference.removeValue());
            holder.number.setText(String.valueOf(model.getNumber()));

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(model.getProductID());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Product product = snapshot.getValue(Product.class);
                        Glide.with(holder.imageProduct.getContext()).load(product.getImage()).into(holder.imageProduct);
                        holder.nameProduct.setText(product.getName());
                        holder.price.setText(String.valueOf(product.getPrice()));
                        holder.money.setText(String.valueOf(product.getPrice() * model.getNumber()));

                        holder.imageProduct.setOnClickListener(v -> {
                            Intent intent1 = new Intent(holder.imageProduct.getContext(), SeeImageActivity.class);
                            intent1.putExtra("image", product.getImage());
                            holder.imageProduct.getContext().startActivity(intent1);
                        });

                        holder.upNumber.setOnClickListener(v -> {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            int numberOrder = model.getNumber() + 1;
                            hashMap.put("number", numberOrder);
                            hashMap.put("sumMoney", numberOrder * product.getPrice());

                            reference.updateChildren(hashMap)
                                    .addOnCompleteListener(task -> {
                                    });
                        });
                        holder.downNumber.setOnClickListener(v -> {
                            int numberOrder = model.getNumber() - 1;
                            if (numberOrder == 0) numberOrder = 1;
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("number", numberOrder);
                            hashMap.put("sumMoney", numberOrder * product.getPrice());

                            reference.updateChildren(hashMap)
                                    .addOnCompleteListener(task -> {
                                    });
                        });
                    } catch (Exception ex) { }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception ex) {}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_shop, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton imageProduct;
        private TextView nameProduct, number, price, money;
        private Button downNumber, upNumber, remove;

        public ViewHolder(View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.imageProduct);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            downNumber = itemView.findViewById(R.id.downNumber);
            number = itemView.findViewById(R.id.number);
            upNumber = itemView.findViewById(R.id.upNumber);
            price = itemView.findViewById(R.id.price);
            money = itemView.findViewById(R.id.money);
            remove = itemView.findViewById(R.id.remove);

        }
    }

}
