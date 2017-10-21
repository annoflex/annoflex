
    ====================================================================
     AnnoFlex - An annotation-based code generator for lexical scanners
    ====================================================================

                                Version: 0.9

License
-------

This software and its additional material is copyrighted, it may only be used in
accordance with the terms and conditions as described in the file "license.txt"
which resides in the same directory as this file.


Installation
------------

The installation of AnnoFlex is easy. Just perform the following two environment
variable adjustments:
  
  * Create a new environment variable called ANNOFLEX_HOME which points to the
    root directory of your extracted AnnoFlex package.
  
  * Add an entry to your PATH environment variable which points relative to the
    bin directory of AnnoFlex. For example: %ANNOFLEX_HOME%/bin

That's it. AnnoFlex is now available in your system.

The default usage of AnnoFlex is "annoflex <MyScanner.java>", whereby
MyScanner.java is a Java source code file which contains a class with methods
marked with AnnoFlex processing instructions that are used to create the code of
the scanner. For more information about the usage of AnnoFlex have a look at the
manual.


IDE Integration
---------------

In order to be able to use AnnoFlex within an IDE it is recommended to add it as
an external tool. In Eclipse for Windows for example this could be performed as
following:

* Create a new external tool launch configuration with the name "AnnoFlex"
* Set the property "Main->Location" to "${env_var:ANNOFLEX_HOME}/bin/annoflex.bat"
* Set the property "Main->Arguments" to "${resource_loc}"

Configurations for other IDEs and platforms are similar. Simply specify the
executable and the current resource to be processed.


Requirements
------------

AnnoFlex requires Java 7 or higher.
Generated scanners are compatible to Java 1.1.


Folder Contents
---------------

%ANNOFLEX_HOME%
|
\-- bin\           Contains scripts to start AnnoFlex.
|
\-- examples\      Contains code examples.
|
\-- lib\           Contains the compiled source code of AnnoFlex.
|
\-- source\        Contains the source code of AnnoFlex.
|
\-- changelog.txt  A list of all changes and improvements of all
|                  versions of AnnoFlex.
|
\-- license.txt    The terms and conditions under which it is allowed
|                  to use and redistribute AnnoFlex.
|
\-- manual.pdf     The manual of AnnoFlex.
|
\-- readme.txt     Describes the most important things you need to know
                   about AnnoFlex.

Copyright
---------

Copyright (c) 2017, Stefan Czaska.
All rights reserved.
