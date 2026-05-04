package documente.model;

public class Copil extends MembruFamilie {
    private boolean areRestrictiiAcces;

    public Copil(int id, String nume, String prenume, String CNP, String email, String parola) {
        super(id, nume, prenume, CNP, email, parola);
        this.areRestrictiiAcces = true;
    }

    public void initiazaCerereAcces(int idDoc, String motiv) {
        String info;
        info = "Cerere de acces trimisă pentru documentul ID: ";
        String msg1;
        msg1 = info + idDoc;
        System.out.println(msg1);

        String m;
        m = "Motivul invocat: ";
        String msg2;
        msg2 = m + motiv;
        System.out.println(msg2);
    }

    public boolean isAreRestrictiiAcces() {
        boolean r;
        r = this.areRestrictiiAcces;
        return r;
    }
}