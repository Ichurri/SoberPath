# Fase 8 - Notificaciones locales y FCM

## Diferencia entre notificacion local y FCM
- Local: se programa en el dispositivo con WorkManager.
- FCM: llega desde Firebase Cloud Messaging y se muestra con NotificationHelper.

## Notificaciones locales
- Se usa WorkManager con `DailyReminderWorker`.
- Se crea un canal en `NotificationHelper`.
- El recordatorio se programa como trabajo periodico cada 24h.

## Permisos (Android 13+)
- Se solicita `POST_NOTIFICATIONS` cuando el usuario activa recordatorios.
- Si el usuario niega el permiso, se muestra un mensaje y no se programa el recordatorio.

## Como probar notificacion local
1. Ejecutar la app en debug.
2. Ir a Settings.
3. Activar recordatorio diario y conceder permiso.
4. Usar el boton de refresh para validar flujo.

## FCM (Firebase Cloud Messaging)
- Se agrega `SoberPathFirebaseMessagingService`.
- Al recibir un mensaje, se crea una notificacion local.

## Como probar FCM
1. Firebase Console > Cloud Messaging.
2. Crear una campana de prueba.
3. Enviar a la app registrada.
4. Verificar la notificacion en el dispositivo.

## Limitaciones sin backend
- El token no se envia a servidor.
- Solo se prueba desde Firebase Console.

