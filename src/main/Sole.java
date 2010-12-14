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
		
//		local[0] = new BigInteger("8");
//		local[1] = new BigInteger("57");
//		local[2] = new BigInteger("17");
//		local[3] = new BigInteger("33");
		
		String buffer = "", buffer1, buffer2;
		
		
		FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream("nyu.png");
            //out = new FileOutputStream("outagain.txt");
            int c;
            String bin;
            int i = 1;
            int j;
            c = in.read();
            while (c != -1) {
            	bin = Integer.toBinaryString(c);
            	for(j=0; j < bin.length(); j++)
            	{
            		buffer = buffer + bin.charAt(j);
            		c = in.read();
            		if(c != -1)//not the end
            		{
            			if(buffer.length() == 2 * b){    
	            			buffer1 = buffer.substring(0, b);
	            			buffer2 = buffer.substring(b, 2 * b) ;
	            			if(local[0] == null)
	            			{
	            				local[0] = new BigInteger(buffer1,2);
	            				local[1] = new BigInteger(buffer2,2);
	            				BigInteger A, B, x, y, Api, Bpi, result;
	            				A = blockSize.add(BigInteger.valueOf(2));
	            				B = blockSize.add(BigInteger.valueOf(2));
	            				x = local[0];
	            				y = local[1];
	            				Api = blockSize.add(BigInteger.valueOf(-(i-1)*8));
	            				Bpi = blockSize.add(BigInteger.valueOf(+(i-0)*8));
	            				result = swap(A,B,Api,Bpi,x,y)[0];
	            				System.out.print(result.toString(16));
	            				System.out.print(" ");
	            			}
	            			else if(local[2] == null)
	            			{
	            				local[2] = new BigInteger(buffer1,2);
	            				local[3] = new BigInteger(buffer2,2);
	            				compOut(blockSize,local,i);
	            				i ++;
	            			}
	            			else
	            			{
	            				local[0] = local[2];
	            				local[1] = local[3];
	            				local[2] = new BigInteger(buffer1,2);
	            				local[3] = new BigInteger(buffer2,2);
	            				compOut(blockSize,local,i);
	            				i ++;
	            			}
	            			buffer = "";
	            			buffer1 = "";
	            			buffer2 = "";
            			}
            		}
            		else//end two blocks
            		{
            			if(buffer.length() == 2 * b)
            			{
            				if(bin.length() - 1 - j > b)
            				{
            					buffer1 = buffer.substring(0, b);
    	            			buffer2 = buffer.substring(b, 2 * b) ;
            				}
            			}
            			buffer = buffer + bin.charAt(j);
            			buffer1 = buffer.substring(0, b);
            			buffer2 = buffer.substring(b, 2 * b) ;
            			if(buffer2 == "")
            			{
            				
            			}
            			
            			
            			
            			local[0] = local[2];
        				local[1] = local[3];
        				
        				
        				local[2] = new BigInteger(buffer1,2);
        				local[3] = new BigInteger(buffer2,2);
            			
            		}
            		//System.out.print(bin.charAt(i));
            		
            		Thread.sleep(10);
            		
            	}
                //out.write(c);
            }
            

        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
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
