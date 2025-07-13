import java.io.*;
import java.net.*;

public class ServerTwoWay {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(5000)) {
            System.out.println("Server is connected at port no: " + ss.getLocalPort());
            System.out.println("Waiting for client...\n");

            try (Socket s = ss.accept()) {
                System.out.println("Client request is accepted from port no: " + s.getPort());
                System.out.println("Server's Communication Port: " + s.getLocalPort());
                DataInputStream input = new DataInputStream(s.getInputStream());
                DataOutputStream output = new DataOutputStream(s.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String clientMessage = "";
                String serverMessage = "";

                while (true) {
                    String receivedStuffed = input.readUTF();
                    System.out.println("Received Stuffed (from client): " + receivedStuffed);

                    clientMessage = bitDestuff(receivedStuffed);
                    System.out.println("Client (De-stuffed): " + clientMessage);

                    String originalMessage = binaryToString(clientMessage);
                    System.out.println("Client (Original String): " + originalMessage);

                    if (originalMessage.equals("stop")) {
                        System.out.println("Client ended the communication.");
                        break;
                    }

                    System.out.print("Server: ");
                    serverMessage = reader.readLine();
                    output.writeUTF(serverMessage);
                    output.flush();

                    if (serverMessage.equals("stop")) {
                        System.out.println("Server ended the communication.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error during communication with client: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    public static String bitDestuff(String data) {
        StringBuilder destuffed = new StringBuilder();
        int count = 0;

        for (int i = 0; i < data.length(); i++) {
            char bit = data.charAt(i);
            if (bit == '1') {
                count++;
                destuffed.append(bit);
                if (count == 5 && i + 1 < data.length() && data.charAt(i + 1) == '0') {
                    i++;
                    count = 0;
                }
            } else {
                destuffed.append(bit);
                count = 0;
            }
        }

        return destuffed.toString();
    }

    public static String binaryToString(String binaryData) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < binaryData.length(); i += 8) {
            String byteString = binaryData.substring(i, i + 8);
            char c = (char) Integer.parseInt(byteString, 2);
            str.append(c);
        }
        return str.toString();
    }
}
