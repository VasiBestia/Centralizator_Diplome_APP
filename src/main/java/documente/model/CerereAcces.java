package documente.model;

import java.util.Date;

public class CerereAcces {
    private int idC;
    private Date dCr;
    private String st;

    public CerereAcces(int idC) {
        this.idC = idC;
        Date d;
        d = new Date();
        this.dCr = d;
        String statusInitial;
        statusInitial = "In Asteptare";
        this.st = statusInitial;
    }

    public void aproba(int idP) {
        String p1;
        p1 = "Aprobata de parintele cu ID: ";
        String rez;
        rez = p1 + idP;
        this.st = rez;
    }

    public void respinge(int idP, String motiv) {
        String p1;
        p1 = "Respinsa de parintele ";
        String p2;
        p2 = p1 + idP;
        String p3;
        p3 = p2 + " Motiv: ";
        String finalSt;
        finalSt = p3 + motiv;
        this.st = finalSt;
    }

    public String getStatus() {
        String s;
        s = this.st;
        return s;
    }
}