package com.movieweb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.movieweb.model.Users;
import com.movieweb.service.UserLoginValidation;

@WebServlet("/login")
public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private final UserLoginValidation userService = new UserLoginValidation();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        String login = request.getParameter("login");

        String password = request.getParameter("password");
        if (login != null)
        {
            login = login.trim();
        }
        Users user = userService.login(login, password);
        if (user == null)
        {
            request.setAttribute(
                    "error",
                    "Invalid username/email/phone or password.");
            request.setAttribute(
                    "login",
                    login);
            request.getRequestDispatcher(
                    "/log_in.jsp")
                    .forward(request, response);
            return;
        }

        //Login successful.
        HttpSession session = request.getSession();
        session.setAttribute(
                "user",
                user);
        response.sendRedirect(
                request.getContextPath()
                + "/main.jsp");
    }
}