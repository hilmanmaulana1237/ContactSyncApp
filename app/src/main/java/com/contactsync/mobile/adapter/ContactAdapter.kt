package com.contactsync.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.contactsync.mobile.R
import com.contactsync.mobile.model.Contact

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onEditClick: ((Contact) -> Unit)? = null,
    private val onDeleteClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
        val tvContactPhone: TextView = itemView.findViewById(R.id.tvContactPhone)
        val tvContactRole: TextView = itemView.findViewById(R.id.tvContactRole)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.tvContactName.text = contact.name
        holder.tvContactPhone.text = contact.phone
        holder.tvAvatar.text = contact.name.firstOrNull()?.uppercase() ?: "?"

        if (!contact.role.isNullOrEmpty()) {
            holder.tvContactRole.text = contact.role
            holder.tvContactRole.visibility = View.VISIBLE
        } else {
            holder.tvContactRole.visibility = View.GONE
        }

        // Edit button
        if (onEditClick != null) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnEdit.setOnClickListener {
                onEditClick.invoke(contact)
            }
        } else {
            holder.btnEdit.visibility = View.GONE
        }

        // Delete button
        holder.btnDelete.setOnClickListener {
            onDeleteClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
