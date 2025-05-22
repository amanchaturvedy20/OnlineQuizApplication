import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Quiz extends JFrame implements ActionListener {
    JLabel questionLabel, questionTimerLabel, quizTimerLabel;
    JRadioButton[] options = new JRadioButton[4];
    JButton nextButton;
    ButtonGroup bg;
    java.util.Timer questionTimer;
    java.util.Timer quizTimer;
    int currentQuestionIndex = 0, score = 0;
    int questionTimeLeft = 20;
    int quizTimeLeft = 120;

    java.util.List<String> questions = new ArrayList<>();
    java.util.List<String[]> optionsList = new ArrayList<>();
    java.util.List<String> answers = new ArrayList<>();
    java.util.List<String> userAnswers = new ArrayList<>();

    public Quiz() {
        setTitle("Quiz Application");
        setSize(700, 500);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        questionLabel = new JLabel();
        questionLabel.setBounds(50, 30, 600, 40);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(questionLabel);

        questionTimerLabel = new JLabel("Question Time Left: 20s");
        questionTimerLabel.setBounds(50, 10, 200, 20);
        questionTimerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(questionTimerLabel);

        quizTimerLabel = new JLabel("Quiz Time Left: 120s");
        quizTimerLabel.setBounds(500, 10, 200, 20);
        quizTimerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(quizTimerLabel);

        bg = new ButtonGroup();
        int y = 100;
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setBounds(50, y, 600, 30);
            bg.add(options[i]);
            add(options[i]);
            y += 40;
        }

        nextButton = new JButton("Next");
        nextButton.setBounds(250, 360, 200, 40);
        nextButton.addActionListener(this);
        add(nextButton);

        loadQuestions();
        randomizeQuestions();
        displayQuestion();
        startQuestionTimer();
        startQuizTimer();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    void loadQuestions() {
        questions.add("Which is not a Java feature?");
        optionsList.add(new String[]{"Object-Oriented", "Use of pointers", "Portable", "Dynamic"});
        answers.add("Use of pointers");

        questions.add("Which company developed Java?");
        optionsList.add(new String[]{"Sun Microsystems", "Oracle", "Microsoft", "Apple"});
        answers.add("Sun Microsystems");

        questions.add("Which keyword is used for inheritance?");
        optionsList.add(new String[]{"extends", "implement", "inherits", "super"});
        answers.add("extends");

        questions.add("Which method is the entry point for Java programs?");
        optionsList.add(new String[]{"start()", "main()", "run()", "init()"});
        answers.add("main()");
    }

    void randomizeQuestions() {
        java.util.List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) indices.add(i);
        Collections.shuffle(indices);

        java.util.List<String> shuffledQuestions = new ArrayList<>();
        java.util.List<String[]> shuffledOptions = new ArrayList<>();
        java.util.List<String> shuffledAnswers = new ArrayList<>();

        for (int index : indices) {
            shuffledQuestions.add(questions.get(index));
            String correctAnswer = answers.get(index);

            String[] originalOptions = optionsList.get(index).clone();
            java.util.List<String> shuffledOptList = new ArrayList<>(Arrays.asList(originalOptions));
            Collections.shuffle(shuffledOptList);
            shuffledOptions.add(shuffledOptList.toArray(new String[0]));

            for (String opt : shuffledOptList) {
                if (opt.equals(correctAnswer)) {
                    shuffledAnswers.add(opt);
                    break;
                }
            }
        }

        questions = shuffledQuestions;
        optionsList = shuffledOptions;
        answers = shuffledAnswers;
    }

    void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        questionLabel.setText("Q" + (currentQuestionIndex + 1) + ": " + questions.get(currentQuestionIndex));
        String[] currentOptions = optionsList.get(currentQuestionIndex);
        for (int i = 0; i < 4; i++) {
            options[i].setText(currentOptions[i]);
        }
        bg.clearSelection();
        resetQuestionTimer();
    }

    void startQuestionTimer() {
        questionTimer = new java.util.Timer();
        questionTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                questionTimeLeft--;
                SwingUtilities.invokeLater(() ->
                        questionTimerLabel.setText("Question Time Left: " + questionTimeLeft + "s"));
                if (questionTimeLeft == 0) {
                    questionTimer.cancel();
                    userAnswers.add("No Answer");
                    currentQuestionIndex++;
                    SwingUtilities.invokeLater(() -> displayQuestion());
                }
            }
        }, 1000, 1000);
    }

    void resetQuestionTimer() {
        if (questionTimer != null) questionTimer.cancel();
        questionTimeLeft = 20;
        startQuestionTimer();
    }

    void startQuizTimer() {
        quizTimer = new java.util.Timer();
        quizTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                quizTimeLeft--;
                SwingUtilities.invokeLater(() ->
                        quizTimerLabel.setText("Quiz Time Left: " + quizTimeLeft + "s"));
                if (quizTimeLeft == 0) {
                    quizTimer.cancel();
                    if (questionTimer != null) questionTimer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Total Quiz Time Up! Auto-submitting...");
                        finishQuiz();
                    });
                }
            }
        }, 1000, 1000);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            if (questionTimer != null) questionTimer.cancel();
            saveUserAnswer();
            currentQuestionIndex++;
            displayQuestion();
        }
    }

    void saveUserAnswer() {
        boolean answered = false;
        for (JRadioButton option : options) {
            if (option.isSelected()) {
                String answer = option.getText();
                userAnswers.add(answer);
                if (currentQuestionIndex < answers.size() && answer.equals(answers.get(currentQuestionIndex))) {
                    score++;
                }
                answered = true;
                break;
            }
        }
        if (!answered) {
            userAnswers.add("No Answer");
        }
    }

    void finishQuiz() {
        if (questionTimer != null) questionTimer.cancel();
        if (quizTimer != null) quizTimer.cancel();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Quiz Over! Your Score: " + score + "/" + questions.size());
            saveScore();
            showDetailedResults();
            showLeaderboard();
            System.exit(0);
        });
    }

    void saveScore() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("scores.txt", true))) {
            bw.write(Login.currentUser + ": " + score);
            bw.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void showDetailedResults() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            result.append("Q").append(i + 1).append(": ").append(questions.get(i)).append("\n");
            result.append("Your Answer: ").append(userAnswers.get(i)).append("\n");
            result.append("Correct Answer: ").append(answers.get(i)).append("\n\n");
        }
        JTextArea textArea = new JTextArea(result.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Detailed Results", JOptionPane.INFORMATION_MESSAGE);
    }

    void showLeaderboard() {
        try (BufferedReader br = new BufferedReader(new FileReader("scores.txt"))) {
            String line;
            java.util.List<String> entries = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                entries.add(line);
            }

            entries.sort((a, b) -> {
                int scoreA = Integer.parseInt(a.split(": ")[1]);
                int scoreB = Integer.parseInt(b.split(": ")[1]);
                return Integer.compare(scoreB, scoreA);
            });

            StringBuilder leaderboard = new StringBuilder("Leaderboard:\n");
            int rank = 1;
            for (String entry : entries) {
                leaderboard.append(rank++).append(". ").append(entry).append("\n");
            }

            JOptionPane.showMessageDialog(this, leaderboard.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Quiz();
    }
}

class Login {
    static String currentUser = "Guest";
}


