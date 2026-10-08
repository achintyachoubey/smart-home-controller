# Smart Home Device Controller

A menu-driven Java console application that simulates controlling household devices
room by room. You can add rooms and devices (lights, fans, air conditioners, smart TVs
and door locks), switch and adjust them, run automation scenes such as "Night Mode",
estimate electricity use and cost in rupees, and everything is saved to text files
between runs. It is a college mini project for an Object-Oriented Programming course,
so every core Java OOP concept appears in a place where the design genuinely needs it
(see [docs/CONCEPTS.md](docs/CONCEPTS.md)). It uses only the JDK: no libraries, no build
tool, no collections.

## Features

- **Dashboard**: every room with each device's ID, type, name, ON/OFF state, settings and live power, plus room and home totals.
- **Rooms**: add (up to 8, unique names), list, rename, remove (with confirmation), and turn everything in a room off (door locks stay powered).
- **Devices**: add any of 5 types with optional settings (Enter keeps the default), remove, list by room, search by ID (case-insensitive), rename, change power rating. Up to 10 devices per room. IDs such as `L001`, `AC002`, `DL001` are generated automatically.
- **Device control**: a sub-menu built from what the device supports: on/off/toggle for all; brightness, speed, temperature or volume for adjustable devices; colour for lights, mode for ACs, channel for TVs; lock, unlock with PIN (3 wrong PINs lock the keypad) and change PIN for door locks.
- **Scenes**: create scenes of up to 15 steps (only actions valid for each device are offered), list, run (a report shows `[OK]` / `[FAILED] reason` per step, and one failing step never stops the rest), delete. Three demo scenes: Night Mode, Away Mode, Movie Time.
- **Energy report**: live power for a device, room or home; kWh and cost for N hours; a monthly estimate comparing a flat rate with an illustrative slab tariff; change the rate for the session.
- **Activity log**: every state-changing action is appended to `data/activity_log.txt` with a timestamp; view the last N entries.
- **Persistence**: state is saved to `data/home_state.txt` and `data/scenes.txt` (on "Save now", on exit, and when the input ends). Corrupt lines are skipped with a warning instead of crashing.
- **Robust input**: letters for numbers, empty input, out-of-range values, unknown IDs, names containing `|` and end of input (Ctrl+D / Ctrl+Z) are all handled.

## OOP concepts at a glance

Full details, with file and method names and a "why" for each, are in [docs/CONCEPTS.md](docs/CONCEPTS.md).

| Concept | Main place(s) in the code |
|---|---|
| Classes & objects | `Room`, `Home`, `Light` ... objects built in `DemoData.buildDemoHome()` and `Main.createDevice()` |
| Constructors (default, parameterized, `this()`, `super()`) | `EnergyCalculator()`, `Light(String)` → `this(...)`, `Light(...)` → `super(...)`, two `Device` constructors |
| Encapsulation | `private` fields with validating setters in every model class; `DoorLock` PIN has no getter; `Room.getDevices()` returns a copy |
| Inheritance | `Light`, `Fan`, `AirConditioner`, `SmartTV`, `DoorLock` extend `Device`; custom exceptions extend `Exception` |
| Abstraction | `abstract class Device` with 4 abstract methods |
| Interface | `Adjustable` implemented by `Light`, `Fan`, `AirConditioner`, `SmartTV` (not `DoorLock`) |
| Method overriding | `getCurrentPowerWatts()`, `getStatus()`, `toFileString()`, `getDeviceType()`, `toString()`, `DoorLock.turnOff()` |
| Runtime polymorphism | `Room.getTotalPowerWatts()`, `FileManager.saveHome()`, `SceneAction.execute()` |
| Method overloading | `Room.findDevice(String)` / `findDevice(int)`; three `EnergyCalculator.calculateKwh(...)`; two `calculateCost(...)` |
| Arrays | fixed-size arrays with hand-written add / search / remove-with-shift / copy in `Room`, `Home`, `Scene`; circular buffer in `FileManager.readLastLogLines()` |
| Operators | `isOn = !isOn` in `Device.toggle()`, ternary in `Device.toString()`, `+=` in `calculateSlabBill()`, `%` in `readLastLogLines()` |
| Control flow | `do-while` main menu, `while` sub-menus, classic `switch`, `if-else` slab chain, `for` / enhanced-`for`, `break` / `continue` |
| Static members | `IdGenerator` counters, `InputHelper` scanner, capacity constants, `DemoData.buildDemoHome()` |
| Enum | `ActionType` used in a `switch` in `SceneAction.execute()` |
| Exception handling | 3 custom checked exceptions, try-catch in `Main`, multi-catch in `Scene.run()`, try-with-resources in `FileManager` |
| File handling | `FileManager`: write (`BufferedWriter`), read (`BufferedReader`), append (`FileWriter(file, true)`) |
| Packages | `com.smarthome`, `.model`, `.service`, `.util`, `.exception` |

## Requirements

- JDK 11 or newer (`java -version` and `javac -version` should both print 11 or higher). The code was compiled with `-Xlint:all` with zero warnings on JDK 21 and also checked with `javac --release 11`.
- Nothing else: no Maven, Gradle, JUnit or external libraries.

## How to run

Always run from the project folder (the folder containing `src/`), because the program keeps its save files in a `data/` folder next to it.

### Windows

Double-click `run.bat`, or in Command Prompt:

```bat
cd path\to\smart-home-controller
run.bat
```

### Linux and macOS

```sh
cd path/to/smart-home-controller
./run.sh
```

(If you see "permission denied", run `chmod +x run.sh test.sh` once.)

### Manually from any command line

```sh
mkdir out
javac -Xlint:all -d out src/com/smarthome/*.java src/com/smarthome/*/*.java
java -cp out com.smarthome.Main
```

On Windows use backslashes in the paths (`src\com\smarthome\*.java src\com\smarthome\model\*.java ...`) or simply use `run.bat`.

On the first run there is no save file, so the program asks `No saved home found. Load demo home? (y/n)`. Answer `y` for the ready-made demo home (3 rooms, 10 devices, 3 scenes) or `n` to name and build your own. Choose `0` to save and exit; delete the `data/` folder to start from scratch again.

### From an IDE

- **VS Code**: install the "Extension Pack for Java", then *File → Open Folder* and choose the project folder. VS Code detects `src/` and `test/` as source folders. Open `src/com/smarthome/Main.java` and click **Run** above `main`. If it does not detect the folders, add `"java.project.sourcePaths": ["src", "test"]` to `.vscode/settings.json`. Make sure the terminal's working folder is the project folder.
- **IntelliJ IDEA**: *File → Open* the project folder. Right-click `src` → *Mark Directory as → Sources Root* and `test` → *Mark Directory as → Test Sources Root*. Set *File → Project Structure → SDK* to JDK 11+. Right-click `Main` → *Run 'Main.main()'*. The default working directory is the project folder, so `data/` is created there. Run tests by right-clicking `TestRunner` → *Run*.
- **Eclipse**: *File → New → Java Project*, untick "Use default location" and browse to the project folder; on the next page make sure `src` (and `test`) are listed as source folders (*Add Folder...* if not). Then right-click `Main.java` → *Run As → Java Application*. Run `TestRunner` the same way.
- **BlueJ**: *Project → Open Non BlueJ...* and select the `src` folder. BlueJ shows the `com` package; open `com → smarthome`, right-click `Main` and choose `void main(String[] args)`. BlueJ's working folder is the folder you opened, so the `data/` folder is created inside `src/`. (To run the tests in BlueJ, copy the files from `test/com/smarthome/` into `src/com/smarthome/` first, then run `TestRunner.main`.)

## How to run the tests

The project has its own tiny test framework (`test/com/smarthome/TestRunner.java`), so JUnit is not needed.

```sh
./test.sh          # Linux / macOS
test.bat           # Windows
```

Both compile `src` and `test` together into `out-test/` and run `com.smarthome.TestRunner`. Each test prints `PASS` or `FAIL`, followed by a summary:

```
Result: 45 of 45 tests passed.
```

The script exits with status 1 if any test fails. Tests use a temporary `test-data/` folder (deleted before and after) and never touch the real `data/` folder. Some tests deliberately load corrupt files, so `Warning: skipped line ...` messages in the output are expected.

## Folder structure

```
smart-home-controller/          (the project folder, called SmartHomeController/ in the brief)
├── src/com/smarthome/
│   ├── Main.java               console UI: one method per screen
│   ├── model/                  Device, Adjustable, Light, Fan, AirConditioner, SmartTV,
│   │                           DoorLock, Room, Home, Scene, SceneAction, ActionType
│   ├── service/                EnergyCalculator, FileManager, DemoData
│   ├── util/                   InputHelper, IdGenerator
│   └── exception/              DeviceNotFoundException, CapacityExceededException,
│                               InvalidInputException
├── test/com/smarthome/         TestRunner and 6 test classes (45 tests)
├── data/                       created at run time (not committed)
├── docs/
│   ├── CLASS_DIAGRAM.md        Mermaid class diagram
│   ├── CONCEPTS.md             every OOP concept with file, class and method
│   └── SAMPLE_OUTPUT.md        a real captured run
├── run.sh / run.bat            compile and run
├── test.sh / test.bat          compile and run the tests
├── .gitignore                  ignores out/, out-test/, data/, test-data/, *.class
└── README.md
```

## File formats

All files are plain text, one record per line, fields separated by `|` (which is why names may not contain `|`).

`data/home_state.txt`

```
HOME|My Smart Home
ROOM|Living Room
DEVICE|LIGHT|L001|Ceiling Light|true|10.0|75|Warm White
DEVICE|FAN|F001|Ceiling Fan|true|75.0|3
DEVICE|AC|AC001|Split AC|false|1500.0|24|COOL
DEVICE|TV|TV001|Smart TV|false|100.0|40|101
ROOM|Entrance
DEVICE|LOCK|DL001|Main Door Lock|true|5.0|true|1234
```

`DEVICE` fields: `DEVICE|type|id|name|isOn|rating|...` followed by the type-specific fields:

| Type | Extra fields |
|---|---|
| LIGHT | brightness, colour |
| FAN | speed |
| AC | temperature, mode |
| TV | volume, channel |
| LOCK | locked, pin |

`data/scenes.txt`

```
SCENE|Night Mode
ACTION|L001|TURN_OFF|0
ACTION|AC002|SET_LEVEL|26
ACTION|DL001|LOCK|0
```

`data/activity_log.txt` (append-only)

```
2026-10-08 21:14:03 | CONTROL | L001 Ceiling Light turned OFF
2026-10-08 21:14:10 | SCENE   | Night Mode ran: 8 of 8 steps succeeded
2026-10-08 21:15:00 | SYSTEM  | State saved (3 rooms, 10 devices, 3 scenes)
```

Log categories: `SYSTEM`, `ROOM`, `DEVICE`, `CONTROL`, `SCENE`, `ENERGY`.

## Energy formulas

Live power while a device is ON (every device draws 0.0 W when OFF):

| Device | Default rating | Live power |
|---|---|---|
| Light | 10 W | rating × brightness / 100 |
| Fan | 75 W | rating × speed / 5 |
| Air conditioner | 1500 W | COOL: rating × (1 + 0.05 × (24 − temperature)), kept between 0.5 × and 1.5 × rating; DRY: 0.6 × rating; FAN: 0.3 × rating |
| Smart TV | 100 W | rating |
| Door lock | 5 W | rating |

- Energy: `kWh = watts × hours / 1000`
- Flat cost: `cost = kWh × rate` (default Rs. 8.00 per kWh; can be changed for the session)
- Monthly energy: `kWh = watts × hoursPerDay × 30 / 1000`
- Slab bill: first 100 units at Rs. 3.00, units 101–300 at Rs. 5.00, units 301–500 at Rs. 7.00, above 500 at Rs. 9.00, plus a fixed charge of Rs. 50.00 (always, even for 0 units).

Example: a 1500 W AC at 24 C in COOL mode for 8 hours a day for 30 days uses 360 kWh, which costs Rs. 2,880.00 at the flat rate and Rs. 1,770.00 on the slab tariff.

> **Note:** the slab tariff is **illustrative only**, chosen to show an if-else chain. It is not the tariff of any real electricity board; real bills have different slabs, fixed charges, taxes and duties.

## Design decisions

These are choices the project brief left open; each was resolved in the simplest reasonable way.

1. **Project folder.** The brief's `SmartHomeController/` folder is this repository's root folder.
2. **Not found = exception, never `null`.** `Home.findRoom()` and `Home.findScene()` throw `InvalidInputException` when the name does not exist; device lookups throw `DeviceNotFoundException`. No lookup method returns `null`.
3. **Room renaming** goes through `Home.renameRoom()`, which keeps room names unique (case-insensitive); `Room.setName()` only validates the name itself.
4. **Powered-off door lock.** `lock()`, `unlock()` and `changePin()` throw `InvalidInputException` ("... is powered off; turn it on first") while the lock has no power, so a scene's LOCK step on a powered-off lock is reported as `[FAILED]` with that reason. The locked/unlocked state is never changed by powering off. `DoorLock.turnOff()` is overridden only to document this; it calls `super.turnOff()`.
5. **Keypad lockout.** A wrong *old* PIN in "Change PIN" also counts as a failed attempt, so the PIN cannot be guessed through that route. The keypad lock is not saved and only resets when the program restarts, as the brief specifies; "Lock" still works while the keypad is locked.
6. **`DoorLock` is `final`.** Its constructor calls `turnOn()` so that a new lock starts powered on; that is only safe when no subclass can override `turnOn()` (javac's `this-escape` lint warns otherwise).
7. **Name rules in one place.** `InputHelper.validateName()` (non-empty after trimming, at most 30 characters, no `|`) is used by both the console prompts and every model constructor/setter. Names are stored trimmed.
8. **IDs.** A new device's ID is generated only after its name and rating pass validation. IDs are stored in upper case and searched case-insensitively. When loading, a device whose ID prefix does not match its type (for example a FAN with ID `L004`) or whose ID is already used is skipped as corrupt.
9. **Choosing things in menus.** Rooms and scenes are chosen from numbered lists; devices are chosen by ID. Typing `0` goes back. An unknown device ID re-prompts until a valid ID or `0` is entered.
10. **Extra menu options** (needed so that every required model method is reachable from the UI): *Manage rooms → Turn off all devices in a room* (`Room.turnAllOff()`), *Manage devices → Rename device* (`Device.setName()`) and *Change power rating* (`Device.setPowerRatingWatts()`), and *Remove a step* in the scene editor (`Scene.removeAction()`).
11. **Scene editor.** The editor refuses to save a scene with 0 steps and finishes automatically when the 15th step is added. Steps that point to a removed device are kept (and listed as `(missing device)`); after removing a room or device the program reports which scene steps are affected.
12. **Energy estimates use the current load.** kWh, cost and the monthly estimate assume the devices that are ON now keep their current settings for the whole period. The "N hours" option accepts 0–720 hours (one 30-day month); the monthly option accepts 0–24 hours per day.
13. **Invalid energy arguments.** `calculateKwh`, `calculateCost`, `monthlyKwh` and `calculateSlabBill` throw the unchecked `IllegalArgumentException` (their required signatures have no `throws` clause); the rate setter and constructor throw the checked `InvalidInputException`. The rate must be more than 0 and at most Rs. 100 per kWh and is not saved (it lasts for the session).
14. **New devices start OFF**, except door locks, which start ON and locked. The power rating must be more than 0 and at most 5000 W; the console asks for at least 0.1 W.
15. **Loading.** A scene is added to the home only after all its steps have been read; a scene with no valid steps, a duplicate scene name, or an action before any `SCENE` line is skipped with a warning. Devices after a corrupt `ROOM` line are skipped too, so they never end up in the wrong room. Booleans in files must be exactly `true` or `false`. If `home_state.txt` has no valid `HOME` line the file is ignored and the first-run prompt is shown.
16. **Reset to demo home** does not save immediately (use 8, or it is saved on exit) and does not touch the activity log.
17. **Warnings go to the normal output** (`System.out`) so they appear in order with the rest of the program's output. A log-file failure prints one warning line and the program carries on. PINs are never written to the log.
18. **Number formatting** uses `String.format("%,.2f", ...)` as required, which follows the computer's default locale (on an English locale it prints `Rs. 1,770.00`).
19. **Dashboard columns.** The light's settings column shows both brightness and colour (`Brightness 75%, Warm White`); the column widths are wider than the example in the brief so 18-character names and the longest settings text line up.
20. **`toString()` is overridden once, in `Device`** (it overrides `Object.toString()`). Each device type overrides the four abstract methods; `Device.toString()` calls `getStatus()` and `getCurrentPowerWatts()`, so every subclass already gets its own dashboard row through runtime polymorphism without repeating the row layout five times.
21. **Tests.** Each test's body is an anonymous inner class (lambdas are not allowed), so the `runAll()` methods in the test classes are long lists of short tests; the 60-line guideline was applied to the program code in `src/`.

## Future scope

- A graphical interface with **Swing or JavaFX** (room tabs, device cards, sliders for levels).
- **Timed scenes** that run at a given time or after a delay, using **threads** (`Thread`, `ScheduledExecutorService`).
- Controlling **real devices** over **MQTT** from a **Raspberry Pi** or ESP32 (relays, smart plugs, IR blasters).
- Storing the home, scenes and log in a database with **JDBC** (MySQL / SQLite) instead of text files.
- **Multiple users** with login, roles (owner, family, guest) and per-user permissions.
- **Energy history**: record readings over time, show daily/monthly graphs and compare against the real tariff of the user's electricity board.
