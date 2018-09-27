# Verisimilitude

## Background

The Oxford Dictionary defines *verisimilitude* as *"The appearance of being true or real."* When we submit solutions to competitive programming contests, the judging system does not check if our solutions are provably correct. Rather, our solutions are accepted if they pass the pre-defined test cases and thus *appear to be correct*.

Verisimilitude (**Veris** for short) began as a simple command-line interfacing script written by **Alex Coleman** and **Timothy Buzzelli** (both former members of the UCF Programming Team and 2018 ACM-ICPC World Finals Bronze Medalists.) After constant iteration and development, Veris made its way from a command-line tool to a robust and powerful judging tool with a simple UI. Since then, Timothy Buzzelli has been constantly adding improvements to Veris based on feature requests from UCF Programming Teammembers.

## Goal

Verisimilitude seeks to be a comprehensive tool for judging competitive programming solutions locally given a solution file, collection of data files, and optionally a checker.

## Tutorial

### Main Window

(Image Here)

* **Solution:** The path to the source file for the solution. Can be dragged and dropped into the window.
* **Data:** The path to a folder containing the input/output files for the judge data. Can be dragged and dropped into the window. When the Data folder is not selected and a solution is dragged in, the data folder will automatically be set to the directory containing the solution source file.
* **Language:** A selector to choose which language the solution file is. When "Detect Language" is selected, Verisimilitude will attempt to detect the language of the source file using the file extension.
* **Time Limit (seconds):** A double value representing the time limit in seconds per test case to allow the solution to run for. If the solution takes longer than this time on a test case, that case result will be a TLE.
* **Checker:** A selector to choose which checker to use when comparing the expected output with the solution's output. The built-in checkers and their specific settings is explained in more detail in the Checkers section.
* **Data regex (optional):** A regular expression to use when looking for data files in the selected data folder. Only data files matching this regular expression **before the file extension** will be considered for judging. If this is not provided, the value is assumed to be ".\*".
