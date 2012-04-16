Ness Computing Core Component
=============================

Component Charter
-----------------

* Hold core types that are not specific to any one service
* May only hold types that could reasonably fit in a "core Java library" e.g. Apache commons, JDK, or Guava

Component Restriction
---------------------

* All changes *must* be approved by a senior backend engineer or architect who is not the contributor.

Component Level
---------------

*Foundation component*

* Must never depend on any other likeness component.
* Should minimize its dependency footprint.

----
Copyright (C) 2012 Ness Computing, Inc.
