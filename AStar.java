import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class AStar implements AIModule{
    
    // To enable a efficient behavior for the PriorityQueue
    public class NodeComparator implements Comparator<AStar.points>, Serializable{  // For the hash map 
      public int compare(AStar.points n1, AStar.points n2){
      if(n1.f>n2.f)
        return 1;
      else if(n1.f < n2.f)
        return -1;
      else return 0;
      }
    }

    public class points{
  public String id; // used as a key for the HashMaps
  public Point pnt; //  "state"
  public AStar.points prev; // from where it came from
  public double g;
  public double f;
  
  // used to see is one coordinate is equal to another
  @Override
  public boolean equals(Object obj){
    final AStar.points other = (AStar.points) obj;
      if ((obj == null)||(getClass() != obj.getClass())){
    return false;
      }
      
      if (this.pnt.equals(other.pnt)){
    return true;
      }
      return false; 
  }

  // Constructor for the class defining the variables 
  public points(final TerrainMap map, Point point, double g){
      this.pnt = point;
      this.id = point.x + "," + point.y;
      this.prev = prev;
      this.g = g;
      this.f = g + getHeuristics(map, this.pnt, map.getEndPoint());
  }
  
    }


    // Function we call to create the final path to turn in
    public List<Point> finalpath(AStar.points node){
    ArrayList<Point> path = new ArrayList<Point>();
    path.add(node.pnt);
    while(node.prev != null){
        path.add(0, node.prev.pnt);
      node = node.prev;
      }
  return path;
  }

    public List<Point> createPath(final TerrainMap map){
  // necessary to indicate with efficiency to closedSet which node needs to be removed
  PriorityQueue<AStar.points> pQueue = new PriorityQueue<AStar.points>(5,new NodeComparator()); 
  //Map<String, AStar.points> openSet = new HashMap<String, AStar.points>();
  Map<String, points> openl = new HashMap<String, points>();   // Open List   --------->   this is a queue
  //Map<String, AStar.points> closedSet = new HashMap<String, AStar.points>();
  Map<String, points> closel = new HashMap<String, points>();    // List to not look in again
  AStar.points start = new AStar.points(map, map.getStartPoint(), 0.0);
  AStar.points end = new AStar.points(map, map.getEndPoint(),0.0);
  Point CurrentPoint = map.getStartPoint();
  // initializations
  openl.put(start.id, start);
  pQueue.add(start);
  
int index = 0;

  while(openl.size()>0){
      // poppingelemt is the element in map with lowest cost
      AStar.points poppingelement = pQueue.poll();
      openl.remove(poppingelement.id);

      if(poppingelement.id.equals(end.id)){ // If we get to the goal we return the path
        return finalpath(poppingelement);
        } 
      else {
        closel.put(poppingelement.id, poppingelement); // the element with lowest cost will not be used
        Point[] neighbors = map.getNeighbors(poppingelement.pnt);
        ArrayList<points> successor = new ArrayList<points>(); // List to hold successor of CurrentPoint
        int counter = 0;
        while(counter < neighbors.length) {
            AStar.points the_neighbor = new AStar.points(map, neighbors[counter], map.getCost(poppingelement.pnt, neighbors[counter]));
            successor.add(the_neighbor);
            successor.get(counter).prev = poppingelement;
            counter++;
        }

  for (int i=0; i < neighbors.length; i++){
        // if the successor is in the closed list we ignore it else we run the make updates to the nodes
        AStar.points visited = closel.get(neighbors[i].x+","+neighbors[i].y); 
          if (visited == null){  // If we havn't seen this coordinate before
           double tentative_score = poppingelement.g + map.getCost(poppingelement.pnt, neighbors[i]); // the cost that we calculate to check against the previous cost if available
            // we find whether the successor is in the map.     
            AStar.points isopen = null;
            isopen = openl.get(neighbors[i].x+","+neighbors[i].y);
      
      
            // If the successor does not exist we add it with a cost
          if (isopen == null){ 
            isopen = new AStar.points(map, neighbors[i], tentative_score);
            isopen.prev = poppingelement;
            openl.put(isopen.id, isopen);
            pQueue.add(isopen);
            }

            
            // if succesor is already in open list, we conapre whether its cost is now lower, if true we update it. 
          else if (tentative_score < isopen.g){ 
            isopen.prev = poppingelement;
            isopen.g = tentative_score;
            isopen.f = tentative_score + getHeuristics(map, isopen.pnt, end.pnt);
           }
        }
}  }  }
return null;
}

  

  private double getHeuristics(final TerrainMap map, final Point pt1, final Point pt2)
      {
      int yd = pt2.y - pt1.y;  // y2 -y1
      int xd = pt2.x - pt1.x;  // x2- x1
      double dist1 = Math.pow((Math.pow(yd,2) + Math.pow(xd,2)), 0.5 );
      double ch = map.getTile(pt2) - map.getTile(pt1);
      double dl = Math.pow(((Math.pow((pt1.x-pt1.y),2))/2),0.5);
      double heuristic = dist1+ ch + dl ;
      return heuristic;
    }
 }

