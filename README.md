# AssemblyLine (Factory Plugin)

A Minecraft Paper plugin that adds a complete factory and assembly-line system to your server, including machines, conveyors, energy networks, vehicle/weapon production, and more.

## Requirements

| Dependency | Version | Required |
|---|---|---|
| [Paper](https://papermc.io/) | 1.21.4+ | Yes |
| Java | 21+ | Yes |
| Gradle | 8.8+ | Build only |
| [QualityArmory](https://www.spigotmc.org/resources/quality-armory.47561/) | 2.0.2+ | Yes (runtime) |
| [Vehicles](https://www.spigotmc.org/resources/vehicles.12446/) | — | No (optional integration) |

## Building

```bash
./gradlew build
```

The compiled plugin JAR will be placed in `build/libs/`.

## Development Server

The project includes the [run-paper](https://github.com/jpenilla/run-task) Gradle plugin for quick local testing:

```bash
./gradlew runServer
```

This starts a Paper 1.21.4 test server with the plugin loaded automatically.

## Configuration

The plugin generates the following configuration files inside its data folder (`plugins/Factory/`) on first run:

### `config.yml`

Main plugin configuration. Sections:

| Section | Key | Default | Description |
|---|---|---|---|
| `machines` | `max_level` | `10` | Maximum machine upgrade level |
| `machines` | `upgrade_costs` | `[10, 25, …, 6400]` | Emerald Block cost per level |
| `energy` | `distribution_interval` | `20` | Ticks between energy distribution cycles |
| `energy` | `buffer_capacity` | `1000` | Max energy buffer per machine |
| `energy` | `enable_system` | `true` | Toggle the energy system |
| `vehicles` | `enable_integration` | `true` | Enable Vehicles plugin integration |
| `vehicles` | `require_fuel` | `true` | Require fuel for vehicles |
| `vehicles` | `fuel_consumption_rate` | `1.0` | Fuel consumption multiplier |
| `production` | `enable_weapons` | `true` | Enable weapon production |
| `production` | `enable_vehicles` | `true` | Enable vehicle production |
| `production` | `production_speed_multiplier` | `1.0` | Production speed multiplier |
| `gui` | `enable_animations` | `true` | Toggle GUI animations |
| `gui` | `enable_sounds` | `true` | Toggle GUI sounds |
| `gui` | `update_interval` | `5` | Ticks between GUI refreshes |
| `notifications` | `enable` | `true` | Toggle notifications |
| `notifications` | `cooldown_ms` | `3000` | Cooldown between notifications (ms) |
| `notifications` | `sound_volume` | `0.8` | Notification sound volume (0.0–1.0) |
| `performance` | `enable_optimization` | `true` | Toggle performance optimizations |
| `performance` | `max_concurrent_machines` | `100` | Max machines processing concurrently |
| `performance` | `cleanup_interval` | `6000` | Ticks between data cleanup cycles |
| `debug` | `enable_logging` | `false` | Toggle debug logging |
| `debug` | `verbose` | `false` | Toggle verbose debug output |

### `resourcepack.yml`

Resource pack configuration (auto-generated separately on first run):

| Key | Default | Description |
|---|---|---|
| `resource_pack_url` | `""` | URL to a hosted resource pack (empty = use auto-generated) |
| `force_download` | `false` | Force re-download on startup |
| `auto_send` | `true` | Automatically send pack to players on join |
| `prompt_message` | *(see config)* | Message shown when pack is sent |
| `kick_on_decline` | `false` | Kick players who decline the pack |

### `player_stats.yml`

Automatically managed player statistics — not intended for manual editing.

## Commands

| Command | Permission | Description |
|---|---|---|
| `/givefactory <type> [amount]` | `assemblyline.op` | Give factory blocks |
| `/factoryconfig <type>` | `assemblyline.op` | Configure factories |
| `/energygui` | `factory.energygui` | Open energy control panel |
| `/factoryrp <action>` | `factory.admin` | Manage the resource pack |
| `/factory [subcommand]` | `factory.production` | Production system |

## License

All rights reserved.
