import java.io.*;
import java.net.*;

public class Lab2_Client_35_39 {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("192.168.175.126", 5000);
        System.out.println("Client Connected at server Handshaking port " + s.getPort());
        System.out.println("Client’s communication port " + s.getLocalPort());
        System.out.println("Client is Connected");

        DataOutputStream output = new DataOutputStream(s.getOutputStream());
        DataInputStream input = new DataInputStream(s.getInputStream());

        // Read from file instead of console
        BufferedReader fileReader = new BufferedReader(new FileReader("RabbaniTamal.txt")); // <- your input file
        String line;

        while ((line = fileReader.readLine()) != null) {
            System.out.println("Original: " + line);

            String binaryString = stringToBinary(line);
            System.out.println("Binary (before stuffing): " + binaryString);

            String stuffedData = bitStuff(binaryString);
            System.out.println("Binary (after stuffing): " + stuffedData);

            output.writeUTF(stuffedData);
            output.flush();

            if (line.equals("stop")) break;

            // Read server response
            String serverReply = input.readUTF();
            System.out.println("Server: " + serverReply);

            if (serverReply.equals("stop")) break;
        }

        // Cleanup
        fileReader.close();
        output.close();
        input.close();
        s.close();
    }

    public static String stringToBinary(String str) {
        StringBuilder binaryString = new StringBuilder();
        for (char c : str.toCharArray()) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryString.append(binaryChar);
        }
        return binaryString.toString();
    }

    public static String bitStuff(String binaryData) {
        StringBuilder stuffedData = new StringBuilder();
        int count = 0;
        for (int i = 0; i < binaryData.length(); i++) {
            char bit = binaryData.charAt(i);
            stuffedData.append(bit);
            if (bit == '1') {
                count++;
                if (count == 5) {
                    stuffedData.append('0');
                    count = 0;
                }
            } else {
                count = 0;
            }
        }
        return stuffedData.toString();
    }
}
