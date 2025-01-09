#WiFi Reconnecter for Android
A lightweight Android app designed to periodically check internet connectivity and automatically toggle WiFi off and on if the connection is lost.
I made it for my old Android tablet that has exactly this issue.

#How It Works
The app runs a background task every 15 minutes.
It checks the internet connection by making an HTTP GET request to a specified URL.
If the request fails (e.g., the device cannot reach the internet), the app turns off WiFi for 10 seconds and then turns it back on.
This helps restore WiFi connectivity on devices with unstable network behavior.

#Contributing
Contributions are welcome! If you'd like to improve the app, fix issues, or add features:

#License
This project is open-source and available under the [MIT License](https://choosealicense.com/licenses/mit/).

#Disclaimer
This app is provided as-is and should be used at your own risk. While it has been tested, it is designed for older, potentially unreliable Android devices and may not work optimally on newer devices.
