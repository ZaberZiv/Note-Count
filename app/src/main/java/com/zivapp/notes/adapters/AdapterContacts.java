package com.zivapp.notes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zivapp.notes.R;
import com.zivapp.notes.databinding.ItemContactsBinding;
import com.zivapp.notes.model.User;
import com.zivapp.notes.views.contacts.ContactsListActivity;
import com.zivapp.notes.views.contacts.SelectedUsersListener;

import java.util.ArrayList;

/**
 * Adapter used for the {@link ContactsListActivity}.
 */
public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.ItemViewHolder> {

    private static final String TAG = "AdapterContacts";

    private ArrayList<User> list;
    private SelectedUsersListener mSelectedUsersListener;

    public AdapterContacts(ArrayList<User> list, SelectedUsersListener selectedUsersListener) {
        this.list = list;
        mSelectedUsersListener = selectedUsersListener;
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
        final User user = list.get(position);
        holder.binding.setUser(user);

        final ImageView pickContact = holder.binding.imagePicked;
        if (user.isSelected()) {
            pickContact.setVisibility(View.VISIBLE);
        } else {
            pickContact.setVisibility(View.GONE);
        }

        // pick a contact(s)
        holder.binding.userCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isSelected()) {
                    pickContact.setVisibility(View.GONE);
                    user.setSelected(false);
                    if (getSelectedUsers().size() == 0) {
                        mSelectedUsersListener.onSelectedAction(false);
                    }
                } else {
                    pickContact.setVisibility(View.VISIBLE);
                    user.setSelected(true);
                    mSelectedUsersListener.onSelectedAction(true);
                }
            }
        });
    }

    public ArrayList<User> getSelectedUsers() {
        ArrayList<User> usersList = new ArrayList<>();
        for (User user : list) {
            if (user.isSelected()) {
                usersList.add(user);
            }
        }
        return usersList;
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