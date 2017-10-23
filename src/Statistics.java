import java.util.Arrays;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Statistics{ 
	
	
    public static double[] getStats(int[] data, int n){
    	double mean = getMean(data, n);
    	double var = getVariance(data, n, mean);
    	double stdDev = getStdDev(var);
    	int mode = getMode(data);
    	double[] stats = new double[5];
    	double ss = getMinSampleSize(var, n*0.001);
    	stats[0]=mean;
    	stats[1]=var;
    	stats[2]=stdDev;
    	stats[3]=(double)mode;
    	stats[4]=(double)ss;
    	return stats;
    }   

    private static double getMean(int[] data, int n){
        double sum = 0.0;
        for(int i = 0; i < 256; i++){
            sum += (double)i*data[i];
        }
        return sum/n;
    }

    private static double getVariance(int[] data, int n, double mean){
        double var = 0.0;
        for(int i = 0; i < 256; i++){
            var += ((double)i-mean)*((double)i-mean)*((double)data[i]/(double)n);
        }
        return var;
    }

    private static double getStdDev(double var){
        return Math.sqrt(var);
    }
    
    private static int getMode(int[] data){
    	int mode = 0;
    	for(int i = 0; i < 256; i++){
    		if(data[i] > data[mode]) mode = i;
    	}
    	return mode;
    }
    
    private static int getMinSampleSize(double var, double error){
    	
    	double signification = 0.05;
    	double zOfHalfSignification = 1.96;
    	double n = (zOfHalfSignification * (var/error))*(zOfHalfSignification * (var/error));
    	int df = (int)n - 1;
    	TDistribution ts = new TDistribution(df);
    	n = (ts.cumulativeProbability(signification) * (var/error))*(ts.cumulativeProbability(signification) * (var/error));
    	if (n%(int)n != 0) n +=1;
    	return (int)n;
    }

}
