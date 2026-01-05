package com.contactsync.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.contactsync.mobile.R
import com.contactsync.mobile.model.Contact
import com.contactsync.mobile.model.SuperadminCompanyItem
import com.google.android.material.button.MaterialButton

class CompanyAdapter(
    private var companies: List<SuperadminCompanyItem>,
    private val onEditCompany: (SuperadminCompanyItem) -> Unit,
    private val onDeleteCompany: (SuperadminCompanyItem) -> Unit,
    private val onEditContact: (SuperadminCompanyItem, Contact) -> Unit,
    private val onDeleteContact: (SuperadminCompanyItem, Contact) -> Unit
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCompanyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val tvCompanyUsername: TextView = itemView.findViewById(R.id.tvCompanyUsername)
        val tvPasscode: TextView = itemView.findViewById(R.id.tvPasscode)
        val tvContactCount: TextView = itemView.findViewById(R.id.tvContactCount)
        val rvContacts: RecyclerView = itemView.findViewById(R.id.rvContacts)
        val btnToggle: MaterialButton = itemView.findViewById(R.id.btnToggle)
        val btnEditCompany: ImageButton = itemView.findViewById(R.id.btnEditCompany)
        val btnDeleteCompany: ImageButton = itemView.findViewById(R.id.btnDeleteCompany)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]
        val isExpanded = expandedPositions.contains(position)

        holder.tvCompanyName.text = company.name
        holder.tvCompanyUsername.text = "@${company.username ?: "no-username"}"
        holder.tvPasscode.text = "🔑 ${company.passcode ?: "****"}"
        holder.tvContactCount.text = "${company.contactCount}"

        // Setup nested contacts RecyclerView
        holder.rvContacts.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvContacts.adapter = ContactAdapter(
            company.contacts,
            onEditClick = { contact -> onEditContact(company, contact) },
            onDeleteClick = { contact -> onDeleteContact(company, contact) }
        )

        // Handle expansion
        holder.rvContacts.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.btnToggle.text = if (isExpanded) "▲ Tutup" else "▼ Lihat Kontak (${company.contactCount})"

        holder.btnToggle.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                if (expandedPositions.contains(pos)) {
                    expandedPositions.remove(pos)
                } else {
                    expandedPositions.add(pos)
                }
                notifyItemChanged(pos)
            }
        }

        holder.btnEditCompany.setOnClickListener { onEditCompany(company) }
        holder.btnDeleteCompany.setOnClickListener { onDeleteCompany(company) }
    }

    override fun getItemCount(): Int = companies.size

    fun updateCompanies(newCompanies: List<SuperadminCompanyItem>) {
        companies = newCompanies
        expandedPositions.clear()
        notifyDataSetChanged()
    }
}
