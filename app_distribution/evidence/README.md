# Evidencia - Firebase App Distribution

Coloca aquí las capturas que demuestran el flujo completo de distribución. Sugerencia de
nombres (numerados para que queden ordenados):

| Archivo | Qué debe mostrar |
|--------|------------------|
| `01_app_distribution_habilitado.png` | Firebase Console > App Distribution con la app `com.santiago.soberpath` seleccionada. |
| `02_grupo_testers.png` | El grupo `internal-testers` creado con al menos 1 tester (email). |
| `03_apk_generado.png` | Terminal mostrando `./gradlew assembleDebug` con `BUILD SUCCESSFUL` y la ruta del APK. |
| `04_subida_cli.png` | Salida de `scripts/upload_app_distribution.sh` o de la Firebase CLI con la release subida. |
| `05_release_en_consola.png` | La release (v1.0 build 1) visible en App Distribution con sus release notes. |
| `06_invitacion_email.png` | El correo de invitación recibido por el tester. |
| `07_instalacion_dispositivo.png` | App Tester / descarga e instalación en el dispositivo de prueba. |
| `08_app_corriendo.png` | SoberPath abierta en el dispositivo (onboarding o Home). |

Cuando termines, marca el checklist en `docs/09_firebase_app_distribution.md` (sección
"Runbook") y enlaza estas imágenes en `docs/14_entrega_final.md`.
