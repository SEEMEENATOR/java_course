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

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        String url = System.getProperty("datasource.url");
        String username = System.getProperty("datasource.username");
        String password = System.getProperty("datasource.password");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            productDAO = new ProductDAO(connection);
        } catch (SQLException e) {
            throw new ServletException("Error initializing servlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int productId = Integer.parseInt(idParam);
                Product product = productDAO.getProductById(productId);
                if (product != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonProduct = mapper.writeValueAsString(product);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonProduct);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Product not found with id: " + productId);
                }
            } catch (NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid product ID: " + idParam);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Product ID is required");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int productId = Integer.parseInt(idParam);
                Product existingProduct = productDAO.getProductById(productId);
                if (existingProduct != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> data = mapper.readValue(request.getInputStream(), Map.class);

                    String description = (String) data.get("description");
                    double price = (double) data.get("price");
                    int quantity = (int) data.get("quantity");
                    boolean isWholesale = (boolean) data.get("isWholesale");

                    existingProduct.setDescription(description);
                    existingProduct.setPrice(price);
                    existingProduct.setQuantityInStock(quantity);
                    existingProduct.setWholesale(isWholesale);

                    productDAO.updateProduct(existingProduct);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Product updated successfully");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Product not found with id: " + productId);
                }
            } catch (NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid product ID: " + idParam);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Product ID is required");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int productId = Integer.parseInt(idParam);
                Product existingProduct = productDAO.getProductById(productId);
                if (existingProduct != null) {
                    productDAO.deleteProduct(productId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Product deleted successfully");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Product not found with id: " + productId);
                }
            } catch (NumberFormatException | SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid product ID: " + idParam);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Product ID is required");
        }
    }
    @Override
    public void destroy() {
        super.destroy();
        try {
            productDAO.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
