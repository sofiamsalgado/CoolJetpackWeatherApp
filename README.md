# Tutorial 3 — Secção 3: Cool Jetpack Weather App (MVVM + Compose)

**Curso:** Licenciatura em Engenharia Informática e Multimédia
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)
**Aluno:** Sofia — dam_a51694
**Repositório:** https://github.com/sofiamsalgado/CoolJetpackWeatherApp

---

## 1. Introdução

O objetivo desta secção foi reconstruir a aplicação CoolWeatherApp do Tutorial 2, adaptando-a para usar a arquitetura MVVM com Jetpack Compose em vez de layouts XML. Inclui suporte multilíngue (EN/PT), backgrounds de dia/noite com base no sunrise/sunset da API, location picker com Google Maps e localizações favoritas guardadas com SharedPreferences.

---

## 2. Descrição do Sistema

Aplicação Android que mostra informação meteorológica em tempo real para uma localização definida por latitude e longitude. Usa a API Open-Meteo (gratuita, sem chave).

Parâmetros obrigatórios exibidos:
- Temperatura
- Velocidade do vento
- Direção do vento
- Weather code (com ícone)
- Pressão ao nível do mar

Parâmetros adicionais implementados:
- Humidade relativa
- Probabilidade de precipitação

---

## 3. Arquitetura e Design

Padrão: **MVVM** com Jetpack Compose e StateFlow.

```
CoolJetpackWeatherApp/
└── app/src/main/
    ├── java/com/example/cooljetpackweatherapp/
    │   ├── data/
    │   │   ├── WeatherData.kt
    │   │   ├── WeatherApiClient.kt
    │   │   └── FavoritesManager.kt
    │   ├── ui/
    │   │   ├── WeatherUIState.kt
    │   │   ├── WeatherRow.kt
    │   │   ├── WeatherCard.kt
    │   │   ├── CoordinatesCard.kt
    │   │   ├── FavoritesBar.kt
    │   │   ├── WeatherScreen.kt
    │   │   └── LocationPickerActivity.kt
    │   ├── viewmodel/
    │   │   └── WeatherViewModel.kt
    │   └── MainActivity.kt
    └── res/
        ├── values/strings.xml
        ├── values-pt/strings.xml
        └── drawable/ (ícones do tempo + backgrounds)
```

Fluxo de dados:
```
UI (Composables) ←→ WeatherViewModel ←→ WeatherApiClient ←→ Open-Meteo API
```

---

## 4. Implementação

### 3.1 — Data Layer

**WeatherData.kt** — data classes com `@Serializable`:

```kotlin
@Serializable
data class WeatherData(
    val current_weather: CurrentWeather,
    val hourly: HourlyData,
    val daily: Daily
)
```

**WeatherApiClient.kt** — cliente Ktor com ContentNegotiation e JSON:

```kotlin
object WeatherApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }) // Ignores extra JSON fields
        }
    }

    suspend fun getWeather(lat: Float, lon: Float): WeatherData? {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${lon}&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m,relativehumidity_2m,precipitation_probability&")
            append("daily=sunrise,sunset&")
            append("timezone=auto")
        }
        System.out.println("Getting URL: $reqString")
        return try {
            client.get(reqString).body() // Parses JSON into WeatherData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
```

### 3.2 — User Interface

**WeatherUIState.kt** — estado da UI:

```kotlin
data class WeatherUIState(
    val latitude: Float = 38.7223f,
    val longitude: Float = 9.1393f,
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0f,
    val humidity: Int = 0,
    val precipitationProbability: Int = 0,
    val time: String = "",
    val locationName: String = "",
    val favorites: List<FavoriteLocation> = emptyList(),
    val day: Boolean = true
)
```

**WeatherScreen.kt** — skeleton do enunciado implementado com portrait e landscape. O cálculo de dia/noite usa sunrise/sunset da API:

```kotlin
val timePart = it.current_weather.time.substring(11, 16)
val sunrisePart = it.daily.sunrise[0].substring(11, 16)
val sunsetPart = it.daily.sunset[0].substring(11, 16)
val isDay = timePart in sunrisePart..sunsetPart
```

O background muda automaticamente com base no valor `day`:

- Portrait dia: `sunny_bg.png`
- Portrait noite: `night_bg.png`
- Landscape dia: `sunny_bg_land.png`
- Landscape noite: `night_bg_land.png`

**CoordinatesCard.kt** — card com campos de latitude/longitude e ícone de globo (`Icons.Default.Public`) que abre o `LocationPickerActivity`.

**WeatherCard.kt** — card com todas as linhas de informação meteorológica usando `WeatherRow`.

**FavoritesBar.kt** — lista horizontal scrollável com localizações favoritas e botão para guardar a localização atual.

### 3.3 — ViewModel

**WeatherViewModel.kt** — usa `AndroidViewModel` para aceder ao contexto (necessário para o `FavoritesManager`):

- `fetchWeather()` — chama a API e atualiza o `_uiState`
- `updateLatitude()` / `updateLongitude()` — atualiza as coordenadas
- `addFavorite()` — guarda a localização atual com um nome
- `selectFavorite()` — carrega uma localização favorita e faz fetch

### 3.4 — Multilanguage

Strings definidas em:
- `res/values/strings.xml` — inglês
- `res/values-pt/strings.xml` — português

Todas as strings da UI usam `stringResource(R.string.xxx)` — sem strings hardcoded.

### 3.5 — Funcionalidades Opcionais

**Location Picker** — `LocationPickerActivity` com Google Maps SDK. O utilizador clica no mapa e as coordenadas são devolvidas ao `WeatherScreen` via `registerForActivityResult`.

**Favorite named locations** — `FavoritesManager` guarda as localizações em `SharedPreferences` com serialização JSON. A `FavoritesBar` mostra a lista horizontal e permite guardar e selecionar favoritos.

---

## 5. Screenshots

_Adicionar screenshots aqui após testar no telemóvel_

---

## 6. Instruções de Uso

1. Abrir o projeto no Android Studio
2. Adicionar a chave do Google Maps em `local.properties`:
   ```
   MAPS_API_KEY=a_tua_key_aqui
   ```
3. Copiar os drawables do Tutorial 2 para `res/drawable/`
4. Correr no emulador ou telemóvel

---

## 7. Controlo de Versão

Commits por ordem:

- `3.1 added WeatherData with Serializable annotations and WeatherApiClient`
- `3.2 added UI composables and WeatherScreen`
- `3.3 added WeatherViewModel and updated MainActivity`
- `3.4 added multilanguage support EN and PT`
- `3.5 added LocationPickerActivity with Google Maps`
- `3.5 added FavoritesManager and FavoriteLocation data class`
- `3.5 updated WeatherViewModel with favorites support`
- `3.5 added FavoritesBar composable`
- `3.5 updated WeatherScreen with favorites and day/night support`

---

## 8. Dificuldades e Lições Aprendidas

- O `@Serializable` tem de estar em **todas** as data classes que são desserializadas pelo Ktor — incluindo as aninhadas.
- O `WeatherViewModel` teve de passar de `ViewModel` para `AndroidViewModel` para aceder ao contexto necessário para o `FavoritesManager` com `SharedPreferences`.
- O `kotlin-parcelize` causa conflitos — substituído por `Serializable` para passar dados entre Activities.
- O Google Search grounding no Gemini não está disponível no tier gratuito — foi necessário remover o bloco `tools` da chamada à API.

---

## 9. Melhorias Futuras

- Adicionar mais parâmetros meteorológicos (UV index, visibilidade).
- Implementar notificações para alertas meteorológicos.
- Adicionar widget para o ecrã inicial do telemóvel.

---

## 10. Declaração de Uso de IA (Obrigatório)

Esta tarefa foi realizada com **AC YES, AI NO** conforme indicado no enunciado.

A IA (Claude) foi consultada para:
- Resolver erros de compilação e configuração do Gradle
- Corrigir problemas com o namespace do módulo e o AndroidManifest
- Explicar como funciona o `StateFlow` vs `LiveData`
- Sugerir alternativas quando o `kotlin-parcelize` causava conflitos

Todo o código foi escrito e validado manualmente. O código gerado foi sempre revisto, compreendido e adaptado antes de ser usado.
