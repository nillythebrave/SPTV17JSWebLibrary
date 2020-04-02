/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.Customer;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import session.CustomerFacade;
import session.UserFacade;

/**
 *
 * @author User
 */
@WebServlet(name = "WebController", urlPatterns = {
    "/registration",
    "/login",
    "/logout",
    
})
public class WebController extends HttpServlet {
@EJB CustomerFacade customerFacade;
@EJB UserFacade userFacade;
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
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession(false);
            String json = null;
            JsonObjectBuilder job = Json.createObjectBuilder();
            JsonReader jsonReader = Json.createReader(request.getReader());
            String path = request.getServletPath();
        switch (path) {
            case "/registration":
                JsonObject jsonObject = jsonReader.readObject();
                String login = jsonObject.getString("login");
                String password = jsonObject.getString("password");
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                String phone = jsonObject.getString("phone");
                Customer customer = null;
                User user = null;
                try{
                    customer = new Customer(firstname, lastname, phone);
                    customerFacade.create(customer);
                    user = new User(login, password, customer);
                    userFacade.create(user);
                    job.add("actionStatus", "true");
                    try(Writer writer =new StringWriter()) {
                        Json.createWriter(writer).write(job.build());
                        json = writer.toString(); 
                    }
                } catch (Exception e){
                    if(customer.getId() != null){
                   customerFacade.remove(customer);
                }
                    if(user.getId() != null){
                   userFacade.remove(user);
                }
                    job.add("actionStatus","false").build();
                    try (Writer writer = new StringWriter()){
                      Json.createWriter(writer).write(job.build());
                      json = writer.toString();
                    } 
                }
                break;
            case "/login":
                jsonObject = jsonReader.readObject();
                login = jsonObject.getString("login");
                password = jsonObject.getString("password");
                if(login == null || "".equals(login)
                    || password == null || "".equals(password)){
                    job.add("actionStatus","false");
                    try(Writer writer =new StringWriter()) {
                        Json.createWriter(writer).write(job.build());
                        json = writer.toString(); 
                    }
                    break;
                }
                user = userFacade.findByLogin(login);
                if(user == null){
                    job.add("actionStatus","false");
                    try(Writer writer =new StringWriter()) {
                        Json.createWriter(writer).write(job.build());
                        json = writer.toString(); 
                    }  
                    break;
                }
                if(!password.equals(user.getPassword())){
                    job.add("actionStatus","false");
                    try(Writer writer =new StringWriter()) {
                        Json.createWriter(writer).write(job.build());
                        json = writer.toString(); 
                       }  
                    break;
                }
                session = request.getSession(true);
                session.setAttribute("user", user);
                job.add("actionStatus", "true");
                    try(Writer writer =new StringWriter()) {
                        Json.createWriter(writer).write(job.build());
                        json = writer.toString(); 
                       }  
                    break;
                case "/logout":
                    if(session != null){
                        session.invalidate();
                    }
                    job.add("actionStatus","true");
                        try (Writer writer = new StringWriter()){
                          Json.createWriter(writer).write(job.build());
                          json = writer.toString();
                         } 
                    break;
            }
            if(json != null){
                try (PrintWriter out = response.getWriter()) {
                    out.println(json);        
                }
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
