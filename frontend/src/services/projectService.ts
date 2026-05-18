import type { ApiErrorResponse } from "../types/floorPlan";
import type { Project, ProjectUpsertRequest } from "../types/project";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

async function parseError(response: Response): Promise<string> {
  let errorMessage = `Request failed with status ${response.status}`;

  try {
    const payload = (await response.json()) as ApiErrorResponse;
    if (payload.message) {
      errorMessage = payload.message;
    }
  } catch {
    // Keep default message.
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

export async function getProjects(): Promise<Project[]> {
  return requestJson<Project[]>(`${API_BASE_URL}/api/v1/projects`, {
    method: "GET",
    headers: {
      Accept: "application/json"
    }
  });
}

export async function createProject(payload: ProjectUpsertRequest): Promise<Project> {
  return requestJson<Project>(`${API_BASE_URL}/api/v1/projects`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function updateProject(projectId: number, payload: ProjectUpsertRequest): Promise<Project> {
  return requestJson<Project>(`${API_BASE_URL}/api/v1/projects/${projectId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function deleteProject(projectId: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/api/v1/projects/${projectId}`, {
    method: "DELETE"
  });

  if (!response.ok) {
    throw new Error(await parseError(response));
  }
}

