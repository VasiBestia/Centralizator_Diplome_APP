package documente.repository;

import documente.model.MembruFamilie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MembruFamilieDAO {

    @Autowired
    private DataSource dataSource;

    public MembruFamilie gasesteDupaEmail(String email) {
        String sql;
        sql = "SELECT * FROM MembruFamilie ";
        sql = sql + "WHERE email =?";

        MembruFamilie membru;
        membru = null;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    membru = new MembruFamilie();

                    int idVal;
                    idVal = rs.getInt("id");
                    membru.setId(idVal);

                    String n;
                    n = rs.getString("nume");
                    membru.setNume(n);

                    String p;
                    p = rs.getString("prenume");
                    membru.setPrenume(p);

                    String cnpVal;
                    cnpVal = rs.getString("CNP");
                    membru.setCNP(cnpVal);

                    String e;
                    e = rs.getString("email");
                    membru.setEmail(e);

                    String pass;
                    pass = rs.getString("parola");
                    membru.setParola(pass);

                    boolean admin;
                    admin = rs.getBoolean("esteAdministrator");
                    membru.setEsteAdministrator(admin);

                    boolean restr;
                    restr = rs.getBoolean("areRestrictiiAcces");
                    membru.setAreRestrictiiAcces(restr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return membru;
    }

    public boolean inregistrare(MembruFamilie m) {
        String sqlCheck;
        sqlCheck = "SELECT COUNT(*) FROM MembruFamilie ";
        sqlCheck = sqlCheck + "WHERE email =?";

        String sqlInsert;
        sqlInsert = "INSERT INTO MembruFamilie (nume, prenume, CNP, email, parola, ";
        sqlInsert = sqlInsert + "esteAdministrator, areRestrictiiAcces) ";
        sqlInsert = sqlInsert + "VALUES (?,?,?,?,?,?,?)";

        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement psCheck = c.prepareStatement(sqlCheck)) {
                String emailM;
                emailM = m.getEmail();
                psCheck.setString(1, emailM);

                ResultSet rs;
                rs = psCheck.executeQuery();

                if (rs.next()) {
                    int count;
                    count = rs.getInt(1);
                    if (count > 0) {
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = c.prepareStatement(sqlInsert)) {
                ps.setString(1, m.getNume());
                ps.setString(2, m.getPrenume());
                ps.setString(3, m.getCNP());
                ps.setString(4, m.getEmail());
                ps.setString(5, m.getParola());

                boolean isAdmin;
                isAdmin = m.isEsteAdministrator();
                ps.setBoolean(6, isAdmin);

                boolean isRestr;
                isRestr = m.isAreRestrictiiAcces();
                ps.setBoolean(7, isRestr);

                int affected;
                affected = ps.executeUpdate();

                boolean success;
                success = affected > 0;
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reseteazaParola(String email, String cnp, String parolaNouaCriptata) {
        String sqlCheck;
        sqlCheck = "SELECT COUNT(*) FROM MembruFamilie ";
        sqlCheck = sqlCheck + "WHERE email =? AND CNP =?";

        String sqlUpdate;
        sqlUpdate = "UPDATE MembruFamilie SET parola =? ";
        sqlUpdate = sqlUpdate + "WHERE email =? AND CNP =?";

        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement psCheck = c.prepareStatement(sqlCheck)) {
                psCheck.setString(1, email);
                psCheck.setString(2, cnp);

                ResultSet rs;
                rs = psCheck.executeQuery();

                if (rs.next()) {
                    int num;
                    num = rs.getInt(1);
                    if (num == 0) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            try (PreparedStatement psUpdate = c.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, parolaNouaCriptata);
                psUpdate.setString(2, email);
                psUpdate.setString(3, cnp);

                int rows;
                rows = psUpdate.executeUpdate();

                boolean rez;
                rez = rows > 0;
                return rez;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public MembruFamilie gasesteDupaId(int id) {
        String sql;
        sql = "SELECT * FROM MembruFamilie WHERE id =?";

        MembruFamilie membru;
        membru = null;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    membru = new MembruFamilie();

                    int idI;
                    idI = rs.getInt("id");
                    membru.setId(idI);

                    membru.setNume(rs.getString("nume"));
                    membru.setPrenume(rs.getString("prenume"));
                    membru.setCNP(rs.getString("CNP"));
                    membru.setEmail(rs.getString("email"));
                    membru.setParola(rs.getString("parola"));
                    membru.setEsteAdministrator(rs.getBoolean("esteAdministrator"));
                    membru.setAreRestrictiiAcces(rs.getBoolean("areRestrictiiAcces"));

                    try {
                        String pP;
                        pP = rs.getString("pozaProfil");
                        membru.setPozaProfil(pP);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return membru;
    }

    public boolean actualizeazaProfil(int id, String nume, String prenume, String pozaProfil) {
        String query;
        query = "UPDATE MembruFamilie SET nume =?, prenume =?, pozaProfil =? ";
        query = query + "WHERE id =?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, nume);
            ps.setString(2, prenume);
            ps.setString(3, pozaProfil);
            ps.setInt(4, id);

            int affectedRows;
            affectedRows = ps.executeUpdate();

            boolean status;
            status = affectedRows > 0;
            return status;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}