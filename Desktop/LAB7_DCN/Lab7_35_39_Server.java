import java.io.*;
import java.net.*;

public class Lab7_35_39_Server {

    public static String xor(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            result.append(a.charAt(i) == b.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    public static String binaryToString(String binaryData) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i + 8 <= binaryData.length(); i += 8) {
            String byteString = binaryData.substring(i, i + 8);
            char character = (char) Integer.parseInt(byteString, 2);
            result.append(character);
        }
        return result.toString();
    }

    public static String despreadedCode(String spreadData, String chipCode) {
        int chipLength = chipCode.length();
        StringBuilder recoveredMessage = new StringBuilder();
        for (int i = 0; i + chipLength <= spreadData.length(); i += chipLength) {
            String chunk = spreadData.substring(i, i + chipLength);
            String xoredResult = xor(chunk, chipCode);
            int ones = 0;
            for (char bit : xoredResult.toCharArray()) {
                if (bit == '1') ones++;
            }
            recoveredMessage.append(ones > chipLength / 2 ? '1' : '0');
        }
        return recoveredMessage.toString();
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Server started on port 5000. Waiting for client...");

            Socket s = ss.accept();
            System.out.println("Client connected: " + s.getInetAddress());

            DataInputStream din = new DataInputStream(s.getInputStream());

            int numFiles = din.readInt();
            System.out.println("Expecting " + numFiles + " files.");

            for (int i = 0; i < numFiles; i++) {
                String fileName = din.readUTF();
                if (fileName.equals("SKIP")) {
                    System.out.println("Skipping file " + (i + 1));
                    continue;
                }

                String spreaded = din.readUTF();
                String key = din.readUTF();

                System.out.println("Processing file: " + fileName);
                System.out.println("Received spreaded code length: " + spreaded.length());
                System.out.println("Chip code: " + key);

                String binaryData = despreadedCode(spreaded, key);
                System.out.println("Recovered binary length: " + binaryData.length());

                String message = binaryToString(binaryData);
                System.out.println("Recovered message preview: " + (message.length() > 50 ? message.substring(0, 50) + "..." : message));

                String outputFile = "received_" + fileName;

                try (FileWriter fw = new FileWriter(outputFile)) {
                    fw.write(message);
                    System.out.println("File written: " + outputFile);
                } catch (IOException ioe) {
                    System.out.println("Error writing to file: " + outputFile + " - " + ioe.getMessage());
                }
            }

            din.close();
            s.close();
            ss.close();

        } catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }
}