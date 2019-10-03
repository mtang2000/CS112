package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import structures.Arc;
import structures.Graph;
import structures.Vertex;

public class Driver {

	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) throws IOException {
		PartialTreeList pt = new PartialTreeList();
		System.out.print("Enter Graph File, 'quit' to stop => ");
		String graphfile = keyboard.readLine();
		
		while(!graphfile.endsWith("quit")){
			Graph g = new Graph(graphfile);
			PartialTreeList tree = pt.initialize(g);
			

			System.out.println("Partial Tree List:");
			Iterator iter = tree.iterator();
			while(iter.hasNext())
				System.out.println(iter.next().toString());
			
			
			ArrayList<Arc> path = pt.execute(tree);
			
			System.out.println(path.toString());
			int ans = 0;
			for(Arc temp: path)
				ans += temp.getWeight();
		
			System.out.println("The sum of the edge weights for this spanning tree is " + ans);
			
			System.out.print("\nEnter Graph File, 'quit' to stop => ");
			graphfile = keyboard.readLine();
		}
	}

}

