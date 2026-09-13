package com.movieweb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession(false);

        // If a session exists, remove the logged-in user
        if (session != null)
        {
            session.removeAttribute("user");
        }
        // Return to main page
        response.sendRedirect(
                request.getContextPath()
                + "/main.jsp");
    }
}