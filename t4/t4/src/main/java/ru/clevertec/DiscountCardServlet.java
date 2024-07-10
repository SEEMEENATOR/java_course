package ru.clevertec;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/discountcards")
public class DiscountCardServlet extends HttpServlet {

    private DiscountCardDAO discountCardDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        String url = System.getProperty("datasource.url");
        String username = System.getProperty("datasource.username");
        String password = System.getProperty("datasource.password");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            discountCardDAO = new DiscountCardDAO(connection);
        } catch (SQLException e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(request.getInputStream(), Map.class);

        String number = (String) data.get("discountCard");
        int discountPercentage = (int) data.get("discountAmount");

        DiscountCard newDiscountCard = new DiscountCard(0, number, discountPercentage);

        try {
            discountCardDAO.addDiscountCard(newDiscountCard);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Discount card added successfully with ID: " + newDiscountCard.getId());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to add discount card: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int discountCardId = Integer.parseInt(idParam);
                DiscountCard discountCard = discountCardDAO.getDiscountCardById(discountCardId);
                if (discountCard != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonDiscountCard = mapper.writeValueAsString(discountCard);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonDiscountCard);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Discount card not found with id: " + discountCardId);
                }
            } catch (NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid discount card ID: " + idParam);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Discount card ID is required");
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int discountCardId = Integer.parseInt(idParam);
                DiscountCard existingCard = discountCardDAO.getDiscountCardById(discountCardId);
                if (existingCard != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> data = mapper.readValue(request.getInputStream(), Map.class);

                    String number = (String) data.get("discountCard");
                    int discountPercentage = (int) data.get("discountAmount");

                    existingCard.setNumber(number);
                    existingCard.setDiscountPercentage(discountPercentage);

                    discountCardDAO.updateDiscountCard(existingCard);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Discount card updated successfully");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Discount card not found with id: " + discountCardId);
                }
            } catch (NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid discount card ID: " + idParam);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Discount card ID is required");
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int discountCardId = Integer.parseInt(idParam);
                discountCardDAO.deleteDiscountCardById(discountCardId);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Discount Card deleted successfully");
            } catch (NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid discount card ID: " + idParam);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Discount Card ID is required");
        }
    }
    @Override
    public void destroy() {
        super.destroy();
        try {
            if (discountCardDAO != null) {
                discountCardDAO.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
