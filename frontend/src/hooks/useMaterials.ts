import { useCallback, useEffect, useState } from "react";
import { getMaterials } from "../services/materialService";
import type { Material } from "../types/material";

type UseMaterialsResult = {
  materials: Material[];
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
};

export function useMaterials(): UseMaterialsResult {
  const [materials, setMaterials] = useState<Material[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);

    try {
      const response = await getMaterials();
      setMaterials(response);
    } catch (err) {
      setMaterials([]);
      setError(err instanceof Error ? err.message : "No se pudieron cargar los materiales.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void reload();
  }, [reload]);

  return { materials, loading, error, reload };
}

