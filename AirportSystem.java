import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.io.IOException;


public class AirportSystem {

	public static void main(String[] args) {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in)); // Initialize input system
		boolean loaded = false; // Set a flag for if the data is loaded
		try {
			boolean done = false;
			while(!done) { // Loop for menu
				Graph myAirline = null; // Initalize the Airport
				if(loaded) {
					myAirline = loadData(); // If the load selection has been chosen, the airline is the load data
				}
				System.out.println("What would you like to do?");
				System.out.println("1. Load Airport Data \n 2. Print All Airports \n 3. Check if there is a path between every given two airports in the data \n 4. Display all the critical airports \n 5. Check if there is a path (flight) in between two given airports \n 6. Exit");
				int choice = Integer.parseInt(input.readLine());
				// All of thse are basically just calling the required function, and displaying a message if there is no data loaded 
				if(choice == 1) {
					loaded = true;
				}else if(choice == 2) {
					if(!loaded) {
						System.out.println("Load Airport data first!!");
						
					}else {
						printAirports(myAirline);
					}
				}else if(choice ==3) {
					if(!loaded) {
						System.out.println("Load Airport data first!!");
					}else {
						checkAirportConnectivity(myAirline);
					}
				}else if(choice ==4) {
					if(!loaded) {
						System.out.println("Load Airport data first!!");
					}else {
						displayCritical(myAirline);
					}
				}else if(choice ==5) {
					if(!loaded) {
						System.out.println("Load Airport data first!!");
					}else {
						System.out.println("Where are you departing from?");
						String depart = input.readLine();
						System.out.println("Where are you arriving?");
						String arrive = input.readLine();
						if(myAirline.FlightCheck(depart, arrive)) {
							System.out.println("There is a flight!");
						}else {
							System.out.println("Sorry, there are no flights available");
						}
					}
				}else if(choice ==6) {
					done = true;
				}else {
					System.out.println("Invalid Input");
				}
			}
			input.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static Graph loadData() {
		Graph myAirline =null;
		try {
			
			// First we read the Vertex Table! 
			FileReader fileReaderV = new FileReader("V.txt");
			BufferedReader bufferedReaderV = new BufferedReader(fileReaderV);
			int numAirports = Integer.parseInt(bufferedReaderV.readLine()); 
			//Now we know the number of vertecies so we can construct the graph
			myAirline = new Graph(numAirports);
			int index = 0;
			String line = null;
			//In every line, we create a new Vertex Object to store into the airport graph
			while((line = bufferedReaderV.readLine()) != null) {
				Scanner input = new Scanner(line).useDelimiter(",");
				while(input.hasNext()) {
					String name = input.next();
					String city = input.next();
					Vertex newAirport = new Vertex(index,name,city);
					myAirline.addVertexAtPosition(newAirport, index);
					index++;
				}
				input.close();
				
			}
			//remember to close! 
			fileReaderV.close();
			bufferedReaderV.close();
			
			// Now we have all the airports, bow we need to do the relations, as such i will need a new File/BufferedReader
			FileReader fileReaderE = new FileReader("E.txt");
			BufferedReader bufferedReaderE = new BufferedReader(fileReaderE);
			String line2 = null;
			// While there is still information, we add analyze the relation
			while((line2 = bufferedReaderE.readLine()) != null){
				Scanner input2 = new Scanner(line2).useDelimiter(",");
				while(input2.hasNext()) {
					String Airport1 = input2.next();
					String Airport2 = input2.next();
					myAirline.addRelation(Airport1, Airport2);// more on this function in the function
				}
				input2.close();
			}
			//remember to close! 
			bufferedReaderE.close();
			fileReaderE.close();
		}catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}catch(IOException io) {
			io.printStackTrace();
		}
		return myAirline;
	}
	public static void printAirports(Graph myAirline) {
		// Literally just print these since I overrid the toString function of verecies
		for(int i = 0; i < 6; i++) {
			System.out.println(myAirline.getVertexList()[i]);
		}
	}
	public static void checkAirportConnectivity(Graph myAirline) {
		myAirline.ConnectivitySearch(0);
	}
	public static void displayCritical(Graph myAirline) { 
		// Checks every vertex if there is a critical point, and if thereit is you display it!
		System.out.println("These are the Critical Airports (if there are any):");
		for(int i = 0; i < myAirline.getVertexList().length; i++) {
			if (myAirline.CheckCriticalPoint(i)) {
				System.out.println(myAirline.getVertexList()[i]);
			}
		}
	}
}

class Vertex{
	public int AirportIndex;
	public String name;
	public String city;
	public int numAdj;
	public boolean visited = false;
	
	Vertex(){}
	Vertex(int index, String name, String city){
		this.AirportIndex = index;
		this.name = name;
		this.city = city;
	}
	
	@Override
	public String toString() {
		// Override so that the print gives what you need! 
		String returnString = "Airport Name: " + name + "| City: " + city + "| Number of adjacent airports: " + Integer.toString(numAdj);
		return returnString;
	}
	
}
class Graph{
	private Vertex vertexList[];
	private int adjMat[][];
	private int nVerts = 0;
	
	public Vertex[] getVertexList() {
		return vertexList;
	}
	public void setVertexList(Vertex[] vertexList) {
		this.vertexList = vertexList;
	}
	public int[][] getAdjMat() {
		return adjMat;
	}
	public void setAdjMat(int[][] adjMat) {
		this.adjMat = adjMat;
	}
	public int getnVerts() {
		return nVerts;
	}
	public void setnVerts(int nVerts) {
		this.nVerts = nVerts;
	}
	Graph(){}
	Graph(int numVerts){
		this.vertexList = new Vertex[numVerts];
		this.adjMat = new int[numVerts][numVerts];
		this.nVerts = numVerts;
	}
	//Some useful methods 
	public int getAUV(int start) {
		// Finds an Adjacent Unvisited Vertex by searching the adjacency matrix! 
		for(int i= 0; i< nVerts; i++) {
			if(adjMat[start][i] == 1 && vertexList[i].visited ==false) {
				return i;
			}
		}return -1;
	}
	// Change to Connectivity search
	public void ConnectivitySearch(int start) {
		ArrayList<Integer> indexes = new ArrayList<Integer>(); // We keep track of the indexes of the verticies that are connected to our start
		Stack theStack = new Stack(); // Start a Stck
		vertexList[start].visited = true; // Set the first airport as visited 
		theStack.push(this.vertexList[start].AirportIndex); // Push the position of the first Airport to the top of the stack
		indexes.add(this.vertexList[start].AirportIndex); //add the start index to the list of verts
		// While there are things to check
		while(!theStack.isEmpty()) {
			int experiment = getAUV((int)theStack.peek());
			if(experiment == -1) { // If there are none to check, we are done with this vertex!
				theStack.pop();
			}
			else { // If we find an unvisited node
				vertexList[experiment].visited = true; // WE visit and add to the count of nodes we have visited
				indexes.add(this.vertexList[experiment].AirportIndex); // Add this index to the list of ones we visited here
				theStack.push(experiment);// Then we push the index of the new node to the top of the stack! 
			}
		} // If we exit the while loop, all the paths have been visited! 
		// Check if all have been visited 
		boolean done = true;
		for(Vertex i: this.vertexList) {
			if(i.visited == false) { // If there are ANY that havent been visited, we have to do it again to find the other cycles
				done = false;
				break;
			}
		}
		if(done) {// If we get here, we're done! 
			//Reset visitation status
			if(indexes.size() == nVerts) { // If we visited all the idecies possible, the whole graph is connected s we display this message
				System.out.println("You airport is fully connected!");
			}else { // If not, there's one more cycle to display before we're done
				System.out.print("This is a connected cycle of airports: ");
				for(int l: indexes) {
					System.out.println(" " + this.vertexList[l].name+ " ");
				}
			}
			System.out.println();
			System.out.println("That's it!");
			//Reset visitation status
			for(int j = 0;j < this.vertexList.length ; j++) {
				this.vertexList[j].visited = false;
			}
			return; // Exit now that you're done
		}else { 
			//Since you're not done, print all the Nodes in this cycle
			System.out.print("This is a connected cycle of airports: ");
			for(int l: indexes) {
				System.out.println(" " + this.vertexList[l].name+ " ");
			}
			System.out.println();
			//Since you're not done, do this again but with a new start that hasn't been visited 
			int newstart = 0;
			for(Vertex i: this.vertexList) {
				if(i.visited == false) {
					newstart = i.AirportIndex;
					break;
				}
			}
			ConnectivitySearch(newstart);
		}		
	}
	public boolean FlightCheck(String Depart, String Arrive) {
		int start=0;int end=0; boolean onefound = false; boolean twofound = false;
		// First find the airports and if they exist 
		for(int i = 0; i < this.vertexList.length; i++) {
			if(Depart.equals(vertexList[i].name)) {
				start = i;
				onefound = true;
			}else if(Arrive.equals(vertexList[i].name)) {
				end = i;
				twofound = true;
			}
		}
		if(!onefound || !twofound) { // If they don't exist, return false because there is no path between nonexistent airports
			return false;
		}
		Stack theStack = new Stack(); // Start a Stack
		vertexList[start].visited = true; // Set the first airport as visited 
		theStack.push(this.vertexList[start].AirportIndex); // Push the position of the first Airport to the top of the stack
		// While there are things to check
		while(!theStack.isEmpty()) {
			int experiment = getAUV((int)theStack.peek());
			if(experiment == -1) { // If there are none to check, we are done with this vertex!
				theStack.pop();
			}
			else { // If we find an unvisited node
				vertexList[experiment].visited = true; // WE visit
				if(experiment == end) { // If this is the right index, we have found a path and return true!
					return true;
				}
				theStack.push(experiment);// Then we push the index of the new node to the top of the stack! 
			}
		} // If we exit the while loop, all the paths have been visited! 
		//Reset all of the visited status
		for(int j = 0;j < this.vertexList.length ; j++) {
			this.vertexList[j].visited = false;
		}
		// Since we never found it, there is no path between them
		return false;
	}
	public boolean CheckCriticalPoint(int cp) {
		vertexList[cp].visited = true;
		int start = (cp+1) %this.nVerts;
		//Below is the Code for a Connectivity search! If the graph is still connected when the Critical point is marked as visited, it is not a critical point
		int count = 1;  
		Stack theStack = new Stack(); // Start a Stack
		vertexList[start].visited = true; // Set the first airport as visited 
		theStack.push(this.vertexList[start].AirportIndex); // Push the position of the first Airport to the top of the stack
		// While there are things to check
		while(!theStack.isEmpty()) {
			int experiment = getAUV((int)theStack.peek());
			if(experiment == -1) { // If there are none to check, we are done with this vertex!
				theStack.pop();
			}
			else { // If we find an unvisited node
				vertexList[experiment].visited = true; // WE visit and add to the count of nodes we have visited
				count++;
				theStack.push(experiment);// Then we push the index of the new node to the top of the stack! 
			}
		} // If we exit the while loop, all the paths have been visited! 
		//Reset all of the visited status
		for(int j = 0;j < this.vertexList.length ; j++) {
			this.vertexList[j].visited = false;
		}
		if(count == nVerts-1) {// If we visited all nodes barring the one we already visited, the graph is still connected and therefore the test point is not a critical point
			return false;
		}else { // If it is no longer connected, it is a critical airport and therefore we return true! 
			return true;
		}		
	}
	
	public void addVertexAtPosition(Vertex v,int position) {
		this.vertexList[position] = v; // Simply add it to the vertex list at the position
	}
	public void addRelation(String Airport1, String Airport2) {
		int index1=0;int index2=0; boolean onefound = false; boolean twofound = false;
		// First find the airports and if they exist 
		for(int i = 0; i < this.vertexList.length; i++) {
			if(Airport1.equals(vertexList[i].name)) {
				index1 = i;
				onefound = true;
			}else if(Airport2.equals(vertexList[i].name)) {
				index2 = i;
				twofound = true;
			}
		}
		// Then note the relation on the adjMat and increase the number adjacent for each vertex
		if(onefound && twofound) {
			this.adjMat[index1][index2] = 1;
			this.adjMat[index2][index1] = 1;
			this.vertexList[index1].numAdj++;
			this.vertexList[index2].numAdj++;
		}else {
			// If not, display a message!
			System.out.println("One or more of those airports don't exist!");
		}
	}
}
