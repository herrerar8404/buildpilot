import { FormEvent, useState } from "react";
import { RoomForm } from "./RoomForm";
import type { RoomFormValues, RoomUpsertRequest } from "../types/floorPlan";

type AddRoomModalProps = {
  open: boolean;
  onClose: () => void;
  onSubmit: (payload: RoomUpsertRequest) => Promise<void>;
};

const initialValues: RoomFormValues = {
  name: "",
  roomType: "OTHER",
  shapeType: "RECTANGLE",
  wallCount: "4",
  width: "4",
  length: "4",
  positionX: "0",
  positionY: "0"
};

function toRequest(values: RoomFormValues): RoomUpsertRequest {
  return {
    name: values.name.trim(),
    roomType: values.roomType,
    shapeType: values.shapeType,
    wallCount: Number(values.wallCount),
    width: Number(values.width),
    length: Number(values.length),
    positionX: Number(values.positionX),
    positionY: Number(values.positionY)
  };
}

function isInvalid(values: RoomFormValues): boolean {
  const wallCount = Number(values.wallCount);
  const wallCountByShapeIsValid =
    (values.shapeType === "RECTANGLE" && wallCount === 4) ||
    (values.shapeType === "OPEN_AREA" && wallCount >= 2 && wallCount <= 3) ||
    (values.shapeType === "IRREGULAR" && wallCount >= 5);

  return (
    values.name.trim().length === 0 ||
    Number(values.width) < 0 ||
    Number(values.length) < 0 ||
    Number(values.positionX) < 0 ||
    Number(values.positionY) < 0 ||
    !wallCountByShapeIsValid
  );
}

export function AddRoomModal({ open, onClose, onSubmit }: AddRoomModalProps) {
  const [values, setValues] = useState<RoomFormValues>(initialValues);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!open) {
    return null;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>): Promise<void> {
    event.preventDefault();
    setError(null);

    if (isInvalid(values)) {
      setError("Completa datos validos con dimensiones y coordenadas no negativas.");
      return;
    }

    setSubmitting(true);
    try {
      await onSubmit(toRequest(values));
      setValues(initialValues);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No fue posible crear la habitación.");
    } finally {
      setSubmitting(false);
    }
  }

  function handleChange(field: keyof RoomFormValues, value: string): void {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  function handleClose(): void {
    if (submitting) {
      return;
    }

    setError(null);
    onClose();
  }

  return (
    <div className="fixed inset-0 z-20 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="w-full max-w-2xl rounded-xl bg-white p-5 shadow-xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-slate-900">Agregar habitación</h3>
          <button
            type="button"
            onClick={handleClose}
            className="rounded-md px-2 py-1 text-slate-500 transition hover:bg-slate-100"
          >
            Cerrar
          </button>
        </div>

        <form onSubmit={(event) => void handleSubmit(event)} className="space-y-4">
          <RoomForm values={values} onChange={handleChange} disabled={submitting} />

          {error ? <p className="rounded-md bg-red-50 p-2 text-sm text-red-700">{error}</p> : null}

          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={handleClose}
              className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
              disabled={submitting}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-700 disabled:opacity-50"
              disabled={submitting}
            >
              {submitting ? "Guardando..." : "Crear habitación"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
