package com.ems.service;

import com.ems.db.DBConnection;
import com.ems.model.Exam;
import com.ems.model.Question;
import com.ems.model.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamService {

    // --- Exam Operations ---
    public boolean createExam(Exam exam) {
        String query = "INSERT INTO exams (title, description, duration_minutes, scheduled_at, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, exam.getTitle());
            stmt.setString(2, exam.getDescription());
            stmt.setInt(3, exam.getDurationMinutes());
            stmt.setTimestamp(4, new Timestamp(exam.getScheduledAt().getTime()));
            stmt.setInt(5, exam.getCreatedBy());
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) exam.setExamId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT * FROM exams ORDER BY scheduled_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Exam exam = new Exam();
                exam.setExamId(rs.getInt("exam_id"));
                exam.setTitle(rs.getString("title"));
                exam.setDescription(rs.getString("description"));
                exam.setDurationMinutes(rs.getInt("duration_minutes"));
                exam.setScheduledAt(rs.getTimestamp("scheduled_at"));
                exam.setCreatedBy(rs.getInt("created_by"));
                exams.add(exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    // --- Question Operations ---
    public boolean addQuestions(List<Question> questions) {
        String query = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            for (Question q : questions) {
                stmt.setInt(1, q.getExamId());
                stmt.setString(2, q.getQuestionText());
                stmt.setString(3, q.getOptionA());
                stmt.setString(4, q.getOptionB());
                stmt.setString(5, q.getOptionC());
                stmt.setString(6, q.getOptionD());
                stmt.setString(7, String.valueOf(q.getCorrectOption()));
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Question> getQuestionsForExam(int examId) {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setQuestionId(rs.getInt("question_id"));
                q.setExamId(rs.getInt("exam_id"));
                q.setQuestionText(rs.getString("question_text"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setCorrectOption(rs.getString("correct_option").charAt(0));
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    // --- Result Operations ---
    public boolean saveResult(Result result) {
        String query = "INSERT INTO results (student_id, exam_id, score, total_questions, percentage) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, result.getStudentId());
            stmt.setInt(2, result.getExamId());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalQuestions());
            stmt.setFloat(5, result.getPercentage());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Result> getResultsByStudent(int studentId) {
        List<Result> results = new ArrayList<>();
        String query = "SELECT r.*, e.title as exam_title FROM results r JOIN exams e ON r.exam_id = e.exam_id WHERE r.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Result r = new Result();
                r.setResultId(rs.getInt("result_id"));
                r.setExamTitle(rs.getString("exam_title"));
                r.setScore(rs.getInt("score"));
                r.setTotalQuestions(rs.getInt("total_questions"));
                r.setPercentage(rs.getFloat("percentage"));
                r.setSubmittedAt(rs.getTimestamp("submitted_at"));
                results.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
