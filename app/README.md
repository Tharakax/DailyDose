# Daily Health Tracker

A comprehensive Android health tracking application built with Kotlin, featuring fragments, RecyclerViews, and responsive design for mobile devices.

## Features

### 🏥 Health Tracking
- **Multiple Health Metrics**: Track weight, height, blood pressure, heart rate, blood sugar, temperature, sleep, steps, water intake, and exercise
- **Quick Entry**: Fast entry for common health metrics with intuitive UI
- **Notes Support**: Add personal notes to each health entry
- **Date & Time Tracking**: Automatic timestamping of all entries

### 📊 Dashboard
- **Today's Summary**: View all health entries for the current day
- **Quick Actions**: One-tap access to common health metrics
- **Goals Progress**: Visual progress tracking for health goals
- **Responsive Design**: Optimized layouts for both portrait and landscape orientations

### 📈 History & Analytics
- **Filter Options**: View all entries, today's entries, or weekly entries
- **Chronological View**: Entries sorted by date and time
- **Empty State**: User-friendly messaging when no data is available

### 🎯 Goal Management
- **Set Goals**: Create health goals with target values
- **Progress Tracking**: Visual progress bars showing goal completion
- **Goal Types**: Support for all health metric types

### ⚙️ Settings & Data Management
- **Data Export**: Export all health data in text format
- **Data Clearing**: Option to clear all stored data
- **Profile Management**: Basic profile settings
- **About Information**: App version and feature information

## Technical Architecture

### 🏗️ Architecture Components
- **Fragments**: Modular UI components for different app sections
- **RecyclerViews**: Efficient list display with custom adapters
- **Navigation**: Bottom navigation with fragment management
- **ViewBinding**: Type-safe view references
- **ViewModel**: MVVM architecture for data management

### 📱 Responsive Design
- **Mobile-First**: Optimized for mobile devices
- **Tablet Support**: Enhanced layouts for larger screens (sw600dp)
- **Landscape Mode**: Specialized layouts for landscape orientation
- **Material Design**: Modern Material 3 design system

### 💾 Data Persistence
- **SharedPreferences**: No database dependency as requested
- **JSON Serialization**: Gson for data serialization
- **Local Storage**: All data stored locally on device

## Project Structure

```
src/main/java/com/example/dailydose/
├── adapters/                 # RecyclerView adapters
│   ├── HealthEntryAdapter.kt
│   ├── HealthTypeAdapter.kt
│   └── GoalAdapter.kt
├── data/                     # Data layer
│   └── HealthRepository.kt
├── fragments/                # UI fragments
│   ├── DashboardFragment.kt
│   ├── AddEntryFragment.kt
│   ├── HistoryFragment.kt
│   └── SettingsFragment.kt
├── model/                    # Data models
│   ├── HealthEntry.kt
│   └── HealthGoal.kt
├── viewmodel/                # ViewModels
│   └── HealthViewModel.kt
└── MainActivity.kt
```

## Dependencies

- **AndroidX Core**: Core AndroidX libraries
- **Material Design**: Material 3 components
- **Navigation**: Fragment navigation
- **RecyclerView**: List components
- **Lifecycle**: ViewModel and LiveData
- **Gson**: JSON serialization

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

### Build Configuration
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)
- **Compile SDK**: 36
- **Java Version**: 11

## Usage

### Adding Health Entries
1. Navigate to "Add Entry" tab
2. Select health type from the list
3. Enter value and optional notes
4. Tap "Save Entry"

### Viewing History
1. Go to "History" tab
2. Use filter buttons (All/Today/Week)
3. Scroll through chronological entries

### Setting Goals
1. Navigate to "Settings" tab
2. Tap "Manage Goals"
3. Add new goals with target values
4. Track progress on Dashboard

### Data Management
- **Export**: Settings → Export (shows data summary)
- **Clear**: Settings → Clear All (removes all data)

## Design Features

### 🎨 UI/UX
- **Material 3 Design**: Modern design language
- **Color Scheme**: Health-focused green color palette
- **Typography**: Clear, readable text hierarchy
- **Icons**: Emoji-based icons for visual appeal
- **Cards**: Elevated card design for content organization

### 📱 Responsive Layouts
- **Portrait**: Single-column layout optimized for mobile
- **Landscape**: Two-column layout for better space utilization
- **Tablet**: Enhanced spacing and larger touch targets
- **Dynamic Sizing**: Adaptive text and spacing based on screen size

### 🔄 Navigation
- **Bottom Navigation**: Easy access to main sections
- **Fragment Management**: Proper back stack handling
- **State Preservation**: Data persistence across navigation

## Health Metrics Supported

| Metric | Unit | Icon | Description |
|--------|------|------|-------------|
| Weight | kg | ⚖️ | Body weight tracking |
| Height | cm | 📏 | Height measurement |
| Blood Pressure | mmHg | 🩸 | Systolic/Diastolic pressure |
| Heart Rate | bpm | ❤️ | Resting heart rate |
| Blood Sugar | mg/dL | 🍯 | Glucose levels |
| Temperature | °C | 🌡️ | Body temperature |
| Sleep | hours | 😴 | Sleep duration |
| Steps | count | 👟 | Daily step count |
| Water | L | 💧 | Water intake |
| Exercise | minutes | 🏃 | Exercise duration |

## Latest Enhancements (2025)

### 🎨 Modern UI Design
- **Beautiful Gradient Backgrounds**: Stunning modern color gradients throughout the app
- **Attractive Color Palette**: Carefully selected modern colors (blue, green, purple, pink, orange)
- **Smooth Animations**: Gentle transitions and entrance animations for all components
- **Professional Splash Screen**: Animated logo and feature highlights on app launch

### 📊 Advanced Analytics
- **Interactive Charts**: Beautiful line charts, bar charts, and pie charts using MPAndroidChart
- **Health Trends**: Visual representation of weight trends and health metrics
- **Weekly Activity**: Bar chart showing daily health entry patterns
- **Statistics Dashboard**: Real-time analytics with total entries, daily averages, and insights

### 🔔 Smart Notifications
- **Health Reminders**: Customizable daily health tracking reminders
- **Hydration Alerts**: Water intake reminder notifications
- **Exercise Motivation**: Smart exercise reminder system
- **Settings Integration**: Toggle notifications on/off with modern switch controls

### 🚀 Enhanced Navigation
- **Modern Bottom Navigation**: Expanded with Analytics tab
- **Smooth Page Transitions**: Staggered animations for better user experience
- **Intuitive Flow**: Splash screen → Onboarding → Main app flow

### ⚙️ Advanced Settings
- **Notification Preferences**: Individual control over different reminder types
- **Modern Switch Design**: Beautiful material design switches
- **Animated Layouts**: Cards and buttons with entrance animations
- **Preference Persistence**: Settings saved locally using SharedPreferences

## Technical Improvements

### 🏗️ Architecture Enhancements
- **Notification System**: Complete notification helper with channel management
- **Data Visualization**: MPAndroidChart integration for professional charts
- **Modern Animations**: Custom XML animations for smooth user experience
- **Gradient Designs**: Beautiful gradient drawable resources

### 📱 UI/UX Improvements
- **Material 3 Design**: Latest Material Design components and patterns
- **Responsive Animations**: Cards, buttons, and fragments with smooth transitions
- **Modern Color Scheme**: Professional gradient-based color palette
- **Enhanced Typography**: Improved text hierarchy and spacing

### 🔧 Build System
- **JitPack Integration**: Added for MPAndroidChart dependency management
- **Modern Gradle Setup**: Updated build configuration for latest Android features
- **Resource Optimization**: Efficient drawable and animation resource management

## Future Enhancements

- **Cloud Backup**: Data synchronization across devices
- **Advanced Analytics**: More chart types and health insights
- **Social Features**: Share progress with friends and family
- **Wearable Integration**: Smartwatch compatibility
- **Sharing**: Export to health apps
- **Trends**: Long-term health analysis
- **Widgets**: Home screen widgets

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email support@dailydose.app or create an issue in the repository.

---

**Daily Health Tracker** - Your personal health companion 📱💚


