# Fase 10 - Localizacion de recursos

## Estructura de recursos
- `app/src/main/res/values/strings.xml` (ingles por defecto)
- `app/src/main/res/values-es/strings.xml` (espanol)

## Como agregar un nuevo texto
1. Agregar la clave en `values/strings.xml`.
2. Agregar la traduccion en `values-es/strings.xml`.
3. Actualizar `docs/localization/strings_reference.md`.

## Como agregar un nuevo idioma
1. Crear carpeta `values-<lang>`.
2. Copiar `strings.xml` y traducir.
3. Probar en un dispositivo con ese idioma.

## Compatibilidad con Localise.biz / Loco
- Las claves pueden importarse desde `strings.xml`.
- Las traducciones pueden exportarse como `strings.xml`.
- Si se usa API, no incluir tokens en el repositorio.

## Flujo manual recomendado
1. Exportar `strings.xml`.
2. Traducir en Localise.biz/Loco.
3. Reemplazar `values-es/strings.xml`.

## Como probar el cambio de idioma
1. Cambiar el idioma del dispositivo a Espanol.
2. Reiniciar la app.
3. Verificar textos en `Home` y `Settings`.

