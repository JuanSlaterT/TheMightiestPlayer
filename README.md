<p align="center">
  <img src="docs/assets/the-mightiest-player-banner.png" alt="TheMightiestPlayer voxel competition arena" width="100%">
</p>

<p align="center">
  <img src="docs/assets/the-mightiest-player-icon.png" alt="TheMightiestPlayer trophy emblem" width="150">
</p>

<h1 align="center">TheMightiestPlayer</h1>

<p align="center">
  A configurable competition and timed-event system for Minecraft Spigot servers.
</p>

<p align="center">
  <a href="#english">English</a> · <a href="#español">Español</a>
</p>

---

<a id="english"></a>

## English

### About

TheMightiestPlayer is a Java plugin created for Minecraft servers running Spigot. It lets server owners define scheduled competitions in which players earn points by defeating other players, killing entities, or breaking blocks.

Each event is described in YAML and can have its own dates, scoring rules, gameplay filters, goal type, storage mode, menu presentation, and rewards. Player activity is captured by Bukkit listeners, converted into points, kept in memory, and persisted locally or in a shared MySQL database.

This repository is preserved as an archived project. Development took place in March 2021 and is no longer active. This document describes the feature set and source code contained in the repository.

### Features

- Scheduled events with automatic `NOT_STARTED`, `STARTED`, `FINISHED`, and `DISABLED` states.
- Individual and server-wide global goals.
- Configurable scoring and gameplay filters.
- Local YAML and shared MySQL storage modes.
- Per-player progress and reward-claim tracking.
- Configurable inventory GUI with a different item model for every event state.
- Minimum-point and event-finish rewards.
- Console commands, player commands, messages, sounds, and menu actions as rewards.
- PlaceholderAPI expansion for player and global progress.
- LeaderHeads-compatible placeholders for ranking displays.
- Administrative commands for inspecting event types and reloading configuration.

### How it works

```text
config.yml
    ↓
EventCreator parses each event definition
    ↓
EventManager determines its state from the configured dates
    ↓
A Bukkit listener captures the selected gameplay action
    ↓
Points are stored in the in-memory event database
    ↓
Progress is saved to YAML or MySQL
    ↓
The GUI and placeholders display the current progress
    ↓
Players claim configured rewards from the event menu
```

### Built-in event types

| Event type | Trigger | Filters |
| --- | --- | --- |
| `PLAYER_KILL` | A player kills another player | World, permission, held tool, tool name |
| `PLAYER_KILL_ENTITY` | A player kills a living entity | World, permission, held tool, tool name, entity type |
| `PLAYER_BREAK_BLOCK` | A player breaks a block | World, permission, held tool, tool name, block type |

Common event properties support individual values and semicolon-separated lists:

| Property | Meaning |
| --- | --- |
| `POINTS` | Points awarded for a valid action |
| `WORLD_NAME` / `WORLDS_NAME` | Required world or list of worlds |
| `PERMISSION` | Permission required to earn points |
| `TOOL` / `TOOLS` | Required held material or list of materials |
| `TOOL_NAME` / `TOOL_NAMES` | Required custom item name or list of names |
| `TOOL_CONTAIN` | Text contained in the held material name |
| `ENTITY_TYPE` / `ENTITY_TYPES` | Required entity type or list of types |
| `BLOCK_TYPE` / `BLOCK_TYPES` | Required block material or list of materials |
| `BLOCK_CONTAIN` / `BLOCK_CONTAINS` | Block-material name matching options |

The event type and its properties are written as a single value:

```yaml
EVENT_TYPE: "PLAYER_BREAK_BLOCK:WORLD_NAME=pvp,POINTS=1"
```

### Goals and storage

The plugin contains two goal models:

- `INDIVIDUAL`: every player has an independent score and completion state.
- `GLOBAL`: all contributions increase a shared score with a configurable `MAX_POINTS` target.

An event may also define a personal minimum-point requirement. Reaching it marks the player's progress as completed and enables its reward.

| Storage type | Model |
| --- | --- |
| `MULTIWORLD` | Local YAML files managed by the plugin, with one player file containing statistics for every event |
| `SHARED` | MySQL-backed event data intended to share individual progress between servers |

Local player data includes score, completion state, minimum-point reward state, and final-reward state. Global local-event progress is stored in `plugin-data.yml`.

### Event configuration

Events are declared below the `events` section of `config.yml`:

```yaml
events:
  'block-competition':
    event-specifications:
      TYPE: MULTIWORLD
      EVENT_TYPE: "PLAYER_BREAK_BLOCK:WORLD_NAME=pvp,POINTS=1"
      starts: '27-02-21_19:40'
      ends: '27-02-21_21:22'
      GOAL_TYPE: INDIVIDUAL

      min-points-reward:
        enabled: true
        min-points: 12
        ACTIONS:
          - 'CONSOLE|eco give %player_name% 1200'
          - 'MESSAGE|Reward claimed.'

      finish-event-reward:
        enabled: true
        ACTIONS:
          - 'CONSOLE|eco give %player_name% 5000'
          - 'MESSAGE|Final reward claimed.'

    gui-specifications:
      SLOT: 10
```

Dates use the `dd-MM-yy_HH:mm` format.

### Reward actions

Reward entries use the `ACTION|VALUE` format:

| Action | Behavior |
| --- | --- |
| `CONSOLE` | Dispatches a command as the server console |
| `PLAYER_COMMAND` | Makes the player execute a command |
| `MESSAGE` | Sends a formatted message to the player |
| `SOUND` | Plays a Bukkit sound for the player |
| `CLOSE` | Closes the player's inventory menu |

Messages, commands, item names, and lore support `&` color codes and installed PlaceholderAPI placeholders.

### Graphical menu

The base command opens an inventory GUI. Its title, rows, decorative items, slots, materials, names, lore, data values, amounts, enchantments, and click actions are loaded from `config.yml`.

Each event can define a visual representation for these states:

- `not-started-event`
- `started-but-not-completed`
- `started-completed`
- `ended-not-completed`
- `ended-completed`

The menu displays player progress, remaining time, completion state, global score, and ranking placeholders.

| Internal placeholder | Value |
| --- | --- |
| `%tmp-player-value%` | Current player's event points |
| `%date-starts%` | Time remaining until the event begins |
| `%date-ends%` | Time remaining until the event ends |
| `%tmp-global-value%` | Current global score |
| `%tmp-global-value-limited%` | Global score limited to the event target |

### PlaceholderAPI

When PlaceholderAPI is installed, the plugin registers the `TMP` expansion:

```text
%tmp_value_<event-id>%
%tmp_global_<event-id>%
%tmp_maxglobal_<event-id>%
```

These values can also be consumed by other plugins. The bundled configuration demonstrates their use with LeaderHeads ranking placeholders.

### Commands and permission

Primary command and aliases:

```text
/themightiestplayer
/mightiestplayer
/mp
/tmp
```

| Command | Description |
| --- | --- |
| `/tmp` | Opens the event GUI |
| `/tmp help` | Displays the plugin command list |
| `/tmp about <CustomEvent>` | Displays information about a loaded event type |
| `/tmp customEventList` | Lists the built-in event types |
| `/tmp reload` | Reloads configuration, data, menu definitions, and event managers |

Administrative subcommands use the `tmp.admin` permission.

### Project structure

```text
TheMightiestPlayer/
├── config.yml
├── plugin-data.yml
├── plugin.yml
├── pom.xml
└── src/hotdoctor/plugin/themightiestplayer/
    ├── Main.java
    ├── commands/
    ├── event_types/
    │   └── list/
    ├── extensions/
    ├── listeners/
    ├── managers/
    ├── progress/
    └── utils/
        └── databasetypes/
```

`Main` initializes configuration, commands, menus, event definitions, listeners, and the PlaceholderAPI expansion. `EventCreator` converts YAML definitions into `EventManager` instances. Event-type classes listen for Bukkit gameplay actions, while the database classes coordinate in-memory, YAML, and MySQL data.

### Original technology stack

- Java 8 and Maven
- Spigot API `1.14.4-R0.1-SNAPSHOT`
- PlaceholderAPI `2.10.9`
- HikariCP `3.4.1`
- SLF4J JDK14 `1.7.25`
- Optional PlaceholderAPI and LeaderHeads runtime integrations

The Maven configuration packages the project as `TheMightiestPlayer.jar` and shades HikariCP and SLF4J into the resulting artifact.

### License

The repository includes the GNU General Public License, version 3. See [LICENSE](LICENSE).

---

<a id="español"></a>

## Español

### Acerca del proyecto

TheMightiestPlayer es un plugin Java creado para servidores Minecraft que utilizan Spigot. Permite definir competiciones programadas en las que los jugadores obtienen puntos al derrotar a otros jugadores, eliminar entidades o romper bloques.

Cada evento se describe mediante YAML y puede tener sus propias fechas, reglas de puntuación, filtros de juego, tipo de objetivo, almacenamiento, presentación en el menú y recompensas. Los listeners de Bukkit capturan la actividad, la convierten en puntos y guardan el progreso localmente o en una base de datos MySQL compartida.

Este repositorio se conserva como un proyecto archivado. Su desarrollo tuvo lugar en marzo de 2021 y ya no se encuentra activo. Este documento describe las funciones y el código fuente contenidos en el repositorio.

### Funciones

- Eventos programados con estados automáticos `NOT_STARTED`, `STARTED`, `FINISHED` y `DISABLED`.
- Objetivos individuales y objetivos globales para todo el servidor.
- Puntuación y filtros de juego configurables.
- Almacenamiento mediante YAML local o MySQL compartido.
- Seguimiento del progreso y las recompensas de cada jugador.
- GUI de inventario configurable para cada estado del evento.
- Recompensas por puntuación mínima y por finalización.
- Comandos, mensajes, sonidos y acciones de menú como recompensas.
- Expansión de PlaceholderAPI para el progreso individual y global.
- Placeholders compatibles con LeaderHeads para clasificaciones.
- Comandos administrativos de consulta y recarga.

### Funcionamiento

```text
config.yml
    ↓
EventCreator interpreta cada evento
    ↓
EventManager determina su estado mediante las fechas
    ↓
Un listener de Bukkit captura la acción de juego
    ↓
Los puntos se almacenan en memoria
    ↓
El progreso se guarda en YAML o MySQL
    ↓
La GUI y los placeholders muestran el progreso
    ↓
Los jugadores reclaman las recompensas desde el menú
```

### Tipos de evento incorporados

| Tipo | Activador | Filtros |
| --- | --- | --- |
| `PLAYER_KILL` | Un jugador elimina a otro | Mundo, permiso, herramienta, nombre de herramienta |
| `PLAYER_KILL_ENTITY` | Un jugador elimina una entidad viva | Mundo, permiso, herramienta, nombre, tipo de entidad |
| `PLAYER_BREAK_BLOCK` | Un jugador rompe un bloque | Mundo, permiso, herramienta, nombre, tipo de bloque |

Propiedades disponibles:

| Propiedad | Significado |
| --- | --- |
| `POINTS` | Puntos concedidos por una acción válida |
| `WORLD_NAME` / `WORLDS_NAME` | Mundo requerido o lista de mundos |
| `PERMISSION` | Permiso necesario para obtener puntos |
| `TOOL` / `TOOLS` | Material equipado o lista de materiales |
| `TOOL_NAME` / `TOOL_NAMES` | Nombre personalizado o lista de nombres |
| `TOOL_CONTAIN` | Texto contenido en el nombre del material equipado |
| `ENTITY_TYPE` / `ENTITY_TYPES` | Tipo de entidad o lista de tipos |
| `BLOCK_TYPE` / `BLOCK_TYPES` | Material de bloque o lista de materiales |
| `BLOCK_CONTAIN` / `BLOCK_CONTAINS` | Coincidencia por nombre del material del bloque |

El tipo y sus propiedades se escriben como un único valor:

```yaml
EVENT_TYPE: "PLAYER_BREAK_BLOCK:WORLD_NAME=pvp,POINTS=1"
```

### Objetivos y almacenamiento

- `INDIVIDUAL`: cada jugador tiene una puntuación y un estado independientes.
- `GLOBAL`: todas las contribuciones aumentan una puntuación compartida con un objetivo `MAX_POINTS`.

Un evento también puede definir una puntuación mínima personal que habilita su recompensa al ser alcanzada.

| Tipo | Modelo |
| --- | --- |
| `MULTIWORLD` | Archivos YAML locales; cada archivo de jugador contiene las estadísticas de todos los eventos |
| `SHARED` | Datos respaldados por MySQL para compartir el progreso individual entre servidores |

Los datos locales contienen la puntuación, el estado de finalización y el estado de las recompensas. El progreso global local se guarda en `plugin-data.yml`.

### Configuración de eventos

```yaml
events:
  'block-competition':
    event-specifications:
      TYPE: MULTIWORLD
      EVENT_TYPE: "PLAYER_BREAK_BLOCK:WORLD_NAME=pvp,POINTS=1"
      starts: '27-02-21_19:40'
      ends: '27-02-21_21:22'
      GOAL_TYPE: INDIVIDUAL

      min-points-reward:
        enabled: true
        min-points: 12
        ACTIONS:
          - 'CONSOLE|eco give %player_name% 1200'
          - 'MESSAGE|Recompensa reclamada.'

      finish-event-reward:
        enabled: true
        ACTIONS:
          - 'CONSOLE|eco give %player_name% 5000'
          - 'MESSAGE|Recompensa final reclamada.'

    gui-specifications:
      SLOT: 10
```

Las fechas utilizan el formato `dd-MM-yy_HH:mm`.

### Acciones de recompensa

| Acción | Comportamiento |
| --- | --- |
| `CONSOLE` | Ejecuta un comando como la consola |
| `PLAYER_COMMAND` | Hace que el jugador ejecute un comando |
| `MESSAGE` | Envía un mensaje al jugador |
| `SOUND` | Reproduce un sonido de Bukkit |
| `CLOSE` | Cierra el menú del jugador |

El formato es `ACCIÓN|VALOR`. Los mensajes, comandos, nombres y lore admiten colores con `&` y placeholders instalados.

### Menú gráfico

El comando principal abre una GUI cuyo título, filas, objetos decorativos, slots, materiales, nombres, lore, cantidades, encantamientos y acciones se leen desde `config.yml`.

Cada evento puede definir una apariencia para:

- `not-started-event`
- `started-but-not-completed`
- `started-completed`
- `ended-not-completed`
- `ended-completed`

| Placeholder interno | Valor |
| --- | --- |
| `%tmp-player-value%` | Puntos actuales del jugador |
| `%date-starts%` | Tiempo restante hasta el inicio |
| `%date-ends%` | Tiempo restante hasta el final |
| `%tmp-global-value%` | Puntuación global actual |
| `%tmp-global-value-limited%` | Puntuación global limitada al objetivo |

### PlaceholderAPI

El plugin registra la expansión `TMP` cuando PlaceholderAPI está instalado:

```text
%tmp_value_<event-id>%
%tmp_global_<event-id>%
%tmp_maxglobal_<event-id>%
```

Estos valores pueden ser utilizados por otros plugins. La configuración incluida demuestra su uso con clasificaciones de LeaderHeads.

### Comandos y permiso

```text
/themightiestplayer
/mightiestplayer
/mp
/tmp
```

| Comando | Descripción |
| --- | --- |
| `/tmp` | Abre la GUI de eventos |
| `/tmp help` | Muestra los comandos del plugin |
| `/tmp about <CustomEvent>` | Muestra información de un tipo de evento |
| `/tmp customEventList` | Enumera los tipos de evento incorporados |
| `/tmp reload` | Recarga la configuración, los datos, el menú y los eventos |

Los subcomandos administrativos utilizan el permiso `tmp.admin`.

### Estructura

```text
TheMightiestPlayer/
├── config.yml
├── plugin-data.yml
├── plugin.yml
├── pom.xml
└── src/hotdoctor/plugin/themightiestplayer/
    ├── Main.java
    ├── commands/
    ├── event_types/list/
    ├── extensions/
    ├── listeners/
    ├── managers/
    ├── progress/
    └── utils/databasetypes/
```

`Main` inicializa la configuración, los comandos, el menú, los eventos, los listeners y PlaceholderAPI. `EventCreator` convierte las definiciones YAML en instancias de `EventManager`. Las clases de evento escuchan acciones de Bukkit y las clases de base de datos coordinan los datos en memoria, YAML y MySQL.

### Tecnologías originales

- Java 8 y Maven
- Spigot API `1.14.4-R0.1-SNAPSHOT`
- PlaceholderAPI `2.10.9`
- HikariCP `3.4.1`
- SLF4J JDK14 `1.7.25`
- Integraciones opcionales con PlaceholderAPI y LeaderHeads

Maven empaqueta el proyecto como `TheMightiestPlayer.jar` e incluye HikariCP y SLF4J en el artefacto resultante.

### Licencia

El repositorio incluye la GNU General Public License, versión 3. Consulta [LICENSE](LICENSE).
