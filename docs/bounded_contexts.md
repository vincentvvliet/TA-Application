# Bounded Contexts: general descriptions of microservices

## Applications
Manages process of apply for a course.
Stores running applications.
Creates applications when a student applies for available courses.
Accepts applications when a lecturer hires a TA.
Provides applications per course for lecturers upon request.

## Authentication
Manages user authentication when logging in to the system.

## Course
Manages data on courses and grades.
Stores all courses including past editions.
Stores grades students have.
Lecturer can create courses and add grades for students per course.
Provides this information for other microservices that need it.

## Contract
Creates a pdf contract for a TA when a TA is hired, and forwards it to the TA and lecturer.

## TA's
(a TA is a student)
Manages existing and past TA's.
Keeps track of logged hours per TA for current courses.
Keeps track of past experience per TA.
Potentially stores ratings per TA experience.
Stores data to be put on contract.

## Users
Entrance point for users to the system.
Manages user accounts and what users are able to do. 
Manages user interactions with the whole system.
Stores user account data. 
3 types of users with different privileges: Student, Lecturer, Admin.
An admin can edit all data.
