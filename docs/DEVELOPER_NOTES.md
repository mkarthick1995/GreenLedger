# GreenLedger - Developer Notes

## Project Overview

GreenLedger is an Android application designed for farmers and labourers to manage their expenses, raw materials inventory, and labour time tracking. The app uses Firebase for authentication and real-time data synchronization.

**Package**: `com.greenledger.app`
**Version**: 1.0 (Version Code: 1)
**Last Updated**: October 31, 2025

---

## Table of Contents

1. [Technology Stack](#technology-stack)
2. [Build Configuration](#build-configuration)
3. [Project Structure](#project-structure)
4. [Firebase Configuration](#firebase-configuration)
5. [Architecture & Design Patterns](#architecture--design-patterns)
6. [Database Schema](#database-schema)
7. [Authentication Flow](#authentication-flow)
8. [UI/UX Design](#uiux-design)
9. [Dependencies](#dependencies)
10. [Build & Deployment](#build--deployment)
11. [Known Issues & Solutions](#known-issues--solutions)
12. [Development Guidelines](#development-guidelines)
13. [Testing](#testing)
14. [Future Enhancements](#future-enhancements)

---

## Technology Stack

### Core Technologies
- **Language**: Java 17+ (compatible with Java 21)
- **Build System**: Gradle 8.5
- **Android Gradle Plugin**: 8.2.2
- **IDE**: Android Studio Hedgehog (2023.1.1+) or newer

### Android SDK
- **Compile SDK**: API 34 (Android 14)
- **Target SDK**: API 34 (Android 14)
- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Device Coverage**: ~95% of active Android devices

### Backend Services
- **Firebase Authentication**: Email/Password provider
- **Firebase Realtime Database**: Real-time data synchronization
- **Firebase BOM**: 32.7.0

### UI Framework
- **Material Design**: 3.x (version 1.11.0)
- **AndroidX Libraries**: Latest stable versions
- **Design Pattern**: Activity-based with Material Components

---

## Build Configuration

### Gradle Configuration

#### Root `build.gradle`
```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.2'
        classpath 'com.google.gms:google-services:4.4.0'
    }
}

plugins {
    id 'com.android.application' version '8.2.2' apply false
}
```

#### App-level `build.gradle`
```gradle
plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.greenledger.app'
    compileSdk 34

    defaultConfig {
        applicationId "com.greenledger.app"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
}
```

#### `gradle-wrapper.properties`
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

### AndroidManifest Configuration

```xml
<application
    android:enableOnBackInvokedCallback="true"
    android:icon="@drawable/ic_launcher"
    android:theme="@style/Theme.GreenLedger">
```

**Key Features**:
- Back gesture support enabled (Android 13+)
- Custom vector drawable icon
- Material Theme 3

---

## Project Structure

```
com.greenledger.app/
├── activities/
│   ├── LoginActivity.java          # Entry point, handles user login
│   ├── RegisterActivity.java       # New user registration
│   ├── DashboardActivity.java      # Main navigation hub
│   ├── ExpenseActivity.java        # Expense management
│   ├── RawMaterialActivity.java    # Raw materials tracking
│   └── LabourActivity.java         # Labour management
├── adapters/
│   ├── ExpenseAdapter.java         # RecyclerView adapter for expenses
│   ├── RawMaterialAdapter.java     # RecyclerView adapter for materials
│   └── LabourAdapter.java          # RecyclerView adapter for labour
├── models/
│   ├── User.java                   # User data model
│   ├── Expense.java                # Expense data model
│   ├── RawMaterial.java            # Raw material data model
│   └── Labour.java                 # Labour data model
└── utils/
    └── FirebaseHelper.java         # Firebase singleton helper
```

### Resource Structure

```
res/
├── drawable/
│   ├── ic_launcher.xml             # App icon (green leaf design)
│   └── ic_launcher_background.xml  # Icon background
├── layout/
│   ├── activity_*.xml              # Activity layouts (6 files)
│   ├── dialog_add_*.xml            # Dialog layouts (3 files)
│   └── item_*.xml                  # RecyclerView item layouts (3 files)
├── values/
│   ├── strings.xml                 # String resources
│   ├── colors.xml                  # Color palette
│   └── themes.xml                  # Material Theme definitions
├── menu/
│   └── dashboard_menu.xml          # Dashboard toolbar menu
└── xml/
    ├── backup_rules.xml            # Backup configuration
    └── data_extraction_rules.xml   # Data extraction rules
```

---

## Firebase Configuration

### Project Details
- **Project ID**: greenledger-e0d3a
- **Package Name**: com.greenledger.app
- **Database Region**: us-central1 (default)

### Authentication Configuration
- **Provider**: Email/Password
- **Email Verification**: Disabled (using phone as identifier)
- **Password Policy**: Minimum 6 characters

### Authentication Implementation
```java
// Phone number is converted to email format
String email = phone + "@greenledger.app";

// Example: 9876543210 becomes 9876543210@greenledger.app
firebaseAuth.signInWithEmailAndPassword(email, password);
```

### Realtime Database Structure
```
greenledger-e0d3a/
├── users/
│   └── {userId}/
│       ├── userId: String
│       ├── name: String
│       ├── phone: String
│       ├── userType: String ("Farmer" | "Labourer")
│       └── createdAt: Long (timestamp)
├── expenses/
│   └── {expenseId}/
│       ├── expenseId: String
│       ├── userId: String
│       ├── category: String
│       ├── amount: Double
│       ├── description: String
│       ├── date: String (dd/MM/yyyy)
│       └── timestamp: Long
├── rawMaterials/
│   └── {materialId}/
│       ├── materialId: String
│       ├── userId: String
│       ├── name: String
│       ├── quantity: Double
│       ├── unit: String
│       ├── costPerUnit: Double
│       └── timestamp: Long
└── labour/
    └── {labourId}/
        ├── labourId: String
        ├── userId: String
        ├── name: String
        ├── phone: String
        ├── hoursWorked: Double
        ├── hourlyRate: Double
        ├── workDate: String (dd/MM/yyyy)
        ├── workDescription: String
        └── timestamp: Long
```

### Security Rules (Production)
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
      ".indexOn": ["userId"],
      "$expenseId": {
        ".validate": "newData.child('userId').val() === auth.uid"
      }
    },
    "rawMaterials": {
      ".read": "auth != null",
      ".write": "auth != null",
      ".indexOn": ["userId"],
      "$materialId": {
        ".validate": "newData.child('userId').val() === auth.uid"
      }
    },
    "labour": {
      ".read": "auth != null",
      ".write": "auth != null",
      ".indexOn": ["userId"],
      "$labourId": {
        ".validate": "newData.child('userId').val() === auth.uid"
      }
    }
  }
}
```

---

## Architecture & Design Patterns

### Architectural Pattern
**Activity-Based Architecture** with Firebase backend

### Design Patterns Used

#### 1. Singleton Pattern
```java
// FirebaseHelper.java
public class FirebaseHelper {
    private static FirebaseHelper instance;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }
}
```

#### 2. ViewHolder Pattern
Used in all RecyclerView adapters for efficient view recycling:
```java
static class ExpenseViewHolder extends RecyclerView.ViewHolder {
    // View references cached for reuse
}
```

#### 3. Observer Pattern
Firebase real-time listeners for data updates:
```java
firebaseHelper.getExpensesRef()
    .addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            // Update UI with new data
        }
    });
```

### Component Communication
- **Activity to Activity**: Intents
- **Activity to Firebase**: FirebaseHelper singleton
- **Firebase to Activity**: ValueEventListener callbacks
- **Activity to RecyclerView**: Adapter pattern

---

## Database Schema

### User Model
```java
public class User {
    private String userId;        // Firebase UID
    private String name;          // Full name
    private String phone;         // Phone number (10 digits)
    private String userType;      // "Farmer" or "Labourer"
    private long createdAt;       // Registration timestamp
}
```

### Expense Model
```java
public class Expense {
    private String expenseId;     // Unique ID
    private String userId;        // Owner's Firebase UID
    private String category;      // Predefined categories
    private double amount;        // Expense amount (₹)
    private String description;   // Details
    private String date;          // Display date (dd/MM/yyyy)
    private long timestamp;       // Sort/filter timestamp
}
```

**Categories**: Seeds, Fertilizers, Pesticides, Equipment, Labour, Water/Irrigation, Transportation, Other

### RawMaterial Model
```java
public class RawMaterial {
    private String materialId;    // Unique ID
    private String userId;        // Owner's Firebase UID
    private String name;          // Material name
    private double quantity;      // Amount
    private String unit;          // kg, liters, bags, units
    private double costPerUnit;   // Cost per unit (₹)
    private long timestamp;       // Creation timestamp

    public double getTotalCost() {
        return quantity * costPerUnit;
    }
}
```

### Labour Model
```java
public class Labour {
    private String labourId;      // Unique ID
    private String userId;        // Owner's Firebase UID
    private String name;          // Labour name
    private String phone;         // Contact number
    private double hoursWorked;   // Hours
    private double hourlyRate;    // Rate per hour (₹)
    private String workDate;      // Work date (dd/MM/yyyy)
    private String workDescription; // Work details
    private long timestamp;       // Creation timestamp

    public double getTotalPay() {
        return hoursWorked * hourlyRate;
    }
}
```

---

## Authentication Flow

### Registration Flow
```
1. User enters: name, phone, userType, password, confirmPassword
2. Validate inputs (phone length, password match, etc.)
3. Convert phone to email: phone@greenledger.app
4. Create Firebase Auth user
5. Save user data to Realtime Database (/users/{userId})
6. Navigate to Dashboard
```

### Login Flow
```
1. User enters: phone, password
2. Validate inputs
3. Convert phone to email: phone@greenledger.app
4. Authenticate with Firebase
5. Load user data from database
6. Navigate to Dashboard
```

### Session Management
- Firebase handles session persistence automatically
- User stays logged in until explicit logout
- Session checked in `LoginActivity.onCreate()`:
```java
if (firebaseHelper.isUserLoggedIn()) {
    navigateToDashboard();
    return;
}
```

### Logout Flow
```
1. User clicks logout in Dashboard menu
2. Call firebaseHelper.logout()
3. Firebase signs out user
4. Navigate to Login screen (clear back stack)
```

---

## UI/UX Design

### Color Scheme
```xml
<!-- Primary Colors - Green theme for agriculture -->
<color name="primary">#4CAF50</color>
<color name="primary_dark">#388E3C</color>
<color name="primary_light">#81C784</color>

<!-- Accent Colors -->
<color name="accent">#FF9800</color>
<color name="accent_dark">#F57C00</color>

<!-- Background -->
<color name="background">#F5F5F5</color>
<color name="card_background">#FFFFFF</color>

<!-- Text -->
<color name="text_primary">#212121</color>
<color name="text_secondary">#757575</color>
<color name="text_hint">#BDBDBD</color>

<!-- Status -->
<color name="success">#4CAF50</color>
<color name="error">#F44336</color>
<color name="warning">#FFC107</color>
<color name="info">#2196F3</color>
```

### App Icon
- **Design**: Green leaf on green background
- **Format**: Vector drawable (XML)
- **Symbolism**: Agriculture, growth, sustainability
- **File**: `res/drawable/ic_launcher.xml`

### Navigation Pattern
```
LoginActivity
    ↓ (login success)
RegisterActivity ← → DashboardActivity → ExpenseActivity
                        ↓                → RawMaterialActivity
                        ↓                → LabourActivity
                    (logout)
```

### Screen Flow
1. **Login**: Email-style input, password toggle, register link
2. **Register**: Form with user type dropdown
3. **Dashboard**: Card-based navigation, welcome message
4. **Feature Screens**: RecyclerView list + FloatingActionButton
5. **Dialogs**: Material dialogs for adding new entries

---

## Dependencies

### Firebase Dependencies
```gradle
implementation platform('com.google.firebase:firebase-bom:32.7.0')
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-firestore'
```

### AndroidX Dependencies
```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
```

### Material Design
```gradle
implementation 'com.google.android.material:material:1.11.0'
```

### Testing Dependencies
```gradle
testImplementation 'junit:junit:4.13.2'
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

### Gradle Plugins
```gradle
classpath 'com.android.tools.build:gradle:8.2.2'
classpath 'com.google.gms:google-services:4.4.0'
```

---

## Build & Deployment

### Debug Build
```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build
```bash
# Generate release APK
gradlew.bat assembleRelease

# Generate signed APK (requires keystore)
gradlew.bat bundleRelease
```

### ProGuard Configuration
Basic rules in `proguard-rules.pro`:
```proguard
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
```

### Keystore Setup (Release)
```bash
keytool -genkey -v -keystore greenledger.keystore \
  -alias greenledger -keyalg RSA -keysize 2048 -validity 10000
```

Add to `app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file("greenledger.keystore")
            storePassword "YOUR_PASSWORD"
            keyAlias "greenledger"
            keyPassword "YOUR_PASSWORD"
        }
    }
}
```

---

## Known Issues & Solutions

### Issue 1: Gradle-Java Compatibility
**Error**: `Java 21 incompatible with Gradle 8.0`

**Solution**: Updated to Gradle 8.5
```properties
# gradle-wrapper.properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

### Issue 2: Missing Launcher Icons
**Error**: `AAPT: error: resource mipmap/ic_launcher not found`

**Solution**: Created vector drawable icon
```xml
<!-- res/drawable/ic_launcher.xml -->
<vector android:width="108dp" android:height="108dp">
    <!-- Green leaf design -->
</vector>
```

### Issue 3: Firebase Authentication Disabled
**Error**: `This operation is not allowed`

**Solution**: Enable Email/Password in Firebase Console
- Authentication → Sign-in method → Email/Password → Enable

### Issue 4: Back Button Warning
**Warning**: `OnBackInvokedCallback is not enabled`

**Solution**: Added to AndroidManifest
```xml
<application android:enableOnBackInvokedCallback="true">
```

---

## Development Guidelines

### Code Style
- **Java Naming**: CamelCase for classes, camelCase for methods/variables
- **XML Naming**: snake_case for all resources
- **Constants**: UPPER_SNAKE_CASE
- **Package Structure**: Feature-based grouping

### Git Workflow
```bash
# Create feature branch
git checkout -b feature/expense-categories

# Commit changes
git add .
git commit -m "Add expense category filter"

# Push to remote
git push origin feature/expense-categories

# Create Pull Request
```

### Code Review Checklist
- [ ] Firebase queries use proper error handling
- [ ] Input validation on all user inputs
- [ ] Loading states shown for async operations
- [ ] Null checks on Firebase data
- [ ] Memory leaks checked (listeners removed)
- [ ] UI responsive on different screen sizes
- [ ] Tested on API 24 and API 34

### Firebase Best Practices
1. **Always remove listeners** in `onDestroy()`
2. **Use single value listeners** when appropriate
3. **Index queried fields** in Firebase rules
4. **Validate data** before saving to database
5. **Handle offline scenarios** gracefully

---

## Testing

### Manual Testing Checklist

#### Authentication
- [ ] Register with valid phone/password
- [ ] Register with invalid inputs (error handling)
- [ ] Login with correct credentials
- [ ] Login with wrong credentials
- [ ] Logout functionality
- [ ] Session persistence (app restart)

#### Expense Management
- [ ] Add expense with all fields
- [ ] View expense list
- [ ] Empty state when no expenses
- [ ] Date picker functionality
- [ ] Category dropdown

#### Raw Materials
- [ ] Add material with quantity
- [ ] View material list
- [ ] Total cost calculation
- [ ] Unit dropdown

#### Labour Management
- [ ] Add labour entry
- [ ] View labour list
- [ ] Total pay calculation
- [ ] Work date picker

### Test Devices
- **Minimum**: API 24 (Android 7.0)
- **Target**: API 34 (Android 14)
- **Screen Sizes**: Phone, Tablet
- **Orientations**: Portrait, Landscape

---

## Future Enhancements

### Phase 2 Features
1. **Analytics Dashboard**
   - Expense charts by category
   - Monthly expense trends
   - Material usage statistics

2. **Export Functionality**
   - PDF reports generation
   - CSV export for accounting

3. **Multi-language Support**
   - Hindi, Marathi, Tamil, Telugu
   - RTL language support

4. **Offline Mode**
   - Local SQLite cache
   - Sync when online

5. **Advanced Features**
   - Image attachments for receipts
   - Push notifications for reminders
   - Crop management module
   - Weather integration

### Technical Improvements
- Migrate to Jetpack Compose
- Add Room database for offline
- Implement MVVM architecture
- Add Dagger/Hilt for DI
- Write instrumented tests
- CI/CD pipeline setup

---

## Appendix

### Useful Commands
```bash
# Check Java version
java -version

# Check Gradle version
gradlew --version

# Clean build
gradlew clean

# Build and install debug
gradlew installDebug

# View logcat
adb logcat | grep GreenLedger

# Clear app data
adb shell pm clear com.greenledger.app
```

### Firebase Console URLs
- **Project**: https://console.firebase.google.com/project/greenledger-e0d3a
- **Authentication**: https://console.firebase.google.com/project/greenledger-e0d3a/authentication
- **Database**: https://console.firebase.google.com/project/greenledger-e0d3a/database

### Support Resources
- [Firebase Documentation](https://firebase.google.com/docs)
- [Material Design Guidelines](https://m3.material.io/)
- [Android Developers](https://developer.android.com/)

---

**Last Updated**: October 31, 2025
**Maintained By**: GreenLedger Development Team
**Version**: 1.0

---

*This document should be updated whenever significant changes are made to the project configuration, architecture, or dependencies.*
