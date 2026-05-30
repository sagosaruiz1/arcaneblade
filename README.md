# Arcane Blade

A 2D side-scrolling action-platformer built entirely in **Java** using **Swing and AWT**, inspired by the Metroidvania genre — specifically *Hollow Knight*. Explore a single interconnected world divided into three atmospheric zones, master fluid movement mechanics, and battle enemies using a dual combat system combining melee sword attacks and arcane lightning magic.

---

## Screenshots


---

## Gameplay Features

### Movement
- **Variable Jump Height** — tap for a short hop, hold for maximum height
- **Coyote Time** — 6-frame grace window for jumping after walking off a ledge
- **Wall Slide** — slow your fall by pressing into a wall mid-air
- **Wall Jump** — launch off walls to reach higher platforms
- **Smooth Dash** — momentum-based dash with smooth-step easing curve and cooldown
- **Acceleration & Deceleration** — gradual speed buildup and friction-based stopping

### Combat
- **Melee Combo** — chain Attack 1 into Attack 2 mid-animation
- **Lightning Strike** — ranged arcane spell consuming mana, with area damage and visual effects
- **Attack Sound Effects** — unique audio per combat action

### World
- **3 Interconnected Zones** — Surface Ruins → Underground Caverns → Deep Abyss
- **Seamless Gate Transitions** — walk into a gate and instantly travel to the connected zone, no loading screen
- **Bidirectional Gates** — Gate A, Gate B, Gate C each connect specific areas between zones
- **Zone-Unique Backgrounds** — each zone has its own atmospheric background image

### Enemy AI — NightBorne
- **Finite State Machine** with 5 states: `IDLE → RUNNING → ATTACK → HURT → DEAD`
- Patrols platforms and turns at edges
- Line-of-sight detection — pursues player when spotted
- 12-frame attack animation with precise damage window at frame 10
- Full 23-frame death animation

### Objects & Items
- **Health Potions** — restore 15 HP on contact, floating hover animation
- **Energy Potions** — restore 30 mana on contact
- **Barrels & Boxes** — destructible containers that drop potions when attacked

### Audio
- Background music for menu and in-game zones
- Sound effects for sword attack and lightning strike
- Volume slider and mute toggles for music and SFX

### UI
- Real-time **Health Bar** and **Mana Bar** HUD
- **Pause Menu** with audio controls
- **Game Over Screen** with retry and menu options
- **Level Completed Overlay** on final zone clear
- **Options Screen** accessible from main menu

---

## Controls

| Key | Action |
|-----|--------|
| `A` | Move Left |
| `D` | Move Right |
| `W` or `Space` | Jump (hold for higher jump) |
| `Shift` | Dash |
| `K` | Melee Attack |
| `L` | Lightning Strike |
| `Left Click` | Melee Attack (mouse) |
| `Escape` | Pause / Unpause |

---

## Zone Overview

```
Zone 1 — Surface Ruins
  │
  ├── Gate A (col 47, row 29) ──────────► Zone 2, Gate A Spawn (col 45, row 0)
  │
  └── Gate B (col 99, row 13) ─────────► Zone 2, Gate B Spawn (col 72, row 4)

Zone 2 — Underground Caverns
  │
  ├── Gate A Return ───────────────────► Zone 1, Gate A Spawn
  ├── Gate B Return ───────────────────► Zone 1, Gate B Spawn
  │
  └── Gate C ────────────────────────► Zone 3, Gate C Spawn

Zone 3 — Deep Abyss
  │
  └── Gate C Return ─────────────────► Zone 2, Gate C Spawn
```

---

## Project Structure

```
src/
├── audio/
│   └── AudioPlayer.java          — WAV music and SFX management
├── entities/
│   ├── Entity.java               — Abstract base: position, hitbox, health
│   ├── Enemy.java                — Abstract AI base: FSM, patrol, sight
│   ├── NightBorne.java           — Concrete enemy implementation
│   ├── EnemyManager.java         — Manages all active enemies per level
│   └── Player.java               — Full player controller with physics
├── gamestates/
│   ├── Statemethods.java         — Interface: update(), draw(), input handlers
│   ├── Gamestate.java            — Enum: PLAYING, MENU, OPTIONS, QUIT
│   ├── State.java                — Base state class
│   ├── Playing.java              — Main gameplay state
│   ├── Menu.java                 — Main menu state
│   └── GameOptions.java          — Options/settings state
├── io/arcaneblade/
│   ├── MainClass.java            — Entry point
│   ├── Game.java                 — Game loop, state routing, constants
│   ├── GamePanel.java            — JPanel render surface
│   └── GameWindow.java           — JFrame window
├── io/inputs/
│   ├── KeyboardInputs.java       — Key event routing
│   └── MouseInputs.java          — Mouse event routing
├── levels/
│   ├── Level.java                — Parses PNG into tile/enemy/object data
│   └── LevelManager.java         — Manages level loading and gate setup
├── objects/
│   ├── GameObject.java           — Base class for world objects
│   ├── Potion.java               — Collectible health/mana item
│   ├── GameContainer.java        — Destructible barrel/box
│   ├── LightningStrike.java      — Lightning spell visual effect
│   └── ObjectManager.java        — Manages all objects per level
├── ui/
│   ├── PauseButton.java          — Base UI button with bounds
│   ├── UrmButton.java            — Undo/Replay/Menu button
│   ├── SoundButton.java          — Mute toggle button
│   ├── VolumeButton.java         — Draggable volume slider
│   ├── MenuButton.java           — Main menu button
│   ├── AudioOptions.java         — Reusable audio control component
│   ├── PauseOverlay.java         — In-game pause screen
│   ├── GameOverOverlay.java      — Death screen
│   └── LevelCompletedOverlay.java — Zone complete screen
└── utilz/
    ├── Constants.java            — All game constants organized by domain
    ├── HelpMethods.java          — Collision detection and PNG level parsing
    ├── LoadSave.java             — File I/O and resource loading
    └── Gate.java                 — Zone transition data class

resources/
├── audio/
│   ├── menu.wav
│   ├── ingame.wav
│   ├── attack.wav
│   └── lightning_strike.wav
├── entities/
│   ├── player/
│   │   ├── Run.png
│   │   ├── Idle.png
│   │   ├── Jump.png
│   │   ├── Fall.png
│   │   ├── Dash.png
│   │   ├── Death.png
│   │   ├── Hurt.png
│   │   ├── Attack 1.png
│   │   └── Attack 2.png
│   └── enemy/
│       └── NightBorne.png
├── maps/
│   ├── tilemap.png               — 48-tile sprite sheet (12 cols x 4 rows)
│   └── lvls/
│       ├── 1.png                 — Zone 1 level data
│       ├── 2.png                 — Zone 2 level data
│       └── 3.png                 — Zone 3 level data
└── ui/
    ├── playing_background_img.png
    ├── zone2_bg.png
    ├── health_power_bar.png
    ├── button_atlas.png
    ├── menu_background.png
    ├── background_menu.png
    ├── pause_menu.png
    ├── sound_button.png
    ├── urm_buttons.png
    ├── volume_buttons.png
    ├── potions_sprites.png
    ├── objects_sprites.png
    ├── completed_sprite.png
    ├── death_screen.png
    ├── options_background.png
    └── lightning_strike.png
```

---

## Technical Details

| Property | Value |
|----------|-------|
| Language | Java |
| Rendering | Java Swing / AWT |
| Resolution | 1248 x 672 pixels |
| Tile Size | 48px (32px × 1.5 scale) |
| Screen Tiles | 26 wide × 14 tall |
| Target FPS | 120 |
| Update Rate | 200 UPS |
| Map Size | 100 × 30 tiles per zone |

---

## PNG Level Encoding System

The entire game world is encoded as pixel colors in PNG image files. No external level editor or separate data format is required — levels are designed visually in a pixel art editor and read at runtime.

| Channel | Value | Meaning |
|---------|-------|---------|
| 🔴 Red | 0–47 | Tile index from tilemap sprite sheet |
| 🟢 Green | `0` | NightBorne enemy spawn |
| 🔵 Blue | `0` | Health Potion |
| 🔵 Blue | `1` | Energy Potion |
| 🔵 Blue | `2` | Barrel |
| 🔵 Blue | `3` | Box |
| RGB | `11, 255, 255` | Player spawn point |

Gates are defined programmatically in `LevelManager.setupGates()` using tile coordinates.

---

## OOP Concepts

### Inheritance
```
Entity
├── Player
└── Enemy (abstract)
    └── NightBorne

GameObject
├── Potion
└── GameContainer

PauseButton
├── SoundButton
├── UrmButton
└── VolumeButton

State
├── Playing
├── Menu
└── GameOptions
```

### Polymorphism
- `Statemethods` interface implemented by `Playing`, `Menu`, and `GameOptions` — the game loop calls `update()` and `draw()` without knowing the concrete type
- `NightBorne` overrides `isPlayerCloseForAttack()` from `Enemy` with a geometry-based hitbox intersection check

### Encapsulation
- `Player` health and mana are `private` — only modifiable through `changeHealth()` and `changePower()` which enforce bounds
- Dash cooldown logic is encapsulated in `Player.dash()` — external code cannot bypass the cooldown
- All audio complexity hidden behind `AudioPlayer`'s simple public API

### Abstraction
- `HelpMethods.CanMoveHere()` hides four-corner tile collision testing behind a single boolean call
- `LoadSave.GetSpriteAtlas()` hides all file I/O and ImageIO behind a path-in, image-out method
- `Level` hides all PNG pixel parsing — callers receive ready-to-use game objects

---

## How to Run

### Requirements
- Java JDK **21 or higher**
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java extension)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/arcane-blade.git
cd arcane-blade
```

**2. Open in your IDE**
- IntelliJ: `File → Open` → select the project folder
- Eclipse: `File → Import → Existing Projects into Workspace`

**3. Set the source and resource directories**
- Source root: `src/`
- Resources root: `src/main/resources/` (or wherever your PNG/WAV files are)

**4. Run**
- Find `MainClass.java` in `io/arcaneblade/`
- Run the `main()` method

### Running from command line
```bash
javac -sourcepath src -d out src/io/arcaneblade/MainClass.java
java -cp out io.arcaneblade.MainClass
```

---

## Known Issues

- NightBorne sprite sheet has a solid black background — transparency not yet implemented
- Gate C (Zone 2 → Zone 3) connection depends on Zone 3 level PNG content
- Wall jump direction detection uses `playerSpeed` instead of actual wall side detection

---

## Academic Context

This project was developed as a final project submission demonstrating the four pillars of **Object-Oriented Programming**:

| Pillar | Demonstrated In |
|--------|----------------|
| Inheritance | `Entity → Enemy → NightBorne` hierarchy |
| Polymorphism | `Statemethods` interface across all game states |
| Encapsulation | `Player` private health/mana with controlled access |
| Abstraction | `HelpMethods` collision and `Level` PNG parsing |

---

## License

This project is for educational purposes. All original pixel art assets were created specifically for this project.

---

*Built with Java Swing — no external game engines or libraries*
