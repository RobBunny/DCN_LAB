import java.io.*;

public class TestServer {
    public static int xor(char a, char b) {
        return (a == b) ? 0 : 1;
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
            StringBuilder xoredResult = new StringBuilder();
            
            // Perform XOR between chunk and chipCode
            for (int j = 0; j < chipLength; j++) {
                xoredResult.append(xor(chunk.charAt(j), chipCode.charAt(j)));
            }
            
            int ones = 0;
            for (char bit : xoredResult.toString().toCharArray()) {
                if (bit == '1') ones++;
            }
            recoveredMessage.append(ones > chipLength / 2 ? '1' : '0');
        }
        return recoveredMessage.toString();
    }

    public static void main(String[] args) {
        // Test the algorithm with sample data
        String chipCode = "1010"; // 4-bit chip code
        String originalMessage = "DATACOM";
        
        // Convert to binary (simulate client's toBinary method)
        StringBuilder binary = new StringBuilder();
        for (char c : originalMessage.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        String binaryData = binary.toString();
        
        // Spread the data (simulate client's spreadedData method)
        StringBuilder spreadedCode = new StringBuilder();
        for (int i = 0; i < binaryData.length(); i++) {
            char bit = binaryData.charAt(i);
            for (int j = 0; j < chipCode.length(); j++) {
                int result = (bit == chipCode.charAt(j)) ? 0 : 1;
                spreadedCode.append(result);
            }
        }
        String spreaded = spreadedCode.toString();
        
        System.out.println("Original: " + originalMessage);
        System.out.println("Binary: " + binaryData);
        System.out.println("Spreaded: " + spreaded);
        
        // Now test the server's despread method
        String recovered = despreadedCode(spreaded, chipCode);
        String message = binaryToString(recovered);
        
        System.out.println("Recovered binary: " + recovered);
        System.out.println("Recovered message: " + message);
        
        // Test file writing
        try (FileWriter fw = new FileWriter("test_output.txt")) {
            fw.write(message);
            System.out.println("File written successfully: test_output.txt");
        } catch (IOException ioe) {
            System.out.println("Error writing to file: " + ioe.getMessage());
        }
    }
}
