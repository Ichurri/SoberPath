# Fase 2 - Capa domain

## Que es la capa domain
La capa domain contiene la logica de negocio pura y los modelos centrales del proyecto. No depende de Android, Room, Firebase ni Compose.

## Por que no depende de frameworks
- Permite probar la logica en aislamiento.
- Facilita el mantenimiento y la escalabilidad.
- Evita el acoplamiento con detalles de infraestructura.

## Modelos
- Habit
- DailyCheckIn
- MotivationReason
- Milestone
- AppConfig
- SobrietyProgress

## Repositorios (interfaces)
- HabitRepository
- CheckInRepository
- MotivationRepository
- ConfigRepository

## Casos de uso
- CreateHabitUseCase
- GetActiveHabitUseCase
- RegisterRelapseUseCase
- GetSobrietyProgressUseCase
- SaveDailyCheckInUseCase
- GetDailyCheckInsUseCase
- AddMotivationReasonUseCase
- GetMotivationReasonsUseCase
- GetMilestonesUseCase
- GetRemoteConfigUseCase

## Como se conecta con data y presentation
- data implementa los repositorios con Room y servicios remotos.
- presentation usa los casos de uso desde ViewModels.
- domain no conoce detalles de UI ni de persistencia.

