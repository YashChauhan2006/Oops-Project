package com.ems.ui;

import com.ems.model.Exam;
import com.ems.model.Question;
import com.ems.model.Result;
import com.ems.model.User;
import com.ems.service.ExamService;
import com.ems.ui.components.CustomButton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ExamRoomFrame extends JFrame {
    private User student;
    private Exam exam;
    private List<Question> questions;
    private Map<Integer, Character> answers = new HashMap<>(); // Question ID -> Selected Option
    private int currentIdx = 0;
    private int timeLeftSeconds;
    private Timer timer;
    private ExamService examService = new ExamService();

    private JLabel timerLabel, questionLabel, totalQuestionsLabel;
    private JRadioButton optA, optB, optC, optD;
    private ButtonGroup optionsGroup;
    private CustomButton prevBtn, nextBtn, submitBtn;

    public ExamRoomFrame(User student, Exam exam) {
        this.student = student;
        this.exam = exam;
        this.questions = examService.getQuestionsForExam(exam.getExamId());
        this.timeLeftSeconds = exam.getDurationMinutes() * 60;

        setTitle("Exam: " + exam.getTitle());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        header.setBackground(new Color(0, 86, 179));
        
        JLabel title = new JLabel(exam.getTitle());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        timerLabel = new JLabel("Time Remaining: --:--");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(timerLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        totalQuestionsLabel = new JLabel("Question 1 of " + questions.size());
        gbc.gridy = 0;
        content.add(totalQuestionsLabel, gbc);

        questionLabel = new JLabel("Question text goes here...");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridy = 1;
        content.add(questionLabel, gbc);

        // Options
        optionsGroup = new ButtonGroup();
        optA = new JRadioButton("Option A");
        optB = new JRadioButton("Option B");
        optC = new JRadioButton("Option C");
        optD = new JRadioButton("Option D");

        JRadioButton[] ops = {optA, optB, optC, optD};
        for (int i = 0; i < ops.length; i++) {
            ops[i].setFont(new Font("Segoe UI", Font.PLAIN, 16));
            ops[i].setBackground(Color.WHITE);
            optionsGroup.add(ops[i]);
            gbc.gridy++;
            content.add(ops[i], gbc);
        }

        add(content, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        footer.setBackground(Color.WHITE);

        prevBtn = new CustomButton("Previous");
        nextBtn = new CustomButton("Next");
        submitBtn = new CustomButton("SUBMIT EXAM");
        submitBtn.setBackgroundColor(new Color(40, 167, 69)); // Green for submit

        footer.add(prevBtn);
        footer.add(nextBtn);
        footer.add(submitBtn);
        add(footer, BorderLayout.SOUTH);

        // Actions
        prevBtn.addActionListener(e -> navigate(-1));
        nextBtn.addActionListener(e -> navigate(1));
        submitBtn.addActionListener(e -> confirmSubmit());

        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found for this exam.", "No Questions", JOptionPane.WARNING_MESSAGE);
            new StudentDashboard(student).setVisible(true);
            this.dispose();
        } else {
            showQuestion(0);
            startTimer();
        }
    }

    private void showQuestion(int index) {
        saveCurrentAnswer();
        currentIdx = index;
        Question q = questions.get(index);
        totalQuestionsLabel.setText("Question " + (index + 1) + " of " + questions.size());
        questionLabel.setText("<html><body style='width: 500px'>" + q.getQuestionText() + "</body></html>");
        optA.setText(q.getOptionA());
        optB.setText(q.getOptionB());
        optC.setText(q.getOptionC());
        optD.setText(q.getOptionD());

        optionsGroup.clearSelection();
        if (answers.containsKey(q.getQuestionId())) {
            char sel = answers.get(q.getQuestionId());
            switch(sel) {
                case 'A': optA.setSelected(true); break;
                case 'B': optB.setSelected(true); break;
                case 'C': optC.setSelected(true); break;
                case 'D': optD.setSelected(true); break;
            }
        }

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);
    }

    private void saveCurrentAnswer() {
        if (currentIdx >= 0 && currentIdx < questions.size()) {
            Question q = questions.get(currentIdx);
            if (optA.isSelected()) answers.put(q.getQuestionId(), 'A');
            else if (optB.isSelected()) answers.put(q.getQuestionId(), 'B');
            else if (optC.isSelected()) answers.put(q.getQuestionId(), 'C');
            else if (optD.isSelected()) answers.put(q.getQuestionId(), 'D');
        }
    }

    private void navigate(int delta) {
        showQuestion(currentIdx + delta);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeLeftSeconds > 0) {
                    timeLeftSeconds--;
                    updateTimerDisplay();
                } else {
                    timer.cancel();
                    autoSubmit();
                }
            }
        }, 1000, 1000);
    }

    private void updateTimerDisplay() {
        int m = timeLeftSeconds / 60;
        int s = timeLeftSeconds % 60;
        timerLabel.setText(String.format("Time Remaining: %02d:%02d", m, s));
    }

    private void autoSubmit() {
        JOptionPane.showMessageDialog(this, "Time is up! Your exam will be submitted automatically.");
        submitExam();
    }

    private void confirmSubmit() {
        int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to submit the exam?", "Submit Confirmation", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            submitExam();
        }
    }

    private void submitExam() {
        saveCurrentAnswer();
        timer.cancel();
        
        int score = 0;
        for (Question q : questions) {
            Character selected = answers.get(q.getQuestionId());
            if (selected != null && selected == q.getCorrectOption()) {
                score++;
            }
        }

        Result result = new Result();
        result.setStudentId(student.getUserId());
        result.setExamId(exam.getExamId());
        result.setScore(score);
        result.setTotalQuestions(questions.size());
        result.setPercentage((float) (score * 100) / questions.size());

        if (examService.saveResult(result)) {
            JOptionPane.showMessageDialog(this, "Exam submitted successfully! Score: " + score + "/" + questions.size());
        }

        new StudentDashboard(student).setVisible(true);
        this.dispose();
    }
}
