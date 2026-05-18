import { NavLink, Outlet } from "react-router-dom";

function buildLinkClass(isActive: boolean): string {
  return isActive
    ? "rounded-md bg-blue-50 px-3 py-2 text-sm font-semibold text-brand-700"
    : "rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 hover:text-slate-900";
}

export function AppLayout() {
  return (
    <div className="min-h-screen bg-slate-100">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex w-full max-w-7xl items-center justify-between px-6 py-4 md:px-10">
          <div>
            <h1 className="text-xl font-bold text-slate-900">BuildPilot</h1>
            <p className="text-xs text-slate-500">Panel administrativo</p>
          </div>

          <nav className="flex flex-wrap gap-2">
            <NavLink to="/dashboard" className={({ isActive }) => buildLinkClass(isActive)}>
              Dashboard
            </NavLink>
            <NavLink to="/floor-plan" className={({ isActive }) => buildLinkClass(isActive)}>
              Plano
            </NavLink>
            <NavLink to="/materials" className={({ isActive }) => buildLinkClass(isActive)}>
              Materiales
            </NavLink>
            <NavLink to="/quotations" className={({ isActive }) => buildLinkClass(isActive)}>
              Cotizaciones
            </NavLink>
          </nav>
        </div>
      </header>

      <main className="mx-auto w-full max-w-7xl px-6 py-6 md:px-10">
        <Outlet />
      </main>
    </div>
  );
}
