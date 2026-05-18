export type MaterialType =
  | "CEMENT"
  | "BRICK"
  | "STEEL"
  | "PAINT"
  | "FLOORING"
  | "SAND"
  | "GRAVEL"
  | "WOOD"
  | "GLASS"
  | "OTHER";

export type UnitMeasure = "PIECE" | "BAG" | "KG" | "TON" | "M2" | "M3" | "LITER" | "UNIT";

export interface Material {
  id: number;
  name: string;
  materialType: MaterialType;
  unitMeasure: UnitMeasure;
  unitPrice: number;
  description: string | null;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface MaterialUpsertRequest {
  name: string;
  materialType: MaterialType;
  unitMeasure: UnitMeasure;
  unitPrice: number;
  description: string;
  isActive: boolean;
}

export interface MaterialFormValues {
  name: string;
  materialType: MaterialType;
  unitMeasure: UnitMeasure;
  unitPrice: string;
  description: string;
  isActive: boolean;
}

