package documente;

import documente.controller.CerereAccesController;
import documente.repository.CerereAccesDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test pentru diagrama de activitati: "Cerere acces document restrictionat".
 *
 * Fluxul modelat in diagrama:
 *   Copil   -> selecteaza document restrictionat
 *   Sistem  -> afiseaza formular si, la trimitere, apeleaza initiazaCerereAcces()
 *              (creeaza CerereAcces cu status "In Asteptare" si notifica Parintele)
 *   Parinte -> vizualizeaza cererea si decide:
 *                Da -> aproba()  -> status "Aprobata"
 *                Nu -> respinge() -> status "Respinsa"
 *   Sistem  -> notifica statusul catre Copil
 */
public class CerereAccesActivitateTest {

    @InjectMocks
    private CerereAccesController cerereController;

    @Mock
    private CerereAccesDAO cerereDAO;

    @Mock
    private HttpSession sesiuneCopil;

    private AutoCloseable resurseMock;

    @BeforeEach
    public void pregatire() {
        AutoCloseable r;
        r = MockitoAnnotations.openMocks(this);
        this.resurseMock = r;
    }

    @AfterEach
    public void inchidere() throws Exception {
        AutoCloseable r;
        r = this.resurseMock;
        r.close();
    }

    private static Stream<Arguments> scenariiDecizieParinte() {
        List<Arguments> seturi;
        seturi = new ArrayList<>();

        // Ramura "Da" - Parintele aproba cererea
        Arguments aprobare;
        aprobare = Arguments.of("aprobare", "Aprobata", HttpStatus.OK);
        seturi.add(aprobare);

        // Ramura "Nu" - Parintele respinge cererea
        Arguments respingere;
        respingere = Arguments.of("respingere", "Respinsa", HttpStatus.OK);
        seturi.add(respingere);

        Stream<Arguments> flux;
        flux = seturi.stream();
        return flux;
    }

    @ParameterizedTest
    @MethodSource("scenariiDecizieParinte")
    public void testeazaScenariuCerereAccesCompleta(
            String actiuneParinte,
            String statusAsteptat,
            HttpStatus codAsteptat) {

        // ----------------------------------------------------------------
        // |Copil| Selecteaza document restrictionat + completeaza formular
        // |Copil| Apasa "Trimite Cerere"
        // ----------------------------------------------------------------
        int idCopilLogat;
        idCopilLogat = 5;

        int idDocumentRestrictionat;
        idDocumentRestrictionat = 42;

        int idParinteAtribuit;
        idParinteAtribuit = 1;

        Mockito.when(sesiuneCopil.getAttribute("idUtilizator"))
                .thenReturn(Integer.valueOf(idCopilLogat));

        // |Sistem| Apeleaza initiazaCerereAcces() -> creeaza CerereAcces "In Asteptare"
        Mockito.when(cerereDAO.adaugaCerere(idCopilLogat, idDocumentRestrictionat, idParinteAtribuit))
                .thenReturn(true);

        ResponseEntity<?> raspunsTrimitere;
        raspunsTrimitere = cerereController.adaugaCerere(
                idDocumentRestrictionat, idParinteAtribuit, sesiuneCopil);

        assertEquals(HttpStatus.OK, raspunsTrimitere.getStatusCode(),
                "Pasul 1 esuat: cererea trebuia salvata cu status 'In Asteptare'.");

        Mockito.verify(cerereDAO, Mockito.times(1))
                .adaugaCerere(idCopilLogat, idDocumentRestrictionat, idParinteAtribuit);

        // ----------------------------------------------------------------
        // |Parinte| Vizualizeaza detaliile cererii
        // ----------------------------------------------------------------
        int idCerereCreata;
        idCerereCreata = 77;

        Mockito.when(cerereDAO.actualizeazaStatusCerere(idCerereCreata, statusAsteptat))
                .thenReturn(true);

        // ----------------------------------------------------------------
        // |Parinte| Decide: Aproba (Da) sau Respinge (Nu)
        // |Sistem| Apeleaza aproba() / respinge() si seteaza statusul corespunzator
        // ----------------------------------------------------------------
        ResponseEntity<?> raspunsDecizie;
        raspunsDecizie = cerereController.proceseazaCerere(idCerereCreata, actiuneParinte);

        // |Sistem| Afiseaza notificare status catre Copil
        assertEquals(codAsteptat, raspunsDecizie.getStatusCode(),
                "Pasul 2 esuat: decizia parintelui nu a fost procesata corect.");

        Mockito.verify(cerereDAO, Mockito.times(1))
                .actualizeazaStatusCerere(idCerereCreata, statusAsteptat);
    }

    /**
     * Ramura alternativa: copilul nu este autentificat cand incearca
     * sa initieze cererea -> sistemul refuza si fluxul nu mai continua.
     */
    @Test
    public void testeazaCopilNeautentificatNuPoateTrimiteCerere() {
        Mockito.when(sesiuneCopil.getAttribute("idUtilizator")).thenReturn(null);

        ResponseEntity<?> raspuns;
        raspuns = cerereController.adaugaCerere(42, 1, sesiuneCopil);

        assertEquals(HttpStatus.UNAUTHORIZED, raspuns.getStatusCode(),
                "Un copil nelogat nu ar trebui sa poata initia o cerere de acces.");

        Mockito.verify(cerereDAO, Mockito.never())
                .adaugaCerere(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    /**
     * Ramura alternativa: parintele trimite o actiune invalida (nici
     * "aprobare", nici "respingere") -> sistemul raspunde BAD_REQUEST
     * si nu modifica statusul cererii.
     */
    @Test
    public void testeazaActiuneInvalidaLaProcesareCerere() {
        ResponseEntity<?> raspuns;
        raspuns = cerereController.proceseazaCerere(77, "altceva");

        assertEquals(HttpStatus.BAD_REQUEST, raspuns.getStatusCode(),
                "O actiune diferita de 'aprobare'/'respingere' trebuie respinsa.");

        Mockito.verify(cerereDAO, Mockito.never())
                .actualizeazaStatusCerere(Mockito.anyInt(), Mockito.anyString());
    }

    /**
     * Ramura alternativa: DAO-ul esueaza la salvarea cererii ->
     * sistemul raspunde 500 si fluxul nu ajunge la parinte.
     */
    @Test
    public void testeazaEsecLaSalvareaCereriiInBazaDeDate() {
        Mockito.when(sesiuneCopil.getAttribute("idUtilizator")).thenReturn(Integer.valueOf(5));
        Mockito.when(cerereDAO.adaugaCerere(5, 42, 1)).thenReturn(false);

        ResponseEntity<?> raspuns;
        raspuns = cerereController.adaugaCerere(42, 1, sesiuneCopil);

        assertTrue(raspuns.getStatusCode().is5xxServerError(),
                "Daca persistenta cererii esueaza, sistemul trebuie sa raporteze eroare.");
    }
}
