# Fase 1 - Configuracion base del proyecto

## Dependencias agregadas
- Jetpack Compose (BOM, UI, Material 3, tooling).
- Lifecycle: runtime, viewmodel, viewmodel-compose y runtime-compose.
- Navigation Compose.
- Coroutines (core y android).
- Koin (android y androidx-compose).
- Room (runtime, ktx y compiler con KSP).
- Firebase BOM + Remote Config + Messaging.
- WorkManager (work-runtime-ktx).
- DataStore Preferences.
- Plugins: Google Services y KSP (App Distribution se aplicara en la Fase 9).

## Para que sirve cada dependencia
- Compose / Material 3: UI declarativa.
- Lifecycle + ViewModel: estados con StateFlow y ciclo de vida.
- Navigation Compose: navegacion entre pantallas.
- Coroutines: asincronia y manejo de hilos.
- Koin: inyeccion de dependencias.
- Room: base de datos local.
- Firebase Remote Config: configuracion remota sin actualizar app.
- Firebase Messaging: soporte basico para notificaciones push.
- WorkManager: tareas diferidas y recordatorios.
- DataStore: preferencias simples y seguras.
- KSP: generacion de codigo para Room sin kapt.
- App Distribution: distribucion de builds internas.

## Como sincronizar Gradle
1. Abrir el proyecto en Android Studio.
2. Ejecutar "Sync Project with Gradle Files".
3. Verificar que no existan errores en la pestaña Build.

## Como ejecutar la app
1. Seleccionar un dispositivo o emulador.
2. Presionar Run.

## Errores comunes de sync y solucion
- Falta de google-services.json: agregarlo en `app/` si se usa Firebase.
- Versiones incompatibles: ajustar versiones en `gradle/libs.versions.toml`.
- Cache corrupta: "Invalidate Caches / Restart".

## Notas
- Se habilito `android.disallowKotlinSourceSets=false` para compatibilidad de KSP con Kotlin integrado en AGP.

## Notas de seguridad
- No incluir archivos con credenciales reales en el repositorio.
- Cualquier configuracion sensible debe ir en archivos ignorados por Git.



