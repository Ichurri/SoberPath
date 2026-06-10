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

---

# Runbook paso a paso (entrega)

Este runbook cubre lo que requiere tu cuenta de Firebase y un dispositivo real. Lo que ya quedo
automatizado en el repo: el plugin de Gradle (activable con `-PenableAppDistribution=true`),
`app_distribution/release_notes.txt`, y `scripts/upload_app_distribution.sh` (que **autodetecta
el App ID** desde `app/google-services.json` para `com.santiago.soberpath`).

> Proyecto Firebase actual (segun `app/google-services.json`): **programovil-edd72**
> Paquete: **com.santiago.soberpath**

## 0. Requisitos
- Node + Firebase CLI: `npm install -g firebase-tools`
- Sesion iniciada: `firebase login`
- Acceso al proyecto Firebase `programovil-edd72` (o cambia el `google-services.json` por el de tu proyecto).

## 1. Habilitar App Distribution
1. Firebase Console > tu proyecto > **App Distribution** > Comenzar.
2. Selecciona la app Android `com.santiago.soberpath`.
3. Captura: `app_distribution/evidence/01_app_distribution_habilitado.png`

## 2. Crear el grupo de testers
- En consola: App Distribution > Testers y grupos > crear grupo **`internal-testers`** y agregar emails.
- O por CLI:
  - `firebase appdistribution:group:create internal-testers "Internal testers" --project programovil-edd72`
  - `firebase appdistribution:testers:add tester1@example.com --project programovil-edd72`
- Captura: `02_grupo_testers.png`

## 3. Generar el APK (debug o release)
- Debug: `./gradlew assembleDebug` -> `app/build/outputs/apk/debug/app-debug.apk`
- Release: `./gradlew assembleRelease` -> `app/build/outputs/apk/release/app-release.apk`
- Captura: `03_apk_generado.png`

## 4. Subir la build
Opcion A (recomendada, usa la CLI y autodetecta el App ID):
```
scripts/upload_app_distribution.sh
# o, para release:
scripts/upload_app_distribution.sh app/build/outputs/apk/release/app-release.apk
```
Opcion B (Gradle, si tu version de AGP lo soporta):
```
export FIREBASE_TOKEN=... # o usa firebaseAppDistributionServiceCredentialsFile
./gradlew assembleDebug appDistributionUploadDebug -PenableAppDistribution=true
```
- Captura: `04_subida_cli.png` y `05_release_en_consola.png`

## 5. Instalar desde la invitacion
1. El tester recibe un email de invitacion (captura `06_invitacion_email.png`).
2. Acepta, instala la app **App Tester** de Firebase y descarga SoberPath.
3. Instala en el dispositivo (puede pedir habilitar "fuentes desconocidas").
- Capturas: `07_instalacion_dispositivo.png` y `08_app_corriendo.png`

## 6. Checklist de entrega
- [ ] App Distribution habilitado para `com.santiago.soberpath`.
- [ ] Grupo `internal-testers` con >= 1 tester.
- [ ] APK/AAB generado (`BUILD SUCCESSFUL`).
- [ ] Build subida y visible en consola con sus release notes.
- [ ] Invitacion recibida por el tester.
- [ ] App instalada desde la invitacion en un dispositivo real.
- [ ] Capturas guardadas en `app_distribution/evidence/` (ver su README).

## 7. Demo final (guion sugerido)
1. Mostrar la release en Firebase Console (App Distribution).
2. Mostrar el email de invitacion y la instalacion en el dispositivo.
3. Abrir SoberPath: onboarding -> elegir idioma -> crear seguimiento -> Home con contador.
4. Cambiar idioma en Ajustes y mostrar que toda la app cambia (ES/EN).
5. Mostrar `./gradlew testDebugUnitTest` en verde como evidencia de pruebas.


