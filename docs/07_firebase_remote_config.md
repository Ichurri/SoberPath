# Fase 7 - Firebase Remote Config

## Configuracion en Firebase Console
1. Crear un proyecto en Firebase Console.
2. Registrar la app Android con el package `com.santiago.soberpath`.
3. Descargar `google-services.json`.
4. Colocar `google-services.json` en `app/` (no subirlo al repositorio).
5. Verificar el plugin de Google Services en `app/build.gradle.kts`.
6. Sincronizar Gradle en Android Studio.
7. Ejecutar la app para validar que no hay errores de inicializacion.

## Parametros sugeridos
- `remote_message` (String)
- `emergency_tips_enabled` (Boolean)
- `daily_reminder_enabled_default` (Boolean)
- `min_supported_version` (String o Number)
- `motivational_quote` (String)
- `show_milestone_animation` (Boolean)
- `checkin_required` (Boolean)

## Defaults locales
- Se definen en `FirebaseRemoteConfigDataSource` para que la app funcione sin internet.
- La UI usa fallback local si `motivational_quote` o `remote_message` vienen vacios.

## Como probar cambios remotos
1. Cambiar un parametro en Firebase Console.
2. Publicar el cambio.
3. Abrir la app y navegar a Home para ver el mensaje actualizado.

## Fallback local
- Si falla la descarga, se usan los defaults locales y la app no se bloquea.
- La UI muestra mensajes locales si Remote Config esta vacio.

