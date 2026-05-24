# Fase 3 - Room database

## Tablas
- habits: informacion del habito activo.
- daily_check_ins: check-ins diarios por habito.
- motivation_reasons: razones personales por habito.
- milestones: logros por dias sin recaida.

## Relacion con modelos de dominio
- HabitEntity -> Habit
- DailyCheckInEntity -> DailyCheckIn
- MotivationReasonEntity -> MotivationReason
- MilestoneEntity -> Milestone

## Inicializacion de milestones
Se insertan automaticamente al crear la base de datos con los dias:
1, 3, 7, 14, 30, 60, 90.

## Como inspeccionar o probar datos
- Usar Database Inspector en Android Studio.
- Ejecutar la app, crear datos y revisar tablas en tiempo real.

## Notas
- Las entidades Room no se exponen fuera de la capa data.
- Los repositorios de domain usan interfaces y no conocen Room.

