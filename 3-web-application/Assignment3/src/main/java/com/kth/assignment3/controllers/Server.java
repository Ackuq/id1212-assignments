/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kth.assignment3.controllers;

import com.kth.assignment3.models.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import javax.persistence.*;

/**
 *
 * @author axel
 */
@WebServlet(name = "HttpServlet", urlPatterns = {"/HttpServlet", "/login", "/"})
public class Server extends HttpServlet {
    
    private static final String LOGIN_ENDPOINT = "/login";
       
    private static final EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("com.kth_Assignment3_war_1.0PU");
    
    @PersistenceContext
    private final EntityManager entityManager = emFactory.createEntityManager();
   
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        ServletContext sc = getServletContext();
        
        if(sc.getAttribute("quiz") == null) {
            sc.setAttribute("quiz", new QuizBean());
        }
        
        HttpSession session = request.getSession(true);
        
        if(session.isNew()) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Server methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
   
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch(request.getServletPath()){
            case LOGIN_ENDPOINT:
                handleLogin(request, response);
                break;
            default:
                handle404(request, response);
        }
    }
    
    /**
     * 
     * Handlers for specific endpoints
     * 
     */

    protected void handle404(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setStatus(404);
    }
    
    protected void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        UserBean user = entityManager.find(UserBean.class, email);
        
        HttpSession session = request.getSession(true);
        
        if(user == null) {
            UserBean newUser = new UserBean();
            entityManager.getTransaction().begin();
            newUser.setEmail(email);
            newUser.setPassword(password);
            entityManager.persist(newUser);
            entityManager.getTransaction().commit();
            session.setAttribute("user", newUser);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else if(!user.getPassword().equals(password)) {
            String message  = "Wrong credentials";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } else if(user.getUsername() == null) {
            session.setAttribute("user", user);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else {
            session.setAttribute("user", user);
            request.getRequestDispatcher("/quiz.jsp").forward(request, response);
        }
    }
    
    
    /**
     * 
     * Utility functions
     * 
     */
    
    private boolean checkUser(String username) {
        return false;
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
