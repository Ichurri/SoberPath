# Fase 11 - Conectividad con Retrofit (opcional)

## Decision
En esta entrega no se integra Retrofit. Room y Remote Config ya cubren el almacenamiento local y la configuracion remota necesaria para el proyecto universitario.

## Como se podria agregar despues
1. Agregar dependencias de Retrofit y OkHttp.
2. Crear interfaz `MotivationApi` con un endpoint publico o simulado.
3. Crear DTOs en `data/remote/dto/`.
4. Crear mapper DTO -> domain.
5. Implementar `MotivationRemoteDataSource` y repositorio que combine remoto + local.

## Endpoint sugerido
- Un endpoint publico que devuelva frases motivacionales o un mock local en JSON.

## Archivos que se crearian
- `app/src/main/java/com/santiago/soberpath/data/remote/api/MotivationApi.kt`
- `app/src/main/java/com/santiago/soberpath/data/remote/dto/MotivationQuoteDto.kt`
- `app/src/main/java/com/santiago/soberpath/data/remote/source/MotivationRemoteDataSource.kt`
- `app/src/main/java/com/santiago/soberpath/data/mapper/MotivationQuoteMapper.kt`

## Notas
- La app no debe depender completamente de internet.
- Si la API falla, se usa fallback local.

