package documente.controller;

import documente.model.MembruFamilie;
import documente.repository.MembruFamilieDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private MembruFamilieDAO membruFamilieDAO;

    @GetMapping("/me")
    public ResponseEntity<?> getUtilizatorCurent(HttpSession session) {
        Object attr;
        attr = session.getAttribute("idUtilizator");

        Integer idUtilizator;
        idUtilizator = (Integer) attr;

        if (idUtilizator == null) {
            HttpStatus h;
            h = HttpStatus.UNAUTHORIZED;
            return ResponseEntity.status(h).body("Neautorizat");
        }

        MembruFamilie user;
        user = membruFamilieDAO.gasesteDupaId(idUtilizator);

        if (user!= null) {
            user.setParola(null);
            ResponseEntity<?> r;
            r = ResponseEntity.ok(user);
            return r;
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/settings")
    public ResponseEntity<?> actualizeazaSetari(
            @RequestParam("nume") String nume,
            @RequestParam("prenume") String prenume,
            @RequestParam(value = "poza", required = false) MultipartFile poza,
            HttpSession session) {

        Object o;
        o = session.getAttribute("idUtilizator");
        Integer idUtilizator;
        idUtilizator = (Integer) o;

        if (idUtilizator == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Neautorizat");
        }

        String calePozaDB;
        calePozaDB = null;

        if (poza!= null &&!poza.isEmpty()) {
            try {
                String folderRoot;
                folderRoot = "uploads";
                Path uploadPath;
                uploadPath = Paths.get(folderRoot);

                if (Files.exists(uploadPath) == false) {
                    Files.createDirectories(uploadPath);
                }

                String numeOrig;
                numeOrig = poza.getOriginalFilename();
                int pos;
                pos = numeOrig.lastIndexOf(".");
                String ext;
                ext = numeOrig.substring(pos);

                long timestamp;
                timestamp = System.currentTimeMillis();

                String numeFisier;
                numeFisier = "user_" + idUtilizator + "_" + timestamp + ext;

                Path tinta;
                tinta = uploadPath.resolve(numeFisier);
                Files.copy(poza.getInputStream(), tinta, StandardCopyOption.REPLACE_EXISTING);

                calePozaDB = "/uploads/" + numeFisier;

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la salvarea pozei pe server.");
            }
        } else {
            MembruFamilie oldUser;
            oldUser = membruFamilieDAO.gasesteDupaId(idUtilizator);
            String vecheaCale;
            vecheaCale = oldUser.getPozaProfil();
            calePozaDB = vecheaCale;
        }

        boolean succes;
        succes = membruFamilieDAO.actualizeazaProfil(idUtilizator, nume, prenume, calePozaDB);

        if (succes == true) {
            return ResponseEntity.ok("Profil actualizat");
        } else {
            HttpStatus dbErr;
            dbErr = HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(dbErr).body("Eroare la baza de date");
        }
    }
}