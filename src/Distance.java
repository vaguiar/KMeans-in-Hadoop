

import java.util.ArrayList;

import ObjectClasses.GeneObject;

public class Distance {

	public static void main(String[] args) {

	}

	public Double distance(GeneObject g1, GeneObject g2) {

		return distance(g1.exps, g2.exps);
	}

	public Double distance(ArrayList<Double> d1, ArrayList<Double> d2) {
		Double result = new Double(0);
		if (d1.size() == d2.size()) {

			for (int i = 0; i < d1.size(); i++) {
				result += Math.pow(d1.get(i) - d2.get(i), 2);
			}
			result = Math.sqrt(result);

		}
		return result;
	}

//
//	// Calculates the distance from the avg of each value in the list and the
//	// constant 'c'.
//	public Double distance(ArrayList<Double> list, Double c) {
//		Double result = new Double(0);
//
//		for (Double d : list) {
//			result += d;
//		}
//		return Math.abs(c- result/list.size());
//
//	}

}
