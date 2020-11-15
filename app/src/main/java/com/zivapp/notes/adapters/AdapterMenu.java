package com.zivapp.notes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.zivapp.notes.R;
import com.zivapp.notes.databinding.ItemGroupMenuBinding;
import com.zivapp.notes.databinding.ItemMainMenuBinding;
import com.zivapp.notes.model.FormatSum;
import com.zivapp.notes.model.MainMenuNote;
import com.zivapp.notes.model.Note;
import com.zivapp.notes.views.mainmenu.MenuNotesActivity;

import java.util.ArrayList;

/**
 * A ViewModel used for the {@link MenuNotesActivity}.
 */
public class AdapterMenu extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AdapterMenu";
    private static final int TYPE_NOTE = 1;
    private static final int TYPE_GROUP = 2;

    private ArrayList<MainMenuNote> list;
    private Context context;

    public AdapterMenu(ArrayList<MainMenuNote> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setNoteList(ArrayList<MainMenuNote> list) {
        this.list = list;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private ItemMainMenuBinding binding;
        private MenuItemsBinding menuItemsBinding;

        public NoteViewHolder(@NonNull ItemMainMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            menuItemsBinding = new MenuItemsBinding();
        }

        public void setNoteDetails(ArrayList<MainMenuNote> list, int position) {
            // Binding data to XML file: item_main_menu.xml
            binding.setNote(list.get(position));

            // If User didn't set title name the "New note" will be added by default
            String defaultName = menuItemsBinding.getDefaultName(position);
            binding.setItem(new Note(defaultName));

            // Binding formated total sum to XML file: item_main_menu.xml
            String formated_total_sum = menuItemsBinding.getFormatTotalSum(list, position);
            binding.setFormat(new FormatSum(formated_total_sum));
        }

        public void cardClickListener(ArrayList<MainMenuNote> list, int position) {
            menuItemsBinding.cardClickListener(binding.cardViewNote, context, list, position);
        }

        public void cardLongClickListener(ArrayList<MainMenuNote> list, int position) {
            menuItemsBinding.cardLongClickListener(binding.cardViewNote, context, new AdapterMenu(list, context), list, position);
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private final ItemGroupMenuBinding binding;
        private MenuItemsBinding menuItemsBinding;

        public GroupViewHolder(@NonNull ItemGroupMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            menuItemsBinding = new MenuItemsBinding();
        }

        public void setNoteDetails(ArrayList<MainMenuNote> list, int position) {
            // Binding data to XML file: item_main_menu.xml
            binding.setNote(list.get(position));

            // If User didn't set title name the "New note" will be added by default
            String defaultName = menuItemsBinding.getDefaultName(position);
            binding.setItem(new Note(defaultName));

            // Binding formated total sum to XML file: item_main_menu.xml
            String formated_total_sum = menuItemsBinding.getFormatTotalSum(list, position);
            binding.setFormat(new FormatSum(formated_total_sum));
        }

        public void cardClickListener(ArrayList<MainMenuNote> list, int position) {
            menuItemsBinding.cardClickListener(binding.cardViewNote, context, list, position);
        }

        public void cardLongClickListener(ArrayList<MainMenuNote> list, int position) {
            menuItemsBinding.cardLongClickListener(binding.cardViewNote, context, new AdapterMenu(list, context), list, position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_NOTE) { // notes layout
            ItemMainMenuBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.item_main_menu, parent, false);
            return new NoteViewHolder(binding);

        } else { // group notes layout
            ItemGroupMenuBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.item_group_menu, parent, false);
            return new GroupViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_NOTE) {
            ((NoteViewHolder) holder).setNoteDetails(list, position);
            ((NoteViewHolder) holder).cardClickListener(list, position);
            ((NoteViewHolder) holder).cardLongClickListener(list, position);
        } else {
            ((GroupViewHolder) holder).setNoteDetails(list, position);
            ((GroupViewHolder) holder).cardClickListener(list, position);
            ((GroupViewHolder) holder).cardLongClickListener(list, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).isGroup()) {
            return TYPE_GROUP;
        } else {
            return TYPE_NOTE;
        }
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