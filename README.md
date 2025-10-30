# GreenLedger

An Android application for farmers and labourers to manage their expenses, raw materials, and labour time tracking.

## Features

- **User Authentication**: Phone number-based registration and login using Firebase Authentication
- **Expense Management**: Track and categorize farm-related expenses (seeds, fertilizers, equipment, etc.)
- **Raw Materials Tracking**: Monitor inventory of raw materials with quantity and cost tracking
- **Labour Management**: Record labour hours, rates, and calculate total payments
- **Firebase Integration**: Real-time data sync with Firebase Realtime Database

## Technology Stack

- **Language**: Java 17+ (Compatible with Java 21)
- **Build Tool**: Gradle 8.5
- **Android Gradle Plugin**: 8.2.2
- **Android SDK**: API 34 (Android 14)
- **Minimum SDK**: API 24 (Android 7.0)
- **Backend**: Firebase Authentication, Firebase Realtime Database
- **UI**: Material Design 3 Components

## Project Structure

```
GreenLedger/
├── app/
│   ├── src/main/
│   │   ├── java/com/greenledger/app/
│   │   │   ├── activities/          # All activity classes
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── DashboardActivity.java
│   │   │   │   ├── ExpenseActivity.java
│   │   │   │   ├── RawMaterialActivity.java
│   │   │   │   └── LabourActivity.java
│   │   │   ├── adapters/            # RecyclerView adapters
│   │   │   │   ├── ExpenseAdapter.java
│   │   │   │   ├── RawMaterialAdapter.java
│   │   │   │   └── LabourAdapter.java
│   │   │   ├── models/              # Data models
│   │   │   │   ├── User.java
│   │   │   │   ├── Expense.java
│   │   │   │   ├── RawMaterial.java
│   │   │   │   └── Labour.java
│   │   │   └── utils/               # Helper classes
│   │   │       └── FirebaseHelper.java
│   │   ├── res/
│   │   │   ├── drawable/            # App icon (green leaf design)
│   │   │   │   ├── ic_launcher.xml
│   │   │   │   └── ic_launcher_background.xml
│   │   │   ├── layout/              # XML layouts
│   │   │   │   ├── activity_login.xml
│   │   │   │   ├── activity_register.xml
│   │   │   │   ├── activity_dashboard.xml
│   │   │   │   ├── activity_expense.xml
│   │   │   │   ├── activity_raw_material.xml
│   │   │   │   ├── activity_labour.xml
│   │   │   │   ├── dialog_add_expense.xml
│   │   │   │   ├── dialog_add_material.xml
│   │   │   │   ├── dialog_add_labour.xml
│   │   │   │   ├── item_expense.xml
│   │   │   │   ├── item_material.xml
│   │   │   │   └── item_labour.xml
│   │   │   ├── values/              # Strings, colors, themes
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── menu/                # Menu resources
│   │   │   │   └── dashboard_menu.xml
│   │   │   └── xml/                 # Backup and data extraction rules
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle                 # App-level build file
│   ├── proguard-rules.pro           # ProGuard configuration
│   └── google-services.json         # Firebase configuration
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties # Gradle 8.5 configuration
├── build.gradle                     # Project-level build file
├── settings.gradle                  # Project settings
├── gradle.properties                # Gradle properties
├── local.properties                 # Local SDK path (not in git)
├── .gitignore                       # Git ignore rules
└── README.md                        # This file
```

## Prerequisites

1. **Android Studio**: Arctic Fox (2020.3.1) or newer (recommended: Hedgehog 2023.1.1+)
2. **JDK**: Java 17 or higher (tested with Java 21)
3. **Android SDK**: API 34 (Android 14)
4. **Firebase Project**: Already configured with google-services.json

## Setup Instructions

### 1. Clone or Open the Project

Open the project in Android Studio or your preferred IDE.

### 2. Configure SDK Path

Edit `local.properties` and set your Android SDK path:
```properties
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### 3. Firebase Configuration

The project already includes `google-services.json` with Firebase configuration for:
- **Project ID**: greenledger-e0d3a
- **Package Name**: com.greenledger.app

#### **CRITICAL**: Enable Firebase Services (REQUIRED BEFORE RUNNING APP)

The app will crash with authentication errors if Firebase services are not properly configured.

**Step-by-Step Firebase Setup:**

1. **Go to Firebase Console**
   - Visit [Firebase Console](https://console.firebase.google.com/)
   - Select your project: **greenledger-e0d3a**

2. **Enable Authentication (REQUIRED)**
   - In the left sidebar, click **Authentication**
   - Click **Get Started** (if first time)
   - Go to the **Sign-in method** tab
   - Find **Email/Password** in the provider list
   - Click on **Email/Password**
   - Toggle **Enable** switch to ON
   - Click **Save**

   ⚠️ **Without this step, you will get this error:**
   ```
   This operation is not allowed. This may be because the given
   sign-in provider is disabled for this Firebase project.
   ```

3. **Enable Realtime Database (REQUIRED)**
   - In the left sidebar, click **Realtime Database**
   - Click **Create Database**
   - Choose a location (e.g., us-central1)
   - Start in **Test mode** (for development)
   - Click **Enable**

   ⚠️ **For production, update security rules (see section below)**

4. **Verify Configuration**
   - Authentication → Sign-in method → Email/Password should show "Enabled"
   - Realtime Database should show your database URL

### 4. Firebase Security Rules (Recommended)

For Realtime Database, use these security rules:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "expenses": {
      ".read": "auth != null",
      ".write": "auth != null",
      ".indexOn": ["userId"]
    },
    "rawMaterials": {
      ".read": "auth != null",
      ".write": "auth != null",
      ".indexOn": ["userId"]
    },
    "labour": {
      ".read": "auth != null",
      ".write": "auth != null",
      ".indexOn": ["userId"]
    }
  }
}
```

### 5. Build the Project

#### Using Android Studio:
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build** → **Make Project**
4. Run on emulator or device: **Run** → **Run 'app'**

#### Using Command Line:
```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Application Flow

1. **Login Screen**: Users log in with phone number and password
2. **Registration Screen**: New users register with name, phone, user type (Farmer/Labourer), and password
3. **Dashboard**: Main screen with cards for:
   - Expense Management
   - Raw Materials
   - Labour Management
4. **Feature Screens**: Each feature has its own screen with list view and add functionality

## User Authentication

- Phone numbers are converted to email format for Firebase Auth: `{phone}@greenledger.app`
- Passwords must be at least 6 characters
- User data is stored in Firebase Realtime Database under `/users/{userId}`

## Database Schema

### Users
```json
{
  "userId": "string",
  "name": "string",
  "phone": "string",
  "userType": "Farmer|Labourer",
  "createdAt": timestamp
}
```

### Expenses
```json
{
  "expenseId": "string",
  "userId": "string",
  "category": "string",
  "amount": number,
  "description": "string",
  "date": "string",
  "timestamp": timestamp
}
```

### Raw Materials
```json
{
  "materialId": "string",
  "userId": "string",
  "name": "string",
  "quantity": number,
  "unit": "string",
  "costPerUnit": number,
  "timestamp": timestamp
}
```

### Labour
```json
{
  "labourId": "string",
  "userId": "string",
  "name": "string",
  "phone": "string",
  "hoursWorked": number,
  "hourlyRate": number,
  "workDate": "string",
  "workDescription": "string",
  "timestamp": timestamp
}
```

## Troubleshooting

### Gradle Build Fails

**Issue**: Java version incompatibility
```
Your build is currently configured to use incompatible Java X and Gradle Y
```
**Solution**:
- This project uses **Gradle 8.5** which is compatible with **Java 17-21**
- Ensure you have Java 17+ installed: `java -version`
- In Android Studio: **File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK** (select Java 17 or 21)
- Clean and rebuild: `gradlew clean build`

**Issue**: Resource linking failed (missing launcher icons)
```
AAPT: error: resource mipmap/ic_launcher not found
```
**Solution**:
- This has been fixed by using drawable icons instead of mipmap
- Ensure `app/src/main/res/drawable/ic_launcher.xml` exists
- The app icon is a green leaf design representing agriculture

### Firebase Connection Issues
- Verify `google-services.json` is in the `app/` directory
- Check Firebase console for project status
- Ensure Firebase services are enabled:
  - **Authentication** → Email/Password provider must be enabled
  - **Realtime Database** → Database must be created (use test mode for development)

### App Crashes on Launch
- Check Firebase Authentication is enabled
- Verify Realtime Database is created
- Enable **Email/Password** authentication method in Firebase Console
- Check logcat for error messages: `adb logcat | grep -i firebase`

### Build Configuration
- **Gradle Version**: 8.5 (configured in `gradle/wrapper/gradle-wrapper.properties`)
- **Android Gradle Plugin**: 8.2.2 (configured in root `build.gradle`)
- **Java Compatibility**: Source and target set to Java 17 (in `app/build.gradle`)

## App Icon

The app features a custom-designed green leaf icon that represents:
- 🌿 **Agriculture and farming** - Core purpose of the application
- 🟢 **Growth and sustainability** - Green color symbolizes nature
- 📱 **Modern design** - Vector drawable for crisp display on all devices

The icon is implemented as a vector drawable (`ic_launcher.xml`) ensuring:
- Sharp display on all screen densities
- Small file size
- Easy customization

## Recent Updates

### Version 1.0 (Current)
- ✅ Fixed Gradle compatibility with Java 21
- ✅ Updated to Gradle 8.5 and Android Gradle Plugin 8.2.2
- ✅ Replaced mipmap launcher icons with vector drawable
- ✅ Created custom green leaf app icon
- ✅ Full Firebase integration for authentication and database
- ✅ Complete CRUD operations for expenses, materials, and labour
- ✅ Material Design 3 UI components
- ✅ Phone number-based authentication
- ✅ Real-time data synchronization

## Future Enhancements

- 📊 Dashboard analytics and charts (expense trends, category breakdown)
- 📄 PDF export of expense reports
- 🌐 Multi-language support (Hindi, Marathi, Tamil, Telugu, and other regional languages)
- 💾 Offline mode with automatic sync when online
- 🔔 Push notifications for payment reminders and expense alerts
- 📷 Image attachments for receipts and invoices
- 🔍 Advanced search and filter options
- 📈 Monthly/yearly expense comparison
- 👥 Multi-user collaboration for farm management
- 🌤️ Weather integration for agricultural planning
- 💰 Profit/loss calculation tools
- 📱 Crop management and harvest tracking

## Contributing

This is an open-source project for agricultural management. Contributions are welcome!

### How to Contribute:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is created for agricultural management purposes and is available for educational and commercial use.

## Support

For issues or questions:
- Check Firebase Console logs for backend issues
- Use Android Logcat for app debugging: `adb logcat | grep GreenLedger`
- Review the Troubleshooting section above
- File an issue in the project repository

## Acknowledgments

- Built with ❤️ for farmers and agricultural workers
- Firebase for backend services
- Material Design for UI components
- Android Open Source Project

---

**GreenLedger** - Simplifying farm management, one entry at a time. 🌾
