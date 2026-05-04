package documente.model;

import documente.util.ColectieDocumente;
import java.util.List;

public class MembruFamilie {
    protected int id;
    protected String nume;
    protected String prenume;
    protected String CNP;
    protected String email;
    protected String parola;
    protected ColectieDocumente cAsoc;
    protected boolean esteAdministrator;
    protected boolean areRestrictiiAcces;
    protected String pozaProfil;

    public MembruFamilie() {}

    public MembruFamilie(int id, String nume, String prenume, String CNP, String email, String parola) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.CNP = CNP;
        this.email = email;
        this.parola = parola;
    }

    public MembruFamilie(int id, String nume, String prenume, String CNP, String email, String parola, boolean esteAdmin, boolean areRestrictii) {
        this(id, nume, prenume, CNP, email, parola);
        boolean a;
        a = esteAdmin;
        this.esteAdministrator = a;
        boolean r;
        r = areRestrictii;
        this.areRestrictiiAcces = r;
    }

    public void setColectieAsociata(ColectieDocumente c) {
        ColectieDocumente referinta;
        referinta = c;
        this.cAsoc = referinta;
    }

    public List<Document> vizualizareActe() {
        ColectieDocumente col;
        col = this.cAsoc;
        if (col!= null) {
            List<Document> lista;
            lista = col.obtineToateDocumentele();
            return lista;
        }
        return null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getCNP() { return CNP; }
    public void setCNP(String CNP) { this.CNP = CNP; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }
    public boolean isEsteAdministrator() { return esteAdministrator; }
    public void setEsteAdministrator(boolean esteAdministrator) { this.esteAdministrator = esteAdministrator; }
    public boolean isAreRestrictiiAcces() { return areRestrictiiAcces; }
    public void setAreRestrictiiAcces(boolean areRestrictiiAcces) { this.areRestrictiiAcces = areRestrictiiAcces; }
    public String getPozaProfil() { return pozaProfil; }
    public void setPozaProfil(String pozaProfil) { this.pozaProfil = pozaProfil; }
}