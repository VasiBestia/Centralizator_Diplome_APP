package documente.controller;

import documente.model.AlertaExpirare;
import documente.repository.AlertaDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerte")
public class AlertaController {

    @Autowired
    private AlertaDAO alertaDAO;

    @GetMapping
    public ResponseEntity<?> getAlerte(HttpSession session) {
        Object userObj;
        userObj = session.getAttribute("idUtilizator");

        if (userObj == null) {
            HttpStatus statusNeautorizat;
            statusNeautorizat = HttpStatus.UNAUTHORIZED;
            String corpMesaj;
            corpMesaj = "Nu ești conectat!";
            return ResponseEntity.status(statusNeautorizat).body(corpMesaj);
        }

        List<AlertaExpirare> listaAlerte;
        listaAlerte = alertaDAO.obtineAlerteActive();

        return ResponseEntity.ok(listaAlerte);
    }
}