import { FormEvent, useEffect, useState } from "react";
import type { Material, MaterialFormValues, MaterialType, MaterialUpsertRequest, UnitMeasure } from "../../types/material";

type MaterialFormModalProps = {
  open: boolean;
  mode: "create" | "edit";
  initialMaterial?: Material | null;
  onClose: () => void;
  onSubmit: (payload: MaterialUpsertRequest) => Promise<void>;
};

const MATERIAL_TYPES: MaterialType[] = [
  "CEMENT",
  "BRICK",
  "STEEL",
  "PAINT",
  "FLOORING",
  "SAND",
  "GRAVEL",
  "WOOD",
  "GLASS",
  "OTHER"
];

const UNIT_MEASURES: UnitMeasure[] = ["PIECE", "BAG", "KG", "TON", "M2", "M3", "LITER", "UNIT"];

function toInitialValues(material?: Material | null): MaterialFormValues {
  if (!material) {
    return {
      name: "",
      materialType: "OTHER",
      unitMeasure: "UNIT",
      unitPrice: "",
      description: "",
      isActive: true
    };
  }

  return {
    name: material.name,
    materialType: material.materialType,
    unitMeasure: material.unitMeasure,
    unitPrice: String(material.unitPrice),
    description: material.description ?? "",
    isActive: material.isActive
  };
}

function toRequest(values: MaterialFormValues): MaterialUpsertRequest {
  return {
    name: values.name.trim(),
    materialType: values.materialType,
    unitMeasure: values.unitMeasure,
    unitPrice: Number(values.unitPrice),
    description: values.description.trim(),
    isActive: values.isActive
  };
}

function validate(values: MaterialFormValues): string | null {
  if (!values.name.trim()) {
    return "El nombre es obligatorio.";
  }
  if (!values.unitPrice || Number(values.unitPrice) <= 0) {
    return "El precio unitario debe ser mayor a 0.";
  }
  return null;
}

export function MaterialFormModal({ open, mode, initialMaterial, onClose, onSubmit }: MaterialFormModalProps) {
  const [values, setValues] = useState<MaterialFormValues>(toInitialValues(initialMaterial));
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setValues(toInitialValues(initialMaterial));
    setError(null);
  }, [initialMaterial, mode, open]);

  if (!open) {
    return null;
  }

  function setField<K extends keyof MaterialFormValues>(field: K, value: MaterialFormValues[K]): void {
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
      setError(err instanceof Error ? err.message : "No fue posible guardar el material.");
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-30 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="w-full max-w-2xl rounded-xl bg-white p-5 shadow-xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-slate-900">
            {mode === "create" ? "Nuevo material" : "Editar material"}
          </h3>
          <button type="button" onClick={onClose} className="rounded-md px-2 py-1 text-slate-500 hover:bg-slate-100">
            Cerrar
          </button>
        </div>

        <form onSubmit={(event) => void handleSubmit(event)} className="space-y-4">
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Nombre</span>
              <input
                value={values.name}
                onChange={(event) => setField("name", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
                required
              />
            </label>

            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Categoría</span>
              <select
                value={values.materialType}
                onChange={(event) => setField("materialType", event.target.value as MaterialType)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              >
                {MATERIAL_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {type.replace(/_/g, " ")}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Unidad de medida</span>
              <select
                value={values.unitMeasure}
                onChange={(event) => setField("unitMeasure", event.target.value as UnitMeasure)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
              >
                {UNIT_MEASURES.map((measure) => (
                  <option key={measure} value={measure}>
                    {measure}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <span className="mb-1 block text-sm font-medium text-slate-700">Precio unitario</span>
              <input
                type="number"
                step="0.01"
                min="0"
                value={values.unitPrice}
                onChange={(event) => setField("unitPrice", event.target.value)}
                className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm"
                disabled={saving}
                required
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

            <label className="inline-flex items-center gap-2 text-sm text-slate-700">
              <input
                type="checkbox"
                checked={values.isActive}
                onChange={(event) => setField("isActive", event.target.checked)}
                disabled={saving}
              />
              Activo
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
              {saving ? "Guardando..." : mode === "create" ? "Crear material" : "Guardar cambios"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

