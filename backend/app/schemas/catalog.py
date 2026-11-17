from pydantic import BaseModel, ConfigDict, Field

from app.models import Menu
from app.models.enums import MenuCategory, MenuStatus
from app.schemas.common import NutritionValues


class VendorOut(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str
    description: str | None
    image_url: str | None
    open_hours: str | None
    is_active: bool


class NutritionOut(NutritionValues):
    is_verified: bool


class MenuOut(BaseModel):
    id: int
    vendor_id: int
    vendor_name: str
    name: str
    description: str | None
    category: MenuCategory
    price: int
    stock: int
    image_url: str | None
    status: MenuStatus
    nutrition: NutritionOut | None

    @classmethod
    def from_menu(cls, menu: Menu) -> "MenuOut":
        n = menu.nutrition
        nutrition = None
        if n is not None:
            nutrition = NutritionOut(
                calories=n.calories, carbs=n.carbs, protein=n.protein, fat=n.fat, salt=n.salt, sugar=n.sugar,
                is_verified=n.verified_by is not None,
            )
        return cls(
            id=menu.id, vendor_id=menu.vendor_id, vendor_name=menu.vendor.name, name=menu.name,
            description=menu.description, category=MenuCategory(menu.category), price=menu.price,
            stock=menu.stock, image_url=menu.image_url, status=MenuStatus(menu.status), nutrition=nutrition,
        )


class MenuCreate(BaseModel):
    """Menu baru yang diajukan penjual. Gizi masih berupa estimasi sampai diverifikasi admin."""

    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{
                "name": "Nasi Rawon", "description": "Rawon daging sapi khas Jawa Timur", "category": "MAKANAN",
                "price": 20000, "stock": 15, "image_url": None,
                "nutrition": {"calories": 550, "carbs": 60, "protein": 30, "fat": 20, "salt": 1200, "sugar": 4},
            }]
        }
    )

    name: str = Field(min_length=1, max_length=100)
    description: str | None = Field(default=None, max_length=500)
    category: MenuCategory
    price: int = Field(ge=0)
    stock: int = Field(ge=0)
    image_url: str | None = Field(default=None, max_length=500)
    nutrition: NutritionValues


class MenuUpdate(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{
                "name": "Nasi Ayam Geprek", "description": "Ayam geprek sambal bawang", "category": "MAKANAN",
                "price": 18000, "image_url": None,
            }]
        }
    )

    name: str = Field(min_length=1, max_length=100)
    description: str | None = Field(default=None, max_length=500)
    category: MenuCategory
    price: int = Field(ge=0)
    image_url: str | None = Field(default=None, max_length=500)


class StockUpdate(BaseModel):
    model_config = ConfigDict(json_schema_extra={"examples": [{"stock": 30}]})

    stock: int = Field(ge=0)

