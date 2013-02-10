package ObjectClasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.WritableComparable;

public class GeneObject implements WritableComparable<Object> {
	// public static boolean print = false;
	public Integer geneID = null;
	public Float groundTruth = null;
	public ArrayList<Double> exps = new ArrayList<Double>();

	@Override
	public String toString() {
		String str = new String();
		str = geneID + "\t" + groundTruth;
		for (Double f : exps) {
			str += "\t" + f;
		}
		return str;
	}

	public GeneObject() {

	}

	// Overridden and to be used by the MAP-REDUCE functions
	public void write(DataOutput out) throws IOException {
		out.writeInt(geneID);
		out.writeFloat(groundTruth);
		out.writeInt(exps.size());
		for (int index = 0; index < exps.size(); index++) {
			out.writeDouble(exps.get(index));
		}
	}

	public void readFields(DataInput in) throws IOException {
		geneID = in.readInt();
		groundTruth = in.readFloat();
		int size = in.readInt();
		exps.clear();
		for (int index = 0; index < size; index++) {
			exps.add(in.readDouble());
		}
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return geneID == ((GeneObject) obj).geneID;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return geneID;
	}

	@Override
	public int compareTo(Object o) {

		return geneID - ((GeneObject) o).geneID;
	}

}
