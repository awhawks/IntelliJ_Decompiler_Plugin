Decompile with Options and Attach
=================================

IntelliJ java IDE plugin for bulk decompile of jars.

Upstream/Original code information and links from [rferguson/decompile-and-attach](https://github.com/devendor/decompile-and-attach)


|||
|------------------------|-------------------------------                                                              |
|**Creator**             | [Babur](https://github.com/bduisenov)                                                       |
|**Upstream**            | [babur/decompile-and-attach](https://github.com/bduisenov/decompile-and-attach)             |
|**Upstream**            | [rferguson/decompile-and-attach](https://github.com/devendor/decompile-and-attach)          |
|**Upstream Maintainer** | [rferguson](https://github.com/devendor)                                                    |
|**Upstream Links**      | [source](https://github.com/devendor/decompile-and-attach) [Distribution](https://plugins.jetbrains.com/plugin/11094-decompile-and-attach) [Issues](https://github.com/devendor/decompile-and-attach/issues) [Documentation](https://www.devendortech.com/articles/decompile.html) |
| _______________________|_____________________________________________________________________________________________|
|**This rewrite Creator**| [awhawks](https://github.com/awhawks)                                                       |
|**link**                | [awhawks/IntelliJ_Decompiler_Plugin](https://github.com/awhawks/IntelliJ_Decompiler_Plugin)||



Details
-------

A rework of the [rferguson](https://github.com/devendor/decompile-and-attach) decompile and attach plugin for IntelliJ IDEA
originally created by [Babur](https://github.com/bduisenov) to add ability to modify decompiler options.

IntelliJ_Decompiler_Plugin decompiles jar packages using Intellij's Fernflower
decompiler with the options you select and optionally attaches the decompiled source jars to your project within
intellij java ides.

The decompiled source is included within Search Scopes or can be
unpacked and analyzed however you wish.

Select one or more jar files in the project menu. Then right click and
select the "Decompile And Attach" action.

Check your event log for warnings. Failure to decompile will log a
warning and continue to the next jar.

Don't expect perfection. It's a good decompiler, but can't reproduce
original source code.

Changlog
--------
**0.0.1 awhawks 4/3/2019** 
- rewrite with compiler options using 2018.3 - 2019.1 

**1.8.4 rferguson 12/03/2018** \* rebuilt for 2018.3

**1.8.2 rferguson 09/22/2018**

-   Added java-decompiler plugin dependency to build.gradle plugin.xml.
-   BugFix - Prior release would blindly produce empty classes with "{
    //compiled content" bodies when decompile and attach was used before
    any other decompile activity that triggered legal terms acceptance
    and related key setting. Plugin now reproduced legal disclaimer from
    java-decompiler when needed and sets acceptance key to fix bug.

**1.8.1 rferguson 09/04/2018**

-   Documentation is prettier.

**1.8 rferguson 09/03/2018**

-   Moved plugin to com.devendortech namespace.
-   Updated for compatibility with IU-182.4129.33
-   Add try / catch so decompile logs warning and continues when
    something doesn't decompile.
-   Transitioned to gradle plugin dev framework.
-   Releasing to IntelliJ as the original author has abandoned his
    project.

**1.7 rferguson 11/9/2017**

-   Fix for idea 172 builds
-   plugin.xml in 1.6 excludes support for 2017.2 releases. 1.5 allows
    it but doesn't work change to plugin.xml to allow this to install on
    newer release
-   The file list included the jar name itself which was passing through
    to attach and causing an fault
-   An empty directory would pass through to decompile and cause a
    fault.
-   Any non-class files were excluded from source export jars which
    limited context like reflections and packages.
-   The above covers issues 9, 10, 12 and 13 from
    `Babur's github issues`\_

**1.6 babur 12/03/2016**

-   fixed plugin for Intellij 163

**1.5 babur 12/1/2015**

-   jar decompilation continues even if decompilation for some classes
    failed
-   added multiple jars decompilation
-   added cancellation for process
-   added form for selecting folder where sources would be stored
-   switched from BYTECODE\_SOURCE\_MAPPING to USE\_DEBUG\_LINE\_NUMBERS
    for decompiler fix. See \#6 from `Babur's github issues`\_.

**1.4 babur 11/27/2015**

-   decompiled sources are attached to source jar lib instead of
    creating a new lib

**1.3 11/23/2015 babur**

-   added functionality for attaching decompiled sources to owner module
    of a jar

**1.2 11/18/2015**

-   fixed jar archive generation.

**1.1 11/17.2015**

-   fixes
