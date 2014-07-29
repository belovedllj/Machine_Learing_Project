
import java.io.*;
import java.util.*;

/*
 * Author Peng Peng
 */

public class ProEmailDetect {

	private static Map<String, Double> wordFrequencyInPromotions = new HashMap<String, Double>();
	private static Map<String, Double> wordFrequencyInRegulars = new HashMap<String, Double>();
	//private static PriorityQueue<StringWithPPW> queue = null;
	
	private static class StringWithPPW implements Comparable {
		private String wordName;
		private double PPW;
		public StringWithPPW() {
			
		}
		public StringWithPPW (String wordName, double number) {
			this.wordName = wordName;
			PPW = number;			
		}
		@Override
		public int compareTo(Object obj) {
			if (PPW - ((StringWithPPW)obj).PPW > 0)
			return 1;
			else if (PPW - ((StringWithPPW)obj).PPW < 0) {
				return -1;
			}
			return 0;				
		}		
	}
	
	public ProEmailDetect() {
	
	}
	
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		trainingPromotionsData();
		trainingRegularData();	
		int counter = 0;
		//int counter2 = 0;
		PrintWriter wr;
		//wr = new PrintWriter(new FileWriter("record2.csv"));
		for (int index = 1; index <= 10; index++) {
			String filename = "pro_test" + index + ".txt";
			try {
				PriorityQueue<StringWithPPW> queue = new PriorityQueue<StringWithPPW>(15, Collections.reverseOrder());
				Scanner scan = new Scanner(new BufferedReader(new FileReader(filename)));
				scan.useDelimiter("[\\s+.,:;?!*()&|{}/]");
				Set<String> recordInOneEmail = new HashSet<String>();
				while (scan.hasNext()) {
					String a = scan.next();
					if(a.length() <= 3) continue;
					a = a.toLowerCase(); // save words to lowercase
					if (recordInOneEmail.contains(a)) {
						continue;
					}
					recordInOneEmail.add(a);
					double frequency = caculatePSW(a);
					StringWithPPW strPPW = new StringWithPPW(a, frequency);
					queue.offer(strPPW);
					
					
					// System.out.println(a);	 
		        }
				double finalValue = caculateP(queue);
				if (finalValue > 0.999) counter++;
				//counter2++;
				System.out.println("fianla values is: " + finalValue);
				//PrintWriter wr = new PrintWriter(new FileWriter("record3.csv"));
				//wr.println(finalValue);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//wr.close();
		System.out.println("counter is " + counter);
		
	}
	
	private static double caculateP(PriorityQueue<StringWithPPW> queue) {
		double[] PArray = new double[10];
		for(int i = 0; i < 23; i++) {
			queue.remove();
			//System.out.println(PArray[i]);
		}
		for(int i = 0; i < PArray.length; i++) {
			PArray[i] = queue.remove().PPW;
			//System.out.println(PArray[i]);
		}
		double finalPValue;
		double a = 1;
		double b = 1;
		for(int i = 0; i < PArray.length; i++) {
			a *= PArray[i];
			b *= (1 - PArray[i]);
		}
		finalPValue = (double)a / (a + b);
		return finalPValue;
		
	}
	
	private static void trainingPromotionsData() {
		for(int index = 1; index <= 100; index++) {
			String filename = "pro" + index + ".txt";
			try {
				Scanner sc = new Scanner(new BufferedReader(new FileReader(filename)));
				sc.useDelimiter("[\\s+.,:;?!*()&|{}/]"); 
				// Separate words in sc by regular expression [\\s+.,:;?!()&|{}]
				Set<String> recordInOneEmail = new HashSet<String>(); 
				while (sc.hasNext()) {
					String a = sc.next();
					if(a.length() <= 3) continue;
					a = a.toLowerCase(); // save words to lowercase
					if(!recordInOneEmail.contains(a)) {
						recordInOneEmail.add(a);
					}				
		          // System.out.println(a);	 
		        }
				for(String words : recordInOneEmail) {
					if (wordFrequencyInPromotions.containsKey(words)) {
						double fre = wordFrequencyInPromotions.get(words);						
						wordFrequencyInPromotions.put(words, fre + 1.0/100.0);
						
						//System.out.println(fre);
					}
					else {
						wordFrequencyInPromotions.put(words, 1.0/100.0);
					}
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(wordFrequencyInPromotions.get("shop"));
		}
	}
	
	//the regular expression [.,:;?] specifies a pattern that matches ., ,, :, ;, or	?.
	private static void trainingRegularData() {
		for(int index = 1; index <= 100; index++) {
			String filename = "reg" + index + ".txt";
			try {
				Scanner sc2 = new Scanner(new BufferedReader(new FileReader(filename)));
				sc2.useDelimiter("[\\s+.,:;?!*()&|{}/]"); 
				// Separate words in sc by regular expression [\\s+.,:;?!()&|{}]
				Set<String> recordInOneEmail = new HashSet<String>(); 
				while (sc2.hasNext()) {
					String a = sc2.next();
					if(a.length() <= 3) continue;
					a = a.toLowerCase(); // save words to lowercase
					if(!recordInOneEmail.contains(a)) {
						recordInOneEmail.add(a);
					}				
		          // System.out.println(a);	 
		        }
				for(String words : recordInOneEmail) {
					if (wordFrequencyInRegulars.containsKey(words)) {
						double fre = wordFrequencyInRegulars.get(words);						
						wordFrequencyInRegulars.put(words, fre + 1.0/100.0);
						
						//System.out.println(fre);
					}
					else {
						wordFrequencyInRegulars.put(words, 1.0/100.0);
					}
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(wordFrequencyInPromotions.get("shop"));
		}
	}
	
	private static double caculatePSW(String word) {
		double PWP, PWR; 
		if (wordFrequencyInPromotions.containsKey(word)) {
			PWP = wordFrequencyInPromotions.get(word);
		}
		else {
			PWP = 0;
		}
		
		if (wordFrequencyInRegulars.containsKey(word)) {
			PWR = wordFrequencyInRegulars.get(word);
		}
		else {
			PWR = 0.01;
		}
		if(PWP == 0 && PWR == 0) return 0.4;
		return (PWP * 0.5) / (PWP * 0.5 + PWR * 0.5);
		
	}
	
	
}
