package com.zivapp.countandnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.zivapp.countandnote.R
import com.zivapp.countandnote.databinding.ItemContactsBinding
import com.zivapp.countandnote.model.User
import com.zivapp.countandnote.view.contacts.SelectedUsersListener
import java.util.*

class AdapterContacts(selectedUsersListener: SelectedUsersListener) :
    RecyclerView.Adapter<AdapterContacts.ItemViewHolder>() {

    private val TAG = "AdapterContacts"
    private var mSelectedUsersListener: SelectedUsersListener = selectedUsersListener

    var list: List<User> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ItemViewHolder(binding: ItemContactsBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val mBinding: ItemContactsBinding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemContactsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_contacts, parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val user: User = list[position]
        holder.mBinding.user = user

        val pickContact: ImageView = holder.mBinding.imagePicked
        if (user.selected) {
            pickContact.visibility = View.VISIBLE
        } else {
            pickContact.visibility = View.GONE
        }

        // pick a contact(s)
        holder.mBinding.userCardView.setOnClickListener {
            if (user.selected) {
                pickContact.visibility = View.GONE
                user.selected = false
                if (getSelectedUsers().size == 0) {
                    mSelectedUsersListener.onSelectedAction(false)
                }
            } else {
                pickContact.visibility = View.VISIBLE
                user.selected = true
                mSelectedUsersListener.onSelectedAction(true)
            }
        }
    }

    fun getSelectedUsers(): ArrayList<User> {
        val usersList = ArrayList<User>()
        for (user in list) {
            if (user.selected) {
                usersList.add(user)
            }
        }
        return usersList
    }

    override fun getItemCount(): Int {
        return list.size
    }
}