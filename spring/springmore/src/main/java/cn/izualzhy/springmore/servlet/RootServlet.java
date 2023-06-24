package cn.izualzhy.springmore.servlet;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

@WebServlet(urlPatterns = {"/ready"})
public class RootServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("success");
    }
}