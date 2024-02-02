import java.util.List;

class Question {
    String query;
    List<String> options;
    int correctAnswer;

    public Question(String query, List<String> options, int correctAnswer) {
        this.query = query;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

}