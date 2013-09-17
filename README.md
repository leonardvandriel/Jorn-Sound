Jorn Sound
==========

*A experimental playground for developing automated beat matching strategies.*


How To
------
Make sure to have Java SDK installed. Now compile the application using:

    javac -d bin -sourcepath src src/peen/jornsound/main/Main.java

And run the application:

    java -cp bin peen.jornsound.main.Main

Use the mouse cursor to set the primary beat frequency (horizontal) and phase (vertical), use shift to control the secondary beat.


About the code
--------------
Most of the code deals with sound synthesis and display. The beat matching algorithm is located in `ChasingPhaser.java`, this is where the magic happens.


License
-------
Jorn Sound is licensed under the terms of the Apache License version 2.0, see the included LICENSE file.
