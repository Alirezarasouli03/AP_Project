import java.util.HashSet;
import java.util.Set;

public class Player {
    private String name;
    private int score;
    private Set<Integer> answeredQuestions;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.answeredQuestions = new HashSet<>();
    }
}
/*
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Set<Integer> getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(Set<Integer> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }
}*/
