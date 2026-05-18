import type { Material } from "../../types/material";

type DeleteMaterialDialogProps = {
  material: Material | null;
  deleting: boolean;
  onCancel: () => void;
  onConfirm: () => Promise<void>;
};

export function DeleteMaterialDialog({ material, deleting, onCancel, onConfirm }: DeleteMaterialDialogProps) {
  if (!material) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-30 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="w-full max-w-md rounded-xl bg-white p-5 shadow-xl">
        <h3 className="text-lg font-semibold text-slate-900">Eliminar material</h3>
        <p className="mt-2 text-sm text-slate-600">
          Se eliminará <strong>{material.name}</strong> del catálogo. Esta acción no se puede deshacer.
        </p>

        <div className="mt-4 flex justify-end gap-2">
          <button
            type="button"
            onClick={onCancel}
            className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
            disabled={deleting}
          >
            Cancelar
          </button>
          <button
            type="button"
            onClick={() => void onConfirm()}
            className="rounded-md border border-red-300 bg-red-50 px-4 py-2 text-sm font-semibold text-red-700 hover:bg-red-100 disabled:opacity-50"
            disabled={deleting}
          >
            {deleting ? "Eliminando..." : "Eliminar"}
          </button>
        </div>
      </div>
    </div>
  );
}

