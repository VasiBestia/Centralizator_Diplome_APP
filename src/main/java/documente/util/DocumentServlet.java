package documente.util;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.Gson;
import documente.model.Document;
import documente.model.TipDocument;
import documente.repository.DocumentDAO;

@WebServlet("/api/documente")
public class DocumentServlet extends HttpServlet {

    private DocumentDAO docDAO = new DocumentDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder sb;
        sb = new StringBuilder();

        BufferedReader reader;
        reader = request.getReader();

        String line;
        line = reader.readLine();

        while (line!= null) {
            sb.append(line);
            line = reader.readLine();
        }

        String jsonBrut;
        jsonBrut = sb.toString();

        TempDoc temp;
        temp = gson.fromJson(jsonBrut, TempDoc.class);

        Document d;
        d = new Document();

        String t;
        t = temp.titlu;
        String p;
        p = temp.numeProprietar;
        String tipS;
        tipS = temp.tip;

        d.setTitlu(t);
        d.setNumeProprietar(p);

        TipDocument tipEnum;
        tipEnum = TipDocument.valueOf(tipS);
        d.setTip(tipEnum);

        boolean succes;
        succes = docDAO.adaugaDocument(d);

        String tipContinut;
        tipContinut = "application/json";
        response.setContentType(tipContinut);

        if (succes == true) {
            int codOk;
            codOk = 201;
            response.setStatus(codOk);
            String raspunsOk;
            raspunsOk = "{\"status\": \"succes\"}";
            response.getWriter().write(raspunsOk);
        } else {
            int codErr;
            codErr = 500;
            response.setStatus(codErr);
            String raspunsErr;
            raspunsErr = "{\"status\": \"eroare\"}";
            response.getWriter().write(raspunsErr);
        }
    }

    private static class TempDoc {
        String titlu;
        String numeProprietar;
        String tip;
    }
}