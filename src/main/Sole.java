package main;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class Sole {
	
	public static int b = 19;// number of bits in a block
	public static int mode = 2;// 1 means 3 blocks overhead, 2 means 1 block overhead
	public static boolean enableHex = false, enableFileInput = false;
	public static boolean test = false;

	
	
	
//	public final static int b = 6;// number of bits in a block
//	public static int mode = 2;// 1 means 3 blocks overhead, 2 means 1 block overhead
//	public static boolean enableHex = false, enableFileInput = false;
//	public static boolean test = false;
	
	
	
	
	
	
//	public final static int b = 512;
//	public static int mode = 2;
//	public static boolean enableHex = true, enableFileInput = true;
//	public static boolean test = false;
	
	
	
	
	
	public static boolean enableHash = false;
	
	public static FileInputStream in;
	public static FileOutputStream out;
	public static FileWriter fstream;
    public static BufferedWriter fout;
    
    public static String inputString = "8,57,17,33,4,8,111,2,5,6,8,8,9,10,9,11111";
    
	public static boolean printOutput = false, printInput = true;
	public final static int reg = 0, head_mask = 1, tail_mask = 2, cut_front = 4;
	public static BigInteger n = (BigInteger.valueOf(2).pow(b / 2))
			.add(BigInteger.valueOf(1 / 2));
	public static BigInteger blockSize = BigInteger.valueOf(2).pow(b);
	public static BigInteger local[] = new BigInteger[4];
	public static BigInteger A, B, x, y, Api, Bpi, nextx, nexty, z, xypi[];
	
	public static BigInteger index = BigInteger.ONE, decoderIndex = BigInteger.ONE;
	public static String bin, buffer;
	public static BigInteger decoderBuffer[] = new BigInteger[4];
	public static byte[] datablock = new byte[64];
	

	
	public static void sendResultToHash(BigInteger comingBigInt, int control) {
		if(enableHash){
			byte[] tempBytes = comingBigInt.toByteArray();
			int diff = tempBytes.length - 64;
			if(diff > 0) {
				for(int i=0; i<64; i++) {
					datablock[i] = tempBytes[diff + i];
				}
			}
			else if(diff < 0) {
				for(int i=0; i<64; i++) {
					datablock[i] = 0;
					if(i + diff >= 0) {
						datablock[i] = tempBytes[i + diff];
					}
				}
			}
			else {
				for(int i=0; i<64; i++) {
					datablock[i] = tempBytes[i];
				}
			}
			if((control & head_mask) > 0) {
				Blake32 pass = new Blake32(head_mask, datablock ,null);
				pass.compress32();
			}
			else {
				Blake32 pass = new Blake32(0, datablock ,null);
				pass.compress32();
			}
			System.out.println(Blake32.getHash());
		}
	}
	public static void sendResultToDecoder(BigInteger comingBigInt) {
		decoderBuffer[0] = decoderBuffer[1];
		decoderBuffer[1] = decoderBuffer[2];
		decoderBuffer[2] = decoderBuffer[3];
		decoderBuffer[3] = comingBigInt;
	}
	public static void sendResultToDecoder(BigInteger[] comingBigInt){
		decoderBuffer[0] = decoderBuffer[2];
		decoderBuffer[1] = decoderBuffer[3];
		decoderBuffer[2] = comingBigInt[0];
		decoderBuffer[3] = comingBigInt[1];
	}
	public static void decode() {

	}

	public static void bufferComp() throws IOException {
		String buffer1 = buffer.substring(0, b);
		String buffer2 = buffer.substring(b);
		if (local[0] == null)// compute the first output
		{
			local[0] = new BigInteger(buffer1, 2);
			local[1] = new BigInteger(buffer2, 2);

			xypi = compOut(head_mask);
			printxypi(head_mask);
			handleCompIn(head_mask);
			

			
		} else if (local[2] == null)// compute the 2nd and 3rd output
		{
			local[2] = new BigInteger(buffer1, 2);
			local[3] = new BigInteger(buffer2, 2);

			xypi = compOut(reg);
			printxypi(reg);
			handleCompIn(head_mask);			
			
			
		} else {
			forward();
			local[2] = new BigInteger(buffer1, 2);
			local[3] = new BigInteger(buffer2, 2);

			xypi = compOut(reg);
			printxypi(reg);
			handleCompIn(reg);
		}
	}

	public static BigInteger[] compIn(int control) {
		
		decoderIndex = index.subtract(BigInteger.valueOf(2));
		// 1 pass
		A = blockSize;
		B = blockSize;

		// 1 pass front
		x = decoderBuffer[0];
		y = decoderBuffer[1];

		Api = blockSize.add(decoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		Bpi = blockSize.subtract(decoderIndex.multiply(BigInteger.valueOf(4 * mode)));

		nextx = swap()[1];

		// 1 pass end

		x = decoderBuffer[2];
		y = decoderBuffer[3];

		Api = blockSize.add(decoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));
		Bpi = blockSize.subtract(decoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));

		nexty = swap()[0];

		// 2 pass
		Api = blockSize.add(BigInteger.valueOf(mode));
		Bpi = blockSize.add(BigInteger.valueOf(mode));

		if((control & head_mask) > 0){
			x = decoderBuffer[0];
		}
		else{
			x = nextx;
		}
		y = nexty;
	
		A = blockSize.subtract(decoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		B = blockSize.add(decoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));

		return swap();
	}

	public static BigInteger[] compOut(int control) {

		// 1 pass
		A = blockSize.add(BigInteger.valueOf(mode));
		B = blockSize.add(BigInteger.valueOf(mode));
		// 1 pass front
		x = local[0];
		y = local[1];
		Api = blockSize.subtract(index.subtract(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));
		Bpi = blockSize.add(index.multiply(BigInteger.valueOf(4 * mode)));
		// handle the 1st block
		if ((head_mask & control) > 0) {
			return swap();
		} else {
			nextx = swap()[1];
		}

		// 1 pass end
		x = local[2];
		y = local[3];

		Api = blockSize.subtract(index.multiply(BigInteger.valueOf(4 * mode)));
		Bpi = blockSize.add(index.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));
		nexty = swap()[0];
		// 2 pass
		x = nextx;
		y = nexty;
		A = blockSize.add(index.multiply(BigInteger.valueOf(4 * mode)));
		B = blockSize.subtract(index.multiply(BigInteger.valueOf(4 * mode)));
		Api = blockSize;
		Bpi = blockSize;

		overflowAlert();
		index = index.add(BigInteger.ONE);
		return swap();
	}

	public static void core() throws IOException, InterruptedException {
		if (enableFileInput) {
			readFromFile();
		} else {
			readInput();
		}

	}

	public static void forward() {
		local[0] = local[2];
		local[1] = local[3];
	}

	public static String get3Plus(String more) throws IOException {
		int c = in.read();
		while (c != -1) {
			more = more + Integer.toBinaryString(c);
			if (more.length() <= 3 * b) {
				c = in.read();
			} else {
				break;
			}
		}
		return more;
	}

	public static void handleEOF(String bin) throws IOException {
		
		if (bin.length() <= 2 * b) {
			
			// step 1
			if (local[2] != null) {
				forward();
			}
			if (mode == 1) {
				local[2] = new BigInteger(padBinaryEnd(bin.substring(0, b)), 2);
				local[3] = new BigInteger(padBinaryEnd(bin.substring(b)), 2);
			} else if (mode == 2) {
				local[2] = blockSize;
				local[3] = new BigInteger(padBinaryEnd(bin.substring(0, b)), 2);
			}
			xypi = compOut(reg);
			printxypi(reg);
			handleCompIn(reg);
			// step 2
			forward();
			if (mode == 1) {
				local[2] = blockSize;
				local[3] = blockSize;
			} else if (mode == 2) {
				
				local[2] = new BigInteger(padBinaryEnd(bin.substring(b)), 2);
				local[3] = BigInteger.ZERO;
			}
			xypi = compOut(reg);
			printxypi(reg);
			handleCompIn(cut_front);
			
			//extra step 3 for mode 2
			if(mode ==2)
			{
				forward();
				local[2] = BigInteger.ZERO;
				local[3] = BigInteger.ZERO;
				xypi = compOut(reg);
				printxypi(reg);
				handleCompIn(tail_mask);
			}

		} else// 2b < bin's length <= 3b
		{
			
			// store last two blocks
			BigInteger l0 = local[0];
			BigInteger l1 = local[1];
			BigInteger l2 = local[2];
			BigInteger l3 = local[3];

			// step 1
			int head_flag = 1;
			if (local[2] != null) {
				forward();
				head_flag = 0;
				
			}
			if (mode == 1) {
				local[2] = new BigInteger(padBinaryEnd(bin.substring(0, b)), 2);
				local[3] = new BigInteger(
						padBinaryEnd(bin.substring(b, 2 * b)), 2);
				xypi = compOut(reg);
				printxypi(reg);				
				if((head_flag & head_mask) > 0){
					handleCompIn(head_mask);
				}
				else{
					handleCompIn(reg);
				}
			} else if (mode == 2) {
				local[2] = new BigInteger(padBinaryEnd(bin.substring(0, b)), 2);
				local[3] = blockSize;
			}

			// step 2
			forward();
			if (mode == 1) {
				local[2] = new BigInteger(padBinaryEnd(bin.substring(2 * b)), 2);
				local[3] = blockSize;
				xypi = compOut(reg);
				printxypi(reg);
				handleCompIn(reg);
			} else if (mode == 2) {
				local[2] = new BigInteger(
						padBinaryEnd(bin.substring(b, 2 * b)), 2);
				local[3] = new BigInteger(padBinaryEnd(bin.substring(2 * b)), 2);
			}

			// step 3
			forward();
			local[2] = BigInteger.ZERO;
			local[3] = BigInteger.ZERO;

			if (mode == 1) {
				xypi = compOut(reg);
				printxypi(reg);
				handleCompIn(reg);
				return;// the end for mode 1
			} else if (mode == 2) {
				// extra step 4
				if (xypi[1].compareTo(BigInteger.ZERO) > 0) {
					if(l2 == null){
						local[0] = l0;
						local[1] = l1;
					}
					else{
						local[0] = l2;
						local[1] = l3;
					}
					local[2] = new BigInteger(
							padBinaryEnd(bin.substring(0, b)), 2);
					local[3] = blockSize.add(BigInteger.ONE);

					xypi = compOut(reg);
					printxypi(reg);
					handleCompIn(reg);

					forward();
					local[2] = new BigInteger(padBinaryEnd(bin.substring(b,
							2 * b)), 2);
					local[3] = new BigInteger(
							padBinaryEnd(bin.substring(2 * b)), 2);
					xypi = compOut(reg);
					printxypi(reg);
					handleCompIn(tail_mask);
					
					forward();
					local[2] = BigInteger.ZERO;
					local[3] = BigInteger.ZERO;
					xypi = compOut(reg);
					printxypi(head_mask);
					if(xypi[1].compareTo(blockSize.add(BigInteger.ONE)) == 0){
						xypi[1] = BigInteger.ONE;
					}
					else{
						xypi[1] = BigInteger.ZERO;
					}
					handleCompIn(reg);	
				}
			}
		}
	}

	public static String handleInput(String nums, int radix) {

		String[] numsArr = nums.split(",");
		String binStream = "";
		int i;
		for (i = 0; i < numsArr.length; i++) {
			BigInteger tempBig = new BigInteger(numsArr[i], radix);
			if(tempBig.compareTo(blockSize) >= 0)
			{
				System.out.println("It hits the EOF.");
				break;
			}
			else {
				binStream = binStream + padBinaryFront(tempBig.toString(2));
			}
		}
		return binStream;
	}

	public static void main(String args[]) throws IOException,
			InterruptedException, InterruptedException {
		if (test) {
			test();
		} else {
			core();
		}
	}

	public static void overflowAlert() {
		if (n.compareTo(index.subtract(BigInteger.ONE).multiply(
				BigInteger.valueOf(2).add(BigInteger.ONE))) < 0) {
			System.out.println("");
			System.out.println(">>> >>> overflow <<< <<<");
			System.out.println(index.subtract(BigInteger.ONE).multiply(
					BigInteger.valueOf(2).add(BigInteger.ONE))
					+ ": " + n);
			System.out.println(">>> >>> overflow <<< <<<");
		}
	}

	public static String padBinaryEnd(String bits) {
		while (bits.length() < b) {
			bits = bits + "0";
		}
		return bits;
	}

	public static String padBinaryFront(String bits) {
		while (bits.length() < b) {
			bits = "0" + bits;
		}
		return bits;
	}

	public static String padHexFront(String bits) {
		while (bits.length() * 4 < b) {
			bits = "0" + bits;
		}
		return bits;
	}

	public static void printxypi(int control) {
		if (printOutput) {
			if (enableHex) {
				System.out.println(padHexFront(xypi[0].toString(16)));// hex
			} else {
				System.out.println(xypi[0].toString(10));// decimal
			}
			if ((head_mask & control) == 0) {
				if (enableHex) {
					System.out.println(padHexFront(xypi[1].toString(16)));// hex
				} else {
					System.out.println(xypi[1].toString(10));// decimal
				}
			}
		}
	}

	public static void readFromFile() throws IOException, InterruptedException {
		try {
			in = new FileInputStream("nyu.png");
			out = new FileOutputStream("outagain.txt");
			
			fstream = new FileWriter("out.txt");
		    fout = new BufferedWriter(fstream);
		    
		   
			
			
			bin = get3Plus("");
			while (bin.length() > 3 * b) {
				buffer = bin.substring(0, 2 * b);
				bufferComp();
				bin = get3Plus(bin.substring(2 * b));
			}
			// will handle the EOF here
			handleEOF(bin);

			// System.out.println(bin);
			// Thread.sleep(10);
		} finally {
			if (in != null) {
				in.close();
				out.close();
			}
		}
	}

	public static void readInput() throws IOException {
		String bin = handleInput(inputString, 10);
		while (bin.length() > 3 * b) {
			buffer = bin.substring(0, 2 * b);
			bufferComp();
			bin = bin.substring(2 * b);
		}
		// will handle the EOF here
		handleEOF(bin);
	}

	public static BigInteger[] swap() {
		z = y.multiply(A).add(x);
		return new BigInteger[] { z.mod(Api), z.divide(Api) };
	}
	public static void finalOutput(int control) throws IOException{
		//fout.write(padBinaryFront(xypi[0].toString(2)));
		
		if (printInput) {
			if (enableHex) {
				if((control & cut_front) == 0) {
					System.out.println(padHexFront(xypi[0].toString(16)));// hex
				}
				if((control & tail_mask) == 0) {
					System.out.println(padHexFront(xypi[1].toString(16)));// hex
				}
			} else {
				if((control & cut_front) == 0) {
					System.out.println(xypi[0].toString(10));// decimal
				}
				if((control & tail_mask) == 0) {
					System.out.println(xypi[1].toString(10));// decimal
				}
			}
		}
	}
	public static void handleCompIn(int control) throws IOException{
		if((control & head_mask) > 0){
			sendResultToDecoder(xypi);
			if(decoderBuffer[0] != null)
			{
				xypi = compIn(head_mask);
				sendResultToHash(xypi[0],head_mask);
				sendResultToHash(xypi[1], reg);
				finalOutput(reg);
			}
		}
		else{
			sendResultToDecoder(xypi);
			sendResultToHash(xypi[0], reg);
			if((control & tail_mask) > 0) {
				xypi = compIn(reg);
				finalOutput(tail_mask);
			}
			else {
				sendResultToHash(xypi[1], reg);
				xypi = compIn(reg);
				finalOutput(control);
			}
			
		}
	}
	public static void test() throws IOException {
		local[0] = new BigInteger("8");
		local[1] = new BigInteger("57");
		
		xypi = compOut(head_mask);
		printxypi(head_mask);
		handleCompIn(head_mask);
		
		local[2] = new BigInteger("17");
		local[3] = new BigInteger("33");

		xypi = compOut(reg);
		printxypi(reg);
		handleCompIn(reg);

		
		
		
		
		forward();
		local[2] = new BigInteger("4");
		local[3] = new BigInteger("64");

		xypi = compOut(reg);
		printxypi(reg);
		handleCompIn(reg);
		
		forward();
		local[2] = new BigInteger("0");
		local[3] = new BigInteger("0");

		xypi = compOut(reg);
		printxypi(reg);
		handleCompIn(reg);
	}
}
