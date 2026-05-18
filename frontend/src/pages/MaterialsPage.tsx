import { useMemo, useState } from "react";
import { DeleteMaterialDialog } from "../components/materials/DeleteMaterialDialog";
import { MaterialFormModal } from "../components/materials/MaterialFormModal";
import { MaterialStatusBadge } from "../components/materials/MaterialStatusBadge";
import { useMaterials } from "../hooks/useMaterials";
import { createMaterial, deleteMaterial, updateMaterial } from "../services/materialService";
import type { Material, MaterialType, MaterialUpsertRequest } from "../types/material";

export function MaterialsPage() {
  const { materials, loading, error, reload } = useMaterials();

  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState<MaterialType | "ALL">("ALL");
  const [statusFilter, setStatusFilter] = useState<"ALL" | "ACTIVE" | "INACTIVE">("ALL");

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingMaterial, setEditingMaterial] = useState<Material | null>(null);
  const [materialToDelete, setMaterialToDelete] = useState<Material | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const filteredMaterials = useMemo(() => {
    return materials
      .filter((material) => {
        const query = search.trim().toLowerCase();
        if (!query) {
          return true;
        }
        return (
          material.name.toLowerCase().includes(query) ||
          material.materialType.toLowerCase().includes(query) ||
          material.unitMeasure.toLowerCase().includes(query)
        );
      })
      .filter((material) => (typeFilter === "ALL" ? true : material.materialType === typeFilter))
      .filter((material) => {
        if (statusFilter === "ALL") {
          return true;
        }
        return statusFilter === "ACTIVE" ? material.isActive : !material.isActive;
      })
      .sort((a, b) => a.name.localeCompare(b.name));
  }, [materials, search, typeFilter, statusFilter]);

  const materialTypes = useMemo(
    () => Array.from(new Set(materials.map((material) => material.materialType))).sort(),
    [materials]
  );

  async function handleCreate(payload: MaterialUpsertRequest): Promise<void> {
    setActionError(null);
    await createMaterial(payload);
    await reload();
  }

  async function handleEdit(payload: MaterialUpsertRequest): Promise<void> {
    if (!editingMaterial) {
      throw new Error("No hay material seleccionado para editar.");
    }

    setActionError(null);
    await updateMaterial(editingMaterial.id, payload);
    await reload();
    setEditingMaterial(null);
  }

  async function handleDelete(): Promise<void> {
    if (!materialToDelete) {
      return;
    }

    setDeleting(true);
    setActionError(null);
    try {
      await deleteMaterial(materialToDelete.id);
      await reload();
      setMaterialToDelete(null);
    } catch (err) {
      setActionError(err instanceof Error ? err.message : "No fue posible eliminar el material.");
    } finally {
      setDeleting(false);
    }
  }

  async function handleToggleActive(material: Material): Promise<void> {
    setActionError(null);

    const payload: MaterialUpsertRequest = {
      name: material.name,
      materialType: material.materialType,
      unitMeasure: material.unitMeasure,
      unitPrice: material.unitPrice,
      description: material.description ?? "",
      isActive: !material.isActive
    };

    try {
      await updateMaterial(material.id, payload);
      await reload();
    } catch (err) {
      setActionError(err instanceof Error ? err.message : "No fue posible actualizar el estado del material.");
    }
  }

  return (
    <section className="space-y-5">
      <div className="rounded-xl bg-white p-5 shadow-sm ring-1 ring-slate-200">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Dashboard de materiales</h2>
            <p className="text-sm text-slate-600">Administra catálogo, precios y estado operativo.</p>
          </div>
          <button
            type="button"
            onClick={() => setIsCreateOpen(true)}
            className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700"
          >
            Nuevo material
          </button>
        </div>

        <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-3">
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            className="rounded-md border border-slate-300 px-3 py-2 text-sm"
            placeholder="Buscar por nombre, tipo o unidad"
          />

          <select
            value={typeFilter}
            onChange={(event) => setTypeFilter(event.target.value as MaterialType | "ALL")}
            className="rounded-md border border-slate-300 px-3 py-2 text-sm"
          >
            <option value="ALL">Todas las categorías</option>
            {materialTypes.map((type) => (
              <option key={type} value={type}>
                {type.replace(/_/g, " ")}
              </option>
            ))}
          </select>

          <select
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value as "ALL" | "ACTIVE" | "INACTIVE")}
            className="rounded-md border border-slate-300 px-3 py-2 text-sm"
          >
            <option value="ALL">Todos los estados</option>
            <option value="ACTIVE">Activos</option>
            <option value="INACTIVE">Inactivos</option>
          </select>
        </div>
      </div>

      {actionError ? <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{actionError}</div> : null}

      <div className="overflow-hidden rounded-xl bg-white shadow-sm ring-1 ring-slate-200">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Material</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Categoría</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Unidad</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Precio</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Estado</th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-sm text-slate-500">
                    Cargando materiales...
                  </td>
                </tr>
              ) : error ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-sm text-red-600">
                    Error al cargar materiales: {error}
                  </td>
                </tr>
              ) : filteredMaterials.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-sm text-slate-500">
                    No hay materiales para los filtros seleccionados.
                  </td>
                </tr>
              ) : (
                filteredMaterials.map((material) => (
                  <tr key={material.id}>
                    <td className="px-4 py-3">
                      <p className="font-medium text-slate-900">{material.name}</p>
                      <p className="text-xs text-slate-500">{material.description || "Sin descripción"}</p>
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-700">{material.materialType.replace(/_/g, " ")}</td>
                    <td className="px-4 py-3 text-sm text-slate-700">{material.unitMeasure}</td>
                    <td className="px-4 py-3 text-sm text-slate-700">${material.unitPrice.toFixed(2)}</td>
                    <td className="px-4 py-3">
                      <MaterialStatusBadge active={material.isActive} />
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex justify-end gap-2">
                        <button
                          type="button"
                          onClick={() => void handleToggleActive(material)}
                          className="rounded-md border border-slate-300 px-2.5 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
                        >
                          {material.isActive ? "Desactivar" : "Activar"}
                        </button>
                        <button
                          type="button"
                          onClick={() => setEditingMaterial(material)}
                          className="rounded-md border border-blue-200 bg-blue-50 px-2.5 py-1.5 text-xs font-medium text-blue-700 hover:bg-blue-100"
                        >
                          Editar
                        </button>
                        <button
                          type="button"
                          onClick={() => setMaterialToDelete(material)}
                          className="rounded-md border border-red-200 bg-red-50 px-2.5 py-1.5 text-xs font-medium text-red-700 hover:bg-red-100"
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <MaterialFormModal open={isCreateOpen} mode="create" onClose={() => setIsCreateOpen(false)} onSubmit={handleCreate} />

      <MaterialFormModal
        open={Boolean(editingMaterial)}
        mode="edit"
        initialMaterial={editingMaterial}
        onClose={() => setEditingMaterial(null)}
        onSubmit={handleEdit}
      />

      <DeleteMaterialDialog
        material={materialToDelete}
        deleting={deleting}
        onCancel={() => setMaterialToDelete(null)}
        onConfirm={handleDelete}
      />
    </section>
  );
}
