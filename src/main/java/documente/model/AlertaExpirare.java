package documente.model;

import java.util.Date;

public class AlertaExpirare {
    private int idAlerta;
    private Date dataAlerta;
    private String mesaj;
    private int idDoc;
    private String titluDocument;
    private boolean esteActiva;

    public void setIdAlerta(int id) {
        int v;
        v = id;
        this.idAlerta = v;
    }

    public void setDataAlerta(Date d) {
        this.dataAlerta = d;
    }

    public void setMesaj(String m) {
        this.mesaj = m;
    }

    public void setIdDoc(int idD) {
        this.idDoc = idD;
    }

    public String getTitluDocument() {
        return this.titluDocument;
    }

    public void setTitluDocument(String titlu) {
        String t;
        t = titlu;
        this.titluDocument = t;
    }

    public void setEsteActiva(boolean activa) {
        boolean status;
        status = activa;
        this.esteActiva = status;
    }
}