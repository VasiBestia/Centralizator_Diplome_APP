package documente.repository;

import documente.model.Document;
import documente.model.TipDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DocumentDAO {

    @Autowired
    private DataSource dataSource;

    public boolean adaugaDocument(Document doc) {
        String p1;
        p1 = "INSERT INTO Document (titlu, numeProprietar, tip, numeInstitutie, ";
        String p2;
        p2 = "dataIncarcare, dataExpirare, caleFisier, idUtilizator) ";
        String sql;
        sql = p1 + p2 + "VALUES (?,?,?,?, GETDATE(),?,?,?)";

        boolean stare;
        stare = false;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String t;
            t = doc.getTitlu();
            ps.setString(1, t);

            String numP;
            numP = doc.getNumeProprietar();
            ps.setString(2, numP);

            TipDocument enumT;
            enumT = doc.getTip();
            String tipString;
            tipString = enumT.toString();
            ps.setString(3, tipString);

            String inst;
            inst = doc.getNumeInstitutie();
            ps.setString(4, inst);

            Date dE;
            dE = doc.getDataExpirare();
            if (dE!= null) {
                long msec;
                msec = dE.getTime();
                java.sql.Date sqlD;
                sqlD = new java.sql.Date(msec);
                ps.setDate(5, sqlD);
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            String path;
            path = doc.getCaleFisier();
            ps.setString(6, path);

            int u;
            u = doc.getIdUtilizator();
            ps.setInt(7, u);

            int res;
            res = ps.executeUpdate();

            if (res > 0) {
                stare = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            stare = false;
        }

        return stare;
    }

    public List<Document> obtineToateDocumentele() {
        List<Document> docList;
        docList = new ArrayList<>();

        String query;
        query = "SELECT * FROM Document";

        try (Connection con = dataSource.getConnection();
             PreparedStatement stm = con.prepareStatement(query)) {

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Document d;
                    d = new Document();

                    try {
                        int ident;
                        ident = rs.getInt("idDoc");
                        d.setIdDoc(ident);
                    } catch (Exception ex) {
                    }

                    String tit;
                    tit = rs.getString("titlu");
                    d.setTitlu(tit);

                    String o;
                    o = rs.getString("numeProprietar");
                    d.setNumeProprietar(o);

                    String iN;
                    iN = rs.getString("numeInstitutie");
                    d.setNumeInstitutie(iN);

                    String tipDinBaza = rs.getString("tip");

                    if (tipDinBaza != null && tipDinBaza.trim().length() > 0) {
                        String cautat = tipDinBaza.trim();
                        TipDocument[] toateTipurile = TipDocument.values();
                        boolean gasit = false;

                        int i = 0;
                        while (i < toateTipurile.length) {
                            TipDocument t = toateTipurile[i];

                            if (t.name().equalsIgnoreCase(cautat)) {
                                d.setTip(t);
                                gasit = true;
                                break;
                            }
                            i++;
                        }

                        if (!gasit) {
                            d.setTip(null);
                        }
                    } else {
                        d.setTip(null);
                    }

                    java.sql.Timestamp t1;
                    t1 = rs.getTimestamp("dataIncarcare");
                    if (t1!= null) {
                        long ms1;
                        ms1 = t1.getTime();
                        java.util.Date date1;
                        date1 = new java.util.Date(ms1);
                        d.setDataIncarcare(date1);
                    }

                    java.sql.Timestamp t2;
                    t2 = rs.getTimestamp("dataExpirare");
                    if (t2!= null) {
                        long ms2;
                        ms2 = t2.getTime();
                        java.util.Date date2;
                        date2 = new java.util.Date(ms2);
                        d.setDataExpirare(date2);
                    }

                    String loc;
                    loc = rs.getString("caleFisier");
                    d.setCaleFisier(loc);

                    docList.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return docList;
    }

    public Document gasesteDocumentDupaId(int id) {
        String sql;
        sql = "SELECT * FROM Document WHERE idDoc =?";
        Document docObj;
        docObj = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pStat = conn.prepareStatement(sql)) {

            pStat.setInt(1, id);

            try (ResultSet result = pStat.executeQuery()) {
                if (result.next()) {
                    docObj = new Document();

                    int idReal;
                    idReal = result.getInt("idDoc");
                    docObj.setIdDoc(idReal);

                    String s1;
                    s1 = result.getString("titlu");
                    docObj.setTitlu(s1);

                    String s2;
                    s2 = result.getString("numeProprietar");
                    docObj.setNumeProprietar(s2);

                    docObj.setNumeInstitutie(result.getString("numeInstitutie"));
                    docObj.setDataExpirare(result.getDate("dataExpirare"));

                    String rawTip;
                    rawTip = result.getString("tip");

                    if (rawTip != null) {
                        String valoareCurata = rawTip.trim();

                        if (valoareCurata.length() > 0) {
                            TipDocument[] listaTipuri = TipDocument.values();

                            for (int i = 0; i < listaTipuri.length; i++) {
                                TipDocument tipCurent = listaTipuri[i];

                                if (tipCurent.name().equalsIgnoreCase(valoareCurata)) {
                                    docObj.setTip(tipCurent);
                                    break;
                                }
                            }
                        }
                    }

                    String p;
                    p = result.getString("caleFisier");
                    docObj.setCaleFisier(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docObj;
    }

    public boolean stergeDocument(int id) {
        String q;
        q = "DELETE FROM Document WHERE idDoc =?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {

            ps.setInt(1, id);

            int r;
            r = ps.executeUpdate();

            boolean status;
            status = r > 0;
            return status;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizeazaDocument(Document doc) {
        String p1;
        p1 = "UPDATE Document SET titlu =?, numeProprietar =?, tip =?, ";
        String p2;
        p2 = "numeInstitutie =?, dataExpirare =?, caleFisier =? WHERE idDoc =?";
        String sql;
        sql = p1 + p2;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, doc.getTitlu());
            ps.setString(2, doc.getNumeProprietar());

            TipDocument tip;
            tip = doc.getTip();
            String s;
            s = tip.toString();
            ps.setString(3, s);

            ps.setString(4, doc.getNumeInstitutie());

            Date exp;
            exp = doc.getDataExpirare();
            if (exp!= null) {
                long t;
                t = exp.getTime();
                java.sql.Date d;
                d = new java.sql.Date(t);
                ps.setDate(5, d);
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setString(6, doc.getCaleFisier());

            int idD;
            idD = doc.getIdDoc();
            ps.setInt(7, idD);

            int n;
            n = ps.executeUpdate();

            boolean ok;
            ok = n > 0;
            return ok;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}