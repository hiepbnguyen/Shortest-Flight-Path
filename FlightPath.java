import java.io.*;
import java.util.*;

// Stores the destination, cost, and time
class Edge {
    public String cityName;
    public double cost;
    public int time;
    Edge (String _cityName, double _cost, int _time) {
        cityName = _cityName;
        cost = _cost;
        time = _time;
    }
}

// Stores the path from a source to a destination
class Path {
    public String list;
    public double cost;
    public int time;
    Path (String _list){
        list = _list;
        cost = 0;
        time = 0;
    }
}

// Stack class used for Depth First Search
class StackVisits extends Stack<Boolean[]> {
    public void addNode (HashMap<String, LinkedList<Edge>> aList, String request){
        aList.get(request);
        Boolean[] visited = new Boolean[aList.get(request).size()];
        Arrays.fill(visited, false);
        this.add(visited);
    }
}

public class FlightPath {
    // Sets the delimiter to detect pipes and new lines, based on the operating system
    public static String delimiter = "\\||" + System.lineSeparator();

    // Creates an adjacency list out of the Flight Data
    // Implementation: Hash Map containing Linked Lists
    public static HashMap<String, LinkedList<Edge>>
    createAdjacencyList(String fileName){
        try {
            // Creates a scanner to open file and read each line
            Scanner scan = new Scanner(new File(fileName));
            int count = Integer.parseInt(scan.nextLine());
            HashMap<String, LinkedList<Edge>> aList = new HashMap<String, LinkedList<Edge>>();

            // Creates a hashmap of linked lists from the Flight Data
            for (int i = 0; i < count; i++){

                // Creates a scanner that reads from each line and removes any whitespaces.
                // Replaces any space with a $ so that the scanner can read names with spaces.
                Scanner scan2 = new Scanner(scan.nextLine().replace(" ", "$"));
                scan2.useDelimiter(delimiter);

                // Uses two sets of keys b/c graph is undirected
                String Key1 = scan2.next();
                Edge tempEdge1 = new Edge(scan2.next(), Integer.parseInt(scan2.next()), Integer.parseInt(scan2.next()));

                String Key2 = new String(tempEdge1.cityName);
                Edge tempEdge2 = new Edge(new String(Key1), tempEdge1.cost, tempEdge1.time);
                
                // Creates new linked list if there isn't one.
                // Otherwise, adds more.
                LinkedList<Edge> access1 = aList.get(Key1);
                if (access1 != null){
                    access1.add(tempEdge1);
                }
                else {
                    LinkedList<Edge> tempList = new LinkedList<Edge>();
                    tempList.add(tempEdge1);
                    aList.put(Key1, tempList);
                }

                LinkedList<Edge> access2 = aList.get(Key2);
                if (access2 != null){
                    access2.add(tempEdge2);
                }
                else {
                    LinkedList<Edge> tempList = new LinkedList<Edge>();
                    tempList.add(tempEdge2);
                    aList.put(Key2, tempList);
                }
            }
            scan.close();
            return aList;

        } catch (FileNotFoundException E){
            System.err.println("File not found");
            System.exit(-1);
            return null;
        }
    }

    // Modified Depth First Search Algorithm

    public static LinkedList<Path> DFS(HashMap<String, LinkedList<Edge>> aList, String source, String destination) {
        // Stack of boolean lists for visitied nodes within each node 
        StackVisits stack = new StackVisits();
        stack.addNode(aList, source);
        Stack<String> visiting = new Stack<String>();
        visiting.add(source);
        LinkedList<Path> pathList = new LinkedList<Path>();

        while(!stack.isEmpty()){
            // Checks if arrived at destination. If arrived,
            // add possible path to paths list and pop current node.
            if (visiting.peek().equals(destination)){
                String pathString = "";
                for (String city : visiting){
                    pathString += city + " ";
                }
                pathList.add(new Path(pathString));
                stack.pop();
                visiting.pop();
                continue;
            }

            // Checks current Node for unvisited nodes. If
            // unvisited, add to stack and break from loop.
            boolean unvisitedFound = false;
            for (int i = 0; i < stack.peek().length; i++){
                if (stack.peek()[i] == false) {
                    String nextNodeName = aList.get(visiting.peek()).get(i).cityName;
                    stack.peek()[i] = true;
                    if (!visiting.contains(nextNodeName)){
                        unvisitedFound = true;
                        stack.addNode(aList, nextNodeName);
                        visiting.add(nextNodeName);
                        break;
                    }
                }
            }

            // If no unvisited nodes are found, pop from stack
            if (unvisitedFound == false){
                stack.pop();
                visiting.pop();
            }
        }
        return pathList;
    }

    // Finds the cost and time for each weight.
    public static void findWeights(LinkedList<Path> pathList, HashMap<String, LinkedList<Edge>> aList){

        // Loops through each path in the linked list
        for (int i = 0; i < pathList.size(); i++){
            Scanner scan = new Scanner(pathList.get(i).list);
            double cost = 0;
            int time = 0;
            String currentPath = scan.next();
            String nextPath;

            // Loops through each city in a path and
            // adds weight to the cost and time
            while(scan.hasNext()){
                nextPath = scan.next();
                for(Edge edge : aList.get(currentPath)){
                    if (edge.cityName.equals(nextPath)) {
                        cost += edge.cost;
                        time += edge.time;
                    }
                }
                currentPath = nextPath;
            }
            pathList.get(i).cost = cost;
            pathList.get(i).time = time;
            scan.close();
        }
    }

    public static void printPaths(LinkedList<Path> pathList, int flightNum, String src, String end, String weightType, FileWriter fWriter){
        try {
            Path[] topThree = new Path[3];

            // Loops through each possible path and adds
            // top three into the topThree array 
            for (int i = 0; i < pathList.size(); i++){
                for (int j = 0; j < 3; j++){
                    if (topThree[j] == null || (weightType == "Cost" && pathList.get(i).cost < topThree[j].cost)
                        || (weightType == "Time" && pathList.get(i).time < topThree[j].time)){
                        if(j == 0){
                            topThree[2] = topThree[1];
                            topThree[1] = topThree[0];
                        }
                        else if (j == 1)
                            topThree[2] = topThree[1];
                        topThree[j] = pathList.get(i);
                        break;
                    }
                }
            }

            // Outputs the top three paths into the file.
            fWriter.write("Flight " + flightNum + ": " + src.replace("$", " ") + ", "
                          + end.replace("$", " ") + " (" + weightType + ")\n");
            for (int i = 0; i < 3; i++){
                if (topThree[i] == null)
                    break;
                fWriter.write("Path " + (i+1) +": ");
                Scanner scan = new Scanner(topThree[i].list);

                // Replaces the $ used for the Scanner with a space 
                fWriter.write(scan.next().replace("$"," "));
                while(scan.hasNext()){
                    fWriter.write(" -> " + scan.next().replace("$"," "));
                }
                fWriter.write(". Time: " + topThree[i].time);
                fWriter.write(" Cost: " + String.format("%.2f", topThree[i].cost) + "\n");
                scan.close();
            }
        } catch (IOException E){
            System.err.println("There's been an IOException.");
        }
    }

    public static void main(String args[]){

        // Variables
        HashMap<String, LinkedList<Edge>> adjacencyList;
        LinkedList<Path> pathList;

        try{
            // IO Variables
            FileWriter fWriter = new FileWriter(new File(args[2]));
            Scanner scan1 = new Scanner(new File(args[1]));

            // Delimeter for | and new line
            scan1.useDelimiter(delimiter);
            int count = Integer.parseInt(scan1.nextLine());

            // Creates an adjacency list from the Flight Data File
            adjacencyList = createAdjacencyList(args[0]);

            // Loops through each requested path
            for (int i = 0; i < count; i++){
                Scanner scan2 = new Scanner(scan1.nextLine().replace(" ", "$"));
                scan2.useDelimiter(delimiter);
                // Extracts source, destination, and weightType from file.
                String source = scan2.next();
                String destination = scan2.next();
                String weightType = scan2.next();

                // Creates a list of possible paths through
                // Depth First Search and then finds weights of each path
                pathList = DFS(adjacencyList, source, destination);
                findWeights(pathList, adjacencyList);

                // Sorts each path based on the weight type and then
                // outputs the path into the Output File
                if (weightType.equals("C"))
                    printPaths(pathList, i + 1, source, destination, "Cost", fWriter);
                else
                    printPaths(pathList, i + 1, source, destination, "Time", fWriter);

                if (i != count-1)
                    fWriter.write("\n");
            }
            fWriter.close();
        } catch (FileNotFoundException E) {
            System.err.println("Paths to Calculate file not found.");
        } catch (IOException E){
            System.err.println("There's been an IO Exception.");
        } catch (ArrayIndexOutOfBoundsException E) {
            System.err.println("ArrayIndexOutOfBoundsException. Check for invalid arguments.");
        }
    }
}