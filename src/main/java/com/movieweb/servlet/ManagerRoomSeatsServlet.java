package com.movieweb.servlet;

import java.io.IOException;

import com.movieweb.model.Rooms;
import com.movieweb.model.Users;
import com.movieweb.service.ManagerRoomSeatsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/manager-room-seats")
public class ManagerRoomSeatsServlet extends HttpServlet {
    private final ManagerRoomSeatsService service = new ManagerRoomSeatsService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Users currentUser = (session == null) ? null : (Users) session.getAttribute("user");
        if (currentUser == null || !"MANAGER".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/main.jsp");
            return;
        }

        int roomId;
        try {
            roomId = Integer.parseInt(request.getParameter("roomId"));
        }
        catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager-theaters");
            return;
        }

        Rooms room = service.getManagedRoom(currentUser.getUserId(), roomId);
        if (room == null) {
            response.sendRedirect(request.getContextPath() + "/manager-theaters");
            return;
        }

        request.setAttribute("room", room);
        request.setAttribute("theater", service.getTheater(currentUser.getUserId(), room.getTheater_id()));
        request.setAttribute("seats", service.getSeats(roomId));
        request.setAttribute("seatTypes", service.getSeatTypes());

        String seatMessage = (String) session.getAttribute("seatMessage");
        String seatError = (String) session.getAttribute("seatError");
        if (seatMessage != null) {
            request.setAttribute("seatMessage", seatMessage);
            session.removeAttribute("seatMessage");
        }
        if (seatError != null) {
            request.setAttribute("seatError", seatError);
            session.removeAttribute("seatError");
        }
        request.getRequestDispatcher("/manager_room_seats.jsp").forward(request, response);
    }
}