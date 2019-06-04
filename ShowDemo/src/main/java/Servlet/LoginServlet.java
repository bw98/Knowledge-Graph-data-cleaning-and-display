package Servlet;

import java.io.IOException;

public class LoginServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        try {
            resp.sendRedirect(req.getContextPath()+"/index.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            req.getRequestDispatcher("/login233.jsp").forward(req,resp);
        }
    }
}
