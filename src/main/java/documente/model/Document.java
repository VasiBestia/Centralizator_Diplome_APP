package documente.model;

import java.io.File;
import java.util.Date;

public class Document {
    private int idDoc;
    private String titlu;
    private Date dataIncarcare;
    private String numeProprietar;
    private Date dataExpirare;
    private String numeInstitutie;
    private TipDocument tip;
    private String caleFisier;
    private File fisier;
    private int idUtilizator;

    public Document() {}

    public int getIdDoc() {
        int id;
        id = this.idDoc;
        return id;
    }

    public Date getDataIncarcare() {
        Date d;
        d = this.dataIncarcare;
        return d;
    }

    public String getTitlu() {
        return this.titlu;
    }

    public String getNumeProprietar() {
        String n;
        n = this.numeProprietar;
        return n;
    }

    public Date getDataExpirare() {
        return this.dataExpirare;
    }

    public String getNumeInstitutie() {
        return this.numeInstitutie;
    }

    public TipDocument getTip() {
        TipDocument t;
        t = this.tip;
        return t;
    }

    public String getCaleFisier() {
        return this.caleFisier;
    }

    public File getFisier() {
        File f;
        f = this.fisier;
        return f;
    }

    public void setIdDoc(int idDoc) {
        int v;
        v = idDoc;
        this.idDoc = v;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public void setDataIncarcare(Date dataIncarcare) {
        Date d;
        d = dataIncarcare;
        this.dataIncarcare = d;
    }

    public void setNumeProprietar(String numeProprietar) {
        this.numeProprietar = numeProprietar;
    }

    public void setDataExpirare(Date dataExpirare) {
        this.dataExpirare = dataExpirare;
    }

    public void setNumeInstitutie(String numeInstitutie) {
        String inst;
        inst = numeInstitutie;
        this.numeInstitutie = inst;
    }

    public void setTip(TipDocument tip) {
        this.tip = tip;
    }

    public void setCaleFisier(String caleFisier) {
        this.caleFisier = caleFisier;
    }

    public void setFisier(File fisier) {
        this.fisier = fisier;
    }

    public int getIdUtilizator() {
        return this.idUtilizator;
    }

    public void setIdUtilizator(int idUtilizator) {
        int u;
        u = idUtilizator;
        this.idUtilizator = u;
    }
}