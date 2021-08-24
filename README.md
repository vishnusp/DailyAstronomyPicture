# DailyAstronomyPicture
Android app to display daily astronomy picture using NASA APOD API.

# Instructions to run the code
* Clone the git repository and import the code to Android Studio
* Once the code sync is done by Android Studio, launch an emulator/connect a real device
* Click the 'Run'(Play) button from Android Studio
* To execute from command line, use the command gradlew clean build. The generated APK will be available in app/build folder

# Improvement Areas
* Unit tests to be included
* Currently memory cache is used for image caching, use Disk Cache instead
* Execute and fix lint warning
* Add signing config and proguard rules