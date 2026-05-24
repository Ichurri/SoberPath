# SoberPath

## Descripcion corta
App Android para seguimiento de habitos dificiles de abandonar, con contador de tiempo, check-ins diarios, motivaciones y recordatorios.

## Requisitos tecnicos
- Android Studio (version compatible con el proyecto).
- Android SDK instalado.
- Kotlin.
- Gradle (wrapper incluido).

## Como ejecutar el proyecto
1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en un emulador o dispositivo.

## Arquitectura
- Clean Architecture: data, domain, presentation.
- MVVM + MVI con StateFlow, UiIntent y UiEffect.
- Inyeccion de dependencias con Koin.

## Estado actual del desarrollo
- Fase 0 completada: plan y documentacion inicial.
- Fase 1 completada: configuracion base del proyecto.
- Fase 2 completada: capa domain.
- Fase 3 completada: persistencia local con Room.
- Fase 4 completada: inyeccion de dependencias con Koin.
- Fase 5 completada: navegacion y UI base.
- Fase 6 completada: MVVM + MVI por pantalla.
- Fase 7 completada: Firebase Remote Config.
- Fase 8 completada: notificaciones locales y FCM.

## Funcionalidades implementadas
- Documentacion base del proyecto.
- Pantallas Compose base y navegacion.
- Estado MVI con ViewModels y flujos reactivos.
- Configuracion remota con defaults locales.
- Recordatorios locales y soporte basico FCM.

## Funcionalidades pendientes
- App Distribution.
- Notificaciones locales y localizacion.
- Logica de negocio y funcionalidades finales.
