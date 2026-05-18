export type ProjectStatus = "PENDING" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";

export interface Project {
  id: number;
  projectName: string;
  clientName: string;
  constructionType: string | null;
  landWidth: number | null;
  landLength: number | null;
  address: string | null;
  description: string | null;
  creationDate: string | null;
  status: ProjectStatus;
  roomCount: number;
}

export interface ProjectUpsertRequest {
  projectName: string;
  clientName: string;
  constructionType: string;
  landWidth: number;
  landLength: number;
  address: string;
  description: string;
  creationDate: string;
  status: ProjectStatus;
}

export interface ProjectFormValues {
  projectName: string;
  clientName: string;
  constructionType: string;
  landWidth: string;
  landLength: string;
  address: string;
  description: string;
  creationDate: string;
  status: ProjectStatus;
}

