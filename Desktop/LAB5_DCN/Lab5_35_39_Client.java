import java.io.*;
import java.net.*;
import java.util.*;

public class Lab5_35_39_Client {

    public static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < b.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    public static String findRem(String dividend, String divisor) {
        int part = divisor.length();
        String rem = dividend.substring(0, part);

        while (part < dividend.length()) {
            if (rem.charAt(0) == '1') {
                rem = xor(divisor, rem) + dividend.charAt(part);
            } else {
                rem = xor("0".repeat(part), rem) + dividend.charAt(part);
            }
            part++;
        }

        if (rem.charAt(0) == '1') {
            rem = xor(divisor, rem);
        } else {
            rem = xor("0".repeat(part), rem);
        }

        return rem;
    }

    public static String toBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

     public static String flipOneRandomBit(String data) {
        char[] chars = data.toCharArray();
        Random rand = new Random();

        int index = rand.nextInt(data.length());
        chars[index] = chars[index] == '0' ? '1' : '0';

        return new String(chars);
    }

    public static String flipTwoRandomBits(String data) {
        char[] chars = data.toCharArray();
        Random rand = new Random();

        int len = data.length();
        int i = rand.nextInt(len);
        int j;
        do {
            j = rand.nextInt(len);
        } while (j == i);

        chars[i] = chars[i] == '0' ? '1' : '0';
        chars[j] = chars[j] == '0' ? '1' : '0';
        return new String(chars);
    }

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter server IP address: ");
            String ipAddress = sc.nextLine();

            Socket s = new Socket(ipAddress, 5000);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            System.out.print("Enter generator polynomial: ");
            String key = sc.nextLine();

            File file = new File("input.txt");
            Scanner fileScanner = new Scanner(file);
            StringBuilder content = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                content.append(fileScanner.nextLine());
            }

            String data = content.toString();
            String binaryData = toBinary(data);
            int keyLen = key.length();
            String appendedData = binaryData + "0".repeat(keyLen - 1);
            String remainder = findRem(appendedData, key);
            String codeword = binaryData + remainder;

            System.out.println("File Content: " + data);
            System.out.println("Converted Binary Data: " + binaryData);
            System.out.println("CRC Remainder: " + remainder);
            System.out.println("After appending zeros: " + appendedData);

            System.out.print("Do you want to flip bits? (no/one/two): ");
            String flipChoice = sc.nextLine().trim().toLowerCase();

            String corruptedCodeword = codeword;
            if (flipChoice.equals("one")) {
                corruptedCodeword = flipOneRandomBit(codeword);
            } else if (flipChoice.equals("two")) {
                corruptedCodeword = flipTwoRandomBits(codeword);
            }

            System.out.println("Transmitted Codeword: " + corruptedCodeword);

            dout.writeUTF(codeword);            
            dout.writeUTF(corruptedCodeword);   
            dout.writeUTF(key);                

            dout.close();
            s.close();
            sc.close();
            fileScanner.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
