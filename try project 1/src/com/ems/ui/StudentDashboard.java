package com.ems.ui;

import com.ems.model.Exam;
import com.ems.model.Result;
import com.ems.model.User;
import com.ems.service.ExamService;
import com.ems.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private User currentUser;
    private ExamService examService = new ExamService();
    private JTable examTable, resultTable;
    private DefaultTableModel examModel, resultModel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        setTitle("EMS | Student Dashboard - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(245, 246, 248));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcome = new JLabel("Welcome,");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel name = new JLabel(user.getFullName());
        name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        CustomButton refreshBtn = new CustomButton("Refresh Data");
        CustomButton logoutBtn = new CustomButton("Logout");
        logoutBtn.setBackgroundColor(new Color(220, 53, 69)); // Red for logout

        sidebar.add(welcome);
        sidebar.add(name);
        sidebar.add(new JLabel()); // Spacer
        sidebar.add(refreshBtn);
        sidebar.add(new JLabel()); // Spacer
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Main Content (Tabs)
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Available Exams Panel
        JPanel examsPanel = new JPanel(new BorderLayout());
        examsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] examCols = {"ID", "Title", "Duration", "Scheduled At"};
        examModel = new DefaultTableModel(examCols, 0);
        examTable = new JTable(examModel);
        examTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        examTable.setRowHeight(30);
        examsPanel.add(new JScrollPane(examTable), BorderLayout.CENTER);

        CustomButton startExamBtn = new CustomButton("START SELECTED EXAM");
        examsPanel.add(startExamBtn, BorderLayout.SOUTH);
        tabs.addTab("Available Exams", examsPanel);

        // Results Panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] resultCols = {"Result ID", "Exam", "Score", "Total", "Percentage", "Date"};
        resultModel = new DefaultTableModel(resultCols, 0);
        resultTable = new JTable(resultModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(30);
        resultsPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        tabs.addTab("My Results", resultsPanel);

        add(tabs, BorderLayout.CENTER);

        // Actions
        refreshBtn.addActionListener(e -> refreshData());
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        startExamBtn.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row != -1) {
                int examId = (int) examTable.getValueAt(row, 0);
                startExam(examId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an exam to start.");
            }
        });

        refreshData();
    }

    private void refreshData() {
        // Load Exams
        examModel.setRowCount(0);
        List<Exam> exams = examService.getAllExams();
        for (Exam e : exams) {
            examModel.addRow(new Object[]{e.getExamId(), e.getTitle(), e.getDurationMinutes() + " mins", e.getScheduledAt()});
        }

        // Load Results
        resultModel.setRowCount(0);
        List<Result> results = examService.getResultsByStudent(currentUser.getUserId());
        for (Result r : results) {
            resultModel.addRow(new Object[]{r.getResultId(), r.getExamTitle(), r.getScore(), r.getTotalQuestions(), r.getPercentage() + "%", r.getSubmittedAt()});
        }
    }

    private void startExam(int examId) {
        Exam selectedExam = null;
        for (Exam e : examService.getAllExams()) {
            if (e.getExamId() == examId) {
                selectedExam = e;
                break;
            }
        }
        
        if (selectedExam != null) {
            new ExamRoomFrame(currentUser, selectedExam).setVisible(true);
            this.dispose();
        }
    }
}
