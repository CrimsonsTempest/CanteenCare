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

_TODO (Phase 1)._ Perintah utama:

```
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

## Menjalankan Aplikasi Android

_TODO (Phase 4)._ Emulator dan HP fisik lewat Wi‑Fi.

## Setup Firebase

_TODO (Phase 3)._

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
