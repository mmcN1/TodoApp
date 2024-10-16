# Note-X App

## Project Description
This project is an Android application that helps users manage their to-do lists. Users can add, edit, delete, and search their notes.

## Features

### Splash Screen:
- The initial screen displayed when the app is launched.
- Simple design featuring the app name and logo.


### Login Screen:
- Allows users to sign in with their Google account.
- Secure login process using Firebase Authentication.
![Splash Screen](https://github.com/user-attachments/assets/bea3c658-8145-4b13-9457-797a636de060)
- 


### Main Menu:
- The main screen where users can view their existing notes.
- Separate lists for pinned and other notes.
- Includes buttons for editing notes and adding new ones.
![Login Screen](https://github.com/user-attachments/assets/4c39092d-c9f1-4fa8-ac07-04565f0908b5)
- 


### Add Note Section:
- A screen where users can add new notes.
- Uses `TextInputLayout` for title and note content.
- Saves the note to Firebase when submitted.
![Main Menu](https://github.com/user-attachments/assets/5647ce15-582f-4cc9-beb2-da41b055b864)
- 


### Accessing Old Notes:
- Users can tap on existing notes to view their details.
- Old notes can be viewed and edited.
![Add Note Section](https://github.com/user-attachments/assets/5647ce15-582f-4cc9-beb2-da41b055b864)
- 

### Note Deletion Section:
- Users can tap on a note to access the delete option.
- Deletion process includes a confirmation prompt.


### Profile Section:
- An area where users can view and edit their profile information.
- Includes profile picture and basic information.
- Contains an option to log out.
![Note Deletion Section](https://github.com/user-attachments/assets/6a5eca32-99e2-4493-bfc1-c1ee4ad87ea4)
- 

## Technologies Used
- **Android**: The primary platform for app development.
- **Firebase**: For user authentication and data storage.
- **Room Database**: For local database management.
- **Glide**: For image loading.
- **MaterialSearchView**: For search functionality.

## Setup

### Install Required Dependencies:
- Add necessary Firebase and Room dependencies to your `build.gradle` file.

### Create a Firebase Project:
- Create a project in the Firebase console and configure the required settings.
- Download the `google-services.json` file for your app and add it to your project’s `app` directory.

### Run the Application:
- Open the project in Android Studio.
- Run the application on a device or emulator.

## Usage
1. Open the app and sign in with your Google account from the **Login Screen**.
2. Browse your notes in the main menu.
3. Click the **Add Note** button to create a new note.
4. Tap on an existing note to view or edit its content.
5. To delete a note, click on it and confirm the deletion option.
6. Update your user information or log out from the profile section.
