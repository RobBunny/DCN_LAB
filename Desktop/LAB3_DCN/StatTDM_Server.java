import java.io.*;
import java.net.*;

public class StatTDM_Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server listening on port " + serverSocket.getLocalPort());

            try (Socket socket = serverSocket.accept()) {
                System.out.println("Client connected from port " + socket.getPort());

                DataInputStream input = new DataInputStream(socket.getInputStream());

                int numStreams = input.readInt();
                BufferedWriter[] writers = new BufferedWriter[numStreams];
                for (int i = 0; i < numStreams; i++) {
                    writers[i] = new BufferedWriter(new FileWriter("output_" + (i + 1) + ".txt"));
                }

                int frameNumber = 1;
                while (true) {
                    int frameSize;
                    try {
                        frameSize = input.readInt();
                    } catch (EOFException e) {
                        break;
                    }

                    if (frameSize == 0) break;

                    byte[] frame = new byte[frameSize];
                    input.readFully(frame);

                    System.out.println("\nReceived Frame #" + (frameNumber++) + ":");
                    for (int i = 0; i < frameSize; i += 2) {
                        int streamId = frame[i];
                        char data = (char) frame[i + 1];

                        System.out.println("Stream ID: " + (streamId + 1) + " | Data: " + data);

                        if (streamId >= 0 && streamId < numStreams) {
                            writers[streamId].write(data);
                        }
                    }
                }

                for (BufferedWriter writer : writers) writer.close();
                input.close();
                socket.close();
                System.out.println("\nAll data received and written to output files. Server closing.");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
