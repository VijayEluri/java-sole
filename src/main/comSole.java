package main;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class comSole{
    public static void main(String[] args) throws IOException, InterruptedException {
    	System.out.println(decodeString(10,1,encodeString(10,1,"9 9 9 9 8")));	
    }
    
	private final static int reg = 0, head_mask = 1, cut_tail = 2, cut_front = 4, 
	flip_flag = 8, flip_flag_late = 16, end_flag = 32, odd_flag = 64, even_flag = 128;
	
	private static boolean enableHash = true;
	private static boolean enableFileOutput = false;
	
	private static int b;// number of bits in a block
	private static int mode;// 1 means 3 blocks overhead, 2 means 1 block overhead
	private static BigInteger blockSize, n;
	
	private static FileInputStream in;
	private static FileOutputStream fos;
	private static DataOutputStream out;

	private static BigInteger A, x, y, Api, nextx, nexty, z;
	
	private static BigInteger local[];
	private static BigInteger encoderIndex, decoderIndex;
	private static String bin, buffer, outputBuffer;
	
	private static BigInteger xypi[];
	private static BigInteger decoderBuffer[];
	
	private static byte[] datablock;//for hash usage
	
	private static String decodedOutputString;
	private static String inputString;
	private static String outputString;
	
	private static String filename, format;
	
	static String decodeString(int numberOfBits, int decodingMode, String input) throws IOException {
		init();
		b = numberOfBits;
		mode = decodingMode;
		blockSize = BigInteger.valueOf(2).pow(b);
		n = (BigInteger.valueOf(2).pow(b / 2)).add(BigInteger.valueOf(mode / 2));
		
		String[] inputArray = input.split(" ");
		//System.out.println("You have entered " + inputArray.length + " numbers."); 
		//System.out.println(input);
		xypi[0] = new BigInteger(inputArray[0]);
		xypi[1] = new BigInteger("0");
		handleCompIn(head_mask);

		xypi[0] = new BigInteger(inputArray[1]);
		xypi[1] = new BigInteger(inputArray[2]);
		encoderIndex = encoderIndex.add(BigInteger.ONE);
		handleCompIn(head_mask);
		decodedOutputString = decodedOutputString + xypi[0];
		decodedOutputString = decodedOutputString + " " + xypi[1];
		for(int i=3;i<inputArray.length;i = i+2) {
			xypi[0] = new BigInteger(inputArray[i]);
			xypi[1] = new BigInteger(inputArray[i+1]);
			
			encoderIndex = encoderIndex.add(BigInteger.ONE);
			handleCompIn(reg);

			if(mode == 2){
			if(xypi[0].compareTo(blockSize.add(BigInteger.ONE)) == 0) {
				if(inputArray.length - i == 2) {
					decodedOutputString = decodedOutputString + " " + xypi[0];
					decodedOutputString = decodedOutputString + " " + xypi[1];
					xypi[0] = new BigInteger("1");
					xypi[1] = new BigInteger("0");
					encoderIndex = encoderIndex.add(BigInteger.ONE);
					handleCompIn(reg);
					decodedOutputString = decodedOutputString + " " + xypi[0];
					}
					else {
						decodedOutputString = decodedOutputString + " " + xypi[0];
						decodedOutputString = decodedOutputString + " " + xypi[1];
						xypi[0] = new BigInteger(inputArray[i+2]);
						xypi[1] = new BigInteger("1");
						encoderIndex = encoderIndex.add(BigInteger.ONE);
						handleCompIn(reg);
						decodedOutputString = decodedOutputString + " " + xypi[0];
						decodedOutputString = decodedOutputString + " " + xypi[1];
					}
					
					break;
			}
			else if(xypi[0].compareTo(blockSize) == 0) {
				
				if(inputArray.length - i == 2) {
					decodedOutputString = decodedOutputString + " " + xypi[0];
					decodedOutputString = decodedOutputString + " " + xypi[1];
					xypi[0] = new BigInteger("0");
					xypi[1] = new BigInteger("0");
					encoderIndex = encoderIndex.add(BigInteger.ONE);
					handleCompIn(reg);
					decodedOutputString = decodedOutputString + " " + xypi[0];
				}
				else {
					String out0 = "" + xypi[0];
					String out1 = "" + xypi[1];
					xypi[0] = new BigInteger(inputArray[i+2]);
					xypi[1] = new BigInteger("0");
					encoderIndex = encoderIndex.add(BigInteger.ONE);
					handleCompIn(reg);
					decodedOutputString = decodedOutputString + " " + out1;
					decodedOutputString = decodedOutputString + " " + out0;
					decodedOutputString = decodedOutputString + " " + xypi[0];
					decodedOutputString = decodedOutputString + " " + xypi[1];
				}
				
				break;
			}
			else
			{
				decodedOutputString = decodedOutputString + " " + xypi[0];
				decodedOutputString = decodedOutputString + " " + xypi[1];
			}
			}else{
				decodedOutputString = decodedOutputString + " " + xypi[0];
				decodedOutputString = decodedOutputString + " " + xypi[1];
			}
		}
		
		
		
		return decodedOutputString;
	}
	static String encodeString(int numberOfBits, int encodingMode, String input) throws IOException, InterruptedException {
		init();
		b = numberOfBits;
		mode = encodingMode;
		blockSize = BigInteger.valueOf(2).pow(b);
		n = (BigInteger.valueOf(2).pow(b / 2)).add(BigInteger.valueOf(mode / 2));
		
		inputString = input;
		Integer inputnum = inputString.split(" ").length;
		//System.out.println("You have entered " + inputnum + " numbers.");
		//System.out.println(input);
		
		if(new BigInteger(inputnum.toString()).compareTo(n)>0) {
			return "You should enter at most " + b + " numbers.";
		}
		
		readString();
		//System.out.println("SOLE gives you " + outputString.split(" ").length + " numbers.");
		return outputString;
	}
	static void encodeFile(int numberOfBits, int encodingMode, String inputFileName, String inputFileFormat) throws IOException, InterruptedException {
		init();
		b = numberOfBits;
		mode = encodingMode;
		blockSize = BigInteger.valueOf(2).pow(b);
		n = (BigInteger.valueOf(2).pow(b / 2)).add(BigInteger.valueOf(mode / 2));
		
		filename = inputFileName;
		format = inputFileFormat;
		
		enableFileOutput = true;
		
		readFile();
	}
	private static void init() {
		local = new BigInteger[4];
		encoderIndex = BigInteger.ONE; decoderIndex = BigInteger.ONE;
		bin = ""; buffer= ""; outputBuffer = "";
		xypi = new BigInteger[2];
		decoderBuffer = new BigInteger[4];
		datablock = new byte[64];
		decodedOutputString = "";
		inputString = "";
		outputString = "";
		filename = ""; format = "";
	}
	private static void sendResultToHash(BigInteger comingBigInt, int control) {
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
		}
	}
	private static void sendResultToDecoder(BigInteger[] comingBigInt){
		decoderBuffer[0] = decoderBuffer[2];
		decoderBuffer[1] = decoderBuffer[3];
		decoderBuffer[2] = comingBigInt[0];
		decoderBuffer[3] = comingBigInt[1];
	}

	private static void bufferComp() throws IOException {
		String buffer1 = buffer.substring(0, b);
		String buffer2 = buffer.substring(b);
		if (local[0] == null)// compute the first output
		{
			local[0] = new BigInteger(buffer1, 2);
			local[1] = new BigInteger(buffer2, 2);
			xypi = compOut(head_mask);
			outputString = outputString + xypi[0];
			handleCompIn(head_mask);
		} else if (local[2] == null)// compute the 2nd and 3rd output
		{
			local[2] = new BigInteger(buffer1, 2);
			local[3] = new BigInteger(buffer2, 2);
			xypi = compOut(reg);
			outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
			handleCompIn(head_mask);			
		} else {
			forward();
			local[2] = new BigInteger(buffer1, 2);
			local[3] = new BigInteger(buffer2, 2);

			xypi = compOut(reg);
			outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
			handleCompIn(reg);
		}
	}

	private static BigInteger[] compIn(int control) {
		
		decoderIndex = encoderIndex.subtract(BigInteger.valueOf(2));
		// 1 pass
		A = blockSize;
		// 1 pass front
		x = decoderBuffer[0];
		y = decoderBuffer[1];

		Api = blockSize.add(decoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		blockSize.subtract(decoderIndex.multiply(BigInteger.valueOf(4 * mode)));

		nextx = swap()[1];

		// 1 pass end

		x = decoderBuffer[2];
		y = decoderBuffer[3];

		Api = blockSize.add(decoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));
		blockSize.subtract(decoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));

		nexty = swap()[0];

		// 2 pass
		Api = blockSize.add(BigInteger.valueOf(mode));
		blockSize.add(BigInteger.valueOf(mode));

		if((control & head_mask) > 0){
			x = decoderBuffer[0];
		}
		else{
			x = nextx;
		}
		y = nexty;
	
		A = blockSize.subtract(decoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		blockSize.add(decoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));

		return swap();
	}

	private static BigInteger[] compOut(int control) {

		// 1 pass
		A = blockSize.add(BigInteger.valueOf(mode));
		blockSize.add(BigInteger.valueOf(mode));
		// 1 pass front
		x = local[0];
		y = local[1];
		Api = blockSize.subtract(encoderIndex.subtract(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));
		blockSize.add(encoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		// handle the 1st block
		if ((head_mask & control) > 0) {
			return swap();
		} else {
			nextx = swap()[1];
		}
		
		// 1 pass end
		x = local[2];
		y = local[3];

		Api = blockSize.subtract(encoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		blockSize.add(encoderIndex.add(BigInteger.ONE).multiply(
				BigInteger.valueOf(4 * mode)));
		nexty = swap()[0];
		// 2 pass
		x = nextx;
		y = nexty;
		A = blockSize.add(encoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		blockSize.subtract(encoderIndex.multiply(BigInteger.valueOf(4 * mode)));
		Api = blockSize;
		overflowAlert();
		encoderIndex = encoderIndex.add(BigInteger.ONE);
		return swap();
	}

	private static void forward() {
		local[0] = local[2];
		local[1] = local[3];
	}
	
	private static String get3Plus(String more) throws IOException {
		int c = in.read();
		while (c != -1) {
			more = more + padFront(Integer.toBinaryString(c),8);
			if (more.length() <= 3 * b) {
				c = in.read();
			} else {
				break;
			}
		}
		return more;
	}
	private static String flipBits(String bits) {
		char[] bitsChars = bits.toCharArray();
		for(int i = 0; i < bitsChars.length; i++) {
			if(bitsChars[i] == '0') {
				bitsChars[i] = '1';
			}
			else if(bitsChars[i] == '1'){
				bitsChars[i] = '0';
			}
		}
		return String.valueOf(bitsChars);
	}
	private static void EOF(String bin) throws IOException {
		String lastBlock;
		if (bin.length() <= 2 * b) {
			System.out.println("here");
			lastBlock = bin.substring(b);
			// step 1
			int head_flag = 1;
			if (local[2] != null) {
				forward();
				head_flag = 0;
			}
			if (mode == 1) {
				local[2] = new BigInteger((bin.substring(0, b)), 2);
				local[3] = new BigInteger((bin.substring(b)), 2);
			} else if (mode == 2 || mode == 4) {
				
					if((lastBlock.charAt(0) == '0') && (mode == 4)) {
					//flip bits
					lastBlock = flipBits(lastBlock);
					local[2] = blockSize.add(BigInteger.valueOf(2));
				}
				else
				{
					local[2] = blockSize;
				}				
				local[3] = new BigInteger((bin.substring(0, b)), 2);
			} 
			xypi = compOut(reg);
			outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
			if((head_flag & head_mask)>0) {
				handleCompIn(head_mask);
			}
			else{
				handleCompIn(reg);
			}
			
			// step 2
			forward();
			if (mode == 1) {
				local[2] = blockSize;
				local[3] = blockSize;
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				handleCompIn(reg);
				
				forward();
				local[2] = BigInteger.ZERO;
				local[3] = BigInteger.ZERO;
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				handleCompIn(reg);
			} else if (mode == 2 || mode == 4) {
				
				local[2] = new BigInteger(lastBlock, 2);
				local[3] = BigInteger.ZERO;
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				handleCompIn(cut_front);//front is EOF
			}
			
			
			int flippy = 0;
			if((xypi[0].compareTo(blockSize.add(BigInteger.valueOf(2))) == 0) || 
					(xypi[0].compareTo(blockSize.add(BigInteger.valueOf(3))) == 0))//front B +2 || B + 3
				flippy = flip_flag;
			//extra step 3 for mode 2
			if(mode ==2 || mode == 4)
			{
				forward();
				local[2] = BigInteger.ZERO;
				local[3] = BigInteger.ZERO;
				xypi = compOut(reg);
				//printxypi(reg);//if print just two zeros

				handleCompIn(odd_flag | end_flag | cut_tail | flippy);//tail should be zero
			}

		}
		/* ######################################################################
		 * 
		 * 
		 * 2b < bin's length <= 3b
		 * 
		 * ###################################################################### */
		else {
			lastBlock = bin.substring(2 * b);
			
			char firstBitLastBlock = lastBlock.charAt(0);
			// store last two blocks
			BigInteger l0 = local[0];
			BigInteger l1 = local[1];
			BigInteger l2 = local[2];
			BigInteger l3 = local[3];
			
			BigInteger d0 = decoderBuffer[0];
			BigInteger d1 = decoderBuffer[1];
			BigInteger d2 = decoderBuffer[2];
			BigInteger d3 = decoderBuffer[3];

			// step 1lastBlock
			int head_flag = 1;
			if (local[2] != null) {
				forward();
				head_flag = 0;
				
			}
			if (mode == 1) {
				local[2] = new BigInteger((bin.substring(0, b)), 2);
				local[3] = new BigInteger(
						(bin.substring(b, 2 * b)), 2);
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];			
				if((head_flag & head_mask) > 0){
					handleCompIn(head_mask);
				}
				else{
					handleCompIn(reg);
				}
			} else if (mode == 2 || mode == 4) {
				local[2] = new BigInteger((bin.substring(0, b)), 2);
				local[3] = blockSize;
			}

			// step 2
			forward();
			if (mode == 1) {
				local[2] = new BigInteger((bin.substring(2 * b)), 2);
				local[3] = blockSize;
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				handleCompIn(reg);
			} else if (mode == 2 || mode == 4) {
				local[2] = new BigInteger(
						(bin.substring(b, 2 * b)), 2);
				if((firstBitLastBlock == '0') && (mode == 4)) {
					lastBlock = flipBits(lastBlock);
				}
				local[3] = new BigInteger(lastBlock, 2);
			}

			// step 3
			forward();
			local[2] = BigInteger.ZERO;
			local[3] = BigInteger.ZERO;

			if (mode == 1) {
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				handleCompIn(cut_tail);
				
				return;// the end for mode 1
			} else if (mode == 2 || mode == 4) {
				xypi = compOut(reg);
				boolean lastbitOne = (xypi[1].compareTo(BigInteger.ZERO) > 0);
				encoderIndex = encoderIndex.subtract(BigInteger.ONE);

				// extra step 4

				if(l2 == null){
					local[0] = l0;
					local[1] = l1;
				}
				else{
					local[0] = l2;
					local[1] = l3;
				}
				
				
				

				if(mode == 4) {
					if((firstBitLastBlock == '0') && lastbitOne) {
						local[2] = blockSize.add(BigInteger.valueOf(3));
					}
					else if((firstBitLastBlock == '0') && (!lastbitOne)) {
						local[2] = blockSize.add(BigInteger.valueOf(2));
					}
					else if((!(firstBitLastBlock == '0')) && (lastbitOne)){
						local[2] = blockSize.add(BigInteger.ONE);
					}
					else {
						local[2] = blockSize;
					}
				}
				else if(mode == 2) {
					if(lastbitOne){
						local[2] = blockSize.add(BigInteger.ONE);
					}
					else {
						local[2] = blockSize.add(BigInteger.ZERO);
					}
				}
				
				local[3] = new BigInteger(
						(bin.substring(0, b)), 2);

				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				
				decoderBuffer[0] = d0;
				decoderBuffer[1] = d1;
				decoderBuffer[2] = d2;
				decoderBuffer[3] = d3;
				
				if((head_flag & head_mask) > 0){
					handleCompIn(head_mask);
				}
				else {
				handleCompIn(reg);
				}
				forward();
				local[2] = new BigInteger((bin.substring(b,
						2 * b)), 2);
				local[3] = new BigInteger(
						lastBlock, 2);
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0]  + " " +  xypi[1];
				handleCompIn(cut_front);//front is EOF
				
				BigInteger EOF = xypi[0];
				
				
				int flippy = 0;
				if((xypi[0].compareTo(blockSize.add(BigInteger.valueOf(2))) == 0) || 
						(xypi[0].compareTo(blockSize.add(BigInteger.valueOf(3))) == 0))//front B +2 || B + 3
					flippy = flip_flag_late;
				
				
				forward();
				local[2] = BigInteger.ZERO;
				local[3] = BigInteger.ZERO;
				xypi = compOut(reg);
				outputString = outputString + " " + xypi[0];

			
				/*
				 * #####################################################
				 * 
				 * 
				 * EOF selection
				 * 
				 * 
				 * #####################################################
				 * */
				if((EOF.compareTo(blockSize.add(BigInteger.ONE)) == 0) ||
						(EOF.compareTo(blockSize.add(BigInteger.valueOf(3))) == 0)){
					xypi[1] = BigInteger.ONE;
				}
				else{
					xypi[1] = BigInteger.ZERO;
				}
				
				handleCompIn((even_flag| end_flag | flippy));	
			}
		}
	}
	private static void overflowAlert() {
		if (n.compareTo(encoderIndex.subtract(BigInteger.ONE).multiply(
				BigInteger.valueOf(2).add(BigInteger.ONE))) < 0) {
			
			System.out.println(">>> >>> overflow <<< <<<");
			System.out.println(encoderIndex.subtract(BigInteger.ONE).multiply(
					BigInteger.valueOf(2).add(BigInteger.ONE))
					+ ": " + n);
			System.out.println(">>> >>> overflow <<< <<<");
		}
	}
	private static String padFront(String bits, int len) {
		while (bits.length() < len) {
			bits = "0" + bits;
		}
		return bits;
	}

	private static void readFile() throws IOException, InterruptedException {
		try {
			in = new FileInputStream(filename + "." +format);
			fos = new FileOutputStream(filename + System.currentTimeMillis() / 1000L + "." + format);
			out = new DataOutputStream(fos);
			bin = get3Plus("");
			while (bin.length() > 3 * b) {
				buffer = bin.substring(0, 2 * b);
				bufferComp();
				bin = get3Plus(bin.substring(2 * b));
			}
			EOF(bin);
		}
		finally {
			if (in != null) {
				in.close();
				out.close();
			}
		}
	}

	private static void readString() throws IOException, InterruptedException {
		String bin = readString2(inputString, 10);
		if(bin.compareTo("overflow") == 0) {
			System.out.print("overflow");
		}
		else{
			while (bin.length() > 3 * b) {
				buffer = bin.substring(0, 2 * b);
				bufferComp();
				bin = bin.substring(2 * b);
			}
			EOF(bin);
		}
	}
	private static String readString2(String nums, int radix) {
		String[] numsArr = nums.split(" ");
		if(BigInteger.valueOf(numsArr.length).compareTo(n) > 0) {
			return "overflow";
		}
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
				binStream = binStream + padFront(tempBig.toString(2),b);
			}
		}
		return binStream;
	}
	private static BigInteger[] swap() {
		z = y.multiply(A).add(x);
		return new BigInteger[] { z.mod(Api), z.divide(Api) };
	}
	private static void writeBack(int control) throws NumberFormatException, IOException {
		if(enableFileOutput) {
			String s0 = "",s1 = "";
			s0 = xypi[0].toString(2);
			s1 = xypi[1].toString(2);
			
			if((control & flip_flag) > 0) {
				s0 = flipBits(xypi[0].toString(2));
			}
			else if((control & flip_flag_late) > 0) {			
				s1 = flipBits(xypi[1].toString(2));
			}
			if(s0.length() < b && (((end_flag & control) == 0)||((even_flag & control) > 0))) {
				s0 = padFront(s0,b);
			}
	
			if(s1.length() < b && (((end_flag & control) == 0))) {
				s1 = padFront(s1,b);
			}
	
			
			if(s0.length() > b) {
				outputBuffer = outputBuffer + s1;
			}
			else if((cut_tail & control) > 0) {
				outputBuffer = outputBuffer + s0;
			}
			else {
				outputBuffer = outputBuffer + s0 + s1;
			}
			
	//		fout.write(outputBuffer);
			String byteHolder = "";
			byte abyte = 0;
	
			while(outputBuffer.length() >= 8) {
				byteHolder = outputBuffer.substring(0, 8);
				outputBuffer = outputBuffer.substring(8);
				abyte = buildByte(byteHolder);
				out.write(abyte);
			}
		}
	}
	private static byte buildByte(String bits) {
		byte abyte = 0;
		if(bits.charAt(0) == '1') {
			for(int i=1;i<8;i++) {
				if(bits.charAt(i) == '0') {
					abyte = (byte) (abyte + (1 << (8-1-i)));
				}
			}
			abyte = (byte) (abyte + 1);
			abyte *= -1;
		} else if (bits.charAt(0) == '0') {
			for(int i=1;i<8;i++) {
				if(bits.charAt(i) == '1') {
					abyte = (byte) (abyte + (1 << (8-1-i)));
				}
			}
		}
		return abyte;
	}
	private static void handleCompIn(int control) throws IOException{
		if((control & head_mask) > 0){
			sendResultToHash(xypi[0],head_mask);
			sendResultToDecoder(xypi);
			
			if(decoderBuffer[0] != null)
			{
				sendResultToHash(xypi[0], reg);
				sendResultToHash(xypi[1], reg);
				xypi = compIn(head_mask);
				
				writeBack(reg);
			}
		}
		else{
			sendResultToDecoder(xypi);
			sendResultToHash(xypi[0], reg);
			
			if((control & cut_tail) > 0) {
				xypi = compIn(reg);
			
				writeBack(control);
			}
			else {
				sendResultToHash(xypi[1], reg);
				xypi = compIn(reg);
				
				writeBack(control);
			}
			
		}
	}
	static String getHash() {
		return Blake32.getHash(); 
	}
}
