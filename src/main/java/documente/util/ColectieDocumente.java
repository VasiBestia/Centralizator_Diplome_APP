package documente.util;

import documente.model.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ColectieDocumente {
    private List<Document> documente = new ArrayList<>();
    private Date dMod;
    private int nrDocs;

    public void adaugaDocument(Document doc) {
        List<Document> ref;
        ref = this.documente;
        ref.add(doc);

        int dim;
        dim = documente.size();
        this.nrDocs = dim;

        Date acum;
        acum = new Date();
        this.dMod = acum;
    }

    public boolean stergeDocument(int idDoc) {
        int idx;
        idx = -1;

        int i;
        i = 0;
        while (i < documente.size()) {
            Document dTemp;
            dTemp = documente.get(i);
            int idCurent;
            idCurent = dTemp.getIdDoc();

            if (idCurent == idDoc) {
                idx = i;
                break;
            }
            i = i + 1;
        }

        if (idx!= -1) {
            documente.remove(idx);
            int nouaDim;
            nouaDim = documente.size();
            this.nrDocs = nouaDim;
            this.dMod = new Date();
            return true;
        }
        return false;
    }

    public Document gasestidupaTitlu(String t) {
        int k;
        k = 0;
        while (k < documente.size()) {
            Document d;
            d = documente.get(k);
            String titluD;
            titluD = d.getTitlu();
            if (titluD.equalsIgnoreCase(t)) {
                return d;
            }
            k = k + 1;
        }
        return null;
    }

    public Document gasesteDupaTitlu(String t) {
        int n;
        n = documente.size();
        int j;
        j = 0;
        while (j < n) {
            Document doc;
            doc = documente.get(j);
            String numeDoc;
            numeDoc = doc.getTitlu();

            if (numeDoc!= null) {
                boolean esteEgal;
                esteEgal = numeDoc.equalsIgnoreCase(t);
                if (esteEgal) {
                    return doc;
                }
            }
            j = j + 1;
        }
        return null;
    }

    public List<Document> obtineToateDocumentele() {
        List<Document> rezultat;
        rezultat = this.documente;
        return rezultat;
    }
}