# Fase 6 - MVVM + MVI por pantalla

## Diferencia entre MVVM y MVI en este proyecto
- MVVM: los ViewModels coordinan casos de uso y exponen estado.
- MVI: cada pantalla tiene UiState, UiIntent y UiEffect para modelar entradas, estado y efectos unicos.

## Modelado de UiState
- Contiene el estado completo que la UI necesita renderizar.
- Se expone como StateFlow desde el ViewModel.
- Evita mutaciones directas en la UI.

## Modelado de UiIntent
- Representa acciones del usuario o eventos de UI.
- Se procesa en el ViewModel y actualiza el estado o dispara efectos.

## Modelado de UiEffect
- Efectos de unica ocurrencia: navegacion, snackbars o mensajes.
- Se expone como SharedFlow y se consume con LaunchedEffect.

## Ejemplo concreto (HomeScreen)
- UiState: progreso, ahorro, mensaje motivacional y si hay habito activo.
- UiIntent: taps en botones (check-in, motivaciones, milestones, ajustes).
- UiEffect: navegacion a pantallas y mensajes de feedback.

## Estructura usada
Cada pantalla se divide en:
- Contract (UiState / UiIntent / UiEffect).
- ViewModel (StateFlow + SharedFlow).
- Screen composable (renderiza estado y envia intents).

