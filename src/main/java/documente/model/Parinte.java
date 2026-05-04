package documente.model;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Parinte extends MembruFamilie {
    private boolean esteAdministrator;

    public Parinte(int id, String nume, String prenume, String CNP, String email, String parola) {
        super(id, nume, prenume, CNP, email, parola);
        this.esteAdministrator = true;
    }

    public void setareAlerteExpirare(int idD, Date dExp, String msg) {
        if (this.cAsoc!= null) {
            List<Document> lista;
            lista = this.cAsoc.obtineToateDocumentele();

            int i;
            i = 0;
            while (i < lista.size()) {
                Document d;
                d = lista.get(i);

                int idCurent;
                idCurent = d.getIdDoc();

                if (idCurent == idD) {
                    AlertaExpirare a;
                    a = new AlertaExpirare();

                    double aleator;
                    aleator = Math.random();
                    double calcul;
                    calcul = aleator * 10000;
                    int rId;
                    rId = (int) calcul;

                    a.setIdAlerta(rId);
                    a.setDataAlerta(dExp);
                    a.setMesaj(msg);
                    a.setIdDoc(idD);
                }
                i = i + 1;
            }
        }
    }

    public boolean incarcareDocumentePersonale(File f, TipDocument tip) {
        if (this.cAsoc!= null) {
            Document dNou;
            dNou = new Document();

            double val;
            val = Math.random();
            double gen;
            gen = val * 5000;
            int idGen;
            idGen = (int) gen;

            dNou.setIdDoc(idGen);

            String numeFisier;
            numeFisier = f.getName();
            dNou.setTitlu(numeFisier);

            Date acum;
            acum = new Date();
            dNou.setDataIncarcare(acum);

            String p;
            p = this.getNume();
            dNou.setNumeProprietar(p);

            String sursa;
            sursa = "Incarcat Manual";
            dNou.setNumeInstitutie(sursa);

            dNou.setTip(tip);

            this.cAsoc.adaugaDocument(dNou);
            return true;
        }
        return false;
    }

    public boolean isEsteAdministrator() {
        boolean adm;
        adm = this.esteAdministrator;
        return adm;
    }
}