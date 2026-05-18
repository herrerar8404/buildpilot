import type { ProjectStatus } from "../../types/project";

type ProjectStatusBadgeProps = {
  status: ProjectStatus;
};

const statusClassMap: Record<ProjectStatus, string> = {
  PENDING: "bg-amber-50 text-amber-700 border-amber-200",
  IN_PROGRESS: "bg-blue-50 text-blue-700 border-blue-200",
  COMPLETED: "bg-emerald-50 text-emerald-700 border-emerald-200",
  CANCELLED: "bg-rose-50 text-rose-700 border-rose-200"
};

const statusLabelMap: Record<ProjectStatus, string> = {
  PENDING: "Pendiente",
  IN_PROGRESS: "En progreso",
  COMPLETED: "Completado",
  CANCELLED: "Cancelado"
};

export function ProjectStatusBadge({ status }: ProjectStatusBadgeProps) {
  return (
    <span className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-semibold ${statusClassMap[status]}`}>
      {statusLabelMap[status]}
    </span>
  );
}

