# BlackboxSticksExporter
Description:
BlackboxSticksExporter is used to export Sticks from multiple Blackbox Logs at the same time (Multithreaded batch export). The videos are in the .mov file format and feature transparency.

Usage:
1. run the BlackboxSticksExporter.exe
2. select the Input and Output Folder
3. check the settings
4. start Rendering

Features:
- variable Framerate / Resolution / Tail length / Border (Shadow) / Background
- Batch export
- .mov with alpha
- automatic shutdown / sound notification when finished


Settings:
- "borderThickness": 0...100 (increases render time a lot)
- "backgroundColor": Color in Hex: #000000...#FFFFFF
- "backgroundOpacity": Opacity: 0...255 (0: 100% transparent, 255: 0% transparent)
- "stickColor": Color in Hex: #000000...#FFFFFF (betaflight default: #FF6666)
- "sticksModeVertPos": Vertical position of the mode in percent 0...100 (0: top, 100: bottom)
