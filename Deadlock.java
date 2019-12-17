import java.util.*;
import java.io.*;

/*
 *  Milos Petkovic
 *  CSCI 4401 
 *
 *  Deadlock class
 */
public class Deadlock 
{
  
  // Main method for program execution
  public static void main(String[] args) throws FileNotFoundException 
  {
    if (args.length == 0)
      System.out.println("ERROR: Must specify input file.");
    else {
      Scanner input = new Scanner(new File(args[0]));
      RAG graph = new RAG();
      int i = 0;
      while(input.hasNext()) {
        int process = Integer.parseInt(input.next());
        String type = input.next();
        int resource = 0 - Integer.parseInt(input.next());
        if(type.charAt(0) == 'N'){
          graph.needs(resource, process);
          if (graph.searchDeadlock())
            return;
        } else if (type.charAt(0) == 'R') {
          graph.release(resource, process);
        } else {
          System.out.println("ERROR: Invalid input format.");
          return;
        }
        i++;
      }
      System.out.println("EXECUTION COMPLETED: No deadlock encountered.");
    }
  }
}


/*
 *  RAG class
 */
class RAG 
{
  
  private ArrayList<Edge> edges;
  private boolean cycle;
  
  // RAG Constructor
  public RAG() 
  { 
    this.edges = new ArrayList<Edge>();
    this.cycle = false;
  }
  
  // Edge inner class
  class Edge 
  {
    int start;
    int end;
    boolean visited;
    
    
    // Edge constructor
    private Edge(int start, int end){
      this.start = start;
      this.end = end;
      this.visited = false;
      
    }
    
    // Method to check if Edge equals other Object
    @Override
    public boolean equals(Object o)
    {
      if (o instanceof Edge) 
      {
        Edge edge = (Edge) o;
        return (edge.start == this.start && edge.end == this.end);
      } else 
      {
        return false;
      }
    }
    
  }
  
  // Method to request resource
  public void needs(int resource, int process)
  {
    System.out.print("Process " +
                     process +
                     " needs resource " +
                     (0 - resource) +
                     " - ");
    boolean taken = false;
    for (Edge edge: edges) 
    {
      if (edge.start == resource) 
      {
        taken = true;
        System.out.println("Process " +
                           Integer.toString(process) +
                           " must wait.");
        Edge e = new Edge(process, resource);
        edges.add(e);
        return;
      }
    }
    Edge e = new Edge(resource, process);
    edges.add(e);
    System.out.println("Resource " +
                       (0 - resource) +
                       " is allocated to process " +
                       process +
                       ".");
  }
  
  // Method to release resource
  public void release(int resource, int process)
  {
    System.out.print("Process " +
                     process +
                     " releases resource " +
                     (0 - resource) +
                     " - ");
    Edge e = new Edge(resource, process);
    for (Edge edge: edges) 
    {
      if (edge.equals(e)) 
      {
        edges.remove(edge);
        break;
      }
    }
    for (Edge edge: edges) 
    {
      if (edge.end == resource) 
      {
        int temp = edge.start;
        edge.start = edge.end;
        edge.end = temp;
        System.out.println("Resource " +
                           (0 - resource) +
                           " is allocated to process " +
                           edge.end +
                           ".");
        return;
      }
    }
    System.out.println("Resource " +
                       (0 - resource) +
                       " is now free.");
  }   
  
  // Method to search for deadlock
  public boolean searchDeadlock() 
  {
    for (Edge edge: edges) 
    {
      edge.visited = false;
    }
    for (Edge edge: edges) 
    {
      if (edge.visited == false) 
      {
        ArrayList<Integer> cycles = new ArrayList<Integer>();
        if (!searchDeadlockRecursive(edge, cycles))
          return false;
      }
    }
    return true;
  }
  
  // Method to recursively search for deadlock
  public boolean searchDeadlockRecursive(Edge edge, ArrayList<Integer> cycles){
    edge.visited = true;
    Edge next = next(edge);
    cycles.add(edge.start);
    if(containsCycle(cycles)) 
    {
      this.cycle = true;
      Collections.sort(cycles);
      System.out.println("DEADLOCK DETECTED: Processes " + 
                         getProcessesString(cycles) +
                         "and Resources " +
                         getResourcesString(cycles) +
                         "are found in a cycle.");
    } else 
    {
      if (next != null)
        searchDeadlockRecursive(next, cycles); 
      else
        this.cycle = false;
    }
    return this.cycle;
    
  }
  
  // Method to get next edge
  public Edge next(Edge e)
  {
    Edge next = edges.get(0);
    for (Edge edge: edges) 
    {
      if (edge.start == e.end)
        return edge;
    }
    return null;
  }
  
  // Method to get if cycle exists
  public boolean containsCycle(ArrayList<Integer> cycles) 
  {
    int last = cycles.remove(cycles.size() - 1);
    for (Integer cycle: cycles) 
    {
      if (cycle.equals(last))
        return true;
    }
    cycles.add(last);
    return false;
  }
  
  // Method to get processes String
  public String getProcessesString(ArrayList<Integer> cycles)
  {
    String processesString = "";
    for (Integer cycle: cycles) 
    {
      if (cycle > 0)
        processesString += cycle + ", ";
    }
    return processesString;
  }
  
  // Method to get resources String
  public String getResourcesString(ArrayList<Integer> cycles)
  {
    String resourcesString = "";
    ArrayList<Integer> cycles2 = new ArrayList<Integer>();
    for (Integer cycle: cycles) 
    {
      if (cycle < 0)
        cycles2.add(0 - cycle);
    }
    Collections.sort(cycles2);
    for (Integer cycle: cycles2)
      resourcesString += cycle + ", ";
    return resourcesString;
  }
}