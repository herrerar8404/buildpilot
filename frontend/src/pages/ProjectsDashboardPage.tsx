import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { DeleteProjectDialog } from "../components/projects/DeleteProjectDialog";
import { ProjectFormModal } from "../components/projects/ProjectFormModal";
import { ProjectStatusBadge } from "../components/projects/ProjectStatusBadge";
import { useProjects } from "../hooks/useProjects";
import { createProject, deleteProject, updateProject } from "../services/projectService";
import type { Project, ProjectUpsertRequest } from "../types/project";

export function ProjectsDashboardPage() {
  const { projects, loading, error, reload } = useProjects();
  const [editingProject, setEditingProject] = useState<Project | null>(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [projectToDelete, setProjectToDelete] = useState<Project | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const totalRooms = useMemo(() => projects.reduce((acc, project) => acc + project.roomCount, 0), [projects]);

  async function handleCreate(payload: ProjectUpsertRequest): Promise<void> {
    setActionError(null);
    await createProject(payload);
    await reload();
  }

  async function handleEdit(payload: ProjectUpsertRequest): Promise<void> {
    if (!editingProject) {
      throw new Error("No hay proyecto seleccionado para editar.");
    }

    setActionError(null);
    await updateProject(editingProject.id, payload);
    await reload();
    setEditingProject(null);
  }

  async function handleDelete(): Promise<void> {
    if (!projectToDelete) {
      return;
    }

    setDeleting(true);
    setActionError(null);
    try {
      await deleteProject(projectToDelete.id);
      await reload();
      setProjectToDelete(null);
    } catch (err) {
      setActionError(err instanceof Error ? err.message : "No fue posible eliminar el proyecto.");
    } finally {
      setDeleting(false);
    }
  }

  return (
    <section className="space-y-5">
      <div className="rounded-xl bg-white p-5 shadow-sm ring-1 ring-slate-200">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Dashboard de proyectos</h2>
            <p className="text-sm text-slate-600">Gestiona proyectos, clientes y avance de obra.</p>
          </div>
          <button
            type="button"
            onClick={() => setIsCreateOpen(true)}
            className="rounded-md bg-brand-600 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-700"
          >
            Nuevo proyecto
          </button>
        </div>

        <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-3">
          <div className="rounded-lg border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Proyectos totales</p>
            <p className="text-xl font-bold text-slate-900">{projects.length}</p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Habitaciones registradas</p>
            <p className="text-xl font-bold text-slate-900">{totalRooms}</p>
          </div>
          <div className="rounded-lg border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Estado de carga</p>
            <p className="text-xl font-bold text-slate-900">{loading ? "Sincronizando" : "Al día"}</p>
          </div>
        </div>
      </div>

      {actionError ? <div className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{actionError}</div> : null}

      <div className="overflow-hidden rounded-xl bg-white shadow-sm ring-1 ring-slate-200">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Proyecto</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Cliente</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Estatus</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Terreno</th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">Habitaciones</th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 bg-white">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-sm text-slate-500">
                    Cargando proyectos...
                  </td>
                </tr>
              ) : error ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-sm text-red-600">
                    Error al cargar proyectos: {error}
                  </td>
                </tr>
              ) : projects.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-sm text-slate-500">
                    No hay proyectos registrados. Crea uno nuevo para comenzar.
                  </td>
                </tr>
              ) : (
                projects.map((project) => (
                  <tr key={project.id}>
                    <td className="px-4 py-3">
                      <p className="font-medium text-slate-900">{project.projectName}</p>
                      <p className="text-xs text-slate-500">{project.constructionType || "Sin tipo"}</p>
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-700">{project.clientName}</td>
                    <td className="px-4 py-3">
                      <ProjectStatusBadge status={project.status} />
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-700">
                      {(project.landWidth ?? 0).toFixed(2)}m x {(project.landLength ?? 0).toFixed(2)}m
                    </td>
                    <td className="px-4 py-3 text-sm text-slate-700">{project.roomCount}</td>
                    <td className="px-4 py-3">
                      <div className="flex justify-end gap-2">
                        <Link
                          to={`/floor-plan/${project.id}`}
                          className="rounded-md border border-slate-300 px-2.5 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
                        >
                          Abrir plano
                        </Link>
                        <button
                          type="button"
                          onClick={() => setEditingProject(project)}
                          className="rounded-md border border-blue-200 bg-blue-50 px-2.5 py-1.5 text-xs font-medium text-blue-700 hover:bg-blue-100"
                        >
                          Editar
                        </button>
                        <button
                          type="button"
                          onClick={() => setProjectToDelete(project)}
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

      <ProjectFormModal
        open={isCreateOpen}
        mode="create"
        onClose={() => setIsCreateOpen(false)}
        onSubmit={handleCreate}
      />

      <ProjectFormModal
        open={Boolean(editingProject)}
        mode="edit"
        initialProject={editingProject}
        onClose={() => setEditingProject(null)}
        onSubmit={handleEdit}
      />

      <DeleteProjectDialog
        project={projectToDelete}
        deleting={deleting}
        onCancel={() => setProjectToDelete(null)}
        onConfirm={handleDelete}
      />
    </section>
  );
}

