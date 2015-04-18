/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hintsfromthecrowd.controllers;

import hintsfromthecrowd.engines.CrowdHintsEngine;
import hintsfromthecrowd.engines.CustomizedBM25Engine;
import hintsfromthecrowd.models.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author danh
 */
@WebServlet(name = "ServiceController", urlPatterns = {"/service"})
public class ServiceController extends HttpServlet {

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

        String engine = request.getParameter("engine");
        String query = request.getParameter("query");
        RequestDispatcher rd;
        List<Product> result;

        try {
            if (engine == null || query == null) {
                response.sendRedirect("./404");
                return;
            }

            switch (engine) {
                case "crowdhints":
                    //get result
                    result = new CrowdHintsEngine().run(query);

                    request.setAttribute("products", result);

                    rd = request.getRequestDispatcher("productTable.jsp");
                    rd.forward(request, response);
                    break;
                case "bm25":
                    //get result
                    result = new CustomizedBM25Engine().run(query);
                    Product[] products = new Product[result.size()];
                    result.toArray(products);
                    request.setAttribute("products", products);

                    rd = request.getRequestDispatcher("productTable.jsp");
                    rd.forward(request, response);
                    break;
                default:
                    response.sendRedirect("./404");
                    break;
            }
        } catch (IOException | ServletException ex) {
            System.err.println(ex.getMessage());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        processRequest(request, response);
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
