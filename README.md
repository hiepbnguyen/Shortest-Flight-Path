This program uses Depth-First Search to find the top three shortests paths between two cities. All flights are bi-directional.

How to use:
1. Open FlightDataFile.txt
    a. First line indicates number of flights
    b. Each path is formatted as so:
        Origin|Destination|Cost|Time
        Detroit|Austin|150|72

2. Open PathsToCalculateFile.txt
    a. First line indicates number of paths to calculate
    b. Each path is formatted as so:
        Detroit|Chicago|T
        Detroit|Chicago|C
        Where T or C indicates time or cost calculation

3. Compile using line:
    javac FlightPath.java

4. Run using line:
    java FlightPath FlightDataFile.txt PathsToCalculateFile.txt OutputFile.txt