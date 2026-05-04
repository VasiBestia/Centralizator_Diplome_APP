package documente.controller;

import documente.repository.CerereAccesDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cereri")
public class CerereAccesController {

    @Autowired
    private CerereAccesDAO cerereDAO;

    @Autowired
    private DataSource dataSource;

    @PostMapping
    public ResponseEntity<?> adaugaCerere(@RequestParam int idDoc, @RequestParam int idParinteAtribuit, HttpSession session) {
        Object sessAttr;
        sessAttr = session.getAttribute("idUtilizator");

        Integer idUtilizator;
        idUtilizator = (Integer) sessAttr;

        if (idUtilizator == null) {
            HttpStatus h;
            h = HttpStatus.UNAUTHORIZED;
            return ResponseEntity.status(h).body("Neautorizat");
        }

        boolean succes;
        succes = cerereDAO.adaugaCerere(idUtilizator, idDoc, idParinteAtribuit);

        if (succes == true) {
            String confirmare;
            confirmare = "Cerere trimisă cu succes!";
            return ResponseEntity.ok(confirmare);
        }

        HttpStatus codEroare;
        codEroare = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(codEroare).body("Eroare la salvarea cererii.");
    }

    @GetMapping
    public List<Map<String, Object>> obtineCereriInAsteptare(HttpSession session) {
        Object logatObj;
        logatObj = session.getAttribute("idUtilizator");

        Integer idParinteLogat;
        idParinteLogat = (Integer) logatObj;

        List<Map<String, Object>> listaFinala;
        listaFinala = new ArrayList<>();

        if (idParinteLogat == null) {
            return listaFinala;
        }

        String p1;
        p1 = "SELECT c.idCerere, m.nume AS numeCopil, d.titlu AS numeDoc, d.idDoc ";
        String p2;
        p2 = "FROM CerereAcces c ";
        String p3;
        p3 = "JOIN MembruFamilie m ON c.idCopil = m.id ";
        String p4;
        p4 = "JOIN Document d ON c.idDoc = d.idDoc ";
        String p5;
        p5 = "WHERE c.status = 'In Asteptare' AND c.idParinteAtribuit =?";

        String sql;
        sql = p1 + p2 + p3 + p4 + p5;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            int idRef;
            idRef = idParinteLogat;
            ps.setInt(1, idRef);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item;
                    item = new HashMap<>();

                    int idC;
                    idC = rs.getInt("idCerere");
                    item.put("idCerere", idC);

                    String nC;
                    nC = rs.getString("numeCopil");
                    item.put("numeCopil", nC);

                    String nD;
                    nD = rs.getString("numeDoc");
                    item.put("numeDoc", nD);

                    int idD;
                    idD = rs.getInt("idDoc");
                    item.put("idDoc", idD);

                    listaFinala.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaFinala;
    }

    @GetMapping("/parinti")
    public ResponseEntity<?> obtineListaParinti() {
        List<Map<String, Object>> lista;
        lista = cerereDAO.obtineParinti();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> proceseazaCerere(@PathVariable("id") int idCerere, @RequestParam("actiune") String actiune) {
        String statusNou;
        statusNou = "";

        boolean esteAprobare;
        esteAprobare = "aprobare".equals(actiune);

        boolean esteRespingere;
        esteRespingere = "respingere".equals(actiune);

        if (esteAprobare) {
            statusNou = "Aprobata";
        } else if (esteRespingere) {
            statusNou = "Respinsa";
        } else {
            HttpStatus bad;
            bad = HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(bad).body("Acțiune invalidă.");
        }

        boolean operatiuneReusita;
        operatiuneReusita = cerereDAO.actualizeazaStatusCerere(idCerere, statusNou);

        if (operatiuneReusita) {
            return ResponseEntity.ok("Status actualizat cu succes.");
        } else {
            HttpStatus err;
            err = HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(err).body("Eroare la actualizarea bazei de date.");
        }
    }

    @GetMapping("/aprobate")
    public ResponseEntity<?> getActeAprobate(HttpSession session) {
        Object uId;
        uId = session.getAttribute("idUtilizator");

        if (uId == null) {
            return ResponseEntity.status(401).body("Nu ești logat");
        }

        Integer identitate;
        identitate = (Integer) uId;

        List<Integer> acte;
        acte = cerereDAO.obtineDocumenteAprobatePentruCopil(identitate);

        return ResponseEntity.ok(acte);
    }
}