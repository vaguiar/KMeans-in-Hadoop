import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ObjectClasses.GeneObject;

public class KMeans {

	private static ArrayList<GeneObject> centroids;
	private static ArrayList<GeneObject> new_centroids;
	private static Map<Integer, ArrayList<GeneObject>> k_clusters = new TreeMap<Integer, ArrayList<GeneObject>>();
	private static Distance dist = new Distance();

	// Default Constructor
	public KMeans() {
	}

	// Constructor to instantiate the list of centroids with input from the user
	// via console.
	public KMeans(int k, ArrayList<GeneObject> gene_list) {

		System.out.println("Enter " + k + " centroids delimited by space");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s;
		try {
			s = br.readLine();
			StringTokenizer st = new StringTokenizer(s);

			// Verify that user provides equal number gene_ids as 'k'
			while (st.countTokens() != k) {
				System.out
						.println("Your list centroids doesnt match the count of'k' which you specified as "
								+ k);
				System.out.println("Enter " + k
						+ " centroids delimited by space");
				s = br.readLine();
				st = new StringTokenizer(s);
			}

			// Iterate thru the gene_list and select the particular centroids
			centroids = new ArrayList<GeneObject>();
			for (GeneObject gene : gene_list) {

				while (st.hasMoreTokens()) {
					if (gene.geneID == Double.parseDouble(st.nextToken())) {
						// gene.geneID = centroids.size() == 0 ? 0 : centroids
						// .size();
						GeneObject gObj = new GeneObject();
						gObj.exps = new ArrayList<Double>();
						for (int j = 0; j < gene.exps.size(); j++) {
							gObj.exps.add(gene.exps.get(j));
						}
						centroids.add(gene);
					}
				}
				st = new StringTokenizer(s); // Reset the string tokenizer to
												// point at the first token.
			}

			// Initialize the map 'k_cluster'
			for (int i = 0; i < centroids.size(); i++) {
				ArrayList<GeneObject> arryLst = new ArrayList<GeneObject>();
				k_clusters.put(i, arryLst);
				// centroids.get(i).geneID = 0; // Dont store the gene id for
				// the centroid.
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<Integer, ArrayList<GeneObject>> runKMeans(
			ArrayList<GeneObject> gene_list, int iter) {

		System.out.println(" \nGenerating Clusters --- \n Iteration : " + iter);

		generate_clusters(gene_list);

		new_centroids = calculate_centroids();

		// Print the size of each cluster
		for (int i = 0; i < k_clusters.size(); i++) {
			System.out.println("CLuster " + i + " size:"
					+ k_clusters.get(i).size());
		}

		if (!match(new_centroids, centroids)) {
			System.out.println("\nNew centoids found...");

			// Assign the new_centroids to the static variable 'centroids'
			centroids = new_centroids;

			runKMeans(gene_list, ++iter);
		}
		return k_clusters;
	}

	// Additional work : tweak the func to take values from the clusters
	// instead of the initial gene list.
	private void generate_clusters(ArrayList<GeneObject> gene_list) {

		int index_of_centroid = -1;
		Double euclidean_dist = 0d;

		for (GeneObject gene : gene_list) {
			Double min = 0d; // Set it to dist from 1st gene in the list

			// For each gene, find the closest possible centroid.
			for (int i = 0; i < centroids.size(); i++) {

				euclidean_dist = dist.distance(gene, centroids.get(i));

				// Set the dist from the 1st centroid to be the initial 'min'.
				if (i == 0) {
					min = euclidean_dist;
					index_of_centroid = 0;
				}

				// If gene is closer to any other centroid.
				if (euclidean_dist < min) {
					min = euclidean_dist;
					index_of_centroid = i; // Marks which centroid is closest to
											// the gene.
				}
			}

			// Testing to see if algo has found atleast one close cluster.
			if (index_of_centroid == -1) {
				System.out.println("Coudnt calculate closest centroid.");
				System.exit(1);
			}

			// Iterate thru k_clusters to add the gene to the correct cluster.
			for (Map.Entry<Integer, ArrayList<GeneObject>> entry : k_clusters
					.entrySet()) {

				// Add to the set of genes of closest centroid.
				if (entry.getKey() == index_of_centroid) {
					if (!entry.getValue().contains(gene)) {
						entry.getValue().add(gene);
					}
				}
				// Remove from cluster held by prev closest centroid.
				if (entry.getKey() != index_of_centroid
						&& entry.getValue().contains(gene)) {
					entry.getValue().remove(gene);
				}
			}

		}
	}

	private ArrayList<GeneObject> calculate_centroids() {

		ArrayList<GeneObject> new_temp_centroids = new ArrayList<GeneObject>();

		for (Map.Entry<Integer, ArrayList<GeneObject>> entry : k_clusters
				.entrySet()) {

			GeneObject temp = new GeneObject();

			// Pass the entire array list for the cluster.
			temp = calc_cluster_centroid(entry.getValue());

			new_temp_centroids.add(temp);

		}

		if (new_temp_centroids.size() != centroids.size()) {
			System.out
					.println("The 'calculate_centroids' function returned improper number of cluster centroids.");
			System.exit(1);
		}

		return new_temp_centroids;
	}

	private GeneObject calc_cluster_centroid(ArrayList<GeneObject> value) {

		GeneObject temp = new GeneObject();

		for (GeneObject gene : value) {
			for (int i = 0; i < gene.exps.size(); i++) {
				// Runs only once to initialize the values of temp.
				while (temp.exps.size() != gene.exps.size()) {
					temp.exps.add(0d);
				}

				temp.exps.set(i, temp.exps.get(i) + gene.exps.get(i));

			}
		}

		// Average out the additions.
		for (int i = 0; i < temp.exps.size(); i++) {
			temp.exps.set(i, temp.exps.get(i) / value.size());
		}
		return temp;
	}

	private boolean match(ArrayList<GeneObject> new_centroids,
			ArrayList<GeneObject> centroids) {

		for (int i = 0; i < centroids.size(); i++) {
			if (dist.distance(centroids.get(i), new_centroids.get(i)) != 0) {
				return false;
			}
		}
		return true;
	}

	// ****************************** MAP REDUCE SPECIFIC FUNCTIONS
	// *******************************

	// Iterates on one gene at a time.
	public static GeneObject getClosestCentroid(GeneObject gene,
			ArrayList<GeneObject> centers) {

		int index_of_centroid = -1;
		Double euclidean_dist = 0d;
		Double min = 0d; // Set it to dist from 1st gene in the list

		// For each gene, find the closest possible centroid.
		for (int i = 0; i < centers.size(); i++) {

			euclidean_dist = dist.distance(gene, centers.get(i));

			// Set the dist from the 1st centroid to be the initial 'min'.
			if (i == 0) {
				min = euclidean_dist;
				index_of_centroid = 0;
			}

			// If gene is closer to any other centroid.
			if (euclidean_dist < min) {
				min = euclidean_dist;
				index_of_centroid = i; // Marks which centroid is closest to
										// the gene.
			}
		}

		// Testing to see if algo has found atleast one close cluster.
		if (index_of_centroid == -1) {
			System.out.println("Coudnt calculate closest centroid for geneID:"
					+ gene.geneID);
			System.exit(1);
		}

		return centers.get(index_of_centroid);
	}

}
