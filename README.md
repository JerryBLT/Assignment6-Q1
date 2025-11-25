# Assignment6-Q1
This project implements a real-time altimeter using the pressure sensor on Android devices. The altitude is computed using the standard barometric formula and visualized with a dynamic UI built in Jetpack Compose.

## Features
- Reads live pressure from the device sensor (Sensor.TYPE_PRESSURE)
- Converts pressure to altitude using: <br>
<img width="508" height="132" alt="image" src="https://github.com/user-attachments/assets/79a01d5b-a72f-4f0a-94d2-a7f0fc16f3aa" />
</br>

- Background color darkens as altitude increases
- Simulation mode with slider 
- Real-time updates using Compose state
- Lifecycle-safe sensor registration (onResume / onPause)

## How to Use
- Run the app on a physical device or emulator
- Toggle Simulation Mode to manually test altitude
- Slide pressure values → altitude and background update instantly
