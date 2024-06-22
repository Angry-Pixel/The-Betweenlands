package thebetweenlands.util;

import java.util.ArrayList;
import java.util.List;

// a list that contains a list of weights, order is biome id
public class WeightedList {
	public int total = 0; // just going to reference this rather than make a geter (im a bit of a speed freak with code)
	public List<Integer> weights = new ArrayList<>();

	public WeightedList(int... inputWeight) {
		for (int input : inputWeight) {
			total += input;
			weights.add(input);
		}
	}

	public void add(int... inputWeight) {
		for (int input : inputWeight) {
			total += input;
			weights.add(input);
		}
	}

	public int getRandomItem(int weight) {
		if (this.weights.isEmpty())
			return 0;

		if (total == 0)
			return 0;

		for (int id = 0; id < this.weights.size(); id++) {
			weight -= this.weights.get(id);
			if (weight < 0) {
				return id;
			}
		}
		return 0;
	}
}
