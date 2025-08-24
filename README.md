# Pokemon Explorer

Aplikasi Android sederhana untuk menampilkan daftar Pokémon, detail lengkap, pencarian, dan favorit. Dibangun menggunakan **Kotlin** dan **Jetpack Compose**, dengan arsitektur **MVVM**, **Room Database**, dan **Hilt Dependency Injection**.

## ✨ Fitur Utama

- **Daftar Pokémon**: Data dari [PokeAPI](https://pokeapi.co/), dengan tampilan list menggunakan Jetpack Compose.
- **Halaman Detail**: Menampilkan informasi detail, gambar, statistik, evolusi, dan flavor text.
- **Pencarian & Filter**: Cari Pokémon berdasarkan nama dan filter daftar favorit.
- **Favorit**: Tandai Pokémon favorit, data tersimpan secara lokal.
- **Offline Ready**: Data yang sudah pernah dibuka tersimpan di Room Database. Jika jaringan gagal, aplikasi tetap bisa menampilkan cache + banner offline dengan opsi _retry_.
- **Force Refresh**: Tombol refresh untuk memuat ulang data dari API walaupun sudah ada cache.
- **UI Modern**: Jetpack Compose Material 3, Shimmer placeholder, Pull-to-Refresh.

## 🛠️ Teknologi

- Kotlin
- Jetpack Compose (UI)
- Hilt (Dependency Injection)
- Room Database (local cache)
- Retrofit + Moshi (networking)
- Coroutines + Flow (asynchronous data stream)
- MVVM + Repository pattern

## 📱 Demo

- **APK**: File [`pokemonexplorer.apk`](https://github.com/ade-syofyan/PokemonExplorer/raw/main/pokemonexplorer.apk) tersedia di root repo.
- **Video Demo**: [YouTube Short](https://youtube.com/shorts/Ox_h2I-dJMA?feature=share)

## ▶️ Cara Menjalankan

1. Clone repo ini:
   ```bash
   git clone https://github.com/USERNAME/pokemonexplorer.git
   cd pokemonexplorer
   ```
2. Buka project di **Android Studio (Hedgehog/Koala atau lebih baru)**.
3. Pastikan versi Android Gradle Plugin dan Compose sesuai dengan environment:
   - Android Gradle Plugin: 8.x
   - Kotlin: 1.9.x
   - Compose BOM: 2024.x
4. Sync Gradle, lalu jalankan di emulator/device minimal **Android 8.0 (API 26)**.
5. Atau install langsung file **APK** dari root repo ke device.

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

© 2025 – Ade Syofyan
