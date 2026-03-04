from pydantic import BaseModel

class CategoryRead(BaseModel):
    id: int
    title: str

    model_config = {"from_attributes": True}