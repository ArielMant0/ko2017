package knapsack;

import java.util.Comparator;

public class Item {
	
	public int label;
	public int value;
	public int weight;
	
	/**
	 * Get value/weight ratio (in the following c/w due to laziness)
	 * @return
	 */
	public double getCW(){
		return value/weight;
	}

	/**
	 * Comparator used for initial sorting of items
	 * 
	 */
	public static Comparator<Item> byRatio(){
		return new Comparator<Item>() {
			public int compare(Item i1, Item i2) {
				return Double.compare(i2.getCW(), i2.getCW());
			}
		};
	}
}
