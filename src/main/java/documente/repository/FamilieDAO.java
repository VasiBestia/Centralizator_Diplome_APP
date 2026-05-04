package documente.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FamilieDAO {
    @Autowired
    private DataSource dataSource;

    public List<Map<String, Object>> obtineMembri() {
        List<Map<String, Object>> lista;
        lista = new ArrayList<>();

        String query;
        query = "SELECT id, nume, prenume, email, esteAdministrator FROM MembruFamilie";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> m;
                m = new HashMap<>();

                int idVal;
                idVal = rs.getInt("id");
                m.put("id", idVal);

                String n;
                n = rs.getString("nume");
                m.put("nume", n);

                String p;
                p = rs.getString("prenume");
                m.put("prenume", p);

                String e;
                e = rs.getString("email");
                m.put("email", e);

                boolean admin;
                admin = rs.getBoolean("esteAdministrator");
                m.put("esteAdministrator", admin);

                lista.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean adaugaMembru(String nume, String prenume, String cnp, String email, String parola, boolean esteAdmin) {
        String sql;
        sql = "INSERT INTO MembruFamilie (nume, prenume, cnp, email, parola, esteAdministrator) ";
        sql = sql + "VALUES (?,?,?,?,?,?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nume);
            ps.setString(2, prenume);
            ps.setString(3, cnp);
            ps.setString(4, email);
            ps.setString(5, parola);

            boolean b;
            b = esteAdmin;
            ps.setBoolean(6, b);

            int r;
            r = ps.executeUpdate();

            boolean status;
            if (r > 0) {
                status = true;
            } else {
                status = false;
            }
            return status;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}