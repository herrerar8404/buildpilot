import { FormEvent, useEffect, useState } from "react";
import type { Project, ProjectFormValues, ProjectStatus, ProjectUpsertRequest } from "../../types/project";

type ProjectFormModalProps = {
  open: boolean;
  mode: "create" | "edit";
  initialProject?: Project | null;
  onClose: () => void;
  onSubmit: (payload: ProjectUpsertRequest) => Promise<void>;
};

const STATUS_OPTIONS: ProjectStatus[] = ["PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"];

function buildInitialForm(project?: Project | null): ProjectFormValues {
  if (!project) {
    return {
      projectName: "",
      clientName: "",
      constructionType: "",
      landWidth: "",
      landLength: "",
      address: "",
      description: "",
      creationDate: new Date().toISOString().slice(0, 10),
      status: "PENDING"
    };
  }

  return {
    projectName: project.projectName,
    clientName: project.clientName,
    constructionType: project.constructionType ?? "",
    landWidth: String(project.landWidth ?? ""),
    landLength: String(project.landLength ?? ""),
    address: project.address ?? "",
    description: project.description ?? "",
    creationDate: project.creationDate ?? new Date().toISOString().slice(0, 10),
    status: project.status
  };
}

function toRequest(values: ProjectFormValues): ProjectUpsertRequest {
  return {
    projectName: values.projectName.trim(),
    clientName: values.clientName.trim(),
    constructionType: values.constructionType.trim(),
    landWidth: Number(values.landWidth),
    landLength: Number(values.landLength),
    address: values.address.trim(),
    description: values.description.trim(),
    creationDate: values.creationDate,
    status: values.status
  };
}

function validate(values: ProjectFormValues): string | null {
  if (!values.projectName.trim()) {
    return "El nombre del proyecto es obligatorio.";
  }
  if (!values.clientName.trim()) {
    return "El nombre del cliente es obligatorio.";
  }
  if (values.landWidth && Number(values.landWidth) < 0) {
    return "El ancho del terreno debe ser positivo.";
  }
  if (values.landLength && Number(values.landLength) < 0) {
    return "El largo del terreno debe ser positivo.";
  }
  return null;
}

function formatStatus(status: ProjectStatus): string {
  return status.replace(/_/g, " ");
}

export function ProjectFormModal({ open, mode, initialProject, onClose, onSubmit }: ProjectFormModalProps) {
  const [values, setValues] = useState<ProjectFormValues>(buildInitialForm(initialProject));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setValues(buildInitialForm(initialProject));
    setError(null);
  }, [initialProject, mode, open]);

  if (!open) {
    return null;
  }

  function setField(field: keyof ProjectFormValues, value: string): void {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>): Promise<void> {
    event.preventDefault();
    setError(null);

    const validationError = validate(values);
    if (validationError) {
      setError(validationError);
      return;
    }

    setSaving(true);
    try {
      await onSubmit(toRequest(values));
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No fue posible guardar el proyecto.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-30 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="w-full max-w-3xl rounded-xl bg-white p-5 shadow-xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-slate-900">
            {mode === "create" ? "Nuevo proyecto" : "Editar proyecto"}
          </h3>
          <button type="button" onClick={onClose} className="rounded-md px-2 py-1 text-slate-500 hover:bg-slate-100">
            Cerrar
          </button>
        </div>

        <form onSubmit={(event) => void handleSubmit(event)} className="space-y-4">
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Nombre del proyecto</span>
              <input
                value={values.projectName}
                onChange={(event) => setField("projectName", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                required
                disabled={saving}
              />
            </label>
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Cliente</span>
              <input
                value={values.clientName}
                onChange={(event) => setField("clientName", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                required
                disabled={saving}
              />
            </label>
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Tipo de construcción</span>
              <input
                value={values.constructionType}
                onChange={(event) => setField("constructionType", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              />
            </label>
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Estatus</span>
              <select
                value={values.status}
                onChange={(event) => setField("status", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              >
                {STATUS_OPTIONS.map((status) => (
                  <option key={status} value={status}>
                    {formatStatus(status)}
                  </option>
                ))}
              </select>
            </label>
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Ancho del terreno (m)</span>
              <input
                type="number"
                min="0"
                step="0.01"
                value={values.landWidth}
                onChange={(event) => setField("landWidth", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              />
            </label>
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Largo del terreno (m)</span>
              <input
                type="number"
                min="0"
                step="0.01"
                value={values.landLength}
                onChange={(event) => setField("landLength", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              />
            </label>
            <label className="sm:col-span-2">
              <span className="mb-1 block text-sm font-medium text-slate-700">Dirección</span>
              <input
                value={values.address}
                onChange={(event) => setField("address", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              />
            </label>
            <label className="sm:col-span-2">
              <span className="mb-1 block text-sm font-medium text-slate-700">Descripción</span>
              <textarea
                value={values.description}
                onChange={(event) => setField("description", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                rows={3}
                disabled={saving}
              />
            </label>
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Fecha de creación</span>
              <input
                type="date"
                value={values.creationDate}
                onChange={(event) => setField("creationDate", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              />
            </label>
          </div>

          {error ? <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{error}</p> : null}

          <div className="flex justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
              disabled={saving}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
              disabled={saving}
            >
              {saving ? "Guardando..." : mode === "create" ? "Crear proyecto" : "Guardar cambios"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

