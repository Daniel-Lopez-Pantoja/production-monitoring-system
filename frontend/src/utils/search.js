// Normaliza textos de búsqueda para comparar enums crudos, labels visibles y texto libre de forma consistente.
export function normalizeSearchText(value) {
  return String(value ?? '')
    .replaceAll('_', ' ')
    .toLowerCase()
    .trim()
    .replace(/\s+/g, ' ');
}

// Normaliza enums y badges para comparar valores crudos como IN_PROGRESS con textos visibles como "in progress".
export function normalizeEnumText(value) {
  return normalizeSearchText(value);
}

// Normaliza valores tipo N/A permitiendo que "NA", "n/a" y "N/A" coincidan sin perder la barra visual.
export function normalizeNullableDisplayText(value) {
  const normalized = normalizeSearchText(value);
  return normalized === 'n/a' || normalized === 'na' ? 'n/a' : normalized;
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
  const normalizedQuery = normalizeNullableDisplayText(query);
  return !normalizedQuery || normalizeSearchText(index).includes(normalizedQuery);
}

// Compara un query contra un enum exacto, aceptando formato crudo y formato visible.
export function matchesExactEnum(query, enumValue) {
  const normalizedQuery = normalizeEnumText(query);
  const normalizedEnum = normalizeEnumText(enumValue);
  return Boolean(normalizedQuery) && normalizedQuery === normalizedEnum;
}
