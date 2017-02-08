package main;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Tuple<Int1, Int2> { 
	  public final int x; 
	  public final int y; 
	  public Tuple(int x, int y) { 
	    this.x = x; 
	    this.y = y; 
	  } 
} 

class comp implements Comparator<String> {
	  public int compare(String o1, String o2) {
	    return Integer.compare(o1.length(), o2.length());
	  }
}

public class Spiral {
	
	final static double mp8 = Math.PI/180; // 1 Radian
	
	Spiral(){	//constructor
	}
	
	public static void main(String[] args) throws IOException {
		//System.out.println("Please enter a number for the model");
		//Scanner scan = new Scanner(System.in);
		//String s = scan.nextLine();
		
		drawSackSpiral(1000000);	//one million
		
		/*
		for (double i=-2;i<=2;i+=0.1){
			drawSackSpiral(1000000,i);
		}
		System.out.println("finished the order!");
		*/
	}

	static boolean isPrime(int n){
		//check if n is a multiple of 2
	    if (n%2==0) return false;
	    //if not, then just check the odds
	    for(int i=3;i*i<=n;i+=2) {
	        if(n%i==0)
	            return false;
	    }
	    return true;
	}
	
	public final static boolean isPerfectSquare(long n)
	{
	  if (n < 0)
	    return false;

	  long tst = (long)(Math.sqrt(n) + 0.5);
	  return tst*tst == n;
	}

	
	private static void drawSackSpiral(int n) throws IOException {
		Path file = Paths.get("prime-spiral.asc");
		List<String> coords = new ArrayList<String>();
	    
	    for (int i=0;i<n;i++){
	    	double x = (-1) * Math.cos(Math.sqrt(i)*2*Math.PI)*Math.sqrt(i);
	    	double y = Math.sin(Math.sqrt(i)*2*Math.PI)*Math.sqrt(i) ;
	    	
	    	if ( isPrime(i) ){ 		//if point represents a prime number
	        	coords.add(x+" "+y+" "+0);
	        }
	    }
	    
	    Files.write(file, coords, Charset.forName("UTF-8"));
	    System.out.println("got here, Sack's Spiral");
	}
	
	static Path drawSackSpiral(int n, double u) throws IOException {	//u=2 for default operation
		u = (double)Math.round(u * 10d) / 10d;
		System.out.println(u);
		
		Path file = Paths.get("prime-spiral- "+u+".asc");
		List<String> coords = new ArrayList<String>();
		
	    
	    for (int i=0;i<n;i++){
	    	double x = (-1) * Math.cos(Math.sqrt(i)*2*Math.PI)*Math.sqrt(i);
	    	double y = Math.sin(Math.sqrt(i)*u*Math.PI)*Math.sqrt(i) ;
	    	
	    	if ( isPrime(i) ){ 		//if point represents a prime number
	        	coords.add(x+" "+y+" "+0);
	        }
	    }
	    
	    Files.write(file, coords, Charset.forName("UTF-8"));
	    return file;
	}

}