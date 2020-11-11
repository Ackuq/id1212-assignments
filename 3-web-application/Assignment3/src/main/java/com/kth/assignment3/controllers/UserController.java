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

import javax.persistence.*;

@WebServlet(name = "HttpServlet")
public class UserController extends HttpServlet {
    
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String LOGOUT_ENDPOINT = "/logout";
    private static final String REGISTER_ENDPOINT = "/register";
       
    private static final EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("com.kth_Assignment3_war_1.0PU");
    
    @PersistenceContext
    private final EntityManager entityManager = emFactory.createEntityManager();
   
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        
        if(session.isNew()) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            response.sendRedirect("/Assignment3/quiz/");
        }
    }
    
  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch(request.getServletPath()){
            case LOGIN_ENDPOINT:
                handleLogin(request, response);
                break;
            case REGISTER_ENDPOINT:
                handleRegister(request, response);
                break;
            case LOGOUT_ENDPOINT:
                handleLogout(request, response);
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

    protected void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        
        HttpSession session = request.getSession(false);
        
        
        if(session == null) {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else if(username.equals("") || username == null) {
            String message  = "Username required";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else {
            String email = (String)session.getAttribute("email");
            String password = (String)session.getAttribute("password");
            
            UserBean newUser = new UserBean();
            entityManager.getTransaction().begin();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUsername(username);
            entityManager.persist(newUser);
            entityManager.getTransaction().commit();
            
            session.setAttribute("user", newUser);
            response.sendRedirect(request.getContextPath() + "/");
        }
        
    }
    
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
            session.setAttribute("password", password);
            session.setAttribute("email", email);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else if(!user.getPassword().equals(password)) {
            String message  = "Wrong credentials";
            request.setAttribute("message", message);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            session.setAttribute("user", user);
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
    
    protected void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if(session != null) {
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
