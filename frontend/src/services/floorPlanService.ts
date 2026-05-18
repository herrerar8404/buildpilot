import type { ApiErrorResponse, FloorPlanResponse, RoomUpsertRequest } from "../types/floorPlan";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

async function parseError(response: Response): Promise<string> {
  let errorMessage = `Request failed with status ${response.status}`;

  try {
    const errorPayload = (await response.json()) as ApiErrorResponse;
    if (errorPayload.message) {
      errorMessage = errorPayload.message;
    }
  } catch {
    // Keep default message when backend does not return JSON.
  }

  return errorMessage;
}

async function requestJson<T>(input: RequestInfo | URL, init?: RequestInit): Promise<T> {
  const response = await fetch(input, init);

  if (!response.ok) {
    throw new Error(await parseError(response));
  }

  return (await response.json()) as T;
}

export async function getFloorPlan(projectId: number): Promise<FloorPlanResponse> {
  return requestJson<FloorPlanResponse>(`${API_BASE_URL}/api/v1/projects/${projectId}/floor-plan`, {
    method: "GET",
    headers: {
      Accept: "application/json"
    }
  });
}

export async function createRoom(projectId: number, payload: RoomUpsertRequest): Promise<void> {
  await requestJson(`${API_BASE_URL}/api/v1/projects/${projectId}/rooms`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function updateRoom(roomId: number, payload: RoomUpsertRequest): Promise<void> {
  await requestJson(`${API_BASE_URL}/api/v1/rooms/${roomId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(payload)
  });
}

export async function deleteRoom(roomId: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/api/v1/rooms/${roomId}`, {
    method: "DELETE"
  });

  if (!response.ok) {
    throw new Error(await parseError(response));
  }
}
