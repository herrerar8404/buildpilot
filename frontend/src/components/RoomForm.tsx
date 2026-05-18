import type { ChangeEvent } from "react";
import type { RoomFormValues, RoomShapeType, RoomType } from "../types/floorPlan";

type RoomFormProps = {
  values: RoomFormValues;
  onChange: (field: keyof RoomFormValues, value: string) => void;
  disabled?: boolean;
};

const ROOM_TYPES: RoomType[] = [
  "BEDROOM",
  "KITCHEN",
  "LIVING_ROOM",
  "BATHROOM",
  "DINING_ROOM",
  "GARAGE",
  "OFFICE",
  "PATIO",
  "OTHER"
];

const SHAPE_TYPES: RoomShapeType[] = ["RECTANGLE", "OPEN_AREA", "IRREGULAR"];

function buildInputClass(disabled?: boolean): string {
  return `w-full rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-900 outline-none transition ${
    disabled ? "bg-slate-100 text-slate-500" : "focus:border-brand-600 focus:ring-2 focus:ring-blue-100"
  }`;
}

function handleInput(
  event: ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  onChange: (field: keyof RoomFormValues, value: string) => void
): void {
  const { name, value } = event.target;
  onChange(name as keyof RoomFormValues, value);
}

function getShapeHint(shapeType: RoomShapeType): string {
  if (shapeType === "OPEN_AREA") {
    return "Area abierta: permite 2 o 3 paredes para patios o cocheras abiertas.";
  }
  if (shapeType === "IRREGULAR") {
    return "Área irregular: usa más de 4 paredes para prepararte para geometría futura.";
  }
  return "Rectángulo estándar: usa ancho/largo y 4 paredes por defecto.";
}

function normalizeWallCount(shapeType: RoomShapeType, value: string): string {
  const parsed = Number(value);

  if (!Number.isFinite(parsed)) {
    return shapeType === "RECTANGLE" ? "4" : value;
  }

  if (shapeType === "RECTANGLE") {
    return "4";
  }
  if (shapeType === "OPEN_AREA") {
    return String(Math.min(3, Math.max(2, Math.round(parsed))));
  }
  return String(Math.max(5, Math.round(parsed)));
}

export function RoomForm({ values, onChange, disabled = false }: RoomFormProps) {
  const shapeType = values.shapeType;
  const isRectangle = shapeType === "RECTANGLE";
  const wallCountMin = shapeType === "OPEN_AREA" ? 2 : shapeType === "IRREGULAR" ? 5 : 4;
  const wallCountMax = shapeType === "OPEN_AREA" ? 3 : undefined;

  function handleShapeChange(value: string): void {
    const nextShape = value as RoomShapeType;
    onChange("shapeType", nextShape);
    onChange("wallCount", normalizeWallCount(nextShape, values.wallCount));
  }

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
      <label className="sm:col-span-2">
        <span className="mb-1 block text-sm font-medium text-slate-700">Nombre de habitación</span>
        <input
          name="name"
          value={values.name}
          onChange={(event) => handleInput(event, onChange)}
          className={buildInputClass(disabled)}
          placeholder="Sala"
          disabled={disabled}
          required
        />
      </label>

      <label>
        <span className="mb-1 block text-sm font-medium text-slate-700">Tipo de habitación</span>
        <select
          name="roomType"
          value={values.roomType}
          onChange={(event) => handleInput(event, onChange)}
          className={buildInputClass(disabled)}
          disabled={disabled}
        >
          {ROOM_TYPES.map((type) => (
            <option key={type} value={type}>
              {type.replace(/_/g, " ")}
            </option>
          ))}
        </select>
      </label>

      <label>
        <span className="mb-1 block text-sm font-medium text-slate-700">Forma arquitectónica</span>
        <select
          name="shapeType"
          value={shapeType}
          onChange={(event) => handleShapeChange(event.target.value)}
          className={buildInputClass(disabled)}
          disabled={disabled}
        >
          {SHAPE_TYPES.map((type) => (
            <option key={type} value={type}>
              {type.replace(/_/g, " ")}
            </option>
          ))}
        </select>
      </label>

      <p className="sm:col-span-2 rounded-md bg-slate-50 px-3 py-2 text-xs text-slate-600">{getShapeHint(shapeType)}</p>

      <label>
        <span className="mb-1 block text-sm font-medium text-slate-700">Ancho (m)</span>
        <input
          type="number"
          step="0.01"
          min="0"
          name="width"
          value={values.width}
          onChange={(event) => handleInput(event, onChange)}
          className={buildInputClass(disabled)}
          disabled={disabled}
          required
        />
      </label>

      <label>
        <span className="mb-1 block text-sm font-medium text-slate-700">Largo (m)</span>
        <input
          type="number"
          step="0.01"
          min="0"
          name="length"
          value={values.length}
          onChange={(event) => handleInput(event, onChange)}
          className={buildInputClass(disabled)}
          disabled={disabled}
          required
        />
      </label>

      <label>
        <span className="mb-1 block text-sm font-medium text-slate-700">Posición X (m)</span>
        <input
          type="number"
          step="0.01"
          min="0"
          name="positionX"
          value={values.positionX}
          onChange={(event) => handleInput(event, onChange)}
          className={buildInputClass(disabled)}
          disabled={disabled}
          required
        />
      </label>

      <label>
        <span className="mb-1 block text-sm font-medium text-slate-700">Posición Y (m)</span>
        <input
          type="number"
          step="0.01"
          min="0"
          name="positionY"
          value={values.positionY}
          onChange={(event) => handleInput(event, onChange)}
          className={buildInputClass(disabled)}
          disabled={disabled}
          required
        />
      </label>

      <label className="sm:col-span-2">
        <span className="mb-1 block text-sm font-medium text-slate-700">Número de paredes</span>
        <input
          type="number"
          step="1"
          min={wallCountMin}
          max={wallCountMax}
          name="wallCount"
          value={isRectangle ? "4" : values.wallCount}
          onChange={(event) => onChange("wallCount", normalizeWallCount(shapeType, event.target.value))}
          className={buildInputClass(disabled || isRectangle)}
          disabled={disabled || isRectangle}
          required
        />
      </label>
    </div>
  );
}
