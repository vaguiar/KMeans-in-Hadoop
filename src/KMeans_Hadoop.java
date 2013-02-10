import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import ObjectClasses.GeneObject;

public class KMeans_Hadoop {

	public static class KMeansMap extends MapReduceBase implements
			Mapper<Object, Text, GeneObject, GeneObject> {

		private static ArrayList<GeneObject> centroids = new ArrayList<GeneObject>();

		// Converts the string read from file to a GeneObject
		private GeneObject string_to_Centroid(StringTokenizer st) {

			String arr[] = new String[st.countTokens()];
			int i = 0;

			// Storing the tokens in an array
			while (st.hasMoreTokens()) {
				arr[i] = st.nextToken();
//				System.out.println("Token "+i+": "+arr[i]);
				i++;
			}

			// Creating a new GeneObject from array.
			GeneObject gene = new GeneObject();
			gene.geneID = Integer.parseInt(arr[0]);
			gene.groundTruth = Float.parseFloat(arr[1]);

			// Storing the exp values into gene.exps
			for (Integer j = 2; j < arr.length; j++) {
				gene.exps.add(Double.parseDouble(arr[j]));
			}

			return gene;
		}

		// Read each line from file and return apt values.
		// Called only once, before Mapper.
		public void setupCentroids(FileReader file) {

			try {
				BufferedReader bs = new BufferedReader(file);
				StringTokenizer st;
				String s;

				while ((s = bs.readLine()) != null) {
					st = new StringTokenizer(s, "\t");
					GeneObject centr = string_to_Centroid(st);
					centroids.add(centr);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void configure(JobConf job) {

			FileReader file;
			try {
				super.configure(job);
				// Path path = new Path("/user/hduser/hadoop/data/cho.txt");
				Path[] cacheFiles = DistributedCache.getLocalCacheFiles(job);
				Path path = cacheFiles[0];

				file = new FileReader(path.toUri().getPath());
				setupCentroids(file);
				// After Creating the centroids;
				System.out.println("Centroids created: " + centroids.size());

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		// Assuming a perfect world input, write the code that should work only
		// in your mapper.
		public void map(Object key, Text value,
				OutputCollector<GeneObject, GeneObject> output,
				Reporter reporter) throws IOException {
			
			//String s = key.toString() + "\t" + value.toString();
			String s = value.toString();
			StringTokenizer st = new StringTokenizer(s);
			//GeneObject gene = compose_Gene(st);
			GeneObject gene = string_to_Centroid(st);
			
			GeneObject center = KMeans.getClosestCentroid(gene, centroids);
			System.out.println("Gene: "+gene.geneID+ "\tCentroid: " +center.geneID);
			
			output.collect(center, gene);
		}

	}

	public static class KMeansReduce implements
			Reducer<GeneObject, GeneObject, Object, String> {
		private static Distance dist = new Distance();
		
		@Override
		public void reduce(GeneObject center, Iterator<GeneObject> gene_itr,
				OutputCollector<Object, String> output, Reporter reporter)
				throws IOException {
			GeneObject temp = new GeneObject();
			int count = 0;

			while (gene_itr.hasNext()) {
				ArrayList<Double> exps = new ArrayList<Double>();
				GeneObject gene = gene_itr.next();
				exps = gene.exps;
				//exps = gene_itr.next().exps;

				for (int i = 0; i < exps.size(); i++) {
					// Runs only once to initialize the values of temp.
					while (temp.exps.size() != exps.size()) {
						temp.exps.add(0d);
					}

					temp.exps.set(i, temp.exps.get(i) + exps.get(i));
				}
				count++;
				System.out.println("Gene: "+gene.geneID+ "\tCentroid: " +center.geneID);
			}			

			// Average out the additions.
			for (int i = 0; i < temp.exps.size(); i++) {
				temp.exps.set(i, temp.exps.get(i) / count);
			}
			temp.geneID = center.geneID;
			temp.groundTruth = 0f;

			// Check if new_centroids match with the prev one
			if(dist.distance(temp, center) != 0){
				reporter.incrCounter(Driver.MyCounter.DIFF_COUNTER, 1);
			}			
			
			//Printing out the Cluster Size
			System.out.println("/nCluster Size: "+count);
			
			output.collect(null, temp.toString());
			
		}

		@Override
		public void configure(JobConf arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub

		}
	}

}
