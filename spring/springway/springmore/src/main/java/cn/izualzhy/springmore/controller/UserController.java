package cn.izualzhy.springmore.controller;

import cn.izualzhy.springmore.aspect.UserValidator;
import cn.izualzhy.springmore.pojo.User;
import cn.izualzhy.springmore.service.UserService;
import cn.izualzhy.springmore.view.PdfExportService;
import cn.izualzhy.springmore.view.PdfView;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService = null;

    @RequestMapping("/print")
    @ResponseBody
    public User printUser(Long id, String userName, String note) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setNote(note);

        userService.printUser(user);
        return user;
    }

    @GetMapping("/test")
    public void test() {
        System.out.println("test");
    }

    @RequestMapping("/validate")
    @ResponseBody
    public User validateUser(Long id, String userName, String note) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setNote(note);

        UserValidator userValidator = (UserValidator) userService;
        if (userValidator.validate(user)) {
            userService.printUser(user);
        }

        return user;
    }

    @GetMapping("/export/pdf")
    public ModelAndView exportPdf(String userName, String note) {
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < 6; ++i) {
            userList.add(new User((long) i, "name_" + i, "note_" + i));
        }

        View view = new PdfView(exportService());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(view);
        modelAndView.addObject("userlist", userList);

        return modelAndView;
    }

    @SuppressWarnings("unchecked")
    private PdfExportService exportService() {
        return ((model, document, pdfWriter, request, response) -> {
            try {
                document.setPageSize(PageSize.A4);
                document.addTitle("用户信息");
                document.add(new Chunk("\n"));
                PdfPTable table = new PdfPTable(3);
                PdfPCell cell = null;

                Font f8 = new Font();
                f8.setColor(Color.BLUE);
                f8.setStyle(Font.BOLD);

                cell = new PdfPCell(new Paragraph("id", f8));
                cell.setHorizontalAlignment(1);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph("user_name", f8));
                cell.setHorizontalAlignment(1);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph("note", f8));
                cell.setHorizontalAlignment(1);
                table.addCell(cell);

                List<User> userList = (List<User>) model.get("userlist");
                for (User user : userList) {
                    document.add(new Chunk("\n"));
                    cell = new PdfPCell(new Paragraph(user.getId() + ""));
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(user.getUserName()));
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(user.getNote()));
                    table.addCell(cell);
                }

                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
    }
}
