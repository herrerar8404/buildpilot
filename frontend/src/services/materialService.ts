import type { ApiErrorResponse } from "../types/floorPlan";
import type { Material, MaterialUpsertRequest } from "../types/material";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

async function parseError(response: Response): Promise<string> {
  let errorMessage = `Request failed with status ${response.status}`;

  try {
    const payload = (await response.json()) as ApiErrorResponse;
    if (payload.message) {
      errorMessage = payload.message;
    }
  } catch {
    // Keep fallback message.
  }

  return errorMessage;
}

async function requestJson<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, init);
  if (!response.ok) {
    throw new Error(await parseError(response));
  }
  return (await response.json()) as T;
}

export async function getMaterials(): Promise<Material[]> {
  return requestJson<Material[]>(`${API_BASE_URL}/api/v1/materials`, {
    method: "GET",
    headers: { Accept: "application/json" }
  });
}

export async function createMaterial(payload: MaterialUpsertRequest): Promise<Material> {
  return requestJson<Material>(`${API_BASE_URL}/api/v1/materials`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function updateMaterial(materialId: number, payload: MaterialUpsertRequest): Promise<Material> {
  return requestJson<Material>(`${API_BASE_URL}/api/v1/materials/${materialId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function deleteMaterial(materialId: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/api/v1/materials/${materialId}`, { method: "DELETE" });
  if (!response.ok) {
    throw new Error(await parseError(response));
  }
}

