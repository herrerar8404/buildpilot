export type RoomType =
  | "BEDROOM"
  | "KITCHEN"
  | "LIVING_ROOM"
  | "BATHROOM"
  | "DINING_ROOM"
  | "GARAGE"
  | "OFFICE"
  | "PATIO"
  | "OTHER";

export type RoomShapeType = "RECTANGLE" | "OPEN_AREA" | "IRREGULAR";

export interface FloorPlanRoom {
  id: number;
  name: string;
  roomType: RoomType | null;
  shapeType: RoomShapeType;
  wallCount: number;
  width: number | null;
  length: number | null;
  positionX: number | null;
  positionY: number | null;
}

export interface FloorPlanResponse {
  projectId: number;
  projectName: string;
  landWidth: number | null;
  landLength: number | null;
  rooms: FloorPlanRoom[];
}

export interface ApiErrorResponse {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
  fieldErrors?: string[];
}

export interface RoomUpsertRequest {
  name: string;
  roomType: RoomType | null;
  shapeType: RoomShapeType;
  wallCount: number;
  width: number;
  length: number;
  positionX: number;
  positionY: number;
}

export interface RoomFormValues {
  name: string;
  roomType: RoomType;
  shapeType: RoomShapeType;
  wallCount: string;
  width: string;
  length: string;
  positionX: string;
  positionY: string;
}
