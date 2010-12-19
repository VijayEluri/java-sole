package sole;

// File   : gui/low-level/drawing2/DemoDrawing2.java
// Purpose: Demo creating new graphical component.
//          Uses anonymous inner class listener, BorderLayout
// Author : Fred Swartz - 21 Sept 2006 - Placed in public domain.


import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import javax.swing.*;

///////////////////////////////////////////////////// DemoDrawing2
@SuppressWarnings("serial")
public class SoleDemo extends JFrame {

    //===================================================== fields

	private static JTextArea output = new JTextArea();
    private JTextArea input = new JTextArea();
    private static JTextArea outInput = new JTextArea();
    private JTextArea config = new JTextArea();
    private static JTextArea hash = new JTextArea();
    
    private JLabel l1 = new JLabel();
    private JLabel l2 = new JLabel();
    private JLabel l3 = new JLabel();
    private JLabel l4 = new JLabel();
    private JLabel l5 = new JLabel();
    //================================================ Constructor
    public SoleDemo() {

        
        JButton changeColorBtn = new JButton("Very SOLE!");
        changeColorBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                    	output.setText("");
                    	outInput.setText("");
                    	String configs[] =config.getText().split(","); 
                    	if(configs[2].compareTo("1") == 0) {
                    		enableHex = true;
                    	}
                    	else {
                    		enableHex = false;
                    	}
                    	soleEncode(Integer.parseInt(configs[0]), Integer.parseInt(configs[1]),
                    			input.getText());
                    	if(output.getText().charAt(0) == ',') {
                    		output.setText(output.getText().substring(1));
                    	}
                    	if(outInput.getText().charAt(0) == ',') {
                    		outInput.setText(outInput.getText().substring(1));
                    	}

					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
                }
            });

        //... Add components to layout
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        
        changeColorBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(changeColorBtn);
        
        
        l1.setText("Input(seperate Inputs with comma): ");
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(l1);
        
        input.setLineWrap(true);
        input.setText("8,57,17,33,4");
        content.add(input);
        
        l2.setText("Output: ");
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(l2);
        
        output.setLineWrap(true);
        output.setText(" ");
        content.add(output);
        
        l3.setText("Convert ouput back to input: ");
        l3.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(l3);
        
        outInput.setLineWrap(true);
        outInput.setText(" ");
        content.add(outInput);
        
        l4.setText("Hash(SHA-3 finalist Blake 256-bit version): ");
        l4.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(l4);
        
        hash.setLineWrap(true);
        hash.setText(" ");
        content.add(hash);
        
        l5.setText("Config(#bits=1-32, mode=1,2,4, enable hex output=0,1): ");
        l5.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(l5);
        
        config.setLineWrap(true);
        config.setText("6,1,0");
        content.add(config);

        setPreferredSize(new Dimension(800, 600));
        setContentPane(content);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("SOLE Demo");
        pack();
    }



    //========================================================== main
    public static void main(String[] args) throws IOException, InterruptedException {
        JFrame window = new SoleDemo();
        window.setVisible(true);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

	
	/*
	 * 
	 * finalized parameters
	 * 
	 */
	private final static int reg = 0, head_mask = 1, cut_tail = 2, cut_front = 4, 
	flip_flag = 8, flip_flag_late = 16, end_flag = 32, odd_flag = 64, even_flag = 128;
	/*
	 * 
	 * public configurations
	 */
	
	private static boolean enableHex = false, enableHash = true;
	@SuppressWarnings("unused")
	private static boolean enableFileOutput = false, enableFileInput = false;
	private static boolean printOutput = true, printInput = true;
	/*
	 * 
	 * 
	 * object
	 * come from constructor
	 * 
	 * 
	 */
	
	
	private static int b;// number of bits in a block
	private static int mode;// 1 means 3 blocks overhead, 2 means 1 block overhead
	private static String inputString;
	private static String filename, format;
	
	private static BigInteger blockSize, n;
	

	
	void soleEncode(int numberOfBits, int encodingMode, String input) throws IOException, InterruptedException {
		b = numberOfBits;
		mode = encodingMode;
		inputString = input;
		
		blockSize = BigInteger.valueOf(2).pow(b);
		n = (BigInteger.valueOf(2).pow(b / 2)).add(BigInteger.valueOf(mode / 2));
		init();
		readInput();
		
	}
	static void soleEncode(int numberOfBits, int encodingMode, String inputFileName, String inputFileFormat) throws IOException, InterruptedException {
		b = numberOfBits;
		mode = encodingMode;
		filename = inputFileName;
		format = inputFileFormat;
		
		enableFileInput = true;
		enableFileOutput = true;
		
		blockSize = BigInteger.valueOf(2).pow(b);
		n = (BigInteger.valueOf(2).pow(b / 2)).add(BigInteger.valueOf(mode / 2));
		init();
		readFromFile();
	}
	
	
	private static FileInputStream in;
	private static FileOutputStream fos;
	private static DataOutputStream out;
	
	

	
	/*
	 * 
	 * 
	 * 
	 */
	private static BigInteger local[] = new BigInteger[4];
	private static BigInteger A, x, y, Api, nextx, nexty, z, xypi[];
	private static BigInteger encoderIndex = BigInteger.ONE, decoderIndex = BigInteger.ONE;
	private static String bin, buffer, outputBuffer = "";
	private static BigInteger decoderBuffer[] = new BigInteger[4];
	private static String[] numsArr;
	private static byte[] datablock = new byte[64];//for hash usage

	private static void init() {
		local = new BigInteger[4];
		encoderIndex = BigInteger.ONE; decoderIndex = BigInteger.ONE;
		bin = ""; buffer= ""; outputBuffer = "";
		decoderBuffer = new BigInteger[4];
		datablock = new byte[64];//for hash usage
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
			more = more + padByteFront(Integer.toBinaryString(c));
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
	private static void handleEOF(String bin) throws IOException {
		String lastBlock;
		if (bin.length() <= 2 * b) {
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
			printxypi(reg);
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
				printxypi(reg);
				handleCompIn(reg);
			} else if (mode == 2 || mode == 4) {
				
				local[2] = new BigInteger(lastBlock, 2);
				local[3] = BigInteger.ZERO;
				xypi = compOut(reg);
				printxypi(reg);
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
				printxypi(reg);				
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
				printxypi(reg);
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
				printxypi(reg);
				
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
				printxypi(reg);
				
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
				printxypi(reg);
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
				printxypi(reg);

			
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

	private static String handleInput(String nums, int radix) {
		numsArr = nums.split(",");
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
				binStream = binStream + padBinaryFront(tempBig.toString(2));
			}
		}
		return binStream;
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
	private static String padByteFront(String bits) {
		while (bits.length() < 8) {
			bits = "0" + bits;
		}
		return bits;
	}
	private static String padBinaryFront(String bits) {
		while (bits.length() < b) {
			bits = "0" + bits;
		}
		return bits;
	}

	private static String padHexFront(String bits) {
		while (bits.length() * 4 < b) {
			bits = "0" + bits;
		}
		return bits;
	}
	private static String sep = ",";
	private static void printxypi(int control) {
		if (printOutput) {
			if (enableHex) {
				output.setText(output.getText() + sep + padHexFront(xypi[0].toString(16)));
				System.out.println(padHexFront(xypi[0].toString(16)));
			}
			else {
				output.setText(output.getText() + sep + xypi[0].toString(10));
				System.out.println(xypi[0].toString(10));
			}
			if ((head_mask & control) == 0) {
				if (enableHex) {
					output.setText(output.getText() + sep + padHexFront(xypi[1].toString(16)));
					System.out.println(padHexFront(xypi[1].toString(16)));
				}
				else {
					output.setText(output.getText() + sep + xypi[1].toString(10));
					System.out.println(xypi[1].toString(10));
				}
			}
		}
	}
	private static void readFromFile() throws IOException, InterruptedException {
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
			handleEOF(bin);
		}
		finally {
			if (in != null) {
				in.close();
				out.close();
			}
		}
	}

	private static void readInput() throws IOException, InterruptedException {
		String bin = handleInput(inputString, 10);
		if(bin.compareTo("overflow") == 0) {
			output.setText("INPUT OVERFLOW! the maximun n is " + n);
			outInput.setText("INPUT OVERFLOW! the maximun n is " + n); 
			hash.setText("INPUT OVERFLOW! the maximun n is " + n); 
		}
		else{
			while (bin.length() > 3 * b) {
				buffer = bin.substring(0, 2 * b);
				bufferComp();
				bin = bin.substring(2 * b);
			}
			// will handle the EOF here
			handleEOF(bin);
			hash.setText(Blake32.getHash());
		}
	}

	private static BigInteger[] swap() {
		z = y.multiply(A).add(x);
		return new BigInteger[] { z.mod(Api), z.divide(Api) };
	}
	private static void finalOutput(int control) throws IOException{
		if (printInput) {
			if (enableHex) {
				if((control & cut_front) == 0) {
					outInput.setText(outInput.getText() + sep + padHexFront(xypi[0].toString(16)));// hex
					System.out.println(padHexFront(xypi[0].toString(16)));
				}
				if((control & cut_tail) == 0) {
					outInput.setText(outInput.getText() + sep + padHexFront(xypi[1].toString(16)));// hex
					System.out.println(padHexFront(xypi[1].toString(16)));
				}
			} else {
				if((control & cut_front) == 0) {
					if((control & flip_flag) > 0) {
						outInput.setText(outInput.getText() + sep + new BigInteger(flipBits(xypi[0].toString(2)),2).toString(10));// decimal
						System.out.println(new BigInteger(flipBits(xypi[0].toString(2)),2).toString(10));
					}
					else {
						outInput.setText(outInput.getText() + sep + xypi[0].toString(10));// decimal
						System.out.println(xypi[0].toString(10));
					}
				}
				if((control & cut_tail) == 0) {
					if((control & flip_flag_late) > 0) {
						outInput.setText(outInput.getText() + sep + new BigInteger(flipBits(xypi[1].toString(2)),2).toString(10));// decimal
						System.out.println(new BigInteger(flipBits(xypi[1].toString(2)),2).toString(10));
					}
					else {
						outInput.setText(outInput.getText() + sep + xypi[1].toString(10));// decimal
						System.out.println(xypi[1].toString(10));
					}
				}
			}
		}
		if(enableFileOutput) {
			writeBack(control);
		}
	}
	private static void writeBack(int control) throws NumberFormatException, IOException {
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
			s0 = padBinaryFront(s0);
		}

		if(s1.length() < b && (((end_flag & control) == 0))) {
			s1 = padBinaryFront(s1);
		}

		
		if(s0.length() > b) {
			//System.out.println(xypi[0].toString(16));
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
			if((control & cut_tail) > 0) {
				xypi = compIn(reg);
				finalOutput(control);
			}
			else {
				sendResultToHash(xypi[1], reg);
				xypi = compIn(reg);
				finalOutput(control);
			}
			
		}
	}
}
class Blake32 {
	public static int instanceCounter = 0;
	public int[] v = new int[16];
	public int [] m = new int[16];
	public int round;
	public static int[] h32 = new int[8];
	public int[] salt32 = new int[4];
	public byte[] datablock;
	public static int[] t32 = new int[2];
	public final int NB_ROUNDS32 = 10;
	public final int reg = 0, head_mask = 1;
	public int IV32[]={
		  0x6A09E667, 0xBB67AE85,
		  0x3C6EF372, 0xA54FF53A,
		  0x510E527F, 0x9B05688C,
		  0x1F83D9AB, 0x5BE0CD19
		};
	public int c32[] = {
	    0x243F6A88, 0x85A308D3,
	    0x13198A2E, 0x03707344,
	    0xA4093822, 0x299F31D0,
	    0x082EFA98, 0xEC4E6C89,
	    0x452821E6, 0x38D01377,
	    0xBE5466CF, 0x34E90C6C,
	    0xC0AC29B7, 0xC97C50DD,
	    0x3F84D5B5, 0xB5470917 
	};
	public static int getCount() {
		return instanceCounter;
	}
	
	Blake32(int control, byte[] d, int[] s) {
		instanceCounter ++;
		if((control & head_mask) > 0) {
			for(int i=0;i<8;i++) {
				h32[i] = IV32[i];
			}
			t32[0] = 0;
			t32[1] = 0;
		}
		if(s == null){
			salt32[0] = 0;
			salt32[1] = 0;
			salt32[2] = 0;
			salt32[3] = 0;
		}else
		{
			salt32 = s;
		}
		datablock = d;
	}
	public char sigma[][] = {
	    {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 } ,
	    { 14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3 } ,
	    { 11,  8, 12,  0,  5,  2, 15, 13, 10, 14,  3,  6,  7,  1,  9,  4 } ,
	    {  7,  9,  3,  1, 13, 12, 11, 14,  2,  6,  5, 10,  4,  0, 15,  8 } ,
	    {  9,  0,  5,  7,  2,  4, 10, 15, 14,  1, 11, 12,  6,  8,  3, 13 } ,
	    {  2, 12,  6, 10,  0, 11,  8,  3,  4, 13,  7,  5, 15, 14,  1,  9 } ,
	    { 12,  5,  1, 15, 14, 13,  4, 10,  0,  7,  6,  3,  9,  2,  8, 11 } ,
	    { 13, 11,  7, 14, 12,  1,  3,  9,  5,  0, 15,  4,  8,  6,  2, 10 } ,
	    {  6, 15, 14,  9, 11,  3,  0,  8, 12,  2, 13,  7,  1,  4, 10,  5 } ,
	    { 10,  2,  8,  4,  7,  6,  1,  5, 15, 11,  9, 14,  3, 12, 13 , 0 }, 
	    {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 } ,
	    { 14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3 } ,
	    { 11,  8, 12,  0,  5,  2, 15, 13, 10, 14,  3,  6,  7,  1,  9,  4 } ,
	    {  7,  9,  3,  1, 13, 12, 11, 14,  2,  6,  5, 10,  4,  0, 15,  8 } ,
	    {  9,  0,  5,  7,  2,  4, 10, 15, 14,  1, 11, 12,  6,  8,  3, 13 } ,
	    {  2, 12,  6, 10,  0, 11,  8,  3,  4, 13,  7,  5, 15, 14,  1,  9 } ,
	    { 12,  5,  1, 15, 14, 13,  4, 10,  0,  7,  6,  3,  9,  2,  8, 11 } ,
	    { 13, 11,  7, 14, 12,  1,  3,  9,  5,  0, 15,  4,  8,  6,  2, 10 } ,
	    {  6, 15, 14,  9, 11,  3,  0,  8, 12,  2, 13,  7,  1,  4, 10,  5 } ,
	    { 10,  2,  8,  4,  7,  6,  1,  5, 15, 11,  9, 14,  3, 12, 13 , 0 }  
	  };
	
	
	public static String padHexFront(String bits) {
		while (bits.length() < 8) {
			bits = "0" + bits;
		}
		return bits;
	}
	
	public static String getHash(){
		String hash = "";
		
		for(int x:h32) {
			hash = hash + padHexFront(Integer.toHexString(x));
		}
		return hash;
	}
	
	
	
	
	
	
	
	
	
	
	public byte[] getFourByte (int start)
	{
		byte[] fourchar = new byte[4];
		fourchar[0] = datablock[start];
		fourchar[0] = datablock[start + 1];
		fourchar[0] = datablock[start + 2];
		fourchar[0] = datablock[start + 3];
		return fourchar;
	}
	public void compress32() {
		
		for(int i = 0; i < 16;i++)
		{
			 m[i] = U8TO32_BE(getFourByte(i * 4));
		}
		
		/* initialization */
		for(int i = 0;i < 8; i++)
		{
			v[i] = h32[i];
		}
		v[ 8] = salt32[0] ^ c32[0];
		v[ 9] = salt32[1] ^ c32[1];
		v[10] = salt32[2] ^ c32[2];
		v[11] = salt32[3] ^ c32[3];
		
		v[12] = t32[0] ^ c32[4];
		v[13] = t32[0] ^ c32[5];
		v[14] = t32[1] ^ c32[6];
		v[15] = t32[1] ^ c32[7];
		
		  /*  do 10 rounds */
		  for(round=0; round<NB_ROUNDS32; ++round) {

		    /* column step */
		    G32( 0, 4, 8,12, 0);
		    G32( 1, 5, 9,13, 1);
		    G32( 2, 6,10,14, 2);
		    G32( 3, 7,11,15, 3);    

		    /* diagonal step */
		    G32( 0, 5,10,15, 4);
		    G32( 1, 6,11,12, 5);
		    G32( 2, 7, 8,13, 6);
		    G32( 3, 4, 9,14, 7);

		  }
		  
		  /* finalization */
		  h32[0] ^= v[ 0]^v[ 8]^salt32[0];
		  h32[1] ^= v[ 1]^v[ 9]^salt32[1];
		  h32[2] ^= v[ 2]^v[10]^salt32[2];
		  h32[3] ^= v[ 3]^v[11]^salt32[3];
		  h32[4] ^= v[ 4]^v[12]^salt32[0];
		  h32[5] ^= v[ 5]^v[13]^salt32[1];
		  h32[6] ^= v[ 6]^v[14]^salt32[2];
		  h32[7] ^= v[ 7]^v[15]^salt32[3];
		  
		  t32[0] += 512;
		  if (t32[0] == 0)
			  t32[1] ++;

	}
	public int ROT32(int x, int n) {
		return ((x) << (32 - n) | ((x) >>> (n)));
	}
	public int ADD32(int x, int y) {
		return ((int)((x) + (y)));
	}
	public int XOR32(int x, int y) {
		return ((int)((x) ^ (y)));
	}
	public void G32(int a,int b,int c,int d,int i) {
		 v[a] = ADD32(v[a],v[b])+XOR32(m[sigma[round][2*i]], c32[sigma[round][2*i+1]]);
	     v[d] = ROT32(XOR32(v[d],v[a]),16);
	     v[c] = ADD32(v[c],v[d]);
	     v[b] = ROT32(XOR32(v[b],v[c]),12);
	     v[a] = ADD32(v[a],v[b])+XOR32(m[sigma[round][2*i+1]], c32[sigma[round][2*i]]);
	     v[d] = ROT32(XOR32(v[d],v[a]), 8);
	     v[c] = ADD32(v[c],v[d]);
	     v[b] = ROT32(XOR32(v[b],v[c]), 7);
	}
	
	public int U8TO32_BE(byte p[]) {
	  return
	  (((int)((p)[0]) << 24) | 
	   ((int)((p)[1]) << 16) | 
	   ((int)((p)[2]) <<  8) | 
	   ((int)((p)[3])      ));
	}
	
}
