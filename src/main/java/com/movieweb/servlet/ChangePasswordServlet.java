package com.movieweb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.movieweb.model.Users;
import com.movieweb.service.UpdatePassword;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private UpdatePassword updatePassword;

    @Override
    public void init() throws ServletException
    {
        updatePassword = new UpdatePassword();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Check whether a session exists
        if (session == null)
        {
            response.sendRedirect(request.getContextPath() + "/log_in.jsp");
            return;
        }

        // Get the currently logged-in user
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null)
        {
            response.sendRedirect(request.getContextPath() + "/log_in.jsp");
            return;
        }

        // Get form values
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Process password change
        String error = updatePassword.changePassword(
                currentUser,
                currentPassword,
                newPassword,
                confirmPassword
        );

        // If validation fails, return to the form
        if (error != null)
        {
            request.setAttribute("error", error);

            request.getRequestDispatcher(
                    "/change_password.jsp"
            ).forward(request, response);

            return;
        }

        // Password changed successfully
        request.setAttribute(
                "success",
                "Your password has been changed successfully."
        );

        request.getRequestDispatcher(
                "/settings.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendRedirect(
                request.getContextPath() + "/change_password.jsp"
        );
    }
}