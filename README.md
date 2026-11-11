# Nanny System Application

## Overview
The Nanny System is a secure, role-based Android application and web portal designed to bridge the gap between parents seeking reliable childcare and professional nannies seeking freelance work. Built with Kotlin and powered by Firebase, the platform digitizes the childcare marketplace by providing real-time booking management, integrated messaging, and strict administrative oversight to ensure community safety.

## Key Features
* **Role-Based Architecture:** Custom session management dynamically routes users into three distinct interfaces: Parent Marketplace, Nanny Dashboard, and Administrator Control Panel.
* **Security & Approval Gate:** To enforce platform safety, newly registered Nannies are automatically assigned a `pending` status and locked out of the community until manually verified and `approved` by an Administrator.
* **Real-Time Synchronization:** Utilizes Cloud Firestore snapshot listeners to ensure booking requests, status updates, and chat messages sync instantly across mobile and web platforms without requiring manual page refreshes.
* **Dynamic Profiles:** Nannies can manage and update their professional bios, geographic location, and hourly rates in real-time.
* **In-App Communication:** An integrated, bi-directional chat channel unlocks automatically once a Parent's booking is officially accepted by a Nanny.

## Technology Stack
* **Frontend Mobile:** Native Android (Kotlin, XML, Google Material Design Components)
* **Backend Database:** Firebase Cloud Firestore (NoSQL)
* **Authentication:** Firebase Authentication
* **Local Storage:** Android SharedPreferences (Custom Session Manager)

## Database Schema (Firestore)
* **`users` Collection:** Stores user profiles and credentials.
  * Fields: `userId`, `name`, `email`, `role` (admin/parent/nanny), `accountStatus` (pending/approved), `location`, `hourlyRate`, `bio`.
* **`bookings` Collection:** Stores transaction and job lifecycle data.
  * Fields: `id`, `parentName`, `nannyName`, `date`, `time`, `status` (pending/accepted/completed/declined/paid).

## Installation and Setup
1. Clone the repository to your local machine.
2. Open the project in **Android Studio**.
3. Connect the project to your Firebase Console:
   * Create a new Firebase Project.
   * Enable **Firestore Database** and **Email/Password Authentication**.
   * Download the `google-services.json` file and place it in the `app/` directory of the project.
4. Sync Gradle files and rebuild the project.
5. Deploy the application to an Android Emulator or a physical Android device running API level 24 or higher.

## Author
**Melokuhle Ncamane Maseko**  
