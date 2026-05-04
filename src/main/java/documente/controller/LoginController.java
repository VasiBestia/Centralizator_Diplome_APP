package documente.controller;

import documente.model.MembruFamilie;
import documente.repository.MembruFamilieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private MembruFamilieDAO membruFamilieDAO;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String email;
        email = credentials.get("email");

        String parolaIntrodusa;
        parolaIntrodusa = credentials.get("parola");

        MembruFamilie membru;
        membru = membruFamilieDAO.gasesteDupaEmail(email);

        if (membru!= null) {
            String parolaHash;
            parolaHash = membru.getParola();

            boolean parolaValida;
            parolaValida = passwordEncoder.matches(parolaIntrodusa, parolaHash);

            if (parolaValida == true) {
                int id;
                id = membru.getId();
                session.setAttribute("idUtilizator", id);

                boolean esteAdm;
                esteAdm = membru.isEsteAdministrator();
                session.setAttribute("esteAdministrator", esteAdm);

                boolean areRestr;
                areRestr = membru.isAreRestrictiiAcces();
                session.setAttribute("areRestrictii", areRestr);

                membru.setParola(null);

                ResponseEntity<?> rezOk;
                rezOk = ResponseEntity.ok(membru);
                return rezOk;
            }
        }

        HttpStatus statusErr;
        statusErr = HttpStatus.UNAUTHORIZED;
        String msgErr;
        msgErr = "Email sau parolă incorecte!";

        return ResponseEntity.status(statusErr).body(msgErr);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();

        String msg;
        msg = "Te-ai delogat cu succes.";
        return ResponseEntity.ok(msg);
    }
}