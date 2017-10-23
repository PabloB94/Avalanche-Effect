import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.awt.List;
import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class Manager {
	final static int MEAN = 0;
	final static int VAR = 1;
	final static int STDDEV = 2;
	final static int MODE = 3;
	final static int SS = 4;

	public static void main(String[] args) throws Exception{
		
		Random rnd = new Random();
		int Low = 10;
		int High = 100;
		RipeMD256_Hasher hasher = new RipeMD256_Hasher();
				
		int[] data = new int[256];
		for (int i = 0; i<256; i++){
			data[i]=0;
		}
		int[] cumulativeData = new int[256];
		for (int i = 0; i<256; i++){
			cumulativeData[i]=0;
		}
		int loops = 0;
		int minSample = 1000;
		int testRuns = 0;
		ArrayList<double[]> samples = new ArrayList<double[]>();
		boolean confidenceReached = false;
		//Sample run
		while(!confidenceReached){
			testRuns++;
			while(loops < minSample){
				String inputString = getRandom(rnd.nextInt(High-Low) + Low);
				byte[] input = inputString.getBytes();
				byte[] output = hasher.hashF(input);	        
				byte[] tamperedInput = randomBitFlip(input);
				byte[] tamperedOutput = hasher.hashF(tamperedInput);
				int hd = hammingDistance(output, tamperedOutput);
				data[hd]++;
				loops++;
			}
			double[] stats = Statistics.getStats(data, loops);		
			System.out.println("Statistics for run: " + testRuns);
			System.out.println("Sample size:     " + loops);
			System.out.println("Mean:            " + stats[MEAN]);
			System.out.println("Variance:        " + stats[VAR]);
			System.out.println("Std Deviation:   " + stats[STDDEV]);
			System.out.println("Mode:            " + stats[MODE]);
			System.out.println("Min sample size: " + stats[SS]);
			if(stats[SS] > minSample){
				System.out.println("Insufficient sample size");
				samples.add(stats);
				minSample += 100;
				System.out.println("Variables reset. Sample size increased to: " + minSample);
				System.out.println("");
			}else{				
				samples.add(stats);
				confidenceReached = !confidenceReached;
				System.out.println("");
			}	
//			loops = 0;
		}
//		System.out.println("Variance difference between runs under 5%. Total runs with simple size " + minSample + ": " +  testRuns);
		System.out.println("Total tests: " + loops);
		exportToExcel(data);
	}

	private static byte[] randomBitFlip(byte[] b) {
	    BitSet bitset = BitSet.valueOf(b);
	    Random rnd = new Random();
	    bitset.flip(rnd.nextInt(bitset.length()));
	    return bitset.toByteArray();
	}
	
	private static int hammingDistance(byte[] arg1, byte[] arg2){
		BitSet bits1 = BitSet.valueOf(arg1);
		BitSet bits2 = BitSet.valueOf(arg2);
		int distance = 0;
		for (int i = 0; i < bits1.length(); i++){
			if(bits1.get(i) != bits2.get(i)){
				distance++;
			}
		}
		return distance;
	}
	
	
	private static String getRandom(int numchars){
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while(sb.length() < numchars){
			sb.append(Integer.toHexString(r.nextInt()));
		}
		return sb.toString().substring(0, numchars);
	}
	
//	private static boolean confidenceTest(ArrayList<double[]> samples){
//		if(samples.size()<=1) return false;
//		double highVar = 0.0;
//		double lowVar = samples.get(0)[VAR];
//		for(int i = 0; i < samples.size(); i++){
//			if(samples.get(i)[VAR] > highVar) highVar = samples.get(i)[VAR];
//			if(samples.get(i)[VAR] < lowVar) lowVar = samples.get(i)[VAR];
//		}
//		if(1-(lowVar/highVar)<=0.05){
//			return true;
//		}else{
//			return false;
//		}
//	}
	
	private static void exportToExcel(int[] dataRaw) throws FileNotFoundException, IOException, InvalidFormatException{
		{
			int[][] data = new int[256][2];
			for (int i = 0; i<256; i++){
				data[i][0]=i;
				data[i][1]=dataRaw[i];
			}
	        //Create new workbook and tab
	        Workbook wb = new HSSFWorkbook();
	        FileOutputStream fileOut = new FileOutputStream("Avalanche Effect Results.xslx                                                                             ");
	        Sheet sheet = wb.createSheet("Result Set");
	 
	        //Create 2D Cell Array
	        Row[] row = new Row[data.length];
	        Cell[][] cell = new Cell[row.length][];
	 
	        //Define and Assign Cell Data from Given
	        for(int i = 0; i < row.length; i ++)
	        {
	            row[i] = sheet.createRow(i);
	            cell[i] = new Cell[data[i].length];
	 
	            for(int j = 0; j < cell[i].length; j ++)
	            {
	                cell[i][j] = row[i].createCell(j);
	                cell[i][j].setCellValue(data[i][j]);
	            }
	 
	        }
	 
	        //Export Data
	        wb.write(fileOut);
	        fileOut.close();
	        wb.close();
	 
	    }
	}
	
}
