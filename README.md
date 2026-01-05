# ContactSyncApp

# Our Team
![Teams](https://img.shields.io/badge/Our%20Team-ContactSync-blue)
<div align='center'>

<img src="assets/hilman.png" width="128"/>
<img src="assets/hasna1.png" width="128"/>
<img src="assets/idha1.png" width="128"/>

<br>

[![Hilman](https://img.shields.io/badge/Developer-Hilman-blue)](https://github.com/hilman-dummy)
[![Hasna](https://img.shields.io/badge/Developer-Hasna-blue)](https://github.com/hasna-dummy)
[![Idha](https://img.shields.io/badge/Developer-Idha-blue)](https://github.com/idha-dummy)

<br> [Teknik Informatika](http://if.uinsgd.ac.id/) [UIN Sunan Gunung Djati Bandung](https://uinsgd.ac.id/) 

</div>

# Business Understanding
![Schedule](https://img.shields.io/badge/Schedule-Januari%202026-green)

## Latar Belakang Masalah
Dalam lingkungan korporat yang dinamis, pengelolaan kontak karyawan dan klien merupakan hal yang krusial. Seringkali, karyawan kesulitan untuk mendapatkan kontak rekan kerja terbaru atau kontak bisnis penting karena tidak terpusatnya database kontak. Penyimpanan manual pada perangkat masing-masing individu rentan terhadap kesalahan, duplikasi, dan risiko kehilangan data.

ContactSyncApp hadir sebagai solusi untuk menjembatani masalah tersebut. Aplikasi ini memungkinkan sinkronisasi kontak perusahaan secara otomatis ke perangkat karyawan, memastikan bahwa setiap anggota tim memiliki akses ke informasi kontak yang valid, terbaru, dan aman. Hal ini tidak hanya meningkatkan efisiensi komunikasi tetapi juga menjaga integritas data perusahaan.

Khususnya bagi perusahaan dengan mobilitas tinggi, akses cepat terhadap kontak rekan kerja tanpa perlu menyimpan satu per satu secara manual akan sangat meningkatkan produktivitas.

## Identifikasi Masalah
1.  **Inefisiensi Waktu**: Karyawan menghabiskan waktu berharga untuk menyimpan atau mencari kontak rekan kerja secara manual.
2.  **Data Tidak Terupdate**: Kontak yang disimpan di HP karyawan seringkali usang jika ada perubahan nomor atau rekrutmen baru.
3.  **Resiko Keamanan**: Data kontak bisnis tercampur dengan kontak pribadi tanpa batasan yang jelas.
4.  **Fragmentasi Informasi**: Tidak ada satu sumber kebenaran (single source of truth) untuk data kontak di perangkat mobile.

## Metode Pendekatan Penyelesaian Masalah
Solusi yang ditawarkan adalah pengembangan aplikasi berbasis Mobile (Android) yang terintegrasi dengan Backend Server. Pendekatan ini dipilih untuk memudahkan akses pengguna (mobile first) namun tetap memiliki kontrol terpusat (server-based).

Tahapan pengembangan meliputi:
1.  **Analisis Kebutuhan**: Mengidentifikasi peran (Admin vs Employee).
2.  **Perancangan Sistem**: Desain UI/UX dan arsitektur database.
3.  **Implementasi**: Pengembangan aplikasi Android dengan Kotlin dan Backend API.
4.  **Pengujian**: Testing fungsionalitas sinkronisasi dan keamanan.
5.  **Deployment**: Distribusi aplikasi ke pengguna.

## Tujuan Teknis dan Kriteria Kesuksesan
1.  **Sinkronisasi Otomatis**: Aplikasi mampu mengunduh dan menyimpan kontak dari server ke HP pengguna dengan sukses.
2.  **Role-Based Access**: Membedakan hak akses antara Super Admin, Link Admin, dan Employee.
3.  **Kemudahan Penggunaan**: UI yang intuitif untuk login dan melakukan sinkronisasi dengan satu tombol.
4.  **Keamanan Data**: Implementasi Privacy Policy yang jelas dan persetujuan pengguna sebelum akses kontak.

## Timeline Riset & Pengembangan
1.  Pencarian ide dan studi literatur.
2.  Desain arsitektur aplikasi dan database.
3.  Implementasi Backend (API & Database).
4.  Implementasi Frontend (Android App).
5.  Integrasi dan Testing.
6.  Penyusunan Dokumentasi.

![Timeline_Assets](https://img.shields.io/badge/Status-Development-yellow)

# Data Understanding
![Schedule](https://img.shields.io/badge/Platform-Android%20%26%20Web-orange)

## Kebutuhan Data
Data utama yang dikelola adalah informasi kontak entitas bisnis dan autentikasi pengguna. Data ini harus akurat dan selalu tersedia untuk sinkronisasi.

## Struktur Data
Aplikasi ini mengelola objek `Contact` dengan karakteristik sebagai berikut:

| Field | Tipe Data | Deskripsi |
| :--- | :--- | :--- |
| **id** | String | Identifikasi unik untuk setiap kontak di database. |
| **name** | String | Nama lengkap pemilik kontak. |
| **phone** | String | Nomor telepon yang akan disinkronisasi ke perangkat. |
| **email** | String | Alamat email (opsional). |
| **role** | String | Peran kontak dalam organisasi (misal: Employee, Manager). |

## Fitur Utama Aplikasi
1.  **Role Selection**: Halaman awal untuk memilih login sebagai Admin atau Employee.
2.  **Secure Login**: Autentikasi untuk memastikan hanya personel berwenang yang dapat mengakses data.
3.  **Privacy Policy**: Transparansi penggunaan data dan permintaan izin akses kontak secara eksplisit.
4.  **Multi-Language**: Dukungan Bahasa Indonesia dan Inggris untuk inklusivitas pengguna.
5.  **One-Click Sync**: Fitur utama untuk menyalin kontak korporat ke kontak lokal device hanya dengan satu tombol.

# Software Architecture
![Tech Stack](https://img.shields.io/badge/Tech-Kotlin%20%7C%20Node.js-purple)

Aplikasi dibangun menggunakan arsitektur modern untuk memastikan skalabilitas dan kemudahan maintenance:

*   **Frontend**: Native Android menggunakan **Kotlin**. Menggunakan library modern seperti Retrofit untuk networking dan Material Design untuk UI.
*   **Backend**: Layanan berbasis REST API (Node.js/Express) yang melayani permintaan data kontak.
*   **Database**: Penyimpanan terpusat untuk data pengguna dan kontak.

# Visualization & Dashboard
![Dashboard](https://img.shields.io/badge/View-Dashboard-success)

Dashboard admin (Web) digunakan untuk mengelola data kontak yang akan disinkronisasikan ke aplikasi mobile.
*   [Link Dokumen Teknis & API Spec](#) *([Dummy Link](https://docs.google.com/document/d/10H841goMSSGv1CKSRWeMWxE1YwZevIlRYA7stIH9W0U/edit?tab=t.0))*
*   [Link Download APK](#) *([Dummy Link](https://play.google.com/store/apps/details?id=com.contactsync.mobile))*

---
*Dokumentasi ini disusun sebagai bagian dari tugas pengembangan ContactSyncApp.*
