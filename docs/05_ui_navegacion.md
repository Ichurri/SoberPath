# Fase 5 - Navegacion y UI base

## Flujo de navegacion
- Onboarding: crea el habito inicial.
- Home: muestra el contador y accesos a check-in, motivaciones, milestones y ajustes.
- DailyCheckIn: captura el estado diario.
- Motivation: lista y agrega razones personales.
- Milestones: muestra logros alcanzados y pendientes.
- Settings: gestiona recordatorios e informacion basica.

## Pantallas
- OnboardingScreen: formulario base para crear habito.
- HomeScreen: resumen del progreso y acciones rapidas.
- DailyCheckInScreen: estado de animo, craving, nota y compromiso.
- MotivationScreen: lista de motivaciones y alta manual.
- MilestonesScreen: lista simple de logros.
- SettingsScreen: preferencia de recordatorio y version.

## Conexion UI - ViewModel
En esta fase las pantallas son composables base sin ViewModel. En la Fase 6 se conectaran a MVI con UiState, UiIntent y UiEffect.

## MVVM + MVI
- La UI renderizara estado desde StateFlow.
- Las acciones del usuario se enviaran como intents.
- Los efectos unicos (snackbar, navegacion) se manejaran con UiEffect.

