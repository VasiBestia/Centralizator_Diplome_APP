package documente.service;

import documente.model.Document;
import java.util.ArrayList;
import java.util.List;

public class DocumentService {
    private List<Document> docs = new ArrayList<>();

    public List<Document> gasesteDupaProprietar(String p) {
        List<Document> listaRez;
        listaRez = new ArrayList<>();

        int n;
        n = docs.size();

        int i;
        i = 0;
        while (i < n) {
            Document d;
            d = docs.get(i);

            String numeProp;
            numeProp = d.getNumeProprietar();

            if (numeProp!= null) {
                boolean esteProprietar;
                esteProprietar = numeProp.equalsIgnoreCase(p);

                if (esteProprietar) {
                    listaRez.add(d);
                }
            }
            i = i + 1;
        }
        return listaRez;
    }

    public void stergeDocument(int id) {
        int marime;
        marime = docs.size();

        int indexDeSters;
        indexDeSters = -1;

        int i;
        i = 0;
        while (i < marime) {
            Document doc;
            doc = docs.get(i);

            int idCurent;
            idCurent = doc.getIdDoc();

            if (idCurent == id) {
                indexDeSters = i;
                break;
            }
            i = i + 1;
        }

        if (indexDeSters!= -1) {
            docs.remove(indexDeSters);
        }
    }
}