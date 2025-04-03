# City Cycle

City Cycle App is a mobile application for renting bicycles. Users can locate available bikes, unlock them via QR code scanning, and make payments seamlessly.

# Features

* User Authentication: Sign up and log in functionality with password updating function.
* Bike Availability Tracking: View nearby bikes per location on a map and list.
* Book bike: Book a bicycle from a renting station, select time duration, etc
* Ride History: Track past rides and payments
* 

# Tech Stack

Language: Kotlin

Framework: Android SDK

Backend: SQLite

Maps API: Google Maps

# Installation

## Prerequisites

Android Studio installed

Kotlin and Gradle configured

## Steps

1. Clone the repository:

```bash
git clone https://github.com/your-repo/City-Cycle-App.git
```

2. Open the project in Android Studio.

3. Sync dependencies:
```bash
./gradlew build
```

4. Run the application on an emulator or physical device.

## Configuration

Configure API keys in the local.properties file:
 ```
MAPS_API_KEY=your_google_maps_api_key
STRIPE_API_KEY=your_payment_api_key
```

# Usage

1. Register or log in.
2. Locate available bikes on the map, or in the available bikes list.
3. Click on Book Bikes on the available bikes lists page and book a bike or schedule bike.
4. View booking history on the My Rentals page.
5. Change account password via the My Account page
6. View sample disconts and offers on the Promotions and Discounts page




