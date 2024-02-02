import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;
    private List<Question> questions;

    public ClientHandler(Socket socket, List<Question> questions) {
        this.clientSocket = socket;
        this.questions = questions;
    }

    @Override
    public void run() {
        try {
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            int score = 0;
            for (Question question : questions) {
                dout.writeUTF("Question: " + question.query);
                int optionIndex = 1;
                for (String option : question.options) {
                    dout.writeUTF(optionIndex++ + ": " + option);
                }
                dout.flush();

                int answerIndex = din.readInt(); // Expecting client to send the option index as their answer
                if (answerIndex == question.correctAnswer + 1) { // Correct answer (adjusting for index starting at 1 on client side)
                    score++;
                    dout.writeUTF("Correct!");
                } else {
                    dout.writeUTF("Incorrect. The correct answer was " + (question.correctAnswer + 1));
                }
                dout.flush();
            }

            dout.writeUTF("Game over. Your score: " + score);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (din != null) din.close();
                if (dout != null) dout.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
