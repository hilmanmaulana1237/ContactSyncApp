package com.contactsync.mobile.model

import com.google.gson.annotations.SerializedName

// ==================
// ADMIN MODELS
// ==================

data class AdminLoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("passcode") val passcode: String
)

data class AdminLoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("company") val company: AdminCompanyInfo
)

data class AdminCompanyInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String? = null,
    @SerializedName("contactCount") val contactCount: Int
)

data class CompanyContactsResponse(
    @SerializedName("companyName") val companyName: String,
    @SerializedName("contacts") val contacts: List<Contact>
)

data class DeleteContactResponse(
    @SerializedName("message") val message: String
)

// ==================
// SUPERADMIN MODELS
// ==================

data class SuperadminLoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class SuperadminLoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("role") val role: String
)

data class SuperadminCompaniesResponse(
    @SerializedName("companies") val companies: List<SuperadminCompanyItem>
)

data class SuperadminCompanyItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String? = null,
    @SerializedName("passcode") val passcode: String? = null, // Decrypted for superadmin
    @SerializedName("contactCount") val contactCount: Int,
    @SerializedName("contacts") val contacts: List<Contact>
)

data class DeleteCompanyResponse(
    @SerializedName("message") val message: String,
    @SerializedName("deletedCompany") val deletedCompany: DeletedCompanyInfo? = null
)

data class DeletedCompanyInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

// ==================
// UPDATE MODELS
// ==================

data class UpdateCompanyRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("passcode") val passcode: String? = null
)

data class UpdateCompanyResponse(
    @SerializedName("message") val message: String,
    @SerializedName("company") val company: AdminCompanyInfo? = null
)

data class UpdateContactRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("email") val email: String? = null
)

data class UpdateContactResponse(
    @SerializedName("message") val message: String,
    @SerializedName("contact") val contact: Contact? = null
)

// ==================
// SUPPORT
// ==================

data class SupportEmailResponse(
    @SerializedName("email") val email: String
)
