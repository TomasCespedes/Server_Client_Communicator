import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.math.BigInteger;
import static java.lang.Math.sqrt;


public class Client implements Runnable{

    public BigInteger number, start, stop, factor;

    public Client(BigInteger number, BigInteger start, BigInteger stop) {
        this.number = number;
        this.start = start;
        this.stop = stop;
    }


    public BigInteger getFactor() {
        return factor;
    }

    @Override
    public void run() {
        BigInteger two = new BigInteger("2");
        BigInteger factor = new BigInteger("1");

        do {
            if ((this.number).mod(two).compareTo(BigInteger.ZERO) == 0) {
                factor = two;
            }
            for (BigInteger i = this.start; (this.stop).compareTo(i) >= 0; i = i.add(two)) {
                if ((this.number).mod(i).compareTo(BigInteger.ZERO) == 0) {
                    factor = i;
                    break;
                }
            }
            this.factor = factor;
        }
        while (factor.equals(null));

        //System.out.println(factor);
    }

    public static void main (String [] args) {

        Socket sock;
        Scanner kbd = new Scanner(System.in);
        BufferedReader fromServer;
        PrintWriter toServer;
        String response;
        String clientline;
        BigInteger factored, biginttemp;

        try {
            // Prompt client to enter IP address
            System.out.print("Enter IP address: ");
            System.out.flush();
            clientline = kbd.nextLine();
            // 10.70.27.40 is IP address to connect to for computer

            // Create sockets
            sock = new Socket(clientline,
                    12346);
            System.out.println("Connected to " + sock.getInetAddress());

            // Create server buffer reciever
            fromServer = new BufferedReader(
                    new InputStreamReader(
                            sock.getInputStream()));

            // Create server buffer sender
            toServer = new PrintWriter(
                    sock.getOutputStream(),
                    true);

            // Prompt client to either stop or request wisdom
            System.out.println("Press <Enter> to recieve wisdom or type stop to end");
            System.out.print("Client: ");
            System.out.flush();
            clientline = kbd.nextLine();
            toServer.println(clientline);

            // Keep running until Client types stop
            while (!clientline.equals("stop")) {
                // Check if client hits enter
                if (clientline.equals("")) {
                    // Request response
                    System.out.println("Requesting Wisdom from server");

                    // Read the factor from the server
                    response = fromServer.readLine();
                    System.out.println("Recieved " + response + " from server.");

                    biginttemp = new BigInteger(response);

                    BigInteger one = new BigInteger("1");
                    BigInteger two = new BigInteger("2");
                    BigInteger three = new BigInteger("3");
                    BigInteger four = new BigInteger("4");

                    //System.out.println(biginttemp.bitLength());

                    BigInteger sqrt = biginttemp.shiftRight(biginttemp.bitLength() / 2);
                    BigInteger halfsqrt = biginttemp.shiftRight(biginttemp.bitLength() / 2).divide(two);

                    Client c1 = new Client(biginttemp, three, sqrt);
                    Client c2 = new Client(biginttemp, three, sqrt.multiply(two));
                    Thread t1 = new Thread(c1);
                    Thread t2 = new Thread(c2);

                    t1.start();
                    t2.start();

                    try{
                        t1.join();
                        t2.join();

                        if (!c1.getFactor().equals(one)) {
                            System.out.println("Sending " + c1.getFactor() + " to server from Thread one.");
                            toServer.println(c1.getFactor());
                        } else {
                            toServer.println(c2.getFactor());
                            System.out.println("Sending " + c2.getFactor() + " to server from Thread two.");
                        }




                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }




                    //factored = smallestPrimeFactor(biginttemp);
                    //System.out.println("Sending " + c1.getFactor() + " to server.");


                    // Read the answer from the server
                    response = fromServer.readLine();
                    System.out.println("Recieved " + response + " from server");

                    // Client was correct in his factor
                    if (response.equals("correct")) {
                        response = fromServer.readLine();
                        System.out.println("Recieved wisdom: " + response);
                    }

                    // Client was not correct in his factor
                    // so he must keep trying if he wants wisdom
                    while (response.equals("incorrect")) {

                        // Send a factor back
                        // Will technically be the same factor
                        //factored = smallestPrimeFactor(biginttemp);
                        //System.out.println("Sending " + factored + " to server.");
                        toServer.println(c1.getFactor());

                        // Read the answer from the server
                        response = fromServer.readLine();
                        System.out.println("Recieved " + response + " from server");

                        if (response.equals("correct")) {
                            response = fromServer.readLine();
                            System.out.println("Recieved wisdom: " + response);
                        }
                    }
                }

                // Reset the prompts
                System.out.println("Press <Enter> to recieve wisdom or type stop to end");
                System.out.print("Client: ");
                System.out.flush();
                clientline = kbd.nextLine();
                toServer.println(clientline);
            }

            System.out.print("Goodbye!!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
