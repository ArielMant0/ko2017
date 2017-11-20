package knapsack;

import java.util.*;



/**
 * Branch and Bound solver for binary knapsack problem
 * @author andikrus
 *
 */

public class BnBBinarySolver {
	
	protected List<Item> items;
	protected int capacity;
	
	/**
	 * A node holds information about its position in the search tree,
	 * all items that are packed into the knapsack so far,
	 * a bound plus the sum of all the values and weights of the items 
	 * packed so far. Nodes can be compared by their bound for an 
	 * efficient sorting of nodes.
	 * @author andikrus
	 *
	 */

	private class Node implements Comparable<Node>{
		
		// position in search tree
		public int h;
		//list of items packed so far
		ArrayList<Item> taken;
		public double bound;
		public double value;
		public double weight;
		
		public Node(){
			taken = new ArrayList<Item>();
		}
		
		//
		public Node (Node parent){
			h = parent.h+1;
			taken = new ArrayList<Item>(parent.taken);
			bound = parent.bound;
			value = parent.value;
			weight = parent.weight;
		}
		
		public int compareTo(Node other){
			return(int)(other.bound - bound);
		}
		
		/**
		 * Computation of UB
		 */
		public void computeBound(){
			
			int i = h;
			double w = weight;
			bound = value;
			Item item;
			do {
				//iterate through items beginning at height in search tree and add 
				//up weights and values of items until either capacity would be reached 
				//in the next iteration or all items are considered
				item = items.get(i);
				if(w + item.weight > capacity) break;
				w += item.weight;
				bound += item.value;
				i++;
			} while (i < items.size());
			//last item gets packed fractional to add up to the capacity
			bound += (capacity - w) * (item.value / item.weight);
		}
		
	}

	/**
	 * 
	 * @param items, list of given items
	 * @param capacity, weight limit
	 */
	public BnBBinarySolver(List<Item>items, int capacity){
		this.items = items;
		this.capacity = capacity;
	}
	
	/**
	 * Solves binary knapsack problem, branch and bound algorithm
	 * @return solution for bkp
	 */
	public List<Item> solve() {
		//items get sorted according to c/w ratio
		Collections.sort(items, Item.byRatio());
		
		
		Node best = new Node();
		Node root = new Node();
		//computation of initial bound (root -> h = 0; bound, value, weight = 0.0; taken is empty, default):
		root.computeBound();
		
		//q holds all considerable solutions, sorted by bounds
		PriorityQueue<Node> q = new PriorityQueue<Node>();
		q.offer(root);
		
		while(!q.isEmpty()) {
			Node node = q.poll();
			
			
			if(node.bound > best.value && node.h < items.size() - 1) {
				//Node with: right branch of search tree, xi = 1, item gets packed into knapsack
				Node with = new Node(node);
				Item item = items.get(node.h);
				with.weight += item.weight;
				
				//if the weight of node with is not higher than the capacity...
				if (with.weight <= capacity) {
					//...the item of index node.h gets added to the node with...
					with.taken.add(items.get(node.h));
					with.value += item.value;
					//...and a new bound is computed.
					with.computeBound();
					
					//best gets updated if the current value is the current best
					if (with.value > best.value){
						best = with;
					}
					//and with gets added to the considerable solutions if its bound 
					//is better than the current best
					if (with.bound > best.value){
						q.offer(with);
					}
				}
				//Node without: left branch of sub tree, xi = 0, item does not get packed into knapsack
				Node without = new Node(node);
				without.computeBound();
				
				//Without is also considered as a possible solution, if the computed bound is 
				// bigger than best.value
				if (without.bound > best.value){
					q.offer(without);
				}
				
			}
		}
		//A list of all taken items from the best solution gets returned
		return best.taken;
	}
	
	
}
