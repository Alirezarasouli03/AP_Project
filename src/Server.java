import java.io.*;
import java.net.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;
    private static final List<Question> questions = new ArrayList<>();

    static {
        try {
            loadQuestionsFromFile("C://Users//ASUS//Desktop//Questions Bank.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void loadQuestionsFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    List<String> options = Arrays.asList(parts[1], parts[2], parts[3], parts[4]);
                    int correctAnswer = Integer.parseInt(parts[5]);
                    questions.add(new Question(parts[0], options, correctAnswer));
                }
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        private static final Map<String, UserProfile> userProfiles = new HashMap<>();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    switch (request) {
                        case "LOGIN":
                            handleLogin();
                            break;
                        case "VIEW_PROFILE":
                            handleViewProfile();
                            break;
                        case "START_QUIZ":
                            handleQuizRequest();
                            break;
                        case "EXIT":
                            out.println("Goodbye!");
                            break;
                        default:
                            out.println("Unknown command");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleLogin() throws IOException {
            String username = in.readLine();
            String password = in.readLine();

            UserProfile userProfile = userProfiles.get(username);
            if (userProfile != null && userProfile.getPassword().equals(password)) {
                out.println("Login successful");
            } else {
                out.println("Invalid username or password");
            }
        }

        private void handleViewProfile() throws IOException {
            String username = in.readLine();
            UserProfile userProfile = userProfiles.get(username);
            if (userProfile != null) {
                out.println(userProfile.toString());
            } else {
                out.println("No profile information available for the given username.");
            }
        }

        private void handleQuizRequest() {
            // New method to handle quiz game requests
            String request = null;
            if ("QUIZ:START".equalsIgnoreCase(request)) {
                // Begin the quiz by sending questions to the client
                List<Question> selectedQuestions = selectRandomQuestions();
                for (Question question : selectedQuestions) {
                    out.println(question.getText());
                    out.println(String.join("|", question.getOptions()));
                    out.println(question.getCorrectAnswerIndex());
                }
            }
        }


        private List<Question> selectRandomQuestions() {
            Collections.shuffle(questions);
            return questions.subList(0, 3);
        }
    }

    static class UserProfile {
        private String username;
        private String password;
        private String email;
        private String phoneNumber;

        public UserProfile(String username, String password, String email, String phoneNumber) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        @Override
        public String toString() {
            return "Username: " + username + ", Email: " + email + ", Phone Number: " + phoneNumber;
        }
    }

    static class Question {
        private String text;
        private List<String> options;
        private int correctAnswerIndex;

        public Question(String text, List<String> options, int correctAnswerIndex) {
            this.text = text;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }

        public String getText() {
            return text;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }
    }
}

