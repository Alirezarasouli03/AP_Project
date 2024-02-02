import java.io.*;
import java.net.*;
import java.util.*;


public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final String FILE_PATH = "C://Users//ASUS//Desktop//Players.txt";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            Scanner scanner = new Scanner(System.in);

            boolean isLoggedIn = false;
            while (!isLoggedIn) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        register(scanner);
                        break;
                    case 2:
                        isLoggedIn = login(scanner);
                        break;
                    case 3:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }

            while (true) {
                System.out.println("4. Start Quiz");
                System.out.println("5. View Profile");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                int action = scanner.nextInt();
                scanner.nextLine();

                switch (action) {
                    case 4:
                        startQuiz(din, dout, scanner);
                        break;
                    case 5:
                        viewProfile(scanner, dout, din);
                        break;
                    case 6:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void register(Scanner scanner) throws IOException {
        System.out.println("Registration:");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();

        try (PrintWriter fileWriter = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            fileWriter.println(username);
            fileWriter.println(password);
            fileWriter.println(email);
            fileWriter.println(phoneNumber);
        }
    }

    private static boolean login(Scanner scanner) throws IOException {
        System.out.println("Login:");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && Objects.equals(username, parts[0]) && Objects.equals(password, parts[1])) {
                    System.out.println("Login successfully.");
                    return true;

                }
            }
        }

        return false;
    }

    private static void viewProfile(Scanner scanner, DataOutputStream dout, DataInputStream din) throws IOException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        dout.writeUTF("VIEW_PROFILE:" + username);
        dout.flush();

        String profileInfo = din.readUTF();
        System.out.println("Profile Information:");
        System.out.println(profileInfo);
    }

    private static void startQuiz(DataInputStream din, DataOutputStream dout, Scanner scanner) throws IOException {
        dout.writeUTF("START_QUIZ");
        dout.flush();

        int numQuestions = din.readInt();
        for (int i = 0; i < numQuestions; i++) {
            String question = din.readUTF();
            System.out.println("Question " + (i + 1) + ": " + question);

            String options = din.readUTF();
            System.out.println("Options: " + options);

            System.out.print("Your answer (Enter the option letter): ");
            String userAnswer = scanner.nextLine();

            dout.writeUTF(userAnswer);
            dout.flush();

            String response = din.readUTF();
            System.out.println("Response: " + response);
        }

        System.out.println("Quiz completed.");
    }
}
