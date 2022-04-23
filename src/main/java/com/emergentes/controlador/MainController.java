package com.emergentes.controlador;

import com.emergentes.modelo.Libro;
import com.emergentes.utiles.ConexionDB;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String op;
            op = (request.getParameter("op") != null) ? request.getParameter("op") : "list";

            ArrayList<Libro> lista = new ArrayList<Libro>();
            ConexionDB canal = new ConexionDB();
            Connection conn = canal.conectar();
            PreparedStatement ps;
            ResultSet rs;

            if (op.equals("list")) {
                String sql = "SELECT * FROM libros";

                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    Libro lib = new Libro();

                    lib.setId(rs.getInt("id"));
                    lib.setIsbn(rs.getString("isbn"));
                    lib.setTitulo(rs.getString("titulo"));
                    lib.setCategoria(rs.getString("categoria"));

                    lista.add(lib);
                }
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }

            if (op.equals("nuevo")) {
                // Nuevo registro
                Libro li = new Libro();

                request.setAttribute("lib", li);
                request.getRequestDispatcher("editar.jsp").forward(request, response);
            }
            
            if(op.equals("editar")){
                int id = Integer.parseInt(request.getParameter("id"));
                String sql = "SELECT * FROM libros WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                
                rs = ps.executeQuery();
                
                Libro li = new Libro();
                
                while(rs.next()){
                    li.setId(rs.getInt("id"));
                    li.setIsbn(rs.getString("isbn"));
                    li.setTitulo(rs.getString("titulo"));
                    li.setCategoria(rs.getString("categoria"));
                }
                request.setAttribute("lib", li);
                request.getRequestDispatcher("editar.jsp").forward(request, response);
            }

            if (op.equals("eliminar")) {
                // Eliminar
                int id = Integer.parseInt(request.getParameter("id"));

                String sql = "DELETE FROM libros WHERE id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);

                ps.executeUpdate();
                response.sendRedirect("MainController");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            int id = Integer.parseInt(request.getParameter("id"));
            String isbn = request.getParameter("isbn");
            String titulo = request.getParameter("titulo");
            String categoria = request.getParameter("categoria");

            Libro lib = new Libro();

            lib.setId(id);
            lib.setIsbn(isbn);
            lib.setTitulo(titulo);
            lib.setCategoria(categoria);

            ConexionDB canal = new ConexionDB();
            Connection conn = canal.conectar();

            PreparedStatement ps;

            if (id == 0) {
                // Nuevo Registro
                String sql = "INSERT INTO libros (isbn, titulo, categoria) VALUES (?,?,?)";

                ps = conn.prepareStatement(sql);

                ps.setString(1, lib.getIsbn());
                ps.setString(2, lib.getTitulo());
                ps.setString(3, lib.getCategoria());

                ps.executeUpdate();
                
                response.sendRedirect("MainController");
            }else{
              // Si el registro ya existe
              String sql = "UPDATE libros SET isbn=? , titulo=?, categoria=? WHERE id=?";
              ps = conn.prepareStatement(sql);
              
              ps.setString(1, lib.getIsbn());
              ps.setString(2, lib.getTitulo());
              ps.setString(3, lib.getCategoria());
              ps.setInt(4, lib.getId());
              
              ps.executeUpdate();
              
              response.sendRedirect("MainController");
            }
        } catch (SQLException e) {
            System.out.println("Error en SQL " + e.getMessage());
        }
    }

}
