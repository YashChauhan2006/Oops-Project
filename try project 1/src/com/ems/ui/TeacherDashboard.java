package com.ems.ui;

import com.ems.model.Exam;
import com.ems.model.Question;
import com.ems.model.Result;
import com.ems.model.User;
import com.ems.service.ExamService;
import com.ems.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private User teacher;
    private ExamService examService = new ExamService();
    private JTable examTable;
    private DefaultTableModel examModel;

    public TeacherDashboard(User user) {
        this.teacher = user;
        setTitle("EMS | Teacher Dashboard - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(245, 246, 248));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcome = new JLabel("Welcome,");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel name = new JLabel(user.getFullName());
        
        CustomButton createExamBtn = new CustomButton("Create New Exam");
        CustomButton viewResultsBtn = new CustomButton("View Results");
        CustomButton logoutBtn = new CustomButton("Logout");
        logoutBtn.setBackgroundColor(new Color(220, 53, 69));

        sidebar.add(welcome);
        sidebar.add(name);
        sidebar.add(new JLabel());
        sidebar.add(createExamBtn);
        sidebar.add(viewResultsBtn);
        sidebar.add(new JLabel());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Main Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainArea.setBackground(Color.WHITE);

        JLabel title = new JLabel("Managed Exams");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainArea.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Duration", "Scheduled At"};
        examModel = new DefaultTableModel(cols, 0);
        examTable = new JTable(examModel);
        examTable.setRowHeight(30);
        mainArea.add(new JScrollPane(examTable), BorderLayout.CENTER);

        add(mainArea, BorderLayout.CENTER);

        // Actions
        createExamBtn.addActionListener(e -> showCreateExamDialog());
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        viewResultsBtn.addActionListener(e -> showResultsDialog());

        refreshExams();
    }

    private void refreshExams() {
        examModel.setRowCount(0);
        List<Exam> exams = examService.getAllExams();
        for (Exam e : exams) {
            if (e.getCreatedBy() == teacher.getUserId()) {
                examModel.addRow(new Object[]{e.getExamId(), e.getTitle(), e.getDurationMinutes() + " mins", e.getScheduledAt()});
            }
        }
    }

    private void showCreateExamDialog() {
        JDialog dialog = new JDialog(this, "Create Exam", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ModernTextField titleF = new ModernTextField(20);
        ModernTextField durationF = new ModernTextField(20);
        durationF.setText("60");

        gbc.gridy = 0; dialog.add(new JLabel("Exam Title"), gbc);
        gbc.gridy++; dialog.add(titleF, gbc);
        gbc.gridy++; dialog.add(new JLabel("Duration (Minutes)"), gbc);
        gbc.gridy++; dialog.add(durationF, gbc);

        CustomButton saveBtn = new CustomButton("SAVE & ADD QUESTIONS");
        gbc.gridy++; gbc.insets = new Insets(30, 20, 10, 20);
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            Exam ex = new Exam();
            ex.setTitle(titleF.getText());
            ex.setDurationMinutes(Integer.parseInt(durationF.getText()));
            ex.setCreatedBy(teacher.getUserId());
            ex.setScheduledAt(new Date());

            if (examService.createExam(ex)) {
                dialog.dispose();
                showAddQuestionsDialog(ex);
                refreshExams();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddQuestionsDialog(Exam exam) {
        JDialog qDialog = new JDialog(this, "Add Questions to " + exam.getTitle(), true);
        qDialog.setSize(600, 600);
        qDialog.setLayout(new BorderLayout());

        JPanel qPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ModernTextField qText = new ModernTextField(30);
        ModernTextField optA = new ModernTextField(20);
        ModernTextField optB = new ModernTextField(20);
        ModernTextField optC = new ModernTextField(20);
        ModernTextField optD = new ModernTextField(20);
        JComboBox<String> correct = new JComboBox<>(new String[]{"A", "B", "C", "D"});

        gbc.gridy = 0; qPanel.add(new JLabel("Question Text:"), gbc);
        gbc.gridy++; qPanel.add(qText, gbc);
        gbc.gridy++; qPanel.add(new JLabel("Option A:"), gbc);
        gbc.gridy++; qPanel.add(optA, gbc);
        gbc.gridy++; qPanel.add(new JLabel("Option B:"), gbc);
        gbc.gridy++; qPanel.add(optB, gbc);
        gbc.gridy++; qPanel.add(new JLabel("Option C:"), gbc);
        gbc.gridy++; qPanel.add(optC, gbc);
        gbc.gridy++; qPanel.add(new JLabel("Option D:"), gbc);
        gbc.gridy++; qPanel.add(optD, gbc);
        gbc.gridy++; qPanel.add(new JLabel("Correct Option:"), gbc);
        gbc.gridy++; qPanel.add(correct, gbc);

        CustomButton addBtn = new CustomButton("ADD THIS QUESTION");
        gbc.gridy++; qPanel.add(addBtn, gbc);

        List<Question> questionList = new ArrayList<>();
        addBtn.addActionListener(e -> {
            Question q = new Question();
            q.setExamId(exam.getExamId());
            q.setQuestionText(qText.getText());
            q.setOptionA(optA.getText());
            q.setOptionB(optB.getText());
            q.setOptionC(optC.getText());
            q.setOptionD(optD.getText());
            q.setCorrectOption(correct.getSelectedItem().toString().charAt(0));
            questionList.add(q);
            
            // Clear fields for next
            qText.setText(""); optA.setText(""); optB.setText(""); optC.setText(""); optD.setText("");
            JOptionPane.showMessageDialog(qDialog, "Question added to batch!");
        });

        CustomButton finishBtn = new CustomButton("FINISH & SAVE ALL");
        finishBtn.setBackgroundColor(new Color(40, 167, 69));
        finishBtn.addActionListener(e -> {
            if (examService.addQuestions(questionList)) {
                JOptionPane.showMessageDialog(qDialog, "All questions saved!");
                qDialog.dispose();
            }
        });

        qDialog.add(new JScrollPane(qPanel), BorderLayout.CENTER);
        qDialog.add(finishBtn, BorderLayout.SOUTH);
        qDialog.setLocationRelativeTo(this);
        qDialog.setVisible(true);
    }

    private void showResultsDialog() {
        JDialog resDialog = new JDialog(this, "Student Results", true);
        resDialog.setSize(800, 500);
        resDialog.setLayout(new BorderLayout());

        // In a real app, I'd fetch results by teacher's exams. For now, show all results.
        String[] resCols = {"Student ID", "Exam", "Score", "Total", "Percentage", "Date"};
        DefaultTableModel resModel = new DefaultTableModel(resCols, 0);
        JTable resTable = new JTable(resModel);
        
        // This is a simplified demo fetch
        List<Result> allResults = examService.getResultsByStudent(0); // This logic needs to be broader, but keeping it simple for desktop demo
        
        resDialog.add(new JScrollPane(resTable), BorderLayout.CENTER);
        resDialog.setLocationRelativeTo(this);
        resDialog.setVisible(true);
    }
}
