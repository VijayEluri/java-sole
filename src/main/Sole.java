package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Sole {
	public static void main(String args[]) throws IOException, InterruptedException
	{
		int b = 32;
		BigInteger n = (BigInteger.valueOf(2).pow(b/2)).add(BigInteger.valueOf(1/2));
		BigInteger blockSize = BigInteger.valueOf(2).pow(b);
		BigInteger local[] = new BigInteger[4];
		
		FileInputStream in = null;
        try {
            in = new FileInputStream("nyu.png");
            int i = 0;
            String bin, buffer = "";
            bin = get3Buffers("", in, b);
        	while(bin.length() > 3 * b) {
        		buffer = bin.substring(0,64);
        		if(buffer.length() == 2 * b) {
        			actualComp(buffer, b, local, blockSize, i);
        			i++;
        			buffer = "";
        			bin = get3Buffers(bin.substring(2 * b + 1), in, b);
        			System.out.println(2 * b);
        		}
        	}
        	
        	System.out.println(bin);
        	//Thread.sleep(10);		
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
		
		
		
		
		
		
		
		
	}
	public static String get3Buffers(String more, FileInputStream in, int b) throws IOException {
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
	public static void actualComp(String buffer,int b, BigInteger[] local, BigInteger blockSize, int i) {
		String buffer1 = buffer.substring(0, b);
		String buffer2 = buffer.substring(b, 2 * b) ;
		if(local[0] == null)
		{
			local[0] = new BigInteger(buffer1,2);
			local[1] = new BigInteger(buffer2,2);
			BigInteger A, B, x, y, Api, Bpi, result;
			A = blockSize.add(BigInteger.valueOf(2));
			B = blockSize.add(BigInteger.valueOf(2));
			x = local[0];
			y = local[1];
			Api = blockSize;
			Bpi = blockSize.add(BigInteger.valueOf(8));
			result = swap(A,B,Api,Bpi,x,y)[0];
			System.out.print(result.toString(16));
			System.out.print(" ");
		}
		else if(local[2] == null)
		{
			local[2] = new BigInteger(buffer1,2);
			local[3] = new BigInteger(buffer2,2);
			compOut(blockSize,local,i);
		}
		else
		{
			local[0] = local[2];
			local[1] = local[3];
			local[2] = new BigInteger(buffer1,2);
			local[3] = new BigInteger(buffer2,2);
			compOut(blockSize,local,i);
		}
	}
	public static void compOut(BigInteger blockSize, BigInteger[] local, int i){
		BigInteger A, B, x, y, Api, Bpi, nextx, nexty;
		BigInteger[] xypi = new BigInteger[2];
		//1 pass
		A = blockSize.add(BigInteger.valueOf(2));
		B = blockSize.add(BigInteger.valueOf(2));
		//1 pass front
		x = local[0];
		y = local[1];
		Api = blockSize.add(BigInteger.valueOf(-(i-1)*8));
		Bpi = blockSize.add(BigInteger.valueOf(+(i-0)*8));
		nextx = swap(A,B,Api,Bpi,x,y)[1];
		//1 pass end
		x = local[2];
		y = local[3];
		Api = blockSize.add(BigInteger.valueOf(-(i-0)*8));
		Bpi = blockSize.add(BigInteger.valueOf(+(i+1)*8));
		nexty = swap(A,B,Api,Bpi,x,y)[0];
		
		//2 pass
		x = nextx;
		y = nexty;
		A = blockSize.add(BigInteger.valueOf(+(i-0)*8));
		B = blockSize.add(BigInteger.valueOf(-(i-0)*8));
		Api = blockSize;
		Bpi = blockSize;
		xypi = swap(A,B,Api,Bpi,x,y);
		System.out.print(xypi[0].toString(16));
		System.out.print(" ");
		System.out.print(xypi[1].toString(16));
		System.out.print(" ");
	}
	public static BigInteger[] swap(BigInteger A,BigInteger B,BigInteger Api,BigInteger Bpi,BigInteger x,BigInteger y){
		BigInteger z = y.multiply(A).add(x);
		BigInteger[] out = {z.mod(Api),z.divide(Api)};
		return out;
	}
}
