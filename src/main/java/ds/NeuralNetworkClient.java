package ds;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class NeuralNetworkClient {
    // Read the user input with a Scanner.
    static Scanner scanner = new Scanner(System.in);
    static Gson gson = new Gson();

    void sendRequest(String host, int port) {
        System.out.println("The client is running.");
        DatagramSocket aSocket = null;
        try {
            InetAddress aHost = InetAddress.getByName(host);
//            Create a socket
            aSocket = new DatagramSocket();
//          Display the menu and get the user input
            int userSelection = menu();
            while (userSelection != 5) {
//                Initialize the payload to an empty string
                String payload = new Gson().toJson("");
                switch (userSelection) {
                    case 0: // display the truth table
                        payload = getTruthTablePayload();
                        break;
                    case 1:  // get the range values of the truth table. These values are from the rightmost column of a
                        // standard truth table.
                        System.out.println("Enter the four results of a 4 by 2 truth table. Each value should be 0 or 1.");
                        Double a = scanner.nextDouble();
                        Double b = scanner.nextDouble();
                        Double c = scanner.nextDouble();
                        Double d = scanner.nextDouble();
                        payload = createTruthTablePayload(a, b, c, d);
                        break;

                    case 2: // perform a single trainng step and display total error.
                        payload = train(1);
                        break;
                    case 3: // perform n training steps
                        System.out.println("Enter the number of training sets.");
                        int n = scanner.nextInt();
                        payload = train(n);
                        break;
                    case 4: // test with a pair of inputs.
                        System.out.println("Enter a pair of doubles from a row of the truth table. These are domain values.");
                        double input0 = scanner.nextDouble();
                        double input1 = scanner.nextDouble();
                        payload = testWithPair(input0, input1);
                        break;
                    default:
                        System.out.println("Error in input. Please choose an integer from the main menu.");
                        break;
                }
                byte[] m = payload.getBytes();
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, port);
                aSocket.send(request);

                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                String replyString = new String(reply.getData()).trim();
                this.parseResponse(replyString);
                userSelection = menu();
            }
            System.out.println("UDP Client side quitting");

        } catch (SocketException e) {System.out.println("Socket Exception: " + e.getMessage());
        }catch (IOException e){System.out.println("IO Exception: " + e.getMessage());
        }finally {
            if(aSocket != null) aSocket.close();
        }
    }

//    Creates request payload for requesting the truth table server is currently using.
    String getTruthTablePayload() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("request","getCurrentRange");
        return gson.toJson(payload);
    }

//    Creates request payload for setting the truth table server is currently using.
    String createTruthTablePayload(Double a, Double b, Double c, Double d) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("request","setCurrentRange");
        payload.put("val1",a);
        payload.put("val2",b);
        payload.put("val3",c);
        payload.put("val4",d);
        return gson.toJson(payload);
    }

//    Creates request payload for performing n training steps.
    String train(int iterations){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("request","train");
        payload.put("iterations",iterations);
        return gson.toJson(payload);
    }

//    Creates request payload for testing the neural network with a pair of inputs.
    String testWithPair(double input0, double input1){
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("request","test");
        payload.put("val1",input0);
        payload.put("val2",input1);
        return gson.toJson(payload);
    }

//    Displays the menu and gets the user input.
    public static int menu() {
        System.out.println("Using a neural network to learn a truth table.\nMain Menu");
        System.out.println("0. Display the current truth table.");
        System.out.println("1. Provide four inputs for the range of the two input truth table and build a new neural network. To test XOR, enter 0  1  1  0.");
        System.out.println("2. Perform a single training step.");
        System.out.println("3. Perform n training steps. 10000 is a typical value for n.");
        System.out.println("4. Test with a pair of inputs.");
        System.out.println("5. Exit program.");
        return scanner.nextInt();
    }

    public static void main(String args[]) {
        System.out.println("Enter the port number to connect to:");
        int serverPort = Integer.parseInt(scanner.nextLine());
        System.out.println();
        System.out.println("Enter the host name to connect to:");
        String serverHost = scanner.nextLine();
        NeuralNetworkClient neuralNetworkClient = new NeuralNetworkClient();
        neuralNetworkClient.sendRequest(serverHost,serverPort);
    }

    class Response {
        String response;
        public Response(String response) {
            this.response = response;
        }
    }

    class GetCurrentRangeResponse extends Response {
        String val1;
        String val2;
        String val3;
        String val4;
        public GetCurrentRangeResponse(String response, String val1, String val2, String val3, String val4) {
            super(response);
            this.val1 = val1;
            this.val2 = val2;
            this.val3 = val3;
            this.val4 = val4;
        }
    }

    class TrainTestResponse extends Response {
        String val1;
        public TrainTestResponse(String response, String val1) {
            super(response);
            this.val1 = val1;
        }
    }
//    Parses the response from the server and displays it to the user.
    void parseResponse(String responseString) {
        JsonReader reader = new JsonReader(new StringReader(responseString));
        reader.setLenient(true);
        Response response = gson.fromJson(reader, Response.class);
        String typeResponse = response.response;
        switch (typeResponse) {
            case "getCurrentRange":
                GetCurrentRangeResponse getCurrentRangeResponse = gson.fromJson(responseString, GetCurrentRangeResponse.class);
                System.out.println("The current truth table is: " + getCurrentRangeResponse.val1 + " " + getCurrentRangeResponse.val2 + " " + getCurrentRangeResponse.val3 + " " + getCurrentRangeResponse.val4);
                break;
            case "setCurrentRange":
                System.out.println("The new truth table is updated.");
                break;
            case "train":
                TrainTestResponse trainResponse = gson.fromJson(responseString, TrainTestResponse.class);
                System.out.println("After this step the error is: " + trainResponse.val1);
                break;
            case "test":
                TrainTestResponse testResponse = gson.fromJson(responseString, TrainTestResponse.class);
                System.out.println("The range value is approximately: " + testResponse.val1);
                break;
            default:
                System.out.println("Unknown response type: " + typeResponse);
                break;
        }
    }
}
