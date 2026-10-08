package com.movieweb.servlet;

import java.io.IOException;

import com.movieweb.model.Users;
import com.movieweb.service.ManagerTheaterRequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/manager-request-theater")
public class ManagerRequestTheaterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ManagerTheaterRequestService requestService = new ManagerTheaterRequestService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Users currentUser = (session == null) ? null : (Users) session.getAttribute("user");
        if (currentUser == null || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/main.jsp");
            return;
        }

        String error = requestService.requestTheater(currentUser,
                request.getParameter("theaterId"), session);

        if (error == null) {
            session.setAttribute("theaterRequestSuccess",
                    "Your request has been sent to the administrator.");
        }
        else session.setAttribute("theaterRequestError", error);
        response.sendRedirect(request.getContextPath() + "/manager-theaters");
    }
}