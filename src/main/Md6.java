package main;

public class Md6 {
	public final static int c = 16;//words in compression output
	public final static int w = 64;//bits in word
	public final static int n = 89;//number of w-bit words
	public final static int r = 178;//number of rounds
	public final static int t = r * c;
	public final static long[] A = new long[n + t];
	public static long[] C = new long[c];
	public final static int t0 = 17, t1 = 18, t2 = 21, t3 = 31, t4 = 67, t5= 89;
	
	public final static long S0 = 0x1;
	public final static long Smask = 0x73;
	
	//public static BigInteger[] N = new BigInteger[n];//input array of n w-bit words
	public static void loop_body(long S, int i, int rs, int ls, int step){
		long x = S;
		x = x ^ (A[i + step - t5]);
		x = x ^ (A[i + step - t0]);
		x = x ^ (A[i + step - t1] & A[i + step - t2]);
		x = x ^ (A[i + step - t3] & A[i + step - t4]);
		x = x ^ (x >>> rs);
		A[i + step] = x ^ (x << ls);
	}
	public static void main(String args[]){
		long[] testN = new long[n];
		for(int i=0; i < n; i++)
		{
			testN[i] = i;
		}
		md6_compress(testN);
		for(int i = 0; i < c; i++)
		{
			System.out.println(Long.toBinaryString(C[i]));
		}
	}
	public static void md6_main_compression_loop(){
		long S = S0;
		for(int j = 0, i = n; j < t; j += c){
			loop_body(S, i, 10, 11, 0);//0
			loop_body(S, i, 5, 24, 1);//1
			loop_body(S, i, 13, 9, 2);//2
			loop_body(S, i, 10, 16, 3);//3
			loop_body(S, i, 11, 15, 4);//4
			loop_body(S, i, 12, 9, 5);//5
			loop_body(S, i, 2, 27, 6);//67
			loop_body(S, i, 7, 15, 7);//7
			loop_body(S, i, 14, 6, 8);//8
			loop_body(S, i, 15, 2, 9);//9
			loop_body(S, i, 7, 29, 10);//10
			loop_body(S, i, 13, 8, 11);//11
			loop_body(S, i, 11, 15, 12);//12
			loop_body(S, i, 7, 5, 13);//13
			loop_body(S, i, 6, 31, 14);//14
			loop_body(S, i, 12, 9, 15);//15
			
			S = (S << 1) ^ (S >> (w - 1)) ^ (S & (Smask));
			i += 16;
		}
	}
	public static long[] md6_compress(long[] N){
		for(int i = 0; i < n; i++){
			A[i] = N[i];
		}
		md6_main_compression_loop();
		for(int i = 0; i < c; i++){
			C[i] = A[(r - 1) * c + n + i];
		}
		return C;
	}
}
