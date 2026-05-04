package documente.model;

public enum TipDocument {
    Identitate("Carte de Identitate"),
    Pasaport("Pașaport"),
    PermisConducere("Permis de Conducere"),
    Diploma("Diplomă Studii"),
    CertificatCasatorie("Certificat de Căsătorie"),
    CertificatDeNastere("Certificat de Naștere"),
    ActeCasa("Acte Casă"),
    ActeMasina("Acte Mașină");

    private final String numeUman;

    TipDocument(String numeUman) {
        this.numeUman = numeUman;
    }

    @Override
    public String toString() {
        return this.numeUman;
    }
}