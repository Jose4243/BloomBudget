# BloomBudget 🌸

BloomBudget is a professional personal finance manager that helps users track their spending and grow their savings through a gamified "Money Tree" experience.

## 📺 Demonstration Video
**[https://youtu.be/O_Jy7U9q-CY?feature=shared]**

## ✨ Key Features
* **User Authentication**: Secure registration and login.
* **Smart Dashboard**: Personalized greetings and real-time balance tracking.
* **Savings Goal**: Set a financial target and watch your virtual tree grow as you save.
* **Expense Management**: View all transactions in a sleek list with "Swipe-to-Delete" functionality.
* **Detailed Insights**: Tap any transaction to see the full date, category, and description.

## Version Control & CI/CD Pipeline
This project enforces strict version control practices managed entirely through Git and GitHub.

## 🛠️ Built With
* **Language**: Kotlin
* **Architecture**: XML Layouts & Activity-based logic
* **Data Storage**: SharedPreferences & GSON for persistence

## 🚀 How to Run
1. Clone the repository.
2. Open in Android Studio.
3. Build and run on an emulator or physical device.
4. Use the provided APK file in the root directory for direct installation.


### GitHub Actions Integration
A continuous integration workflow is configured to automatically test, analyze, and build the application code upon every push or merge request to the repository.

```yaml
# .github/workflows/android.yml
name: Android CI Pipeline

on:
  push:
    branches: [ "master", "main" ]
  pull_request:
    branches: [ "master", "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout Source Code Repository
      uses: actions/checkout@v4

    - name: Configure JDK Environment Matrix
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle

    - name: Grant Execute Permission for Gradle Wrapper
      run: chmod +x gradlew

    - name: Execute Compilation Run & Build APK Assembly
      run: ./gradlew assembleDebug


