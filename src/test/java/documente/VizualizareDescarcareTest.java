package documente;

import documente.controller.DocumentController;
import documente.model.Document;
import documente.repository.DocumentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VizualizareDescarcareTest {

    @InjectMocks
    private DocumentController documentController;

    @Mock
    private DocumentDAO documentDAO;

    @Mock
    private HttpSession session;

    private AutoCloseable closeable;
    private List<Path> fisiereTemporareDeSters;

    @BeforeEach
    public void setup() {
        AutoCloseable res;
        res = MockitoAnnotations.openMocks(this);
        this.closeable = res;

        List<Path> listaP;
        listaP = new ArrayList<>();
        this.fisiereTemporareDeSters = listaP;
    }

    @AfterEach
    public void cleanup() throws Exception {
        List<Path> pSters;
        pSters = this.fisiereTemporareDeSters;

        int i;
        i = 0;
        while (i < pSters.size()) {
            Path p;
            p = pSters.get(i);
            Files.deleteIfExists(p);
            i = i + 1;
        }

        AutoCloseable c;
        c = this.closeable;
        c.close();
    }

    private static Stream<Arguments> seturiDeDatePentruDescarcare() {
        List<Arguments> dateTest;
        dateTest = new ArrayList<>();

        Arguments c1;
        c1 = Arguments.of(1, 100, true, true, true, HttpStatus.OK);
        dateTest.add(c1);

        Arguments c2;
        c2 = Arguments.of(null, 100, true, true, true, HttpStatus.UNAUTHORIZED);
        dateTest.add(c2);

        Arguments c3;
        c3 = Arguments.of(1, 999, false, false, false, HttpStatus.NOT_FOUND);
        dateTest.add(c3);

        Arguments c4;
        c4 = Arguments.of(1, 101, true, false, false, HttpStatus.NOT_FOUND);
        dateTest.add(c4);

        Arguments c5;
        c5 = Arguments.of(1, 102, true, true, false, HttpStatus.NOT_FOUND);
        dateTest.add(c5);

        Stream<Arguments> flux;
        flux = dateTest.stream();
        return flux;
    }

    @ParameterizedTest
    @MethodSource("seturiDeDatePentruDescarcare")
    public void testeazaScenariuDescarcareDocument(
            Integer idUtilizatorSesiune,
            int idDocCautat,
            boolean existaInDb,
            boolean areCaleSetata,
            boolean existaFizicPeDisc,
            HttpStatus statusAsteptat) throws IOException {

        Object sessVal;
        sessVal = idUtilizatorSesiune;
        Mockito.when(session.getAttribute("idUtilizator")).thenReturn(sessVal);

        if (existaInDb == true) {
            Document mockDoc;
            mockDoc = new Document();
            mockDoc.setIdDoc(idDocCautat);

            if (areCaleSetata == true) {
                if (existaFizicPeDisc == true) {
                    Path tempFile;
                    tempFile = Files.createTempFile("test_act", ".pdf");

                    String caleAbs;
                    caleAbs = tempFile.toAbsolutePath().toString();
                    mockDoc.setCaleFisier(caleAbs);

                    this.fisiereTemporareDeSters.add(tempFile);
                } else {
                    String caleInvalida;
                    caleInvalida = "C:/cale/invalida/fisier_inexistent.pdf";
                    mockDoc.setCaleFisier(caleInvalida);
                }
            } else {
                mockDoc.setCaleFisier(null);
            }

            Document rezultatDao;
            rezultatDao = mockDoc;
            Mockito.when(documentDAO.gasesteDocumentDupaId(idDocCautat)).thenReturn(rezultatDao);
        } else {
            Mockito.when(documentDAO.gasesteDocumentDupaId(idDocCautat)).thenReturn(null);
        }

        ResponseEntity<?> raspuns;
        raspuns = documentController.descarcaDocument(idDocCautat, session);

        HttpStatus statusPrimit;
        statusPrimit = (HttpStatus) raspuns.getStatusCode();

        Assertions.assertEquals(statusAsteptat, statusPrimit,
                "Statusul HTTP returnat nu corespunde cu ramura decizională testată!");
    }
}