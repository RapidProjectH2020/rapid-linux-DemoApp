# RAPID - Java Demo Application
This is part of the [RAPID Project](http://www.rapid-project.eu) and is an ongoing work.
While RAPID envisions to support heterogeneous devices, this is the demonstration of the tasks offloading on **generic Java applications**.
This demo uses the [RAPID Linux/Windows Offloading Framework](https://github.com/RapidProjectH2020/rapid-linux).  
For code offloading on Android, have a look at the generic 
[RAPID Android Demo Application](https://github.com/RapidProjectH2020/rapid-android-DemoApp).

In this page we will guide you on how to:
* [Quickly Install and Test the Demo Application](#installing-and-testing-the-demo).
* [Start Developing Java Applications with RAPID Offloading Support](#developing-java-applications-with-rapid-offloading-support).

## Intro
RAPID enables automatic computation offloading of heavy tasks on Android and Linux/Windows Java applications.
Moreover, RAPID enables the possibility for embedding CUDA code in applications for generic Android devices
and for Java Linux/Windows.  
RAPID enables highly CPU- or GPU-demanding applications to be offered through physical or virtual devices with lower capabilities or resources than the applications require, potentially backed by remote accelerators of the same class (D2D) or higher classes (hierarchical mode).  
RAPID supports its acceleration service through code offloading to more capable devices or devices with more resources, when this is deemed necessary or beneficial.
<p align="center">
<img src="http://rapid-project.eu/files/rapid-arch.png" width="480">
</p>

### Terminology
* **User Device (UD):** is the low-power device (phone, e.g.) that will be accelerated by code offloading.
In our scenario it will be a low-power machine (a machine with Ubuntu 14+ and Oracle Java 7+ is recommended).
* **Acceleration Client (AC):** is a Java library that enables code offloading on the Java applications.
* **Application (AP):** is the Java application that will be accelerated by the framework.
This application includes the AC as a library and uses the AC's API and the RAPID programming model.
* **VM:** is a Virtual Machine running on virtualized software, with the same operating system as the UD.
In our scenario it will be an Ubuntu 16.04 running on VirtualBox.
* **Acceleration Server (AS):** is a Java application that runs on the VM and is responsible for executing the offloaded code by the client.

## Installing and Testing the Demo

### Description of the Demo App
The demo application shows three representative use case offloading scenarios:

* **Java method offloading.**

  This is the simplest case of computation offloading, dealing with remote execution of Java methods.
  We have selected the [N-Queens puzzle](https://developers.google.com/optimization/puzzles/queens) as a representative for this use case.
  The N-Queens puzzle is the task of *arranging N chess queens in the chess keyboard so that no two queens can attack each other*.
  The current implementation is a brute force algorithm.
  The number of queens varies from 4 to 8, varying this way the difficulty of the problem and its duration.
  The computation will be performed via the RAPID AC several times locally on the device or remotely on the VM.
  Cumulative statistics in terms of number of local/remote executions and average duration of local/remote executions
  will be shown to the user after the demo has finished running.
  The expected result is that while increasing the number of queens, the gap between the local and remote execution should increase,
  with the remote executions being faster for bigger number of queens.

* **C/C++ native function offloading.**

  Java allows developers to include native C/C++ code in their applications for increasing the performance 
  of intensive tasks or for allowing code reusability. 
  A normal Java method can call a native function thanks to the Java Native Interface (JNI). 
  To show that RAPID supports offloading of native functions, we have included in the demo a simple application 
  that simply returns the string "*Hello from JNI*" implemented in C++ and included as a native library in the demo application.
  Also in this case, the user can see cumulative statistics in terms of number and duration of local/remote execution.
  The expected result here is that the local execution will always be faster than the remote one, 
  given that the native function is not computationally intensive, meaning that the remote execution is penalized by the data transmission.
  However, this is just a simple demo serving as a starting point for building applications that include offloadable native functions.

* **Java CUDA programming and CUDA offloading.**

  The third showcase is the most complex one, including CUDA code offloading.
  The demo application in this case is a matrix multiplication performed using CUDA.
  Notice that CUDA development for Java is not officially supported by NVIDIA.
  As such, the developer:
  * Implements the CUDA code as if it were a separate project and generates the Parallel Thread Execution 
  (PTX) file using the NVIDIA CUDA Compiler (nvcc).
  * Then, the PTX file has to be embedded in the `resources` folder of the Java application, 
  where the RAPID framework will look for loading the file during runtime.
  
  When the execution of the method containing CUDA calls is performed locally, if the client device does not have a GPU, 
  RAPID will offload the CUDA calls from the client device to RAPID AS,
  which will take care of running them on the physical GPU of the machine where it is deployed (i.e. the RAPID cloud). 
  When the execution of the method containing the CUDA calls is performed remotely, because it is offloaded by the RAPID AC,
  the CUDA calls will be executed by RAPID on the remote GPU.

### Installing
The demo shows how portion of the application's code can be run locally on the device or can be offloaded on a remote VM.
Installation steps:

0. Dependencies: This project is developed in [IntelliJ IDEA](https://www.jetbrains.com/idea/) with [Maven](https://maven.apache.org/).
1. Clone this project in IntelliJ IDEA.
   * The project has library dependencies that are distributed via [Bintray](https://bintray.com/rapidprojecth2020/rapid).
   You need to add the support for Bintray in the Maven `~/.m2/settings.xml` [settings file](https://maven.apache.org/settings.html).
   The code below is an example of the settings you need to add for including the libraries needed by this project.
   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
    <settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
              xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
        <profiles>
            <profile>
                <repositories>
                    <repository>
                        <snapshots>
                            <enabled>false</enabled>
                        </snapshots>
                        <id>bintray-rapidprojecth2020-rapid</id>
                        <name>bintray</name>
                        <url>http://dl.bintray.com/rapidprojecth2020/rapid</url>
                    </repository>
                </repositories>
                <pluginRepositories>
                    <pluginRepository>
                        <snapshots>
                            <enabled>false</enabled>
                        </snapshots>
                        <id>bintray-rapidprojecth2020-rapid</id>
                        <name>bintray-plugins</name>
                        <url>http://dl.bintray.com/rapidprojecth2020/rapid</url>
                    </pluginRepository>
                </pluginRepositories>
                <id>bintray</id>
            </profile>
        </profiles>
        <activeProfiles>
            <activeProfile>bintray</activeProfile>
        </activeProfiles>
    </settings>
   ```
2. Use `mvn install` to build the application and create the executable jar file.
   * The executable jar file will be created in `<project_location>/target/rapid-demo-linux-0.0.1-SNAPSHOT.jar`, 
   where `<project_location>` is the folder where you downloaded this project.
3. Download the AS executable jar file from the RAPID website [here](http://rapid-project.eu/files/rapid-linux-as.jar).
   * Run the AS jar on a Linux VM or on a physical Linux machine:
     ```bash
     java -Djava.library.path=~/rapid-server/libs/ -jar rapid-linux-as.jar
     ```
   * `-Djava.library.path` is needed to instruct the Java Virtual Machine (JVM) where to find the shared native libraries
    shipped with the application.
    The folder will be created automatically by the RAPID AS and the native libraries integrated in the Jar files
    of the offloadable applications will be copied here during the registration phase of the applications.
   * The jar will automatically start listening for client applications.
   * Get the IP of the machine where the AS is running.
   * Make sure that the low-power client device can ping the machine where the AS is running.
   * ***Notice:** In the final release of the RAPID architecture we will provide VMs with AS running on the RAPID cloud,
   meaning that you will not have to deal with these steps yourself.*
4. Run the demo application on the client device:
   ```bash
   java -Djava.library.path=<project_location>/Resources/libs \
   -jar <project_location>/target/rapid-demo-linux-0.0.1-SNAPSHOT.jar \
   -vm <as_ip> [-conn [clear|ssl]]
   ```
   * `<project_location>` indicates the location where the user downloaded the demo project in her machine.
   * `-Djava.library.path` is needed to instruct the JVM where to find the shared native libraries shipped with the application.
   Since in this demo we also implement and show the native code offloading capabilities of RAPID, 
   we have integrated a simple shared library in the demo application (see below).
   As such, we should indicate where the native library can be found, which in this case is the path in `<project_location>/Resources/libs`.
   * `-vm <as_ip>` indicates the IP of the machine where the AS is running.
   The AS should have been already launched and running in a machine that can be accessed from the client device.
   * The last command line argument, `[-conn [clear|ssl]]`, 
   can be used to set the desired communication to be performed in clear or encrypted with SSL.
   This argument can be omitted and by default the communication will be performed via SSL.
6. When running the demo application, several log messages will be printed, detailing the execution process and facilitating the understanding of the whole system. The final messages printed, shown in Figure 23, are the cumulative statistics for each application.

<p align="center">
<img src="http://rapid-project.eu/files/rapid-linux-demo1.png" width="640">
</p>

## Developing Java Applications with RAPID Offloading Support
