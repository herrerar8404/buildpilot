type MaterialStatusBadgeProps = {
  active: boolean;
};

export function MaterialStatusBadge({ active }: MaterialStatusBadgeProps) {
  if (active) {
    return <span className="rounded-full border border-emerald-200 bg-emerald-50 px-2 py-0.5 text-xs font-semibold text-emerald-700">Activo</span>;
  }

  return <span className="rounded-full border border-slate-200 bg-slate-100 px-2 py-0.5 text-xs font-semibold text-slate-600">Inactivo</span>;
}

