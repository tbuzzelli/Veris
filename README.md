# Verisimilitude

## Background

The Oxford Dictionary defines *verisimilitude* as *"The appearance of being true or real."* When we submit solutions to competitive programming contests, the judging system does not check if our solutions are provably correct. Rather, our solutions are accepted if they pass the pre-defined test cases and thus *appear to be correct*.

Verisimilitude (**Veris** for short) began as a simple command-line interfacing script written by **Alex Coleman** and **Timothy Buzzelli** (both former members of the UCF Programming Team and 2018 ACM-ICPC World Finals Bronze Medalists.) After constant iteration and development, Veris made its way from a command-line tool to a robust and powerful judging tool with a simple UI. Since then, Timothy Buzzelli has been constantly adding improvements to Veris based on feature requests from UCF Programming Teammembers.

## Goal

Verisimilitude seeks to be a comprehensive tool for judging competitive programming solutions locally given a solution file, collection of data files, and optionally a checker.

## Tutorial

### Main Window

![Main Window](/docs/screenshots/main_window.png)

* **Solution:** The path to the source file for the solution. Can be dragged and dropped into the window.
* **Data:** The path to a folder containing the input/output files for the judge data. Can be dragged and dropped into the window. When the Data folder is not selected and a solution is dragged in, the data folder will automatically be set to the directory containing the solution source file.
* **Language:** A selector to choose which language the solution file is. When "Detect Language" is selected, Verisimilitude will attempt to detect the language of the source file using the file extension.
* **Time Limit (seconds):** A double value representing the time limit in seconds per test case to allow the solution to run for. If the solution takes longer than this time on a test case, that case result will be a TLE.
* **Checker:** A selector to choose which checker to use when comparing the expected output with the solution's output. The built-in checkers and their specific settings is explained in more detail in the Checkers section.
* **Data regex (optional):** A regular expression to use when looking for data files in the selected data folder. Only data files matching this regular expression **before the file extension** will be considered for judging. If this is not provided, the value is assumed to be ".\*".

### Results Window

![Results Window Judging Finished](/docs/screenshots/results_window_judging_finished.png)

The results window opens up when *Judge* is pressed in the Main Window. It will give live updates while compiling the code and running the cases one at a time. The **Compile Status** is listed at the top and each test case is represented as a colored and numbered rectangle in the main center section.

Test cases that have finished judging take a color and have an icon which represent the verdict:
* **Accepted:** Green | Check Mark
* **Wrong Answer:** Red | X
* **Time Limit Exceeded:** Blue | Alarm Clock
* **Runtime Error:** Dark Red | Exclamation Point
* **Internal Error:** Grey | Question Mark

The bottom section of the results window gives live updates containing the total time and worst time for each test case. After judging is complete, the final verdict is also shown below.

#### View Compiler Error Message

When the verdict is a Compile Error, the compilers output can be seen by clicking the large **COMPILATION ERROR** text in the top of the window. This will open up a new window containing the output given from the compiler.

#### View Input, Output, Error Stream, Diff

Right clicking a test case that has finished judging will give a context menu containing options to view the case's input, output, the program's expected output, the error stream from the program, and a diff of the expected output and the program's output.

#### Rejudging Cases

Cases can only be rerun **after** all judging has finished.

To rejudge a single test case, right click the case and click **Rerun this case**. This will fetch the solution file again, recompile, and run that case again. **Even if this case passes, the final verdict below will not change.**

To rejudge all failing test cases, right click the verdict at the bottom and click **Rerun failing cases**. This will fetch the solution file again, recompile, and run every test case that failed (did not get Accepted) again. **Even if these cases pass, the final verdict below will not change.**

To rejudge all cases, right click the verdict at the bottom and click **Rejudge**. This will completely rejudge the solution and will affect the final verdict listed below.
