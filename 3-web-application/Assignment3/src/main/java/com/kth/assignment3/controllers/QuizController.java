/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kth.assignment3.controllers;

import com.kth.assignment3.models.QuestionBean;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author chris
 */
@WebServlet(name = "QuizServlet")
public class QuizController extends HttpServlet {
    
    private static final String QUIZ_ENDPOINT = "/";
    private static final String ADMIN_ENDPOINT = "/admin";
    private static final String ADD_QUIZ_ENDPOINT = "/add-quiz";
    private static final String RESET_QUIZ_ENDPOINT = "/reset-quiz";
    
    private static final EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("com.kth_Assignment3_war_1.0PU");
    
    @PersistenceContext
    private final EntityManager entityManager = emFactory.createEntityManager();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if(!handleAuthenticate(request, response)) {
            return;
        }
        
        switch(request.getServletPath()) {
            case ADMIN_ENDPOINT:
                handleAdmin(request, response);
                break;
            case QUIZ_ENDPOINT:
                handleQuiz(request, response);
                break;
            default:
                processRequest(request, response);
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if(!handleAuthenticate(request, response)) {
            return;
        }
        
        switch(request.getServletPath()) {
            case ADD_QUIZ_ENDPOINT:
                handleAddQuiz(request, response);
                break;
            case QUIZ_ENDPOINT:
                handleCheckQuiz(request, response);
                break;
            case RESET_QUIZ_ENDPOINT:
                handleResetQuiz(request, response);
                break;
            default:
                processRequest(request, response);
        }
    }
    
    
    // Request handlers
    
    private boolean handleAuthenticate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if(session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }
    
    private void handleAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    private void handleAddQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String question = request.getParameter("question");
        String a = request.getParameter("1");
        String b = request.getParameter("2");
        String c = request.getParameter("3");
        String d = request.getParameter("4");
        int correct = Integer.parseInt(request.getParameter("correct"));
        
        QuestionBean newQuestion = new QuestionBean();
        
        String[] answers = {a, b, c, d};
        entityManager.getTransaction().begin();
        newQuestion.setQuestion(question);
        newQuestion.setAnswers(answers);
        newQuestion.setCorrect(correct);
        entityManager.persist(newQuestion);
        entityManager.getTransaction().commit();
        
        String message = "Quiz added!";
        request.setAttribute("message", message);
        request.getRequestDispatcher("/admin.jsp").forward(request, response);
    }
    
    private void handleQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        int currentIndex = 0;
        
        Object storedIndex = session.getAttribute("quizIndex");
        
        if(storedIndex != null) {
            currentIndex = (int)storedIndex;
        } else {
            session.setAttribute("quizIndex", currentIndex);
            session.setAttribute("score", 0);
        }
       
       
        List<QuestionBean> allBeans = entityManager.createNamedQuery("QuestionBean.findAll").getResultList();
        
        if(currentIndex >= allBeans.size()) {
            request.setAttribute("correct", session.getAttribute("score"));
            request.getRequestDispatcher("/done.jsp").forward(request, response);
        } else {
            QuestionBean currentQuestion = allBeans.get(currentIndex);
            request.setAttribute("question", currentQuestion.getQuestion());
            String[] answers = currentQuestion.getAnswers();
            request.setAttribute("a", answers[0]);
            request.setAttribute("b", answers[1]);
            request.setAttribute("c", answers[2]);
            request.setAttribute("d", answers[3]);
            request.setAttribute("current", currentIndex + 1);
            request.getRequestDispatcher("/quiz.jsp").forward(request, response);
        }
    }
    private void handleCheckQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        int currentIndex = (int)session.getAttribute("quizIndex");
        
        List<QuestionBean> allBeans = entityManager.createNamedQuery("QuestionBean.findAll").getResultList();
        
        QuestionBean currentBean = allBeans.get(currentIndex);
        
        int userAnswer = Integer.parseInt(request.getParameter("answer"));
        
        if(userAnswer == currentBean.getCorrect()) {
            int currentScore = (int)session.getAttribute("score");
            session.setAttribute("score", ++currentScore);
        }
        session.setAttribute("quizIndex", ++currentIndex);
        
        response.sendRedirect(request.getContextPath() + "/quiz");
    }
    
    private void handleResetQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        session.setAttribute("quizIndex", 0);
        session.setAttribute("score", 0);
        
        response.sendRedirect(request.getContextPath() + "/quiz");
    }
}
