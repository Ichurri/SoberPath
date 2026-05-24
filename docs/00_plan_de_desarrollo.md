# Plan de desarrollo - SoberPath

## Descripcion de la app
SoberPath es una app Android para registrar y dar seguimiento a habitos dificiles de abandonar (alcohol, cigarro, redes sociales, videojuegos, apuestas u otros). Permite al usuario ver el tiempo desde la ultima recaida, realizar compromisos diarios, registrar un diario emocional, recibir recordatorios, visualizar logros y mantener motivaciones personales.

## Funcionalidades principales
- Crear y gestionar un habito principal.
- Contador de tiempo desde la ultima recaida.
- Registro de recaidas.
- Check-in diario con estado de animo, nivel de tentacion y nota.
- Motivaciones personales.
- Logros (milestones) por dias.
- Recordatorios locales y soporte basico de FCM.
- Configuracion remota (Remote Config) para mensajes y banderas.
- Localizacion con recursos en strings.xml.

## Tecnologias usadas
- Kotlin + Android nativo.
- Jetpack Compose + Material 3.
- Clean Architecture (data, domain, presentation).
- MVVM + MVI (StateFlow, Intent, Effect).
- Room (persistencia local).
- Koin (inyeccion de dependencias).
- Firebase Remote Config.
- Firebase Cloud Messaging.
- Firebase App Distribution.
- WorkManager (recordatorios programados).

## Arquitectura propuesta
- domain: modelos puros, repositorios (interfaces) y casos de uso.
- data: implementaciones de repositorio, Room, data sources remotos y mappers.
- presentation: UI Compose, navegacion, ViewModels MVI y estados.
- di: modulos Koin.
- notification: manejo de notificaciones locales y FCM.
- util: helpers, validaciones, fechas.

## Estructura de carpetas (objetivo)
app/src/main/java/com/santiago/soberpath/
- data/
  - local/
  - remote/
  - repository/
  - mapper/
- domain/
  - model/
  - repository/
  - usecase/
- presentation/
  - navigation/
  - screen/
  - component/
  - state/
  - theme/
- di/
- notification/
- util/

## Flujo de navegacion (alto nivel)
- Onboarding -> Home
- Home -> DailyCheckIn
- Home -> Motivation
- Home -> Milestones
- Home -> Settings

## Modelo de datos inicial (domain)
- Habit(id, name, category, startDate, lastRelapseDate, dailyCost, currency, isActive)
- DailyCheckIn(id, habitId, date, mood, cravingLevel, note, completedPledge)
- MotivationReason(id, habitId, text, createdAt)
- Milestone(id, title, daysRequired, achieved)
- AppConfig(dailyReminderEnabled, dailyReminderHour, remoteMessage, emergencyTipsEnabled, minSupportedVersion)

## Plan de commits por fases
- Fase 0: docs: agregar plan inicial del proyecto
- Fase 1: chore: configurar proyecto base
- Fase 2: feat: crear capa de dominio
- Fase 3: feat: agregar persistencia local con Room
- Fase 4: feat: configurar inyeccion con Koin
- Fase 5: feat: crear interfaz y navegacion base
- Fase 6: feat: implementar MVVM MVI
- Fase 7: feat: integrar Firebase Remote Config
- Fase 8: feat: agregar notificaciones
- Fase 9: chore: configurar Firebase App Distribution
- Fase 10: feat: agregar localizacion de recursos
- Fase 11: docs: documentar conectividad opcional (o feat si se implementa)
- Fase 12: feat: completar funciones principales
- Fase 13: docs: agregar checklist de pruebas
- Fase 14: refactor: limpiar estructura final

## Riesgos tecnicos
- Compatibilidad de versiones entre Gradle, Compose, Koin y Room.
- Permisos de notificaciones en Android 13+.
- Manejo correcto de fechas con java.time.
- Sincronizacion de Remote Config sin bloquear UI.
- Evitar dependencias cruzadas entre capas.

## Partes que requieren configuracion manual
- Firebase Console: proyecto, registro de app, Remote Config, FCM, App Distribution.
- Archivo app/google-services.json (no subir credenciales reales al repo publico).
- Permisos y notificaciones en AndroidManifest.xml.
- Localizacion: export/import de strings con Localise.biz o Loco.

