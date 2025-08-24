# 🎮 PokemonExplorer

_Aplikasi Android modern untuk eksplorasi data Pokémon_

---

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blueviolet?logo=kotlin)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-green?logo=jetpackcompose)
![Gradle](https://img.shields.io/badge/Gradle-8.x-brightgreen?logo=gradle)
![License](https://img.shields.io/badge/License-MIT-yellow)
![Platform](https://img.shields.io/badge/Platform-Android%208%2B-orange?logo=android)

---

## 📑 Daftar Isi

- [Overview](#overview)
- [Fitur Utama](#fitur-utama)
- [Teknologi](#teknologi)
- [Demo](#demo)
- [Cara Menjalankan](#cara-menjalankan)
- [Struktur Project](#struktur-project)
- [Lisensi](#lisensi)

---

## 📝 Overview

**PokemonExplorer** adalah aplikasi Android sederhana yang menampilkan daftar Pokémon, detail lengkap, pencarian, dan fitur favorit dengan penyimpanan lokal.  
Dibangun menggunakan **Kotlin**, **Jetpack Compose**, **MVVM**, **Room Database**, dan **Hilt Dependency Injection**, aplikasi ini menekankan pengalaman pengguna yang **responsif**, **offline-capable**, serta **mudah di-maintain**.

### Kenapa PokemonExplorer?

Proyek ini dibuat untuk mendemonstrasikan praktik terbaik Android Development:

- **Arsitektur Modular**: Menggunakan MVVM, dependency injection (Hilt), dan repository pattern untuk kode yang bersih dan terstruktur.
- **Komponen UI Kaya**: Reusable UI seperti kartu Pokémon, SearchBar, efek shimmer, pull-to-refresh, dan Material 3.
- **Dukungan Jaringan & Offline**: API dari [PokeAPI](https://pokeapi.co/) dengan cache lokal via Room, deteksi offline, serta opsi retry.
- **Build Terpusat**: Konfigurasi Gradle yang konsisten untuk memudahkan setup.
- **User-Centric Features**: Favorit Pokémon, pencarian cepat, detail lengkap, serta error handling untuk pengalaman pengguna yang lebih baik.

---

## ✨ Fitur Utama

- **Daftar Pokémon**: Mengambil data dari [PokeAPI](https://pokeapi.co/), ditampilkan dalam list Compose.
- **Halaman Detail**: Informasi lengkap Pokémon (gambar, statistik, evolusi, flavor text).
- **Pencarian & Filter**: Cari Pokémon berdasarkan nama, filter daftar favorit.
- **Favorit**: Tandai Pokémon favorit, tersimpan di database lokal.
- **Offline Ready**: Data tersimpan di cache, tetap bisa dibuka walau offline.
- **Force Refresh**: Tombol untuk memaksa refresh dari API.
- **UI Modern**: Material 3, efek shimmer, pull-to-refresh, desain responsif.

---

## 🛠️ Teknologi

- **Kotlin**
- **Jetpack Compose (UI)**
- **Hilt (Dependency Injection)**
- **Room Database (Local Cache)**
- **Retrofit + Moshi (Networking)**
- **Coroutines + Flow (Asynchronous Data Stream)**
- **MVVM + Repository Pattern**

---

## 📱 Demo

- **APK**: [Download pokemonexplorer.apk](https://github.com/ade-syofyan/PokemonExplorer/raw/main/pokemonexplorer.apk)
- **Video Demo**: <a href="https://youtube.com/shorts/Ox_h2I-dJMA?feature=share" target="_blank">Lihat di YouTube</a>

---

## ▶️ Cara Menjalankan

1. Clone repo ini:
   ```bash
   git clone https://github.com/ade-syofyan/PokemonExplorer.git
   cd PokemonExplorer
   ```
2. Buka project di **Android Studio (Hedgehog/Koala atau lebih baru)**.
3. Pastikan environment sesuai:
   - Android Gradle Plugin: 8.x
   - Kotlin: 1.9.x
   - Compose BOM: 2024.x
4. Sync Gradle, lalu jalankan di emulator/device minimal **Android 8.0 (API 26)**.
5. Atau install langsung file **APK** yang tersedia di repo.

---

## 📂 Struktur Project

```
pokemonexplorer/
 ├── app/                    # Source utama
 │    ├── data/              # Layer data (API, Room DAO, Entity, Repo impl)
 │    ├── domain/            # Layer domain (model, repository interface, usecases)
 │    ├── ui/                # Layer UI (Compose screen, ViewModel)
 │    └── di/                # Hilt modules (Network, Database, Repository)
 ├── pokemonexplorer.apk     # File APK siap install
 └── README.md               # Dokumentasi ini
```

---

## 📜 Lisensi

Proyek ini dirilis dengan lisensi **MIT** – silakan gunakan, modifikasi, dan distribusikan dengan bebas selama mencantumkan atribusi.

---

© 2025 – Ade Syofyan
