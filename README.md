# CanteenCare

Aplikasi Android untuk memesan makanan dan minuman dari stan kantin kampus sambil melihat dan memantau kandungan gizi pesanan.

Final project Praktikum Pengembangan Aplikasi Perangkat Bergerak (PAPB) TIF-A, FILKOM Universitas Brawijaya 2026, Kelompok 10.

> Status: dalam pengembangan. Bagian bertanda _TODO_ akan dilengkapi di fase berikutnya.

## Fitur Inti

1. **Pemesanan Makanan dan Minuman Terintegrasi** — keranjang, total harga, pembayaran simulasi, konfirmasi penjual, notifikasi.
2. **Informasi dan Perhitungan Total Gizi Pesanan** — total kalori, karbohidrat, protein, lemak, garam, dan gula dihitung langsung di keranjang.
3. **Pelacakan Asupan Gizi Harian terhadap Target** — progres gizi hari ini dari pesanan yang dikonfirmasi dibandingkan target pribadi.

## Struktur Repository

```
CanteenCare/
├── CLAUDE.md        # brief proyek lengkap
├── README.md
├── docs/
│   ├── erd.dbml     # desain database
│   └── api.md       # daftar endpoint API
├── backend/         # FastAPI + SQLite
└── android/         # aplikasi Android (Kotlin, Jetpack Compose)
```

## Prasyarat

- Python 3.11 atau lebih baru
- Git
- Android Studio dengan Android SDK
- Akun Firebase, untuk push notification

## Menjalankan Backend

Langkah lengkap ada di [backend/README.md](backend/README.md). Ringkasnya:

```
cd backend
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
copy .env.example .env
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Swagger UI tersedia di http://localhost:8000/docs.

## Menjalankan Aplikasi Android

_TODO (Phase 4)._ Emulator dan HP fisik lewat Wi‑Fi.

## Setup Firebase

Firebase dipakai hanya untuk push notification (FCM). Tanpa Firebase pun backend tetap berjalan, hanya notifikasinya yang tidak terkirim.

**A. Membuat project Firebase**

1. Buka https://console.firebase.google.com dan login dengan akun Google.
2. Klik **Create a project** atau **Add project**, beri nama `CanteenCare`, lalu lanjutkan.
3. Google Analytics boleh dimatikan, karena tidak dipakai. Klik **Create project** dan tunggu sampai selesai.

**B. Mendaftarkan aplikasi Android**

1. Di halaman project, klik ikon **Android** untuk menambah aplikasi.
2. Isi **Android package name** dengan `com.kelompok10.canteencare`, persis tanpa salah ketik. Nickname boleh `CanteenCare`, SHA-1 boleh dikosongkan.
3. Klik **Register app**, lalu **Download google-services.json**.
4. Simpan file itu di tempat aman. Setelah project Android dibuat, file ini dipindahkan ke `android/app/google-services.json`.
5. Langkah tambahan tentang Gradle di wizard boleh dilewati dengan **Next** sampai selesai. Bagian itu dikerjakan saat setup Android.

**C. Kunci service account untuk backend**

1. Klik ikon gerigi di kiri atas → **Project settings** → tab **Service accounts**.
2. Pastikan pilihan **Firebase Admin SDK** aktif, lalu klik **Generate new private key** → **Generate key**. Sebuah file `.json` akan terunduh.
3. Ganti nama file itu menjadi `firebase-service-account.json` dan pindahkan ke folder `backend/`.
4. Restart server backend. Di log harus muncul `Firebase siap, notifikasi aktif.`

**D. Pastikan Cloud Messaging aktif**

Di **Project settings** → tab **Cloud Messaging**, bagian **Firebase Cloud Messaging API (V1)** harus berstatus **Enabled**. Untuk project baru, biasanya sudah aktif otomatis.

> **Penting:** `firebase-service-account.json` adalah kunci rahasia yang memberi akses penuh ke project Firebase. File ini dan `google-services.json` sudah masuk `.gitignore`, jadi jangan pernah diunggah ke GitHub. Teman satu tim perlu meminta kedua file ini langsung kepadamu.

## Akun Demo

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@canteencare.id | admin123 |
| PENJUAL | gapura@canteencare.id | penjual123 |
| PENJUAL | busri@canteencare.id | penjual123 |
| PENJUAL | segar@canteencare.id | penjual123 |
| PEMBELI | daffa@student.ub.ac.id | pembeli123 |
| PEMBELI | rafael@student.ub.ac.id | pembeli123 |

Nilai gizi pada data demo adalah estimasi yang wajar, bukan hasil uji laboratorium.

## Skenario Demo

_TODO (Phase 12)._
