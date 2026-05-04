package documente.controller;

import documente.model.Document;
import documente.model.TipDocument;
import documente.repository.DocumentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/documente")
public class DocumentController {

    @Autowired
    private DocumentDAO documentDAO;

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        Object u;
        u = session.getAttribute("idUtilizator");

        Integer idUtilizator;
        idUtilizator = (Integer) u;

        if (idUtilizator == null) {
            String msg;
            msg = "Nu ești conectat!";
            HttpStatus h;
            h = HttpStatus.UNAUTHORIZED;
            return ResponseEntity.status(h).body(msg);
        }

        List<Document> lista;
        lista = documentDAO.obtineToateDocumentele();

        ResponseEntity<?> rez;
        rez = ResponseEntity.ok(lista);
        return rez;
    }

    @PostMapping
    public ResponseEntity<?> adaugaDocumentDinInterfata(
            @RequestParam("nume") String nume,
            @RequestParam("prop") String prop,
            @RequestParam("tip") String tip,
            @RequestParam("src") String src,
            @RequestParam(value = "dExpirare", required = false) String dExpirareStr,
            @RequestParam("fisier") MultipartFile fisier,
            HttpSession session) {

        Object sessObj;
        sessObj = session.getAttribute("idUtilizator");
        Integer idUtilizator;
        idUtilizator = (Integer) sessObj;

        if (idUtilizator == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nu ești conectat!");
        }

        try {
            Date dataExpirare;
            dataExpirare = null;

            if (dExpirareStr!= null) {
                String dTrim;
                dTrim = dExpirareStr.trim();
                if (dTrim.isEmpty() == false) {
                    SimpleDateFormat sdf;
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dataExpirare = sdf.parse(dTrim);

                    Date acum;
                    acum = new Date();
                    if (dataExpirare.before(acum)) {
                        String errExp;
                        errExp = "Eroare: Documentul introdus este deja expirat!";
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errExp);
                    }
                }
            }

            String c1;
            c1 = "uploads";
            String c2;
            c2 = "acte";
            Path uploadPath;
            uploadPath = Paths.get(c1, c2);

            if (Files.exists(uploadPath) == false) {
                Files.createDirectories(uploadPath);
            }

            String numeFisierOriginal;
            numeFisierOriginal = fisier.getOriginalFilename();
            String extensie;
            extensie = "";

            if (numeFisierOriginal!= null) {
                int p;
                p = numeFisierOriginal.lastIndexOf(".");
                if (p!= -1) {
                    extensie = numeFisierOriginal.substring(p);
                }
            }

            long tS;
            tS = System.currentTimeMillis();
            String numeUnicFisier;
            numeUnicFisier = "doc_" + tS + extensie;

            Path caleaFinala;
            caleaFinala = uploadPath.resolve(numeUnicFisier);
            Files.copy(fisier.getInputStream(), caleaFinala, StandardCopyOption.REPLACE_EXISTING);

            Document doc;
            doc = new Document();
            doc.setTitlu(nume);
            doc.setNumeProprietar(prop);

            if (tip != null && tip.trim().length() > 0) {
                String valoareCautata = tip.trim();
                TipDocument[] toateOptiunile = TipDocument.values();
                boolean amGasitTipul = false;

                int k = 0;
                while (k < toateOptiunile.length) {
                    TipDocument optiune = toateOptiunile[k];

                    if (optiune.name().equalsIgnoreCase(valoareCautata)) {
                        doc.setTip(optiune);
                        amGasitTipul = true;
                        break;
                    }
                    k++;
                }

                if (!amGasitTipul) {
                    String mesajEroare = "Tip document invalid: " + tip;
                    return ResponseEntity.badRequest().body(mesajEroare);
                }
            }

            doc.setNumeInstitutie(src);
            doc.setDataExpirare(dataExpirare);

            String caleScurta;
            caleScurta = "uploads/acte/" + numeUnicFisier;
            doc.setCaleFisier(caleScurta);

            doc.setIdUtilizator(idUtilizator);

            boolean salvat;
            salvat = documentDAO.adaugaDocument(doc);

            if (salvat == true) {
                String okMsg;
                okMsg = "Document salvat cu succes!";
                return ResponseEntity.ok(okMsg);
            } else {
                HttpStatus internal;
                internal = HttpStatus.INTERNAL_SERVER_ERROR;
                return ResponseEntity.status(internal).body("Eroare la salvarea în baza de date.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare internă la procesarea fișierului.");
        }
    }

    @GetMapping("/download/{idDoc}")
    public ResponseEntity<?> descarcaDocument(@PathVariable int idDoc, HttpSession session) {
        Object user;
        user = session.getAttribute("idUtilizator");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nu ești conectat!");
        }

        try {
            Document doc;
            doc = documentDAO.gasesteDocumentDupaId(idDoc);

            if (doc == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fișierul nu a fost găsit în baza de date.");
            }

            String cF;
            cF = doc.getCaleFisier();
            if (cF == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fișierul nu a fost găsit în baza de date.");
            }

            Path caleaCatreFisier;
            caleaCatreFisier = Paths.get(cF).toAbsolutePath().normalize();

            Resource resursa;
            resursa = new UrlResource(caleaCatreFisier.toUri());

            if (resursa.exists() == false) {
                String eF;
                eF = "Fișierul fizic nu mai există pe server.";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eF);
            }

            String headerVal;
            headerVal = "attachment; filename=\"" + resursa.getFilename() + "\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerVal)
                    .body(resursa);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare la descărcarea fișierului.");
        }
    }

    @DeleteMapping("/{idDoc}")
    public ResponseEntity<?> stergeDocument(@PathVariable int idDoc, HttpSession session) {
        Object uSess;
        uSess = session.getAttribute("idUtilizator");

        if (uSess == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nu ești conectat!");
        }

        Document doc;
        doc = documentDAO.gasesteDocumentDupaId(idDoc);

        if (doc!= null) {
            String pathStr;
            pathStr = doc.getCaleFisier();
            if (pathStr!= null) {
                try {
                    Path p;
                    p = Paths.get(pathStr);
                    Files.deleteIfExists(p);
                } catch (Exception e) {
                }
            }
        }

        boolean sters;
        sters = documentDAO.stergeDocument(idDoc);

        if (sters == true) {
            return ResponseEntity.ok("Document șters cu succes!");
        } else {
            HttpStatus hErr;
            hErr = HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(hErr).body("Eroare la ștergere.");
        }
    }

    @PutMapping("/{idDoc}")
    public ResponseEntity<?> editeazaDocument(
            @PathVariable int idDoc,
            @RequestParam("nume") String nume,
            @RequestParam("prop") String prop,
            @RequestParam("tip") String tip,
            @RequestParam("src") String src,
            @RequestParam(value = "dExpirare", required = false) String dExpirareStr,
            @RequestParam(value = "fisier", required = false) MultipartFile fisier,
            HttpSession session) {

        Object logat;
        logat = session.getAttribute("idUtilizator");

        if (logat == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nu ești conectat!");
        }

        try {
            Document docExistent;
            docExistent = documentDAO.gasesteDocumentDupaId(idDoc);

            if (docExistent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document negăsit.");
            }

            docExistent.setTitlu(nume);
            docExistent.setNumeProprietar(prop);
            docExistent.setNumeInstitutie(src);

            if (tip != null && tip.trim().length() > 0) {
                String cautat = tip.trim();
                TipDocument[] listaTipuri = TipDocument.values();

                int i = 0;
                while (i < listaTipuri.length) {
                    TipDocument t = listaTipuri[i];

                    if (t.name().equalsIgnoreCase(cautat)) {
                        docExistent.setTip(t);
                        break;
                    }
                    i++;
                }
            }

            if (dExpirareStr!= null) {
                if (dExpirareStr.isEmpty() == false) {
                    SimpleDateFormat fmt;
                    fmt = new SimpleDateFormat("yyyy-MM-dd");
                    Date dParsed;
                    dParsed = fmt.parse(dExpirareStr);
                    docExistent.setDataExpirare(dParsed);
                }
            }

            if (fisier != null && !fisier.isEmpty()) {
                String vecheaCale = docExistent.getCaleFisier();
                if (vecheaCale != null) {
                    Files.deleteIfExists(Paths.get(vecheaCale));
                }

                String numeOriginal = fisier.getOriginalFilename();
                String extensie = "";
                if (numeOriginal != null && numeOriginal.contains(".")) {
                    extensie = numeOriginal.substring(numeOriginal.lastIndexOf("."));
                }

                String numeNou = "doc_" + System.currentTimeMillis() + extensie;
                Path caleaNoua = Paths.get("uploads/acte/").resolve(numeNou);

                Files.copy(fisier.getInputStream(), caleaNoua, StandardCopyOption.REPLACE_EXISTING);
                docExistent.setCaleFisier("uploads/acte/" + numeNou);
            }

            boolean ok;
            ok = documentDAO.actualizeazaDocument(docExistent);

            if (ok == true) {
                return ResponseEntity.ok("Modificat cu succes!");
            } else {
                return ResponseEntity.internalServerError().body("Eroare DB");
            }

        } catch (Exception e) {
            String exM;
            exM = "Eroare: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exM);
        }
    }
}