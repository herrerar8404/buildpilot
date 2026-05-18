import { FormEvent, useEffect, useState } from "react";
import { RoomForm } from "./RoomForm";
import type { FloorPlanRoom, RoomFormValues, RoomUpsertRequest } from "../types/floorPlan";

type RoomEditPanelProps = {
  selectedRoom: FloorPlanRoom | null;
  onUpdateRoom: (roomId: number, payload: RoomUpsertRequest) => Promise<void>;
  onDeleteRoom: (roomId: number) => Promise<void>;
};

function toFormValues(room: FloorPlanRoom): RoomFormValues {
  return {
    name: room.name,
    roomType: room.roomType ?? "OTHER",
    shapeType: room.shapeType ?? "RECTANGLE",
    wallCount: String(room.wallCount ?? 4),
    width: String(room.width ?? 0),
    length: String(room.length ?? 0),
    positionX: String(room.positionX ?? 0),
    positionY: String(room.positionY ?? 0)
  };
}

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

function hasInvalidValues(values: RoomFormValues): boolean {
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

export function RoomEditPanel({ selectedRoom, onUpdateRoom, onDeleteRoom }: RoomEditPanelProps) {
  const [values, setValues] = useState<RoomFormValues | null>(null);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [feedback, setFeedback] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!selectedRoom) {
      setValues(null);
      setError(null);
      setFeedback(null);
      return;
    }

    setValues(toFormValues(selectedRoom));
    setError(null);
    setFeedback(null);
  }, [selectedRoom]);

  if (!selectedRoom || !values) {
    return (
      <aside className="rounded-xl bg-white p-4 shadow-sm ring-1 ring-slate-200">
        <h3 className="text-base font-semibold text-slate-900">Editar habitación</h3>
        <p className="mt-2 text-sm text-slate-500">Selecciona una habitación en la lista o en el plano para editarla.</p>
      </aside>
    );
  }

  const currentRoom = selectedRoom;
  const currentValues = values;

  function handleChange(field: keyof RoomFormValues, value: string): void {
    setValues((prev) => (prev ? { ...prev, [field]: value } : prev));
  }

  async function handleSave(event: FormEvent<HTMLFormElement>): Promise<void> {
    event.preventDefault();
    setError(null);
    setFeedback(null);

    if (hasInvalidValues(currentValues)) {
      setError("Completa datos validos con dimensiones y coordenadas no negativas.");
      return;
    }

    setSaving(true);
    try {
      await onUpdateRoom(currentRoom.id, toRequest(currentValues));
      setFeedback("Habitación actualizada exitosamente.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No fue posible actualizar la habitación.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(): Promise<void> {
    const confirmed = window.confirm(`Eliminar la habitación \"${currentRoom.name}\"?`);
    if (!confirmed) {
      return;
    }

    setDeleting(true);
    setError(null);
    setFeedback(null);
    try {
      await onDeleteRoom(currentRoom.id);
      setFeedback("Habitación eliminada exitosamente.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "No fue posible eliminar la habitación.");
    } finally {
      setDeleting(false);
    }
  }

  return (
    <aside className="rounded-xl bg-white p-4 shadow-sm ring-1 ring-slate-200">
      <h3 className="text-base font-semibold text-slate-900">Editar habitación</h3>
      <p className="mb-4 text-xs text-slate-500">Seleccionada: {currentRoom.name}</p>

      <form onSubmit={(event) => void handleSave(event)} className="space-y-4">
        <RoomForm values={currentValues} onChange={handleChange} disabled={saving || deleting} />

        {error ? <p className="rounded-md bg-red-50 p-2 text-sm text-red-700">{error}</p> : null}
        {feedback ? <p className="rounded-md bg-emerald-50 p-2 text-sm text-emerald-700">{feedback}</p> : null}

        <div className="flex flex-wrap justify-between gap-2">
          <button
            type="button"
            onClick={() => void handleDelete()}
            className="rounded-md border border-red-300 px-4 py-2 text-sm font-medium text-red-700 transition hover:bg-red-50 disabled:opacity-50"
            disabled={saving || deleting}
          >
            {deleting ? "Eliminando..." : "Eliminar habitación"}
          </button>
          <button
            type="submit"
            className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-700 disabled:opacity-50"
            disabled={saving || deleting}
          >
            {saving ? "Guardando..." : "Guardar cambios"}
          </button>
        </div>
      </form>
    </aside>
  );
}
