import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FloorPlanViewer } from "../components/FloorPlanViewer";

function parseProjectId(value: string | undefined): number | null {
  if (!value) {
    return null;
  }

  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed > 0 ? parsed : null;
}

export function FloorPlanPage() {
  const params = useParams<{ projectId?: string }>();
  const navigate = useNavigate();

  const routeProjectId = parseProjectId(params.projectId);
  const [projectIdInput, setProjectIdInput] = useState<string>(params.projectId ?? "");
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(routeProjectId);

  useEffect(() => {
    setProjectIdInput(params.projectId ?? "");
    setSelectedProjectId(parseProjectId(params.projectId));
  }, [params.projectId]);

  function handleSubmit(event: FormEvent<HTMLFormElement>): void {
    event.preventDefault();

    const parsed = Number(projectIdInput);
    if (!Number.isInteger(parsed) || parsed <= 0) {
      setSelectedProjectId(null);
      return;
    }

    setSelectedProjectId(parsed);
    navigate(`/floor-plan/${parsed}`);
  }

  return (
    <section className="space-y-6">
      <header>
        <h2 className="text-2xl font-bold text-slate-900">Editor de plano 2D</h2>
        <p className="mt-1 text-sm text-slate-600">
          Selecciona un proyecto para visualizar y administrar habitaciones sobre el plano.
        </p>
      </header>

      <section className="rounded-xl bg-white p-4 shadow-sm ring-1 ring-slate-200">
        <form onSubmit={handleSubmit} className="flex flex-wrap items-end gap-3">
          <div className="w-full max-w-xs">
            <label htmlFor="projectId" className="mb-1 block text-sm font-medium text-slate-700">
              ID del proyecto
            </label>
            <input
              id="projectId"
              type="number"
              min={1}
              value={projectIdInput}
              onChange={(event) => setProjectIdInput(event.target.value)}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-slate-900 outline-none transition focus:border-brand-600 focus:ring-2 focus:ring-blue-100"
              placeholder="Ingresa un ID"
            />
          </div>

          <button
            type="submit"
            className="rounded-md bg-brand-600 px-4 py-2 font-medium text-white transition hover:bg-brand-700"
          >
            Abrir plano
          </button>
        </form>
      </section>

      <FloorPlanViewer projectId={selectedProjectId} />
    </section>
  );
}
