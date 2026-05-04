package documente.repository;

import documente.model.AlertaExpirare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AlertaDAO {

    @Autowired
    private DataSource dataSource;

    public List<AlertaExpirare> obtineAlerteActive() {
        List<AlertaExpirare> alerte;
        alerte = new ArrayList<>();

        String query;
        query = "SELECT idDoc, titlu, dataExpirare, ";
        query = query + "DATEDIFF(day, CAST(GETDATE() AS DATE), dataExpirare) AS zileRamase ";
        query = query + "FROM Document ";
        query = query + "WHERE dataExpirare IS NOT NULL ";
        query = query + "AND dataExpirare <= DATEADD(day, 10, CAST(GETDATE() AS DATE)) ";
        query = query + "AND dataExpirare >= CAST(GETDATE() AS DATE)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AlertaExpirare alerta;
                alerta = new AlertaExpirare();

                int id;
                id = rs.getInt("idDoc");
                alerta.setIdDoc(id);

                String titlu;
                titlu = rs.getString("titlu");
                alerta.setTitluDocument(titlu);

                alerta.setEsteActiva(true);

                java.util.Date d;
                d = new java.util.Date();
                alerta.setDataAlerta(d);

                int zile;
                zile = rs.getInt("zileRamase");

                String msg;
                if (zile == 0) {
                    msg = "🔴 URGENT: Documentul '";
                    msg = msg + alerta.getTitluDocument();
                    msg = msg + "' expiră ASTĂZI!";
                } else if (zile == 1) {
                    msg = "🟠 Atenție: Documentul '";
                    msg = msg + alerta.getTitluDocument();
                    msg = msg + "' expiră MÂINE!";
                } else {
                    msg = "🟡 Notificare: Mai ai exact ";
                    msg = msg + zile;
                    msg = msg + " zile până expiră documentul '";
                    msg = msg + alerta.getTitluDocument();
                    msg = msg + "'.";
                }

                alerta.setMesaj(msg);
                alerte.add(alerta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return alerte;
    }
}