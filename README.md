# BlackboxSticksExporter
Description:
BlackboxSticksExporter is used to export Sticks from multiple Blackbox Logs at the same time (Multithreaded batch export). The videos are in the .mov file format and feature transparency.

Usage:
1. export the .BFL files as .csv files in the Betaflight Blackbox Explorer
2. run the BlackboxSticksExporter.exe and close it to generate directories
3. move all .csv files in the /LOG directory
4. open the settings.xml file with any editor and check the settings
5. run the BlackboxSticksExporter.exe and wait until all the files are done.
6. the exported videos are now located in the /OUTPUT directory.

Features:
- variable Framerate / Resolution / Tail length / Border (Shadow) / Background
- Batch export
- .mov with alpha

Settings:
- "borderThickness": 0...5
- "backgroundColor": Color in Hex: #000000...#FFFFFF
- "backgroundOpacity": Opacity: 0...255 (0: 100% transparent, 255: 0% transparent)
