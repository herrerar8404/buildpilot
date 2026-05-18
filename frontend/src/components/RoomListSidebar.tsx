import type { FloorPlanRoom } from "../types/floorPlan";

type RoomListSidebarProps = {
  rooms: FloorPlanRoom[];
  selectedRoomId: number | null;
  onSelectRoom: (roomId: number) => void;
  onAddRoom: () => void;
};

function formatValue(value: number | null): string {
  return value == null ? "-" : `${value}m`;
}

function formatShape(shapeType: FloorPlanRoom["shapeType"]): string {
  if (shapeType === "OPEN_AREA") {
    return "Open Area";
  }
  if (shapeType === "IRREGULAR") {
    return "Irregular";
  }
  return "Rectangle";
}

export function RoomListSidebar({ rooms, selectedRoomId, onSelectRoom, onAddRoom }: RoomListSidebarProps) {
  return (
    <aside className="space-y-3 rounded-xl bg-white p-4 shadow-sm ring-1 ring-slate-200">
      <div className="flex items-center justify-between">
        <h3 className="text-base font-semibold text-slate-900">Habitaciones</h3>
        <button
          type="button"
          onClick={onAddRoom}
          className="rounded-md bg-brand-600 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-brand-700"
        >
          Agregar habitación
        </button>
      </div>

      <div className="max-h-[520px] space-y-2 overflow-auto pr-1">
        {rooms.length === 0 ? (
          <p className="rounded-md bg-slate-50 p-3 text-sm text-slate-500">No hay habitaciones en este proyecto.</p>
        ) : (
          rooms.map((room) => {
            const selected = selectedRoomId === room.id;
            return (
              <button
                key={room.id}
                type="button"
                onClick={() => onSelectRoom(room.id)}
                className={`w-full rounded-lg border p-3 text-left transition ${
                  selected
                    ? "border-brand-600 bg-blue-50 ring-1 ring-brand-600"
                    : "border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50"
                }`}
              >
                <p className="font-medium text-slate-900">{room.name}</p>
                <p className="text-xs text-slate-500">{room.roomType?.replace(/_/g, " ") ?? "OTHER"}</p>
                <div className="mt-1 flex items-center gap-2 text-[11px]">
                  <span className="rounded-full bg-slate-100 px-2 py-0.5 text-slate-600">{formatShape(room.shapeType)}</span>
                  <span className="rounded-full bg-slate-100 px-2 py-0.5 text-slate-600">{room.wallCount} paredes</span>
                </div>
                <p className="mt-1 text-xs text-slate-600">
                  {formatValue(room.width)} x {formatValue(room.length)}
                </p>
              </button>
            );
          })
        )}
      </div>
    </aside>
  );
}
