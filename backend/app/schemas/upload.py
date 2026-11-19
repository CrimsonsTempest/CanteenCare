from pydantic import BaseModel, Field


class ImageUploadOut(BaseModel):
    image_url: str = Field(
        description="Path gambar di server. Simpan nilai ini ke kolom image_url menu.",
        examples=["/uploads/menus/3f2a9c1e0b7d4e5f8a6b2c1d0e9f8a7b.png"],
    )
