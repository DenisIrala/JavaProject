# Summary
Final Project for my OOP course programmed with Java. It uses file management libraries to use a .txt file as a pseudo-database and manages the schedules of Students, Faculty and TA classes.

It offers seven menu options:\
    1- Add a new Faculty to the schedule\
    2- Enroll a Student to a Lecture\
    3- Print the schedule of a Faculty\
    4- Print the schedule of an TA\
    5- Print the schedule of a Student\
    6- Delete a Lecture\
    7- Exit

## Additional Features
- Extensive use of inheritance:
  - Numerous error types inherited from the Exception class and used to validate data. (IdException, PersonNotFoundException, and LectureAlreadyAssigned)
  - 4 different classes used to manage people
    - Person (Abstract)
      - Faculty
      - Student
        - TA (Extends Student)




