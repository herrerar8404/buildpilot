import { Navigate, Route, Routes } from "react-router-dom";
import { AppLayout } from "./layouts/AppLayout";
import { FloorPlanPage } from "./pages/FloorPlanPage";
import { MaterialsPage } from "./pages/MaterialsPage";
import { ProjectsDashboardPage } from "./pages/ProjectsDashboardPage";
import { QuotationsPage } from "./pages/QuotationsPage";

export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/dashboard" element={<ProjectsDashboardPage />} />
        <Route path="/floor-plan" element={<FloorPlanPage />} />
        <Route path="/floor-plan/:projectId" element={<FloorPlanPage />} />
        <Route path="/materials" element={<MaterialsPage />} />
        <Route path="/quotations" element={<QuotationsPage />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Route>
    </Routes>
  );
}
