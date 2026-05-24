// Normaliza textos de búsqueda para comparar enums crudos, labels visibles y texto libre de forma consistente.
export function normalizeSearchText(value) {
  return String(value ?? '')
    .replaceAll('_', ' ')
    .toLowerCase()
    .trim()
    .replace(/\s+/g, ' ');
}

// Construye un texto indexable a partir de varios campos, ignorando valores vacíos o nulos.
export function buildSearchIndex(values) {
  return values
    .filter((value) => value !== null && value !== undefined && value !== '')
    .map((value) => normalizeSearchText(value))
    .join(' ');
}

// Verifica si un índice normalizado contiene la consulta normalizada del usuario.
export function matchesSearch(index, query) {
  const normalizedQuery = normalizeSearchText(query);
  return !normalizedQuery || normalizeSearchText(index).includes(normalizedQuery);
}
