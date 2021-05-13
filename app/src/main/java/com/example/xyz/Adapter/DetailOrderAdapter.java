package com.example.xyz.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.Model.ItemOrder;
import com.example.xyz.Model.Product;
import com.example.xyz.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailOrderAdapter extends FirebaseRecyclerAdapter<ItemOrder, DetailOrderAdapter.ViewHolder> {

    public DetailOrderAdapter(@NonNull FirebaseRecyclerOptions<ItemOrder> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ItemOrder model) {
        try {
            holder.sumMoney.setText("$" + model.getSumMoney());
            holder.number.setText("number: " + model.getNumber());

            try {
                DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference("Products").child(model.getProductID());
                referenceProduct.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        holder.price.setText("price: " + product.getPrice());
                        Glide.with(holder.imageProduct.getContext()).load(product.getImage()).into(holder.imageProduct);
                        holder.nameProduct.setText(product.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception ex) {

            }

        } catch (Exception ex) {

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProduct;
        private TextView nameProduct, price, number, sumMoney;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProduct = itemView.findViewById(R.id.imageProduct);
            nameProduct = itemView.findViewById(R.id.nameProduct);
            price = itemView.findViewById(R.id.price);
            number = itemView.findViewById(R.id.number);
            sumMoney = itemView.findViewById(R.id.sumMoney);

        }
    }

}