# Import semua model di sini supaya Base.metadata.create_all() mengenali semua tabel.
from app.models.menu import Menu
from app.models.nutrition import Nutrition
from app.models.nutrition_target import NutritionTarget
from app.models.order import Order
from app.models.order_item import OrderItem
from app.models.user import User
from app.models.vendor import Vendor

__all__ = ["Menu", "Nutrition", "NutritionTarget", "Order", "OrderItem", "User", "Vendor"]
