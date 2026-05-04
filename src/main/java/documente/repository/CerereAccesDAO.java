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
public class CerereAccesDAO {

    @Autowired
    private DataSource dataSource;

    public boolean adaugaCerere(int idCopil, int idDoc, int idParinteAtribuit) {
        String sql;
        sql = "INSERT INTO CerereAcces (idCopil, idDoc, dataCreare, status, idParinteAtribuit) ";
        sql = sql + "VALUES (?,?, GETDATE(), 'In Asteptare',?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCopil);
            ps.setInt(2, idDoc);
            ps.setInt(3, idParinteAtribuit);

            int r;
            r = ps.executeUpdate();
            boolean succes;
            succes = r > 0;
            return succes;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> obtineCereriInAsteptare() {
        String q;
        q = "SELECT c.idCerere, m.nume AS numeCopil, d.titlu AS numeDoc ";
        q = q + "FROM CerereAcces c ";
        q = q + "JOIN MembruFamilie m ON c.idCopil = m.id ";
        q = q + "JOIN Document d ON c.idDoc = d.idDoc ";
        q = q + "WHERE c.status = 'In Asteptare'";

        List<Map<String, Object>> lista;
        lista = new ArrayList<>();

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(q);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> cerere;
                cerere = new HashMap<>();

                int idC;
                idC = rs.getInt("idCerere");
                cerere.put("idCerere", idC);

                String nC;
                nC = rs.getString("numeCopil");
                cerere.put("numeCopil", nC);

                String nD;
                nD = rs.getString("numeDoc");
                cerere.put("numeDoc", nD);

                lista.add(cerere);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizeazaStatusCerere(int idCerere, String statusNou) {
        String query;
        query = "UPDATE CerereAcces SET status =? WHERE idCerere =?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query)) {

            ps.setString(1, statusNou);
            ps.setInt(2, idCerere);

            int modificate;
            modificate = ps.executeUpdate();

            if (modificate > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> obtineParinti() {
        List<Map<String, Object>> lista;
        lista = new ArrayList<>();

        String query;
        query = "SELECT id, nume FROM MembruFamilie WHERE esteAdministrator = 1";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> parinte;
                parinte = new HashMap<>();

                int idP;
                idP = rs.getInt("id");
                parinte.put("id", idP);

                String numeP;
                numeP = rs.getString("nume");
                parinte.put("nume", numeP);

                lista.add(parinte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Integer> obtineDocumenteAprobatePentruCopil(int idCopil) {
        List<Integer> listaAprobate;
        listaAprobate = new ArrayList<>();

        String sql;
        sql = "SELECT idDoc FROM CerereAcces WHERE idCopil =? AND status = 'Aprobata'";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCopil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id;
                    id = rs.getInt("idDoc");
                    listaAprobate.add(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaAprobate;
    }
}