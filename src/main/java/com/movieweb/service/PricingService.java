package com.movieweb.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import com.movieweb.DAO.RoomsDAO;
import com.movieweb.DAO.Showtime_ticket_typesDAO;
import com.movieweb.DAO.Ticket_typesDAO;
import com.movieweb.model.*;

public class PricingService {
    private static final int NORMAL_TICKET_TYPE_ID = 1;
    private static final int WEEKDAY_PERCENT = -20;
    private static final int OFF_HOUR_PERCENT = -20;
    private static final LocalTime EARLY_LIMIT = LocalTime.of(7, 0);   // <= 07:00
    private static final LocalTime LATE_LIMIT = LocalTime.of(21, 0);   // >= 21:00

    private final RoomsDAO roomsDAO = new RoomsDAO();
    private final Showtime_ticket_typesDAO showtimeTicketTypesDAO = new Showtime_ticket_typesDAO();
    private final Ticket_typesDAO ticketTypesDAO = new Ticket_typesDAO();

    // Fills seat.price for every seat; returns false if pricing data is missing
    public boolean fillSeatPrices(Showtimes showtime, List<Seats> seats) {
        if (showtime == null || seats == null) {
            return false;
        }

        int basePrice = getNormalBasePrice(showtime);
        if (basePrice < 0) {
            return false;
        }

        Rooms room = roomsDAO.getRoomById(showtime.getRoom_id());
        if (room == null || room.getRoomType() == null) {
            return false;
        }

        int fixedPercent = getFixedPercent(showtime, room);
        for (Seats seat : seats) {
            if (seat == null) {
                continue;
            }

            int totalPercent = fixedPercent + parsePercent(seat.getSeat_price_modify());
            seat.setPrice(calcPrice(basePrice, totalPercent));
        }

        return true;
    }

    public int calculatePrice(Showtimes showtime, Seats seat, int ticketTypeId) {
        return calculatePrice(showtime, seat, ticketTypeId, null);
    }

    public int calculatePrice(Showtimes showtime, Seats seat, int ticketTypeId, Promotions promotion) {
        if (showtime == null || seat == null) {
            return -1;
        }

        int basePrice = getNormalBasePrice(showtime);
        if (basePrice < 0) {
            return -1;
        }

        Rooms room = roomsDAO.getRoomById(showtime.getRoom_id());
        if (room == null || room.getRoomType() == null) {
            return -1;
        }

        Ticket_types ticketType = ticketTypesDAO.getTicketTypeById(ticketTypeId);
        if (ticketType == null) {
            return -1;
        }

        int totalPercent = getFixedPercent(showtime, room);

        // Seat type modifier
        totalPercent += parsePercent(seat.getSeat_price_modify());

        // Ticket type modifier
        totalPercent += parsePercent(ticketType.getPrice_modify());

        // Promotion can be either percentage or fixed amount.
        int fixedPromotionAmount = 0;
        if (promotion != null) {
            String promotionModify = promotion.getPrice_modify();
            if (isPercentageModify(promotionModify)) {
                totalPercent += parsePercent(promotionModify);
            } else {
                fixedPromotionAmount = parseFixedAmount(promotionModify);
            }
        }

        // Apply all percentage modifiers at once.
        int price = calcPrice(basePrice, totalPercent);

        // Apply fixed promotion after percentage calculation.
        price += fixedPromotionAmount;
        return Math.max(price, 0);
    }

    private int getNormalBasePrice(Showtimes showtime) {
        if (showtime == null) {
            return -1;
        }

        Showtime_ticket_types normal = showtimeTicketTypesDAO.getShowtimeTicketType(showtime.getShowtime_id(), NORMAL_TICKET_TYPE_ID);
        if (normal == null) {
            return -1;
        }
        return normal.getPrice();
    }

    private int getFixedPercent(Showtimes showtime, Rooms room) {
        return parsePercent(room.getRoomType().getPrice_modify())
                + getWeekdayPercent(showtime)
                + getHourPercent(showtime);
    }

    private int getWeekdayPercent(Showtimes showtime) {
        if (showtime == null || showtime.getStart_at() == null) {
            return 0;
        }

        DayOfWeek day = showtime.getStart_at().toLocalDateTime().getDayOfWeek();
        boolean weekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;

        return weekday ? WEEKDAY_PERCENT : 0;
    }

    private int getHourPercent(Showtimes showtime) {
        if (showtime == null || showtime.getStart_at() == null) {
            return 0;
        }

        LocalTime time = showtime.getStart_at().toLocalDateTime().toLocalTime();
        boolean offHour = !time.isAfter(EARLY_LIMIT) || !time.isBefore(LATE_LIMIT);

        return offHour ? OFF_HOUR_PERCENT : 0;
    }

    public static int parsePercent(String modify) {
        if (modify == null) {
            return 0;
        }

        String s = modify.trim().replace("%", "").replace("+", "");

        if (s.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int parseFixedAmount(String modify) {
        if (modify == null) {
            return 0;
        }

        String s = modify.trim();
        if (s.isEmpty() || s.contains("%")) {
            return 0;
        }

        try {
            long value = Long.parseLong(s.replace("+", ""));
            value *= 1000L;

            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }

            if (value < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }

            return (int) value;
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isPercentageModify(String modify) {
        if (modify == null) {
            return false;
        }
        return modify.trim().endsWith("%");
    }

    public static int calcPrice(
            int basePrice,
            int totalPercent) {

        long price = Math.round(basePrice * (100 + totalPercent) / 100.0);
        return (int) Math.max(price, 0);
    }
}