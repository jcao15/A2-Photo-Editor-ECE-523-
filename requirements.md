Note: This is a duplicate of the instructions found on Canvas. If there is a discrepancy, follow the Canvas instructions. 

# A2: Photo Editor

## What we're doing
Building an app to edit photos taken with the camera. 

This assignment is to be completed individually in Kotlin.

## Why we're doing it
To build our experience with Android programming, we're going to build an app that: 

* Has multiple activities
* Is aware of the activity lifecycle
* Utilizes intents and permissions to use capabilities on the device that are not our app

## Here's how to do it
Create a new Android app with the following features: 

* Use the camera to take a photo and display it on the screen
* Provide functionality for a user to edit the photos they took. 
   * Use a button-based event to trigger the editing
   * Editing can be anything. Examples: 
      * Enter text to overlay the photo
      * Draw on the image to annotate it
      * Change the coloring or other image characteristics (brightness/contrast, etc)
* Provide functionality to save the image to the device

### Submission
Please push your A2 to your Github Classroom repo, and create a corresponding release. Your repo must contain the following items:

* Android Studio Project folder
* Folder called "imgs" or "screenshots"
* README.md file 
   * This file should serve as a quick writeup and description of what you built. As you write this up, you should think of it in terms of showing this off as part of a programming portfolio, and the reader is a potential employer who wants to quickly understand what you did with this project. Or at the least, an easy way for me to understand, and for you to remind yourself in the future. 
   * It should be written using Github markdown (Links to an external site.)
   * It should include: 
      * Your Name
      * Short description of what you built
      * Short instructions on how to use the app 
         * We're not going to focus **too** much this quarter on user experience and such, but it will be helpful to know what to look for.
      * Screenshots of your app, which are stored in a "screenshots" or "imgs" directory 
      * Summary of why you chose this project, what you learned, what you found challenging or unexpected. You might include something you didn't finish or polish or would like to do different in the future. 
* Submit a link to your Github Classroom repo on Canvas.

## Grading (0-100 points)
Your submission will be graded by building and running it and evaluating its functionality. Your code will not be graded on style, but we still encourage you to follow good overall coding style. 

* 0-25 points: The app compiles, builds and loads without errors.
* 0-35 points: The app runs and satisfies minimum functionality (takes a photo using the camera, displays it, has a button-based event to do editing, saves the image)
   * The app should use at least 2 Activities and both implicit and explicit intents
* 0-20 points: Photo editing functionality is added, and it is functionally correct.
* 0-20 points: The user interface is well organized, and well documented. 
