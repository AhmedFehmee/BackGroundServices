# BackGroundServices

Creating a never ending background service in Android is simple.

Android has two ways of working **Foreground** and **background**
You use a background service to provide continuous data collection or processing while the app is no longer in the foreground that might happened because of
- app has been minimised  
- when the app has been closed by the user  
- killed by Android 

## [JobIntentService](https://developer.android.com/reference/android/support/v4/app/JobIntentService)
The Intent Service is an advanced form of background service. It creates a separate working thread to perform background operation. Once his job is finished is automatically destroyed.


The IntentService does not work well with Oreo devices, That is it made our application crash in the Oreo devices. To overcome this problem android introduced JobIntentService that works well in all the devices.

There are three parts to implement it: 
- an Activity (the foreground app) 
- Service 
- BroadcastReceiver which will receive a signal when something kills the service ,its role is to restart the service.

---------------

## [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
WorkManager is one of the Android Architecture Components and part of Android Jetpack
WorkManager provides a battery-friendly API that encapsulates years of evolution of Android’s background behavior restrictions. This is critical for Android applications that need to execute background tasks!

Furthermore, using WorkManager, you can schedule both periodic tasks and complex dependent chains of tasks: background work can be executed in parallel or sequentially, where you can specify an execution order. WorkManager seamlessly handles passing along input and output between tasks.

### How the WorkManager scheduler works
To ensure compatibility back to API level 14, WorkManager chooses an appropriate way to schedule a background task depending on the device API level. WorkManager might use JobScheduler or a combination of BroadcastReceiver and AlarmManager.

### How it works
Before moving forward, We have to understand the class and concept of WorkManager. Let’s understand what are various base classes that are used for Job Scheduling.

- Worker
It specifies what task to perform, The WorkManager API include an abstract worker class and You need to extends this class and perform the work.

- WorkRequest
WorkRequest represents an individual task that is to be performed. Now this WorkRequest, you can add values details for the work. Such as constraint or you can also add data while creating the request

   ##### WorkRequest can be of to type
        1. OneTimeWorkRequest– That means you requesting for non-repetitive work.
        2. PeriodicWorkRequest– This class is used for creating a request for repetitive work

- WorkManager
The WorkManager class in enqueues and manages all the work request. We pass work request object to this WorkManager to enqueue the task.

- WorkInfo
WorkInfo contains the information about a particular task, The work manager provides LiveData for each of the work request objects, We can observe this and get the current status of the task.

### Step for implementation WorkManager to Schedule Tasks
- Create a new project and add WorkManager dependency in app/buid.gradle file
- Create a base class of Worker
- Create WorkRequest
- Enqueue the request with WorkManager.
- Fetch the particular task status
