import java.io.*;
import java.net.*;
import java.util.*;

public class Lab7_35_39_Client {

    public static int xor(char a, char b) {
        return (a == b) ? 0 : 1;
    }

    public static String spreadedData(String binaryData, String key) {
        int keyLen = key.length();
        StringBuilder spreadedCode = new StringBuilder();

        for (int i = 0; i < binaryData.length(); i++) {
            char bit = binaryData.charAt(i);
            for (int j = 0; j < keyLen; j++) {
                spreadedCode.append(xor(bit, key.charAt(j)));
            }
        }

        return spreadedCode.toString();
    }

    public static String toBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter server IP address: ");
            String ipAddress = sc.nextLine();

            System.out.print("Enter chip code: ");
            String key = sc.nextLine();

            System.out.print("Enter number of files: ");
            int n = sc.nextInt();
            sc.nextLine();

            String[] fileNames = new String[n];
            for (int i = 0; i < n; i++) {
                System.out.print("Enter name of file " + (i + 1) + ": ");
                fileNames[i] = sc.nextLine();
            }

            Socket s = new Socket(ipAddress, 5000);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());

            dout.writeInt(n);

            for (String fileName : fileNames) {
                File file = new File(fileName);
                if (!file.exists()) {
                    System.out.println("File not found: " + fileName + ", skipping.");
                    dout.writeUTF("SKIP");
                    continue;
                }

                Scanner fileScanner = new Scanner(file);
                StringBuilder content = new StringBuilder();
                while (fileScanner.hasNextLine()) {
                    content.append(fileScanner.nextLine());
                }
                fileScanner.close();

                String data = content.toString();
                String binaryData = toBinary(data);
                String spreaded = spreadedData(binaryData, key);

                System.out.println("Data: " + data);
                System.out.println("Binary Data: " + binaryData);
                System.out.println("Spreaded Data: " + spreaded);

                dout.writeUTF(fileName);
                dout.writeUTF(spreaded);
                dout.writeUTF(key);

                System.out.println("Sent file: " + fileName);
            }

            dout.close();
            s.close();

        } catch (Exception e) {
            System.out.println("Client Error: " + e);
        }
    }
}
