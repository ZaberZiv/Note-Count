package com.zivapp.notes.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zivapp.notes.R;
import com.zivapp.notes.databinding.ItemContactsBinding;
import com.zivapp.notes.model.User;
import com.zivapp.notes.views.groupnotes.ContactsListActivity;
import com.zivapp.notes.views.groupnotes.GroupNoteActivity;

import java.util.ArrayList;

/**
 * Adapter used for the {@link ContactsListActivity}.
 */
public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.ItemViewHolder> {

    private static final String TAG = "AdapterContacts";

    private ArrayList<User> list;
    private Activity activity;
    private boolean flag;
    private Button button;

    private DatabaseReference mGroupIDReference;
    private DatabaseReference mUserReference;
    private DatabaseReference reference;

    private ArrayList<User> user_array = new ArrayList<>();

    public AdapterContacts(ArrayList<User> list, Activity activity, Button button) {
        this.list = list;
        this.activity = activity;
        this.button = button;
    }

    public void setNoteList(ArrayList<User> list) {
        this.list = list;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemContactsBinding binding;

        public ItemViewHolder(@NonNull ItemContactsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemContactsBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.item_contacts, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        User user = list.get(position);
        holder.binding.setUser(user);

        // pick a contact(s)
        holder.binding.userCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView pickContact = holder.binding.imagePicked;

                if (!flag) {
                    flag = true;
                    pickContact.setVisibility(View.VISIBLE);
                    user_array.add(position, list.get(position));
                    Log.v(TAG, "Item selected: " + user_array.get(position).getPhone());
                    Log.v(TAG, "Item Position: " + position);
                    Log.v(TAG, "Array size: " + user_array.size());

                } else {
                    flag = false;
                    pickContact.setVisibility(View.GONE);
                    Log.v(TAG, "Item removed: " + user_array.get(position).getPhone());
                    user_array.remove(position);
                }
            }
        });

        // open GroupNoteActivity
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseInstances();

                for (User user : user_array) {
                    mGroupIDReference.child("users").child(user.getId()).setValue(true);
                    Log.v(TAG, "User: " + user.getName() + ", id: " + user.getId());

                    getReference(user.getId()).child(mGroupIDReference.getKey()).setValue(true);
                    Log.v(TAG, "KEY 1: " + mGroupIDReference.getKey());
                }

                String key = mGroupIDReference.getKey();
                mUserReference.child(key).setValue(true);

                Intent intent = new Intent(activity, GroupNoteActivity.class);
                intent.putParcelableArrayListExtra("array", user_array);
                intent.putExtra("key", key);
                activity.startActivity(intent);
            }
        });
    }

    private void firebaseInstances() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        mGroupIDReference = reference.child("Groups").push();
        // add current user to the root
        mGroupIDReference.child("members").child(user.getUid()).setValue(true);

        mUserReference = reference.child("users").child(user.getUid()).child("Group");
        Log.v(TAG, "firebaseInstances() worked");
    }

    private DatabaseReference getReference(String uID) {
        return reference.child("users").child(uID).child("Group");
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }
}