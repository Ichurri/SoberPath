# Fase 9 - Firebase App Distribution

## Que es Firebase App Distribution
Permite distribuir builds internas (APK/AAB) a testers sin publicar en Play Store.

## Configuracion en Firebase Console
1. Abrir tu proyecto en Firebase Console.
2. Ir a App Distribution y habilitarlo.
3. Agregar testers (emails) o grupos.

## Configuracion recomendada (Firebase CLI)
- Usar Firebase CLI evita incompatibilidades con el AGP actual.
- Se usa el APK generado por Gradle.
- Las notas de version estan en `app_distribution/release_notes.txt`.

## Autenticacion con Firebase CLI
1. Instalar Firebase CLI:
   - `npm install -g firebase-tools`
2. Iniciar sesion:
   - `firebase login`

## Distribuir una build
1. Generar el APK:
   - `./gradlew assembleDebug`
2. Subir con Firebase CLI:
   - `firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk --app <APP_ID> --release-notes-file app_distribution/release_notes.txt --groups <GRUPO>`
3. Alternativa con script:
   - `scripts/upload_app_distribution.sh app/build/outputs/apk/debug/app-debug.apk <APP_ID> <GRUPO>`

## Credenciales sensibles
- No subir tokens ni credenciales al repositorio.
- Si usas `FIREBASE_TOKEN`, configurarlo como variable de entorno.
- Alternativa: usar `firebaseAppDistributionServiceCredentialsFile` apuntando a un archivo local ignorado por Git.

## Verificacion
- Revisar que los testers reciban invitacion.
- Verificar que el link de descarga funcione.


