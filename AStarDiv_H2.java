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

public class AStarDiv_H2 implements AIModule{

	public class CoordinateComparator implements Comparator<AStarDiv_H2.Coordinate>, Serializable{

		public int compare(AStarDiv_H2.Coordinate n1, AStarDiv_H2.Coordinate n2){

		int n =0;
		if (n1.f < n2.f)
			return -1;
		else if (n1.f> n2.f)
			return 1;
		else 
			return 0; 

    	}
    }
    

    public class Coordinate{
	public String id; // used as a key for the HashMaps
	public Point point; //  "state"
	public AStarDiv_H2.Coordinate parent; // from where it came from
	public double g;
	public double f;
	
	// To enable comparision between states
	@Override
	public boolean equals(Object obj){
	    if ((obj == null)||(getClass() != obj.getClass())){
		return false;
	    }
	    final AStarDiv_H2.Coordinate other = (AStarDiv_H2.Coordinate) obj;
	    if (this.point.equals(other.point)){
		return true;
	    }
	    return false; 
	}

	// Constructor
	public Coordinate(final TerrainMap map, Point point, double g){
	    this.point = point;
	    this.id = point.x + "," + point.y;
	    this.parent = parent;
	    this.g = g;
	    this.f = g + getHeuristics(map, this.point, map.getEndPoint());
	}
	
    }
    /// AStar algorithm
    /**
     * @param map The terrain map that A-Star will compute.
     * @return The path from StartPoint to EndPoint or null in case of failure.
     */
    public List<Point> createPath(final TerrainMap map){
	// necessary to indicate with efficiency to closedSet which Coordinate needs to be removed
	PriorityQueue<AStarDiv_H2.Coordinate> pQueue = new PriorityQueue<AStarDiv_H2.Coordinate>(10,new CoordinateComparator()); 
	Map<String, AStarDiv_H2.Coordinate> openSet = new HashMap<String, AStarDiv_H2.Coordinate>();
	Map<String, AStarDiv_H2.Coordinate> closedSet = new HashMap<String, AStarDiv_H2.Coordinate>();
	AStarDiv_H2.Coordinate start = new AStarDiv_H2.Coordinate(map, map.getStartPoint(), 0.0);
	AStarDiv_H2.Coordinate end = new AStarDiv_H2.Coordinate(map, map.getEndPoint(),0.0);
	
	// initializations
	openSet.put(start.id, start);
	pQueue.add(start);
	while(openSet.size()>0){
	    // x, current Coordinate and also the best Coordinate in the path so far
	    AStarDiv_H2.Coordinate x = pQueue.poll();
	    openSet.remove(x.id);
	    
	    if(x.id.equals(end.id)){ // got to the goal
		return reconstructPath(x);
	    } else {
		closedSet.put(x.id, x); // x will not be investigated anymore
		Point[] neighbors = map.getNeighbors(x.point);
		for (int i=0; i < neighbors.length; i++){
		    // if the neighbor being evaluated is already on the closed list, the algorithm simply ignores it
		    AStarDiv_H2.Coordinate visited = closedSet.get(neighbors[i].x+","+neighbors[i].y); 
		    if (visited == null){
			double g = x.g + map.getCost(x.point, neighbors[i]);
			AStarDiv_H2.Coordinate n = openSet.get(neighbors[i].x+","+neighbors[i].y);
			
			// if the neighbor is not in the openSet, initialize a new one adding it to the sets
			if (n == null){ 
			    n = new AStarDiv_H2.Coordinate(map, neighbors[i], g);
			    n.parent = x;
			    openSet.put(n.id, n);
			    pQueue.add(n);
			}
			// if the neighbor is in the open set, we need to update its state, in case its g is greater than the one found
			else if (g < n.g){ 
			    n.parent = x;
			    n.g = g;
			    n.f = g + getHeuristics(map, n.point, end.point);
			}
		    }
		}
	    }
	}

	return null;
    }

    /// Retrieves the path found
    /**
     * @param Coordinate The goal Coordinate from which the path will be recovered
     * @return The path of points found
     */
    public List<Point> reconstructPath(AStarDiv_H2.Coordinate Coordinate){
	ArrayList<Point> path = new ArrayList<Point>();
	
	path.add(Coordinate.point);

	while(Coordinate.parent != null){
	    path.add(0, Coordinate.parent.point);
	    Coordinate = Coordinate.parent;
	}

	return path;
    }

    /**
     *@param map The terrain map to get the height of each point.
     *@param pt1 The source point.
     *@param pt2 The destination point.
     *@return The value of the best case cost between pt1 and pt2.
     */
    private double getHeuristics(final TerrainMap map, final Point pt1, final Point pt2)
    {
		double VT = map.getTile(pt2) - map.getTile(pt1); // Height difference between 2 points
		int yd = pt2.y - pt1.y;  // y2 -y1
		int xd = pt2.x - pt1.x;  // x2- x1
		//int yd1 = pt1.y - map.getStartPoint().y;  // y2 -y1
		//int xd1 = pt2.x - map.getStartPoint().x;  // x2- x1

		double dist1 = Math.pow((Math.pow(yd,2) + Math.pow(xd,2)), 0.5 );
		//double dist2 = Math.pow((Math.pow(yd1,2) + Math.pow(xd1,2)), 0.5 );

		double ch = map.getTile(pt2) - map.getTile(pt1);

		double dl = Math.pow(((Math.pow((pt1.x-pt1.y),2))/2),0.5);
		double heuristic = dist1+ ch + dl ;
		return heuristic;
	


    }
}