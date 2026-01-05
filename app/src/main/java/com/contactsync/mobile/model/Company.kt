package com.contactsync.mobile.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a company
 */
data class Company(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("passcode")
    val passcode: String? = null,
    
    @SerializedName("contacts")
    val contacts: List<Contact>? = null
)

/**
 * Request body for creating a company
 */
data class CreateCompanyRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("passcode")
    val passcode: String
)

/**
 * Response from creating a company
 */
data class CreateCompanyResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("company")
    val company: CompanyInfo
)

data class CompanyInfo(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String
)

/**
 * Response from GET /api/companies
 */
data class CompaniesListResponse(
    @SerializedName("companies")
    val companies: List<CompanyListItem>
)

data class CompanyListItem(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("contactCount")
    val contactCount: Int
)
