package documente.controller;

import documente.repository.FamilieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/familie")
public class FamilieController {

    @Autowired
    private FamilieDAO familieDAO;

    @GetMapping
    public ResponseEntity<?> getMembri(HttpSession session) {
        Object admObj;
        admObj = session.getAttribute("esteAdministrator");

        Boolean esteAdmin;
        esteAdmin = (Boolean) admObj;

        if (esteAdmin == null || esteAdmin == false) {
            int codInterzis;
            codInterzis = 403;
            return ResponseEntity.status(codInterzis).body("Acces interzis.");
        }

        List<Map<String, Object>> listaMembri;
        listaMembri = familieDAO.obtineMembri();
        return ResponseEntity.ok(listaMembri);
    }

    @PostMapping
    public ResponseEntity<?> adaugaMembru(
            @RequestParam String nume,
            @RequestParam String prenume,
            @RequestParam String cnp,
            @RequestParam String email,
            @RequestParam String parola,
            @RequestParam boolean esteAdmin,
            HttpSession session) {

        Object atributAdmin;
        atributAdmin = session.getAttribute("esteAdministrator");

        boolean confirmareAdmin;
        confirmareAdmin = false;

        if (atributAdmin instanceof Boolean) {
            confirmareAdmin = (Boolean) atributAdmin;
        } else if (atributAdmin instanceof Integer) {
            Integer valInt;
            valInt = (Integer) atributAdmin;
            if (valInt == 1) {
                confirmareAdmin = true;
            }
        } else if (atributAdmin instanceof String) {
            String valStr;
            valStr = (String) atributAdmin;
            boolean cond1 = "true".equalsIgnoreCase(valStr);
            boolean cond2 = "1".equals(valStr);
            if (cond1 || cond2) {
                confirmareAdmin = true;
            }
        }

        if (confirmareAdmin == false) {
            return ResponseEntity.status(403).body("Acces interzis. Nu ești administrator.");
        }

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder bCrypt;
        bCrypt = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        String hash;
        hash = bCrypt.encode(parola);

        boolean adaugat;
        adaugat = familieDAO.adaugaMembru(nume, prenume, cnp, email, hash, esteAdmin);

        if (adaugat) {
            return ResponseEntity.ok("Membru adăugat cu succes");
        }

        return ResponseEntity.internalServerError().body("Eroare la adăugarea în baza de date");
    }
}