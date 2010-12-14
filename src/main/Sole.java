package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
public class Sole {
	public final static int head_mask = 1;
	public final static int end_mask = 2;
	public static int b = 64;
	public static BigInteger n = (BigInteger.valueOf(2).pow(b/2)).add(BigInteger.valueOf(1/2));
	public static BigInteger blockSize = BigInteger.valueOf(2).pow(b);
	public static BigInteger local[] = new BigInteger[4];
	public static BigInteger A, B, x, y, Api, Bpi, nextx, nexty,z,xypi[];
	public static FileInputStream in = null;
	public static int index = 0;
	public static String bin,buffer;
	public static void main(String args[]) throws IOException, InterruptedException
	{
        try {
            in = new FileInputStream("nyu.png");
            bin = get3Plus("");
        	while(bin.length() > 3 * b) {
        		buffer = bin.substring(0, 2 * b);
        		actualComp();
        		bin = get3Plus(bin.substring(2 * b));
        	}
        	//will handle the EOF here
        	handleEOF(bin);
        	
        	System.out.println(bin);
        	//Thread.sleep(10);		
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
	}
	public static String handleEOF(String bin)
	{
		// b < bin's length <= 3b
		if(bin.length() <= 2 * b)
		{
			local[0] = local[2];
			local[1] = local[3];
			local[2] = B;
			local[3] = new BigInteger(bin.substring(0, b), 2);
			
			xypi = compOut(0);

//			System.out.print(xypi[0].toString(16));
//			System.out.print(" ");
//			System.out.print(xypi[1].toString(16));
//			System.out.print(" ");
			
			local[0] = local[2];
			local[1] = local[3];
			local[2] = new BigInteger(bin.substring(b), 2);
			local[3] = BigInteger.ZERO;
			
			xypi = compOut(0);
			
//			System.out.print(xypi[0].toString(16));
//			System.out.print(" ");
//			System.out.print(xypi[1].toString(16));
//			System.out.print(" ");
		}
		else// 2b < bin's length <= 3b
		{
			
		}
			
		return null;
		
	}
	public static String get3Plus(String more) throws IOException {
		int c = in.read();
		while(c != -1) {
			more = more + Integer.toBinaryString(c);
			if(more.length() <= 3 * b) {
				c = in.read();
			}
			else {
				break;
			}
		}
		return more;
	}
	public static void actualComp() {
		String buffer1 = buffer.substring(0, b);
		String buffer2 = buffer.substring(b);
		if(local[0] == null)//compute the first output
		{
			local[0] = new BigInteger(buffer1,2);
			local[1] = new BigInteger(buffer2,2);
			
			xypi = compOut(head_mask);
			
//			System.out.print(xypi[0].toString(16));
//			System.out.print(" ");
		}
		else if(local[2] == null)//compute the 2nd and 3rd output
		{
			local[2] = new BigInteger(buffer1,2);
			local[3] = new BigInteger(buffer2,2);
			
			xypi = compOut(0);
			
//			System.out.print(xypi[0].toString(16));
//			System.out.print(" ");
//			System.out.print(xypi[1].toString(16));
//			System.out.print(" ");
		}
		else
		{
			local[0] = local[2];
			local[1] = local[3];
			local[2] = new BigInteger(buffer1,2);
			local[3] = new BigInteger(buffer2,2);
			
			xypi = compOut(0);
			
//			System.out.print(xypi[0].toString(16));
//			System.out.print(" ");
//			System.out.print(xypi[1].toString(16));
//			System.out.print(" ");
		}
	}
	public static BigInteger[] compOut(int control){
		
		//1 pass
		A = blockSize.add(BigInteger.valueOf(2));
		B = blockSize.add(BigInteger.valueOf(2));
		//1 pass front
		x = local[0];
		y = local[1];
		Api = blockSize.add(BigInteger.valueOf(-(index-1)*8));
		Bpi = blockSize.add(BigInteger.valueOf(+(index-0)*8));
		//handle the 1st block
		if((head_mask & control) > 0)
		{
			return swap();
		}
		else{
			nextx = swap()[1];
		}
		
		//1 pass end
		x = local[2];
		y = local[3];
		Api = blockSize.add(BigInteger.valueOf(-(index-0)*8));
		Bpi = blockSize.add(BigInteger.valueOf(+(index+1)*8));
		nexty = swap()[0];
		//2 pass
		x = nextx;
		y = nexty;
		A = blockSize.add(BigInteger.valueOf(+(index-0)*8));
		B = blockSize.add(BigInteger.valueOf(-(index-0)*8));
		Api = blockSize;
		Bpi = blockSize;
		
		index++;
		return swap();
	}
	public static BigInteger[] swap(){
		z = y.multiply(A).add(x);
		return new BigInteger[] {z.mod(Api),z.divide(Api)};
	}
}
