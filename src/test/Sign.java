package test;

import java.math.BigInteger;

public class Sign {
	public static void main(String args[]){
//		int a = 1;
//		int b = -1;
//		System.out.println(Integer.toBinaryString(b ^ a));
		BigInteger big = new BigInteger("-1");
		Byte bt = big.toByteArray()[0];
		System.out.println(bt);
	}
}
