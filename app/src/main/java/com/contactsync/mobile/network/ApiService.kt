package com.contactsync.mobile.network

import com.contactsync.mobile.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    
    @POST("api/companies")
    suspend fun createCompany(@Body request: CreateCompanyRequest): Response<CreateCompanyResponse>
    
    @GET("api/companies")
    suspend fun getCompanies(): Response<CompaniesListResponse>
    
    @GET("api/support")
    suspend fun getSupportEmail(): Response<SupportEmailResponse>
    
    @POST("api/admin/login")
    suspend fun adminLogin(@Body request: AdminLoginRequest): Response<AdminLoginResponse>
    
    @POST("api/superadmin/login")
    suspend fun superadminLogin(@Body request: SuperadminLoginRequest): Response<SuperadminLoginResponse>
    
    @GET("api/superadmin/companies")
    suspend fun getSuperadminCompanies(): Response<SuperadminCompaniesResponse>
    
    @PUT("api/superadmin/companies/{companyId}")
    suspend fun updateCompany(
        @Path("companyId") companyId: String,
        @Body request: UpdateCompanyRequest
    ): Response<UpdateCompanyResponse>
    
    @DELETE("api/superadmin/companies/{companyId}")
    suspend fun deleteCompany(@Path("companyId") companyId: String): Response<DeleteCompanyResponse>
    
    @PUT("api/superadmin/contacts/{companyId}/{contactId}")
    suspend fun superadminUpdateContact(
        @Path("companyId") companyId: String,
        @Path("contactId") contactId: String,
        @Body request: UpdateContactRequest
    ): Response<UpdateContactResponse>
    
    @DELETE("api/superadmin/contacts/{companyId}/{contactId}")
    suspend fun superadminDeleteContact(
        @Path("companyId") companyId: String,
        @Path("contactId") contactId: String
    ): Response<DeleteContactResponse>
    
    @GET("api/companies/{companyId}/contacts")
    suspend fun getCompanyContacts(@Path("companyId") companyId: String): Response<CompanyContactsResponse>
    
    @POST("api/contacts")
    suspend fun addContact(@Body request: AddContactRequest): Response<AddContactResponse>
    
    @PUT("api/contacts/{companyId}/{contactId}")
    suspend fun updateContact(
        @Path("companyId") companyId: String,
        @Path("contactId") contactId: String,
        @Body request: UpdateContactRequest
    ): Response<UpdateContactResponse>
    
    @DELETE("api/contacts/{companyId}/{contactId}")
    suspend fun deleteContact(
        @Path("companyId") companyId: String,
        @Path("contactId") contactId: String
    ): Response<DeleteContactResponse>
    
    @POST("api/sync")
    suspend fun syncContacts(@Body request: SyncRequest): Response<SyncResponse>
}
