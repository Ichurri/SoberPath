# Fase 12 - Funcionalidades principales

## Progreso y contador
- El tiempo desde la ultima recaida se calcula con `java.time` usando la fecha `lastRelapseDate`.
- El contador se expresa en dias, horas y minutos.

## Registro de recaida
- Al registrar una recaida se actualiza `lastRelapseDate` con la fecha actual.
- El contador se reinicia y el ahorro vuelve a cero.

## Ahorro estimado
- `ahorro = dias_sin_recaida * costo_diario`.
- Se formatea con dos decimales y la moneda configurada.

## Check-in diario
- Se guarda el estado de animo, nivel de tentacion, nota y compromiso.
- Se muestra un historial simple en Home con los ultimos 3 check-ins.

## Motivaciones
- Se guardan razones personales en Room.
- Se muestran en la pantalla de motivaciones.

## Milestones
- Los milestones se marcan como alcanzados si `dias_sin_recaida >= daysRequired`.
- Se calcula en el repositorio de habitos a partir de la fecha de recaida.

## Decisiones de diseno
- La logica esta en repositorios y casos de uso.
- Los ViewModels solo coordinan y exponen estado.
- La UI solo renderiza estado y envia intents.

