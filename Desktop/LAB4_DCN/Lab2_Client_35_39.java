import java.io.*;
import java.net.*;
import java.util.*;

public class Lab2_Client_35_39 {
    public static void main(String[] args) {
        final byte FILLER = '#';

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Server IP Address (e.g., localhost): ");
            String serverIP = scanner.nextLine();

            System.out.print("Enter number of input files (N): ");
            int N = scanner.nextInt();

            System.out.print("Enter Time slot size (T): ");
            int T = scanner.nextInt();
            scanner.nextLine();

            String[] inputFiles = new String[N];
            for (int i = 0; i < N; i++) {
                System.out.print("Enter name of input file " + (i + 1) + ": ");
                inputFiles[i] = scanner.nextLine();
            }

            try (Socket socket = new Socket(serverIP, 5000)) {
                System.out.println("Connected to server at " + socket.getRemoteSocketAddress());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                output.writeInt(N);
                output.writeInt(T);

                BufferedInputStream[] streams = new BufferedInputStream[N];
                boolean[] done = new boolean[N];
                int activeFiles = N;

                for (int i = 0; i < N; i++) {
                    streams[i] = new BufferedInputStream(new FileInputStream(inputFiles[i]));
                }
                while (activeFiles > 0) {
                    byte[] packet = new byte[N * T];
                    boolean hasRealData = false;

                    for (int i = 0; i < N; i++) {
                        if (done[i]) {
                            Arrays.fill(packet, i * T, (i + 1) * T, FILLER);
                            continue;
                        }

                        for (int j = 0; j < T; j++) {
                            int byteRead = streams[i].read();
                            if (byteRead == -1) {
                                packet[i * T + j] = FILLER;
                                done[i] = true;
                            } else {
                                packet[i * T + j] = (byte) byteRead;
                                hasRealData = true;
                            }
                        }

                        if (done[i]) {
                            activeFiles--;
                        }
                    }

                    if (hasRealData) {
                        System.out.println("Sending packet: " + new String(packet));
                        output.write(packet);
                        output.flush();
                    }
                }

                for (BufferedInputStream bis : streams) {
                    bis.close();
                }

                output.close();
                System.out.println("All data sent successfully.");
            }

        } catch (IOException e) {
            System.err.println("Client Error: " + e.getMessage());
        }
    }
}