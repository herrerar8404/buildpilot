import { useCallback, useEffect, useMemo, useState } from "react";
import { AddRoomModal } from "./AddRoomModal";
import { RoomEditPanel } from "./RoomEditPanel";
import { RoomListSidebar } from "./RoomListSidebar";
import { createRoom, deleteRoom, getFloorPlan, updateRoom } from "../services/floorPlanService";
import type { FloorPlanResponse, FloorPlanRoom, RoomUpsertRequest } from "../types/floorPlan";

type FloorPlanViewerProps = {
  projectId: number | null;
};

const SVG_WIDTH = 920;
const SVG_HEIGHT = 560;
const SVG_PADDING = 26;

function toSafeNumber(value: number | null | undefined): number {
  return value ?? 0;
}

function clamp(value: number, min: number, max: number): number {
  return Math.max(min, Math.min(value, max));
}

function roomVisualStyle(room: FloorPlanRoom, selected: boolean) {
  const strokeWidth = selected ? 2 : 1;

  if (room.shapeType === "OPEN_AREA") {
    return {
      fill: selected ? "#bbf7d0" : "#dcfce7",
      stroke: selected ? "#15803d" : "#16a34a",
      strokeDasharray: "8 5",
      strokeWidth
    };
  }

  if (room.shapeType === "IRREGULAR") {
    return {
      fill: selected ? "#ddd6fe" : "#ede9fe",
      stroke: selected ? "#6d28d9" : "#7c3aed",
      strokeDasharray: "2 0",
      strokeWidth
    };
  }

  return {
    fill: selected ? "#93c5fd" : "#bfdbfe",
    stroke: selected ? "#1d4ed8" : "#2563eb",
    strokeDasharray: "2 0",
    strokeWidth
  };
}

function shapeLabel(room: FloorPlanRoom): string {
  if (room.shapeType === "OPEN_AREA") {
    return "Open Area";
  }
  if (room.shapeType === "IRREGULAR") {
    return "Irregular";
  }
  return "Rectangle";
}

export function FloorPlanViewer({ projectId }: FloorPlanViewerProps) {
  const [data, setData] = useState<FloorPlanResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedRoomId, setSelectedRoomId] = useState<number | null>(null);
  const [showAddRoomModal, setShowAddRoomModal] = useState(false);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const loadFloorPlan = useCallback(async (targetProjectId: number): Promise<void> => {
    setLoading(true);
    setError(null);

    try {
      const result = await getFloorPlan(targetProjectId);
      setData(result);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Error desconocido";
      setError(message);
      setData(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!projectId) {
      setData(null);
      setError(null);
      setLoading(false);
      setSelectedRoomId(null);
      return;
    }

    void loadFloorPlan(projectId);
  }, [projectId, loadFloorPlan]);

  useEffect(() => {
    if (!data) {
      return;
    }

    const exists = data.rooms.some((room) => room.id === selectedRoomId);
    if (!exists) {
      setSelectedRoomId(null);
    }
  }, [data, selectedRoomId]);

  const selectedRoom = useMemo(() => {
    if (!data || selectedRoomId == null) {
      return null;
    }

    return data.rooms.find((room) => room.id === selectedRoomId) ?? null;
  }, [data, selectedRoomId]);

  const layout = useMemo(() => {
    const fallback = {
      landWidthMeters: 10,
      landLengthMeters: 10,
      landX: SVG_PADDING,
      landY: SVG_PADDING,
      landWidthPx: SVG_WIDTH - SVG_PADDING * 2,
      landHeightPx: SVG_HEIGHT - SVG_PADDING * 2,
      scale: 1
    };

    if (!data) {
      return fallback;
    }

    const roomExtentX = data.rooms.reduce((max, room) => {
      return Math.max(max, toSafeNumber(room.positionX) + Math.max(toSafeNumber(room.width), 0));
    }, 0);

    const roomExtentY = data.rooms.reduce((max, room) => {
      return Math.max(max, toSafeNumber(room.positionY) + Math.max(toSafeNumber(room.length), 0));
    }, 0);

    const landWidthMeters = Math.max(toSafeNumber(data.landWidth), roomExtentX, 1);
    const landLengthMeters = Math.max(toSafeNumber(data.landLength), roomExtentY, 1);

    const drawableWidth = SVG_WIDTH - SVG_PADDING * 2;
    const drawableHeight = SVG_HEIGHT - SVG_PADDING * 2;
    const scale = Math.min(drawableWidth / landWidthMeters, drawableHeight / landLengthMeters);

    const landWidthPx = landWidthMeters * scale;
    const landHeightPx = landLengthMeters * scale;

    const landX = (SVG_WIDTH - landWidthPx) / 2;
    const landY = (SVG_HEIGHT - landHeightPx) / 2;

    return {
      landWidthMeters,
      landLengthMeters,
      landX,
      landY,
      landWidthPx,
      landHeightPx,
      scale
    };
  }, [data]);

  async function refreshFloorPlan(): Promise<void> {
    if (!projectId) {
      return;
    }

    await loadFloorPlan(projectId);
  }

  async function handleCreateRoom(payload: RoomUpsertRequest): Promise<void> {
    if (!projectId) {
      throw new Error("Se requiere un proyecto valido.");
    }

    await createRoom(projectId, payload);
    setStatusMessage("Habitación creada.");
    await refreshFloorPlan();
  }

  async function handleUpdateRoom(roomId: number, payload: RoomUpsertRequest): Promise<void> {
    await updateRoom(roomId, payload);
    setStatusMessage("Habitación actualizada.");
    await refreshFloorPlan();
  }

  async function handleDeleteRoom(roomId: number): Promise<void> {
    await deleteRoom(roomId);
    setSelectedRoomId(null);
    setStatusMessage("Habitación eliminada.");
    await refreshFloorPlan();
  }

  if (!projectId) {
    return (
      <div className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
        Ingresa un ID de proyecto para cargar el plano.
      </div>
    );
  }

  if (loading && !data) {
    return <div className="rounded-xl bg-white p-8 text-slate-600">Cargando plano...</div>;
  }

  if (error && !data) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-red-700">
        No se pudo cargar el plano: {error}
      </div>
    );
  }

  if (!data) {
    return null;
  }

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3 rounded-xl bg-white p-4 shadow-sm ring-1 ring-slate-200">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">{data.projectName}</h2>
          <p className="text-sm text-slate-600">Proyecto ID: {data.projectId}</p>
        </div>
        <div className="text-sm text-slate-600">
          Terreno: {layout.landWidthMeters.toFixed(2)}m x {layout.landLengthMeters.toFixed(2)}m
        </div>
      </div>

      {statusMessage ? (
        <div className="rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{statusMessage}</div>
      ) : null}

      {error ? <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{error}</div> : null}

      <div className="grid grid-cols-1 gap-4 xl:grid-cols-[280px_minmax(0,1fr)_360px]">
        <RoomListSidebar
          rooms={data.rooms}
          selectedRoomId={selectedRoomId}
          onSelectRoom={setSelectedRoomId}
          onAddRoom={() => setShowAddRoomModal(true)}
        />

        <div className="rounded-xl bg-white p-3 shadow-sm ring-1 ring-slate-200">
          <div className="overflow-hidden rounded-lg border border-slate-200 bg-slate-100">
            <svg
              className="h-[560px] w-full"
              viewBox={`0 0 ${SVG_WIDTH} ${SVG_HEIGHT}`}
              preserveAspectRatio="xMidYMid meet"
              role="img"
              aria-label="2D floor plan preview"
            >
              <rect x={0} y={0} width={SVG_WIDTH} height={SVG_HEIGHT} fill="#f8fafc" />

              <rect
                x={layout.landX}
                y={layout.landY}
                width={layout.landWidthPx}
                height={layout.landHeightPx}
                fill="#f1f5f9"
                stroke="#334155"
                strokeWidth={2}
              />

              {data.rooms.map((room) => {
                const roomX = clamp(toSafeNumber(room.positionX), 0, layout.landWidthMeters);
                const roomY = clamp(toSafeNumber(room.positionY), 0, layout.landLengthMeters);
                const roomWidthMeters = clamp(
                  Math.max(toSafeNumber(room.width), 0),
                  0,
                  Math.max(layout.landWidthMeters - roomX, 0)
                );
                const roomLengthMeters = clamp(
                  Math.max(toSafeNumber(room.length), 0),
                  0,
                  Math.max(layout.landLengthMeters - roomY, 0)
                );

                const x = layout.landX + roomX * layout.scale;
                const y = layout.landY + roomY * layout.scale;
                const width = roomWidthMeters * layout.scale;
                const height = roomLengthMeters * layout.scale;
                const labelX = x + width / 2;
                const labelY = y + height / 2;
                const isSelected = selectedRoomId === room.id;
                const style = roomVisualStyle(room, isSelected);

                return (
                  <g
                    key={room.id}
                    onClick={() => setSelectedRoomId(room.id)}
                    className="cursor-pointer"
                    role="button"
                    tabIndex={0}
                    onKeyDown={(event) => {
                      if (event.key === "Enter" || event.key === " ") {
                        event.preventDefault();
                        setSelectedRoomId(room.id);
                      }
                    }}
                  >
                    <rect
                      x={x}
                      y={y}
                      width={Math.max(width, 1)}
                      height={Math.max(height, 1)}
                      fill={style.fill}
                      fillOpacity={0.8}
                      stroke={style.stroke}
                      strokeWidth={style.strokeWidth}
                      strokeDasharray={style.strokeDasharray}
                      rx={4}
                    />

                    {width > 35 && height > 24 ? (
                      <>
                        <text
                          x={labelX}
                          y={labelY - 11}
                          textAnchor="middle"
                          dominantBaseline="middle"
                          fontSize={13}
                          fontWeight={700}
                          fill="#1e3a8a"
                        >
                          {room.name}
                        </text>
                        <text
                          x={labelX}
                          y={labelY + 4}
                          textAnchor="middle"
                          dominantBaseline="middle"
                          fontSize={11}
                          fill="#334155"
                        >
                          {shapeLabel(room)} - {room.wallCount} paredes
                        </text>
                        <text
                          x={labelX}
                          y={labelY + 16}
                          textAnchor="middle"
                          dominantBaseline="middle"
                          fontSize={11}
                          fill="#334155"
                        >
                          {toSafeNumber(room.width)}m x {toSafeNumber(room.length)}m
                        </text>
                      </>
                    ) : null}
                  </g>
                );
              })}
            </svg>
          </div>

          <p className="mt-3 text-xs text-slate-500">
            La escala se calcula automáticamente según dimensiones del terreno y tamaño de canvas.
          </p>
        </div>

        <RoomEditPanel selectedRoom={selectedRoom} onUpdateRoom={handleUpdateRoom} onDeleteRoom={handleDeleteRoom} />
      </div>

      <AddRoomModal open={showAddRoomModal} onClose={() => setShowAddRoomModal(false)} onSubmit={handleCreateRoom} />
    </section>
  );
}
