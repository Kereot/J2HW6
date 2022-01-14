package j2hw6;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientApp {
    private static Socket socket;
    private static final String ADDRESS = "localhost";
    private static final int PORT = 8189;
    private static Scanner inScanner;
    private static Scanner outScanner;
    private static PrintWriter out;
    private static boolean isTerminated;

    public static void main(String[] args) {

        try {
            socket = new Socket(ADDRESS, PORT);
            System.out.println("Connected to server!");
            System.out.println("Type '/end' to disconnect");
            inScanner = new Scanner(socket.getInputStream());
            outScanner = new Scanner(System.in);
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    while (!socket.isClosed()) {
                        String str = inScanner.nextLine();
                        if (str.equals("Server: /end")) {
                            System.out.println("Disconnect requested, type anything to close the app.");
                            terminate();
                            break;
                        }
                        System.out.println(str);
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("Shutting down...");
                } finally {
                    if (!isTerminated) {
                        System.out.println("Type anything to close the app.");
                        terminate();
                    }
                }
            }).start();

            new Thread(() -> {
                try {
                    while (!socket.isClosed()) {
                        String str = outScanner.nextLine();
                        out.println(str);
                        if (str.equals("/end")) {
                            System.out.println("Disconnect initiated");
                            terminate();
                        }
                    }
                } finally {
                    if (!isTerminated) {
                        terminate();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void terminate() {
        isTerminated = true;
        try {
            socket.close();
            inScanner.close();
            outScanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
