import java.io.*;
import java.net.*;

public class Lab5_35_39_Server {

    public static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < b.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    public static String mod2div(String dividend, String divisor) {
        int pick = divisor.length();
        String tmp = dividend.substring(0, pick);

        while (pick < dividend.length()) {
            if (tmp.charAt(0) == '1') {
                tmp = xor(divisor, tmp) + dividend.charAt(pick);
            } else {
                tmp = xor("0".repeat(pick), tmp) + dividend.charAt(pick);
            }
            pick++;
        }

        if (tmp.charAt(0) == '1') {
            tmp = xor(divisor, tmp);
        } else {
            tmp = xor("0".repeat(pick), tmp);
        }

        return tmp;
    }

    public static int hammingDistance(String a, String b) {
        int distance = 0;
        System.out.print("Error at bit position(s): ");
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                distance++;
                System.out.print(i + " ");
            }
        }
        System.out.println();
        return distance;
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Server started on port 5000. Waiting for client...");

            Socket s = ss.accept();
            System.out.println("Client connected.");

            DataInputStream din = new DataInputStream(s.getInputStream());

            String originalCodeword = din.readUTF();
            String receivedCodeword = din.readUTF();
            String key = din.readUTF();

            System.out.println("Received Codeword: " + receivedCodeword);
            System.out.println("Generator Polynomial: " + key);

            String remainder = mod2div(receivedCodeword, key);
            System.out.println("CRC Remainder: " + remainder);

            if (remainder.contains("1")) {
                System.out.println("Error detected!");

                int errorCount = hammingDistance(originalCodeword, receivedCodeword);
                System.out.println("Total bit error(s) (Hamming Distance): " + errorCount);
            } else {
                System.out.println("No error detected in received data.");
            }

            din.close();
            s.close();
            ss.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
