# BuildPilot Frontend MVP

React + TypeScript + Vite + Tailwind frontend for administrative project management and 2D floor-plan editing.

## Features

- Projects dashboard with list, status, land dimensions, and room counts
- Create, update, and delete projects with modal-based forms
- Open floor plan editor per project from dashboard
- Floor plan editor with SVG rendering, room list, create/edit/delete room flow
- Loading, empty state, and API error handling

## Main Routes

- `/dashboard` - projects administration
- `/floor-plan` - floor plan editor (manual project id)
- `/floor-plan/:projectId` - floor plan editor for a specific project
- `/materials` - placeholder module
- `/quotations` - placeholder module

## Project Structure

- `src/layouts/` app shell and top navigation
- `src/pages/` route pages
- `src/components/` reusable UI building blocks
- `src/components/projects/` project CRUD components
- `src/services/` API clients
- `src/hooks/` page-level business logic hooks
- `src/types/` TypeScript contracts

## Run

```powershell
cd C:\proyectos\buildpilot\frontend
npm install
npm run dev
```

Vite runs on `http://localhost:5173` and proxies `/api` to `http://localhost:8080`.

## Build

```powershell
cd C:\proyectos\buildpilot\frontend
npm run build
npm run preview
```

## Optional environment variable

Create `.env` if needed:

```ini
VITE_API_BASE_URL=http://localhost:8080
```
