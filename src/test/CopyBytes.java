package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyBytes {
    public static void main(String[] args) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream("pic.jpg");
            out = new FileOutputStream("pic_2.jpg");
            int c;
            int counter = 0;
            while ((c = in.read()) != -1) {
            	counter ++;
            	System.out.println(Integer.toBinaryString(c));
                out.write(c);
            }
            System.out.println(counter);
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}