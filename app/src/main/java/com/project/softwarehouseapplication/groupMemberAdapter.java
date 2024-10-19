package com.project.softwarehouseapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class groupMemberAdapter extends RecyclerView.Adapter<groupMemberAdapter.ViewHolder> {
    private List<Member> membersList;
    private Context context;
    public groupMemberAdapter(Context context, List<Member> members) {
        this.context = context;
        this.membersList = members;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView profileImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            profileImageView = itemView.findViewById(R.id.profile_image);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_group_member_adapter, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = membersList.get(position);
        // Set member name
        holder.textViewName.setText(member.getName() != null ? member.getName() : "Unknown");
        // Load profile image
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(member.getProfileImageUrl())
                    .placeholder(R.drawable.img2)
                    .error(R.drawable.img2)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.img2);
        }
    }
    @Override
    public int getItemCount() {
        return membersList.size();
    }
}
