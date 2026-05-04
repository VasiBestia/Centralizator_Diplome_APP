package documente.controller;

import documente.repository.MembruFamilieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordController {

    @Autowired
    private MembruFamilieDAO membruFamilieDAO;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private boolean esteParolaPuternica(String parola) {
        if (parola == null) {
            return false;
        }

        String regex;
        regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).{8,}$";

        boolean rezultatMatch;
        rezultatMatch = parola.matches(regex);
        return rezultatMatch;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetareParola(@RequestBody Map<String, String> dateResetare) {
        String email;
        email = dateResetare.get("email");

        String cnp;
        cnp = dateResetare.get("cnp");

        String parolaNoua;
        parolaNoua = dateResetare.get("parolaNoua");

        boolean valida;
        valida = esteParolaPuternica(parolaNoua);

        if (valida == false) {
            String msgPrompt;
            msgPrompt = "Noua parolă trebuie să aibă minim 8 caractere, o literă mare, una mică, o cifră și un simbol.";
            return ResponseEntity.badRequest().body(msgPrompt);
        }

        String parolaHash;
        parolaHash = passwordEncoder.encode(parolaNoua);

        boolean succesOp;
        succesOp = membruFamilieDAO.reseteazaParola(email, cnp, parolaHash);

        if (succesOp == true) {
            String msgOk;
            msgOk = "Parola a fost schimbată cu succes! Te poți conecta.";
            return ResponseEntity.ok(msgOk);
        } else {
            String msgFail;
            msgFail = "Eroare: Adresa de email sau CNP-ul sunt incorecte.";
            return ResponseEntity.badRequest().body(msgFail);
        }
    }
}