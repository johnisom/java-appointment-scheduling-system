Appointment Scheduling System
=============================

A JavaFX and MYSQL application written by John Isom for WGU's Software II course.

The purpose of this application is to manage customers and appointments for Nameless Corp, LLC, including scheduling
functionalities.

Application Version: v0.0.1
Author: John Isom, Student ID: 001069136
Email Address :jisom4@wgu.edu
Date: 2021-10-20 / November 20, 2021
IDE: IntelliJ IDEA 2021.2.1 (Community Edition)
Java: Java SE JDK 11.0.12, JavaFX SDK 11.0.2
MYSQL driver: mysql-connector-java-8.0.26

Running The Program
-------------------

1. Add the appropriate libraries
2. Fill in the correct information to connect to the database in the src/helper/dbaccess/DBConnection.java file
   - Including host, port, username, etc.
3. Run the "Main" configuration
   - First create it if necessary, entry point is main.Main

If you wish to try out French or English, you can either change the language of the operating system, or head over
to the src/helper/locale/LocaleHelper.java file and edit the `setCurrentLocale` method to hard code a locale
rather than using the system default.

Additional Report Info
----------------------

The additional report that I added as part of rubric requirement A3f reports out the number of appointments by
day of week (Sunday, Monday, etc) and by type of report. It is similar to the month & type report described by
rubric requirement A3f, first bullet point.

JavaDoc
-------

JavaDoc can be fund in the doc/ directory.
