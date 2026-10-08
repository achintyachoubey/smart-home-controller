# Sample Output

This is a real captured run of the program, not hand-written text. The same input file was first piped into the program (`java -cp out com.smarthome.Main < input.txt`) to check that every menu option works and that nothing crashes; it was then replayed through a terminal so that what the user types appears after each prompt, exactly as it would on screen. The input file is listed at the end of this page.

Timestamps in the activity log are the real times of the run. The repeated main menu is shown in full, because that is what the program prints.

## Contents

- Run 1: First run: no save file, so the demo home is offered
- Run 1: 1. Dashboard
- Run 1: Invalid input at the main menu
- Run 1: 9. Reset to demo home
- Run 1: 2. Manage rooms (with invalid names)
- Run 1: 3. Manage devices: add, list, search, rename, change rating, remove (with invalid input)
- Run 1: 4. Control a device: adjusting a light
- Run 1: 4. Control a device: AC mode and TV channel
- Run 1: 4. Control a device: door lock with a wrong PIN
- Run 1: 5. Scenes: listing and running Night Mode
- Run 1: 5. Scenes: creating a scene
- Run 1: 2. Manage rooms: removing a room that still has devices
- Run 1: 5. Scenes: a step on a missing device fails, the rest still runs
- Run 1: 6. Energy report (live power, kWh and cost, monthly estimate with slab bill)
- Run 1: 7. Activity log
- Run 1: 8. Save now
- Run 1: 0. Save and exit
- Run 2: Restart: the saved state is restored
- Run 2: End of input (Ctrl+D) saves and exits cleanly
- Run 3: Corrupt line in home_state.txt is skipped with a warning
- Run 4: Deleted data folder: answering "n" starts an empty home

## Run 1 - first run, every menu option

### First run: no save file, so the demo home is offered

```text
===========================================
       SMART HOME DEVICE CONTROLLER
   Java OOP mini project - console edition
===========================================
No saved home found. Load demo home? (y/n): maybe
Please answer y or n.
No saved home found. Load demo home? (y/n): y
Demo home loaded: 3 rooms, 10 devices, 3 scenes.

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 1. Dashboard

```text
Choose an option: 1

=============== DASHBOARD: My Smart Home ===============

[ Living Room ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L001   LIGHT Ceiling Light      ON    Brightness 75%, Warm White        7.5 W
L002   LIGHT Floor Lamp         OFF   Brightness 40%, Warm White        0.0 W
F001   FAN   Ceiling Fan        ON    Speed 3/5                        45.0 W
AC001  AC    Split AC           OFF   24 C, COOL                        0.0 W
TV001  TV    Smart TV           OFF   Vol 40, Ch 101                    0.0 W
Room total: 52.5 W

[ Bedroom ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L003   LIGHT Bedside Light      ON    Brightness 60%, Warm White        6.0 W
F002   FAN   Bedroom Fan        OFF   Speed 2/5                         0.0 W
AC002  AC    Bedroom AC         OFF   24 C, COOL                        0.0 W
Room total: 6.0 W

[ Entrance ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
L004   LIGHT Porch Light        OFF   Brightness 100%, Warm White       0.0 W
Room total: 5.0 W
-------------------------------------------
Total live power: 63.5 W
Devices ON: 4 of 10
Press Enter to continue...

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### Invalid input at the main menu

```text
Choose an option: abc
Please enter a whole number.
Choose an option: 12
Please enter a number between 0 and 9.
Choose an option:
Please enter a whole number.
```

### 9. Reset to demo home

```text
Choose an option: 9
Replace the current home with the demo home? (y/n): n
Nothing changed.

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
Choose an option: 9
Replace the current home with the demo home? (y/n): y
Demo home restored: 3 rooms, 10 devices, 3 scenes. (Use 8 to save it now, or it is saved on exit.)

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 2. Manage rooms (with invalid names)

```text
Choose an option: 2

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 1
New room name:
Name cannot be empty.
New room name: Garage|Shed
Name cannot contain the '|' character.
New room name: This room name is far too long to be accepted
Name must be at most 30 characters.
New room name: living room
A room named 'living room' already exists.

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 1
New room name: Study
Room 'Study' added.

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 1
New room name: Garage
Room 'Garage' added.

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 2
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Study (0 devices)
 5. Garage (0 devices)

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 3
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Study (0 devices)
 5. Garage (0 devices)
Room number (0 to cancel): 4
New name for 'Study': Home Office
Room 'Study' renamed to 'Home Office'.

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 5
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (0 devices)
 5. Garage (0 devices)
Room number (0 to cancel): 2
All devices in Bedroom are off (door locks keep their power).

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 3. Manage devices: add, list, search, rename, change rating, remove (with invalid input)

```text
Choose an option: 3

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 1
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (0 devices)
 5. Garage (0 devices)
Room number (0 to cancel): 9
Please enter a number between 0 and 5.
Room number (0 to cancel): 4
Device type: 1. Light  2. Fan  3. Air conditioner  4. Smart TV  5. Door lock
Choose type (1-5, 0 to cancel): 7
Please enter a number between 0 and 5.
Choose type (1-5, 0 to cancel): 1
Device name: Desk Lamp
Power rating in watts (0.1-5000) [default 10.0]: -5
Please enter a number between 0.1 and 5000.
Power rating in watts (0.1-5000) [default 10.0]:
Brightness 0-100 [default 100]: abc
Please enter a whole number.
Brightness 0-100 [default 100]: 80
   1. Warm White
   2. Cool White
   3. Daylight
   4. Red
   5. Blue
   6. Green
Colour 1-6 [default 1 = Warm White]: 2
Added Desk Lamp to Home Office with ID L005.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 1
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (1 device)
 5. Garage (0 devices)
Room number (0 to cancel): 4
Device type: 1. Light  2. Fan  3. Air conditioner  4. Smart TV  5. Door lock
Choose type (1-5, 0 to cancel): 3
Device name: Office AC
Power rating in watts (0.1-5000) [default 1500.0]:
Temperature 16-30 C [default 24]:
   1. COOL
   2. DRY
   3. FAN
Mode 1-3 [default 1 = COOL]:
Added Office AC to Home Office with ID AC003.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 1
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (2 devices)
 5. Garage (0 devices)
Room number (0 to cancel): 5
Device type: 1. Light  2. Fan  3. Air conditioner  4. Smart TV  5. Door lock
Choose type (1-5, 0 to cancel): 1
Device name: Garage Light
Power rating in watts (0.1-5000) [default 10.0]:
Brightness 0-100 [default 100]:
   1. Warm White
   2. Cool White
   3. Daylight
   4. Red
   5. Blue
   6. Green
Colour 1-6 [default 1 = Warm White]:
Added Garage Light to Garage with ID L006.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 3
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (2 devices)
 5. Garage (1 device)
Room number (0 to cancel): 4

[ Home Office ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          OFF   Brightness 80%, Cool White        0.0 W
AC003  AC    Office AC          OFF   24 C, COOL                        0.0 W
Room total: 0.0 W

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 4
Device ID to search for: l005
Found in room: Home Office
ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          OFF   Brightness 80%, Cool White        0.0 W

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 4
Device ID to search for: X999
No device with ID X999.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 5
ID of the device to rename (0 to go back): X999
No device with ID X999. Please try again.
ID of the device to rename (0 to go back): ac003
New name for AC003 Office AC: Study AC
AC003 renamed to Study AC.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 6
ID of the device (0 to go back): L005
New power rating in watts (0.1-5000) [now 10.0]: 0
Please enter a number between 0.1 and 5000.
New power rating in watts (0.1-5000) [now 10.0]: 12
L005 Desk Lamp is now rated 12.0 W.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 2
ID of the device to remove (0 to go back): AC003
ID     TYPE  NAME               STATE SETTINGS                          POWER
AC003  AC    Study AC           OFF   24 C, COOL                        0.0 W
Remove AC003 Study AC? (y/n): maybe
Please answer y or n.
Remove AC003 Study AC? (y/n): y
AC003 Study AC removed from Home Office.

--- MANAGE DEVICES ---
 1. Add device
 2. Remove device
 3. List devices in a room
 4. Search device by ID
 5. Rename device
 6. Change power rating
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 4. Control a device: adjusting a light

```text
Choose an option: 4
Device ID to control (0 to go back): L005

ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          OFF   Brightness 80%, Cool White        0.0 W
--- CONTROL L005 Desk Lamp ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Brightness (0-100)
 5. Set colour
 0. Back
Choose an option: 4
Set Brightness (0-100) [now 80]: 150
Please enter a number between 0 and 100.
Set Brightness (0-100) [now 80]: 60

ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          ON    Brightness 60%, Cool White        7.2 W
--- CONTROL L005 Desk Lamp ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Brightness (0-100)
 5. Set colour
 0. Back
Choose an option: 5
   1. Warm White
   2. Cool White
   3. Daylight
   4. Red
   5. Blue
   6. Green
Colour 1-6 [default 2 = Cool White]: 5

ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          ON    Brightness 60%, Blue              7.2 W
--- CONTROL L005 Desk Lamp ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Brightness (0-100)
 5. Set colour
 0. Back
Choose an option: 3

ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          OFF   Brightness 60%, Blue              0.0 W
--- CONTROL L005 Desk Lamp ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Brightness (0-100)
 5. Set colour
 0. Back
Choose an option: 1

ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          ON    Brightness 60%, Blue              7.2 W
--- CONTROL L005 Desk Lamp ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Brightness (0-100)
 5. Set colour
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 4. Control a device: AC mode and TV channel

```text
Choose an option: 4
Device ID to control (0 to go back): AC001

ID     TYPE  NAME               STATE SETTINGS                          POWER
AC001  AC    Split AC           OFF   24 C, COOL                        0.0 W
--- CONTROL AC001 Split AC ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Temperature (16-30)
 5. Set mode
 0. Back
Choose an option: 5
   1. COOL
   2. DRY
   3. FAN
Mode 1-3 [default 1 = COOL]: 2

ID     TYPE  NAME               STATE SETTINGS                          POWER
AC001  AC    Split AC           OFF   24 C, DRY                         0.0 W
--- CONTROL AC001 Split AC ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Temperature (16-30)
 5. Set mode
 0. Back
Choose an option: 4
Set Temperature (16-30) [now 24]: 22

ID     TYPE  NAME               STATE SETTINGS                          POWER
AC001  AC    Split AC           ON    22 C, DRY                       900.0 W
--- CONTROL AC001 Split AC ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Temperature (16-30)
 5. Set mode
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
Choose an option: 4
Device ID to control (0 to go back): tv001

ID     TYPE  NAME               STATE SETTINGS                          POWER
TV001  TV    Smart TV           OFF   Vol 40, Ch 101                    0.0 W
--- CONTROL TV001 Smart TV ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Volume (0-100)
 5. Set channel
 0. Back
Choose an option: 5
Channel (1-999): 1000
Please enter a number between 1 and 999.
Channel (1-999): 205

ID     TYPE  NAME               STATE SETTINGS                          POWER
TV001  TV    Smart TV           OFF   Vol 40, Ch 205                    0.0 W
--- CONTROL TV001 Smart TV ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Set Volume (0-100)
 5. Set channel
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 4. Control a device: door lock with a wrong PIN

```text
Choose an option: 4
Device ID to control (0 to go back): DL001

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 5
Enter PIN: 1111
Wrong PIN. 2 attempt(s) left.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 5
Enter PIN: 1234
Unlocked.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    UNLOCKED                          5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 6
Current PIN: 1234
New PIN (1000-9999): 42
Please enter a number between 1000 and 9999.
New PIN (1000-9999): 5678
PIN changed.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    UNLOCKED                          5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 2
Lock powered off: it stays UNLOCKED but cannot be controlled until powered on.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     OFF   UNLOCKED                          0.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 4
Main Door Lock is powered off; turn it on first.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     OFF   UNLOCKED                          0.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 1

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    UNLOCKED                          5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 4

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 5
Enter PIN: 0000
Wrong PIN. 2 attempt(s) left.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 5
Enter PIN: 1111
Wrong PIN. 1 attempt(s) left.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 5
Enter PIN: 2222
Wrong PIN. The keypad is now LOCKED after 3 wrong attempts.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED [KEYPAD LOCKED]            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 5
Keypad is locked after 3 wrong PINs. It resets when the program restarts.

ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED [KEYPAD LOCKED]            5.0 W
--- CONTROL DL001 Main Door Lock ---
 1. Turn on
 2. Turn off
 3. Toggle
 4. Lock
 5. Unlock (PIN)
 6. Change PIN
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 5. Scenes: listing and running Night Mode

```text
Choose an option: 5

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 2
 1. Night Mode (8 steps)
    1. L001 Ceiling Light : TURN_OFF
    2. L002 Floor Lamp : TURN_OFF
    3. F001 Ceiling Fan : TURN_OFF
    4. AC001 Split AC : TURN_OFF
    5. TV001 Smart TV : TURN_OFF
    6. AC002 Bedroom AC : SET_LEVEL 26
    7. L003 Bedside Light : SET_LEVEL 20
    8. DL001 Main Door Lock : LOCK
 2. Away Mode (10 steps)
    1. L001 Ceiling Light : TURN_OFF
    2. L002 Floor Lamp : TURN_OFF
    3. L003 Bedside Light : TURN_OFF
    4. L004 Porch Light : TURN_OFF
    5. F001 Ceiling Fan : TURN_OFF
    6. F002 Bedroom Fan : TURN_OFF
    7. AC001 Split AC : TURN_OFF
    8. AC002 Bedroom AC : TURN_OFF
    9. TV001 Smart TV : TURN_OFF
    10. DL001 Main Door Lock : LOCK
 3. Movie Time (4 steps)
    1. L001 Ceiling Light : TURN_OFF
    2. L002 Floor Lamp : SET_LEVEL 30
    3. TV001 Smart TV : SET_LEVEL 40
    4. AC001 Split AC : SET_LEVEL 24

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 3
 1. Night Mode
 2. Away Mode
 3. Movie Time
Scene number (0 to cancel): 1
Running 'Night Mode'...
   1. [OK] L001 Ceiling Light -> OFF
   2. [OK] L002 Floor Lamp -> OFF
   3. [OK] F001 Ceiling Fan -> OFF
   4. [OK] AC001 Split AC -> OFF
   5. [OK] TV001 Smart TV -> OFF
   6. [OK] AC002 Bedroom AC -> Temperature 26
   7. [OK] L003 Bedside Light -> Brightness 20
   8. [OK] DL001 Main Door Lock -> LOCKED
Night Mode finished: 8 of 8 steps succeeded.

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
```

### 5. Scenes: creating a scene

```text
Choose an option: 1
Scene name: Night Mode
A scene named 'Night Mode' already exists. Choose another name.
Scene name: Garage Check

Scene 'Garage Check' has 0 steps.
 1. Add a step
 2. Remove a step
 3. Finish and save scene
 0. Cancel (discard scene)
Choose an option: 3
A scene needs at least 1 step.

Scene 'Garage Check' has 0 steps.
 1. Add a step
 2. Remove a step
 3. Finish and save scene
 0. Cancel (discard scene)
Choose an option: 1
Device ID for this step (0 to go back): L006
   1. TURN_ON
   2. TURN_OFF
   3. TOGGLE
   4. SET_LEVEL
Action (1-4): 1
Step 1 added: L006 Garage Light : TURN_ON

Scene 'Garage Check' has 1 step.
 1. Add a step
 2. Remove a step
 3. Finish and save scene
 0. Cancel (discard scene)
Choose an option: 1
Device ID for this step (0 to go back): L004
   1. TURN_ON
   2. TURN_OFF
   3. TOGGLE
   4. SET_LEVEL
Action (1-4): 4
Brightness (0-100): 120
Please enter a number between 0 and 100.
Brightness (0-100): 50
Step 2 added: L004 Porch Light : SET_LEVEL 50

Scene 'Garage Check' has 2 steps.
 1. Add a step
 2. Remove a step
 3. Finish and save scene
 0. Cancel (discard scene)
Choose an option: 1
Device ID for this step (0 to go back): DL001
   1. TURN_ON
   2. TURN_OFF
   3. TOGGLE
   4. LOCK
Action (1-4): 4
Step 3 added: DL001 Main Door Lock : LOCK

Scene 'Garage Check' has 3 steps.
 1. Add a step
 2. Remove a step
 3. Finish and save scene
 0. Cancel (discard scene)
Choose an option: 2
    1. L006 Garage Light : TURN_ON
    2. L004 Porch Light : SET_LEVEL 50
    3. DL001 Main Door Lock : LOCK
Step number to remove (0 to cancel): 3
Removed step: DL001 Main Door Lock : LOCK

Scene 'Garage Check' has 2 steps.
 1. Add a step
 2. Remove a step
 3. Finish and save scene
 0. Cancel (discard scene)
Choose an option: 3
Scene 'Garage Check' saved with 2 steps.

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 2
 1. Night Mode (8 steps)
    1. L001 Ceiling Light : TURN_OFF
    2. L002 Floor Lamp : TURN_OFF
    3. F001 Ceiling Fan : TURN_OFF
    4. AC001 Split AC : TURN_OFF
    5. TV001 Smart TV : TURN_OFF
    6. AC002 Bedroom AC : SET_LEVEL 26
    7. L003 Bedside Light : SET_LEVEL 20
    8. DL001 Main Door Lock : LOCK
 2. Away Mode (10 steps)
    1. L001 Ceiling Light : TURN_OFF
    2. L002 Floor Lamp : TURN_OFF
    3. L003 Bedside Light : TURN_OFF
    4. L004 Porch Light : TURN_OFF
    5. F001 Ceiling Fan : TURN_OFF
    6. F002 Bedroom Fan : TURN_OFF
    7. AC001 Split AC : TURN_OFF
    8. AC002 Bedroom AC : TURN_OFF
    9. TV001 Smart TV : TURN_OFF
    10. DL001 Main Door Lock : LOCK
 3. Movie Time (4 steps)
    1. L001 Ceiling Light : TURN_OFF
    2. L002 Floor Lamp : SET_LEVEL 30
    3. TV001 Smart TV : SET_LEVEL 40
    4. AC001 Split AC : SET_LEVEL 24
 4. Garage Check (2 steps)
    1. L006 Garage Light : TURN_ON
    2. L004 Porch Light : SET_LEVEL 50

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 2. Manage rooms: removing a room that still has devices

```text
Choose an option: 2

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 4
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (1 device)
 5. Garage (1 device)
Room number (0 to cancel): 5
This will also remove 1 device. Continue? (y/n): y
Room 'Garage' removed.
  Note: scene 'Garage Check' step 1 points to missing device L006 (it will be reported as failed when the scene runs).

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 2
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (1 device)

--- MANAGE ROOMS ---
 1. Add room
 2. List rooms
 3. Rename room
 4. Remove room
 5. Turn off all devices in a room (locks stay on)
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 5. Scenes: a step on a missing device fails, the rest still runs

```text
Choose an option: 5

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 3
 1. Night Mode
 2. Away Mode
 3. Movie Time
 4. Garage Check
Scene number (0 to cancel): 4
Running 'Garage Check'...
   1. [FAILED] L006 TURN_ON: No device with ID L006.
   2. [OK] L004 Porch Light -> Brightness 50
Garage Check finished: 1 of 2 steps succeeded.

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 4
 1. Night Mode
 2. Away Mode
 3. Movie Time
 4. Garage Check
Scene number (0 to cancel): 3
Delete scene 'Movie Time'? (y/n): y
Scene 'Movie Time' deleted.

--- SCENES ---
 1. Create scene
 2. List scenes
 3. Run scene
 4. Delete scene
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 6. Energy report (live power, kWh and cost, monthly estimate with slab bill)

```text
Choose an option: 6

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 1
Device ID (0 to go back): L004
Live power of L004 Porch Light: 5.0 W

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 2
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (1 device)
Room number (0 to cancel): 1

[ Living Room ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L001   LIGHT Ceiling Light      OFF   Brightness 75%, Warm White        0.0 W
L002   LIGHT Floor Lamp         OFF   Brightness 40%, Warm White        0.0 W
F001   FAN   Ceiling Fan        OFF   Speed 3/5                         0.0 W
AC001  AC    Split AC           OFF   22 C, DRY                         0.0 W
TV001  TV    Smart TV           OFF   Vol 40, Ch 205                    0.0 W
Room total: 0.0 W

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 3
  Living Room               0.0 W
  Bedroom                1352.0 W
  Entrance                 10.0 W
  Home Office               7.2 W
  WHOLE HOME             1369.2 W

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 4
Device ID (0 to go back): L005
Number of hours (0-720): abc
Please enter a number.
Number of hours (0-720): 5
L005 Desk Lamp for 5.0 h: 0.036 kWh, cost Rs. 0.29 at Rs. 8.00 per kWh

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 5
 1. Living Room (5 devices)
 2. Bedroom (3 devices)
 3. Entrance (2 devices)
 4. Home Office (1 device)
Room number (0 to cancel): 2
Number of hours (0-720): 8
Bedroom for 8.0 h: 10.816 kWh, cost Rs. 86.53 at Rs. 8.00 per kWh

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 6
Number of hours (0-720): 24
My Smart Home for 24.0 h: 32.861 kWh, cost Rs. 262.89 at Rs. 8.00 per kWh

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 7
Average hours per day (0-24) [default 8]: 25
Please enter a number between 0 and 24.
Average hours per day (0-24) [default 8]:

Monthly estimate for My Smart Home: current load 1369.2 W x 8.0 h/day x 30 days
  Units (kWh)    Flat @ Rs. 8.00/kWh        Slab tariff*
  328.61         Rs. 2,628.86               Rs. 1,550.26
  * Illustrative slab tariff: 0-100 units Rs. 3, 101-300 Rs. 5, 301-500 Rs. 7,
    above 500 Rs. 9 per unit, plus a fixed charge of Rs. 50.00.

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 9
Current rate: Rs. 8.00 per kWh (default Rs. 8.00).

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 8
New rate in Rs. per kWh (0.01-100): 0
Please enter a number between 0.01 and 100.
New rate in Rs. per kWh (0.01-100): 9.5
Rate changed to Rs. 9.50 per kWh for this session.

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 7
Average hours per day (0-24) [default 8]: 10

Monthly estimate for My Smart Home: current load 1369.2 W x 10.0 h/day x 30 days
  Units (kWh)    Flat @ Rs. 9.50/kWh        Slab tariff*
  410.76         Rs. 3,902.22               Rs. 2,125.32
  * Illustrative slab tariff: 0-100 units Rs. 3, 101-300 Rs. 5, 301-500 Rs. 7,
    above 500 Rs. 9 per unit, plus a fixed charge of Rs. 50.00.

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 9
Current rate: Rs. 9.50 per kWh (default Rs. 8.00).

--- ENERGY REPORT ---
 1. Live power of a device
 2. Live power of a room
 3. Live power of the whole home
 4. Energy and cost of a device for N hours
 5. Energy and cost of a room for N hours
 6. Energy and cost of the whole home for N hours
 7. Monthly estimate for the home (flat vs slab)
 8. Change electricity rate (this session)
 9. Show current rate
 0. Back
Choose an option: 0

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 7. Activity log

```text
Choose an option: 7
How many recent entries (1-100) [default 20]: abc
Please enter a whole number.
How many recent entries (1-100) [default 20]: 15
2026-10-08 10:52:00 | CONTROL | DL001 Main Door Lock wrong PIN entered (2 attempt(s) left)
2026-10-08 10:52:01 | CONTROL | DL001 Main Door Lock UNLOCKED
2026-10-08 10:52:02 | CONTROL | DL001 Main Door Lock PIN changed
2026-10-08 10:52:03 | CONTROL | DL001 Main Door Lock turned OFF
2026-10-08 10:52:03 | CONTROL | DL001 Main Door Lock turned ON
2026-10-08 10:52:04 | CONTROL | DL001 Main Door Lock LOCKED
2026-10-08 10:52:04 | CONTROL | DL001 Main Door Lock wrong PIN entered (2 attempt(s) left)
2026-10-08 10:52:05 | CONTROL | DL001 Main Door Lock wrong PIN entered (1 attempt(s) left)
2026-10-08 10:52:06 | CONTROL | DL001 Main Door Lock keypad locked after 3 wrong PINs
2026-10-08 10:52:08 | SCENE   | Night Mode ran: 8 of 8 steps succeeded
2026-10-08 10:52:14 | SCENE   | Scene Garage Check created with 2 steps
2026-10-08 10:52:16 | ROOM    | Room Garage removed with 1 device
2026-10-08 10:52:18 | SCENE   | Garage Check ran: 1 of 2 steps succeeded
2026-10-08 10:52:19 | SCENE   | Scene Movie Time deleted
2026-10-08 10:52:27 | ENERGY  | Rate changed from Rs. 8.00 to Rs. 9.50 per kWh
Press Enter to continue...

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 8. Save now

```text
Choose an option: 8
Saved: 4 rooms, 11 devices, 3 scenes.

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### 0. Save and exit

```text
Choose an option: 0
State saved. Goodbye!
```

## Run 2 - restart: state restored, then end of input

### Restart: the saved state is restored

```text
===========================================
       SMART HOME DEVICE CONTROLLER
   Java OOP mini project - console edition
===========================================
Loaded My Smart Home: 4 rooms, 11 devices, 3 scenes.

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
Choose an option: 1

=============== DASHBOARD: My Smart Home ===============

[ Living Room ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L001   LIGHT Ceiling Light      OFF   Brightness 75%, Warm White        0.0 W
L002   LIGHT Floor Lamp         OFF   Brightness 40%, Warm White        0.0 W
F001   FAN   Ceiling Fan        OFF   Speed 3/5                         0.0 W
AC001  AC    Split AC           OFF   22 C, DRY                         0.0 W
TV001  TV    Smart TV           OFF   Vol 40, Ch 205                    0.0 W
Room total: 0.0 W

[ Bedroom ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L003   LIGHT Bedside Light      ON    Brightness 20%, Warm White        2.0 W
F002   FAN   Bedroom Fan        OFF   Speed 2/5                         0.0 W
AC002  AC    Bedroom AC         ON    26 C, COOL                     1350.0 W
Room total: 1352.0 W

[ Entrance ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
DL001  LOCK  Main Door Lock     ON    LOCKED                            5.0 W
L004   LIGHT Porch Light        ON    Brightness 50%, Warm White        5.0 W
Room total: 10.0 W

[ Home Office ]
ID     TYPE  NAME               STATE SETTINGS                          POWER
L005   LIGHT Desk Lamp          ON    Brightness 60%, Blue              7.2 W
Room total: 7.2 W
-------------------------------------------
Total live power: 1369.2 W
Devices ON: 5 of 11
Press Enter to continue...

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
```

### End of input (Ctrl+D) saves and exits cleanly

```text
Choose an option:
End of input detected.
State saved. Goodbye!
```

## Run 3 - a corrupt line in the save file

Before this run one corrupt line was added to `data/home_state.txt` (under `ROOM|Home Office`):

```text
DEVICE|FAN|F009|Broken Fan|true|75.0|fast
```

### Corrupt line in home_state.txt is skipped with a warning

```text
===========================================
       SMART HOME DEVICE CONTROLLER
   Java OOP mini project - console edition
===========================================
Warning: skipped line 16 of home_state.txt (bad number: For input string: "fast")
Loaded My Smart Home: 4 rooms, 11 devices, 3 scenes.

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
Choose an option: 0
State saved. Goodbye!
```

## Run 4 - data folder deleted, empty home chosen

Before this run the `data/` folder was deleted.

### Deleted data folder: answering "n" starts an empty home

```text
===========================================
       SMART HOME DEVICE CONTROLLER
   Java OOP mini project - console edition
===========================================
No saved home found. Load demo home? (y/n): n
Enter a name for your home:
Name cannot be empty.
Enter a name for your home: My|Flat
Name cannot contain the '|' character.
Enter a name for your home: My Flat
Created empty home 'My Flat'. Start by adding a room.

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
Choose an option: 1

=============== DASHBOARD: My Flat ===============
(no rooms yet - use 2. Manage rooms)
-------------------------------------------
Total live power: 0.0 W
Devices ON: 0 of 0
Press Enter to continue...

---------------- MAIN MENU ----------------
 1. Dashboard            6. Energy report
 2. Manage rooms         7. Activity log
 3. Manage devices       8. Save now
 4. Control a device     9. Reset to demo home
 5. Scenes               0. Save and exit
-------------------------------------------
Choose an option: 0
State saved. Goodbye!
```

## Input file used for run 1

Each line is one thing typed at a prompt (an empty line means just pressing Enter). Lines starting with `##` are only section labels for this page and are not sent to the program.

```text
## First run: no save file, so the demo home is offered
maybe
y
## 1. Dashboard
1

## Invalid input at the main menu
abc
12

## 9. Reset to demo home
9
n
9
y
## 2. Manage rooms (with invalid names)
2
1

Garage|Shed
This room name is far too long to be accepted
living room
1
Study
1
Garage
2
3
4
Home Office
5
2
0
## 3. Manage devices: add, list, search, rename, change rating, remove (with invalid input)
3
1
9
4
7
1
Desk Lamp
-5

abc
80
2
1
4
3
Office AC



1
5
1
Garage Light



3
4
4
l005
4
X999
5
X999
ac003
Study AC
6
L005
0
12
2
AC003
maybe
y
0
## 4. Control a device: adjusting a light
4
L005
4
150
60
5
5
3
1
0
## 4. Control a device: AC mode and TV channel
4
AC001
5
2
4
22
0
4
tv001
5
1000
205
0
## 4. Control a device: door lock with a wrong PIN
4
DL001
5
1111
5
1234
6
1234
42
5678
2
4
1
4
5
0000
5
1111
5
2222
5
0
## 5. Scenes: listing and running Night Mode
5
2
3
1
## 5. Scenes: creating a scene
1
Night Mode
Garage Check
3
1
L006
1
1
L004
4
120
50
1
DL001
4
2
3
3
2
0
## 2. Manage rooms: removing a room that still has devices
2
4
5
y
2
0
## 5. Scenes: a step on a missing device fails, the rest still runs
5
3
4
4
3
y
0
## 6. Energy report (live power, kWh and cost, monthly estimate with slab bill)
6
1
L004
2
1
3
4
L005
abc
5
5
2
8
6
24
7
25

9
8
0
9.5
7
10
9
0
## 7. Activity log
7
abc
15

## 8. Save now
8
## 0. Save and exit
0
```
