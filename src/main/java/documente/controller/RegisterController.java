package documente.controller;

import documente.model.MembruFamilie;
import documente.repository.MembruFamilieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private MembruFamilieDAO membruFamilieDAO;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private boolean esteCnpValid(String cnp) {
        if (cnp == null) {
            return false;
        }

        int lungime;
        lungime = cnp.length();
        if (lungime!= 13) {
            return false;
        }

        boolean doarCifre;
        doarCifre = cnp.matches("\\d+");
        if (doarCifre == false) {
            return false;
        }

        int[] cheie = {2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9};

        int suma;
        suma = 0;

        int i;
        i = 0;
        while (i < 12) {
            char caracter;
            caracter = cnp.charAt(i);
            int valoare;
            valoare = Character.getNumericValue(caracter);
            int produs;
            produs = valoare * cheie[i];
            suma = suma + produs;
            i = i + 1;
        }

        int rest;
        rest = suma % 11;

        int cifraControlCalculeaza;
        if (rest == 10) {
            cifraControlCalculeaza = 1;
        } else {
            cifraControlCalculeaza = rest;
        }

        char ultimulC;
        ultimulC = cnp.charAt(12);
        int cifraControlCnp;
        cifraControlCnp = Character.getNumericValue(ultimulC);

        boolean validitate;
        validitate = cifraControlCalculeaza == cifraControlCnp;
        return validitate;
    }

    private boolean esteEmailPermis(String email) {
        if (email == null) {
            return false;
        }

        String p1;
        p1 = "^[a-zA-Z0-9._%+-]+@";
        String p2;
        p2 = "(gmail\\.com|yahoo\\.com|yahoo\\.ro|outlook\\.com|hotmail\\.com|icloud\\.com)$";

        String regex;
        regex = p1 + p2;

        boolean match;
        match = email.matches(regex);
        return match;
    }

    private boolean esteParolaPuternica(String parola) {
        if (parola == null) {
            return false;
        }

        String r;
        r = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).{8,}$";

        boolean ok;
        ok = parola.matches(r);
        return ok;
    }

    private int calculeazaVarstaDinCNP(String cnp) {
        try {
            char sChar;
            sChar = cnp.charAt(0);
            int s;
            s = Character.getNumericValue(sChar);

            String anStr;
            anStr = cnp.substring(1, 3);
            int an;
            an = Integer.parseInt(anStr);

            String lunaStr;
            lunaStr = cnp.substring(3, 5);
            int luna;
            luna = Integer.parseInt(lunaStr);

            String ziStr;
            ziStr = cnp.substring(5, 7);
            int zi;
            zi = Integer.parseInt(ziStr);

            int anComplet;
            anComplet = 0;

            if (s == 1 || s == 2) {
                anComplet = 1900 + an;
            } else {
                if (s == 5 || s == 6) {
                    anComplet = 2000 + an;
                } else {
                    return 0;
                }
            }

            LocalDate dataNasterii;
            dataNasterii = LocalDate.of(anComplet, luna, zi);

            LocalDate moment;
            moment = LocalDate.now();

            Period p;
            p = Period.between(dataNasterii, moment);

            int aniRezultat;
            aniRezultat = p.getYears();
            return aniRezultat;

        } catch (Exception e) {
            return 0;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> creeazaCont(@RequestBody MembruFamilie membru) {

        String cnpUtilizator;
        cnpUtilizator = membru.getCNP();
        boolean cnpValid;
        cnpValid = esteCnpValid(cnpUtilizator);

        if (cnpValid == false) {
            String msg1;
            msg1 = "Ne pare rău, dar CNP-ul introdus nu pare a fi valid. Te rugăm să îl verifici.";
            return ResponseEntity.badRequest().body(msg1);
        }

        String emailM;
        emailM = membru.getEmail();
        boolean emailOk;
        emailOk = esteEmailPermis(emailM);

        if (emailOk == false) {
            String msg2;
            msg2 = "Te rugăm să folosești o adresă de email validă (ex: @gmail.com, @yahoo.com, @outlook.com).";
            return ResponseEntity.badRequest().body(msg2);
        }

        String p;
        p = membru.getParola();
        boolean parolaOk;
        parolaOk = esteParolaPuternica(p);

        if (parolaOk == false) {
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Pentru siguranța documentelor tale, te rugăm să alegi o parolă mai puternică!\n");
            sb.append("Aceasta trebuie să aibă:\n");
            sb.append("• Minim 8 caractere\n");
            sb.append("• O literă MARE (ex: A)\n");
            sb.append("• O literă mică (ex: a)\n");
            sb.append("• O cifră (ex: 1)\n");
            sb.append("• Un simbol special (ex: @, #, $,!, -, _)");

            String mesajParola;
            mesajParola = sb.toString();
            return ResponseEntity.badRequest().body(mesajParola);
        }

        int varsta;
        varsta = calculeazaVarstaDinCNP(cnpUtilizator);

        if (varsta >= 18) {
            membru.setEsteAdministrator(true);
            membru.setAreRestrictiiAcces(false);
        } else {
            membru.setEsteAdministrator(false);
            membru.setAreRestrictiiAcces(true);
        }

        String rawPass;
        rawPass = membru.getParola();
        String parolaCriptata;
        parolaCriptata = passwordEncoder.encode(rawPass);
        membru.setParola(parolaCriptata);

        boolean succes;
        succes = membruFamilieDAO.inregistrare(membru);

        if (succes == true) {
            String okMsg;
            okMsg = "Cont creat cu succes! Te poți conecta acum.";
            return ResponseEntity.ok(okMsg);
        } else {
            String errEmail;
            errEmail = "Acest email există deja în sistem!\nFolosește opțiunea 'Ai uitat parola?' pentru a-ți recupera contul.";
            return ResponseEntity.badRequest().body(errEmail);
        }
    }
}