import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import ObjectClasses.GeneObject;

public class Driver extends Configured implements Tool {

	private static int ITERATION = 0;

	static enum MyCounter {
		DIFF_COUNTER;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Map-Reduce Started executing ..");

		ToolRunner.run(new Configuration(), new Driver(), args);

		System.out.println("Program End ..");
	}

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try {
			int my_diff_count = 1;
			while (my_diff_count != 0) {
				JobConf conf = new JobConf(getConf(), Driver.class);
				System.out.println("Current Iteration :" + (ITERATION + 1));

				// Hardcoded seed.
				// Path path;
				// if (ITERATION == 0) {
				// path = new Path("/user/hduser/hadoop/seed/cho_seed.txt");
				// } else {
				// path = new Path("/user/hduser/hadoop/output/"
				// + (ITERATION-1) + "/part-00000");
				// }

				Path path;
				if (ITERATION == 0) {
					path = new Path(args[0]);
				} else {
					path = new Path(args[2] + "/"+ (ITERATION - 1) + "/part-00000");
				}

				DistributedCache.addCacheFile(path.toUri(), conf);

				// TODO: specify output types
				conf.setOutputKeyClass(GeneObject.class);
				conf.setOutputValueClass(GeneObject.class);

				// TODO: specify input and output DIRECTORIES (not files)
				FileInputFormat.setInputPaths(conf, new Path(args[1]));
				FileOutputFormat.setOutputPath(conf, new Path(args[2] + "/"
						+ ITERATION)); // Writes each iteration to a new folder

				// TODO: specify a mapper
				conf.setMapperClass(KMeans_Hadoop.KMeansMap.class);

				// TODO: specify a reducer
				conf.setReducerClass(KMeans_Hadoop.KMeansReduce.class);

				// Running the Job and Incrementing a counter.
				RunningJob job = JobClient.runJob(conf);
				Counters counters = job.getCounters();
				Counter shift_counter = counters
						.findCounter(MyCounter.DIFF_COUNTER);
				my_diff_count = (int) shift_counter.getValue();
				shift_counter.setValue(0);

				ITERATION++;

				// Stopping criteria
				System.out.println("Global Counter value:" + my_diff_count);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
