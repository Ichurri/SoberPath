# Fase 4 - Inyeccion de dependencias con Koin

## Que es Koin
Koin es un framework de inyeccion de dependencias para Kotlin que permite declarar modulos y resolver objetos sin boilerplate.

## Modulos
- DatabaseModule: crea Room y DAOs.
- RepositoryModule: implementaciones concretas de repositorios.
- UseCaseModule: casos de uso.
- ViewModelModule: ViewModels (se agregaran en la Fase 6).
- FirebaseModule: dependencias Firebase (Fase 7).
- NotificationModule: dependencias de notificaciones (Fase 8).

## Como se conectan las capas
- presentation depende de use cases.
- domain expone modelos y repositorios (interfaces).
- data implementa repositorios y usa Room.

## Como agregar una nueva dependencia
1. Crear o ubicar el modulo correspondiente en `app/src/main/java/com/santiago/soberpath/di`.
2. Declarar la dependencia con `single` o `factory`.
3. Inyectar en el constructor donde corresponda.
4. Actualizar el modulo en `SoberPathApp` si es necesario.

