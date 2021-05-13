package com.example.xyz.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.xyz.DetailOrderActivity;
import com.example.xyz.Model.Order;
import com.example.xyz.Model.User;
import com.example.xyz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderAdapter2 extends RecyclerView.Adapter<OrderAdapter2.ViewHolder> {

    private List<Order> list;

    public OrderAdapter2(List<Order> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final Order order = list.get(position);

            holder.address.setText(order.getAddress());
            holder.totalMoney.setText("$" + order.getTotalMoney());

            holder.remove.setOnClickListener(v -> {
                DatabaseReference referenceOrder = FirebaseDatabase.getInstance().getReference("Orders").child(order.getID());
                referenceOrder.removeValue();
            });

            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.cardView.getContext(), DetailOrderActivity.class);
                intent.putExtra("OrderID", order.getID());
                holder.cardView.getContext().startActivity(intent);
            });

            DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Users").child(order.getUser());
            referenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    holder.username.setText(user.getUsername());
                    Glide.with(holder.username.getContext()).load(user.getAvatar()).into(holder.imageUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception ex) {
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageUser;
        private CardView cardView;
        private TextView username, address, totalMoney;
        private Button remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.imageUser);
            cardView = itemView.findViewById(R.id.CardView);
            username = itemView.findViewById(R.id.username);
            address = itemView.findViewById(R.id.address);
            totalMoney = itemView.findViewById(R.id.totalMoney);
            remove = itemView.findViewById(R.id.remove);

        }
    }

}
