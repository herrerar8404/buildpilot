import { useCallback, useEffect, useState } from "react";
import { getProjects } from "../services/projectService";
import type { Project } from "../types/project";

type UseProjectsResult = {
  projects: Project[];
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
};

export function useProjects(): UseProjectsResult {
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);

    try {
      const result = await getProjects();
      setProjects(result);
    } catch (err) {
      setProjects([]);
      setError(err instanceof Error ? err.message : "No se pudieron cargar los proyectos.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void reload();
  }, [reload]);

  return { projects, loading, error, reload };
}

