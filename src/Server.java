import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Random;
import java.math.BigInteger;

public class Server  {

    public static void main(String [] args) {
        ServerSocket sock;
        Socket client;
        PrintWriter toClient;
        BufferedReader fromClient;
        Random ran = new Random();
        String response;
        BigInteger numbertofactor, numberfactored, factored, bigint1, bigint2;
        int bitlength = 25;

        // Wisdom Array
        String[] wisdomArray = new String[]{"A bird in the hand is safer than one overhead.",
                "A clean desk is a sign of a sick mind.",
                "A computer makes as many mistakes in one second as three people working for thirty years straight",
                "A conference is simply an admission that you want somebody else to join you in your troubles.",
                "A dog is a dog except when he is facing you. Then he is Mr.Dog.",
                "A great deal of money is never enough once you have it.",
                "A major failure will not occur until after the unit has passed final inspection.",
                "A meeting is an event at which the minutes are kept and the hours are lost.", "42",
                "A misplaced decimal point will always end up where it will do the greatest damage.",
                "A perfectly calm day will turn gusty the instant you drop a $20 bill.",
                "A stockbroker is someone who invests your money until it is all gone.",
                "A synonym is a word you use when you can't spell the other one.",
                "A waist is a terrible thing to mind."
        };

        // get a scanner for input
        Scanner kbd = new Scanner(System.in);

        try {
            sock = new ServerSocket(12346);
            System.out.println("Waiting for Connection");
            // sit and wait for connection
            client = sock.accept();
            System.out.println("Connected to " +
                    client.getInetAddress());

            // Hook up the PrintWriter and BufferedReader
            fromClient = new BufferedReader(
                    new InputStreamReader(
                            client.getInputStream()));
            toClient = new PrintWriter(client.getOutputStream(),
                    true);

            // Wait for a response
            System.out.println("Waiting for response ");
            // Reads line that client says
            response = fromClient.readLine();

            // Keep running until user types stop
            while (!response.equals("stop")) {
                // Check the response value wise
                // if it's equal to "" client wants wisdom
                if (response.equals("")) {
                    System.out.println("Recieved wisdom request from client.");

                    // Send client a number to factor
                    bigint1 = BigInteger.probablePrime(bitlength, ran);
                    bigint2 = BigInteger.probablePrime(bitlength, ran);
                    System.out.println(bigint1);
                    System.out.println(bigint2);

                    numbertofactor = bigint1.multiply(bigint2);

                    System.out.println("Sending " + numbertofactor + " to client.");
                    toClient.println(numbertofactor);

                    // Attempt to read client's factor
                    response = fromClient.readLine();
                    System.out.println("Recieved factor of " + response);

                    // Save the response of the client that
                    // they think is the factor
                    numberfactored = new BigInteger(response);

                    // Check if input is correct
                    if (numberfactored.equals(bigint1) || numberfactored.equals(bigint2)) {
                        // Send correct to the client
                        System.out.println("Sending correct");
                        toClient.println("correct");

                        // Choose a wisdom to send randomly
                        String wisdom = wisdomArray[ran.nextInt(13)];

                        // Send wisdom
                        System.out.println("Sending wisdom: " + wisdom);
                        toClient.println(wisdom);

                    }

                    // factored was incorrect so
                    // client has to try again
                    else {
                        // Send incorrect and set tracker
                        String correcttracker = "incorrect";
                        System.out.println("Sending incorrect");
                        toClient.println("incorrect");

                        while (correcttracker.equals("incorrect")) {
                            // read the client's response
                            response = fromClient.readLine();
                            System.out.println("Recieved factor of " + response);
                            numberfactored = new BigInteger(response);

                            // Check if response equals either factors
                            if (numberfactored.equals(bigint1) || numberfactored.equals(bigint2)) {
                                //if (factored.equals(numberfactored)) {
                                System.out.println("Sending correct");
                                toClient.println("correct");

                                // Choose a wisdom to send randomly
                                String wisdom = wisdomArray[ran.nextInt(13)];

                                // Send wisdom
                                System.out.println("Sending wisdom: " + wisdom);
                                toClient.println(wisdom);

                                // Change tracker to correct since client was
                                // correct
                                correcttracker = "correct";

                            } else {
                                // Send incorrect
                                System.out.println("Sending incorrect");
                                toClient.println("incorrect");
                            }

                        }

                    }

                }

                // Wait for a response
                System.out.println("Waiting for response ");
                // Reads line that client says
                response = fromClient.readLine();
            }
            System.out.print("Goodbye!!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
