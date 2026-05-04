package documente.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecuritateFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 1. Definim rutele publice (care pot fi accesate fără logare)
        boolean esteResursaPublica = path.equals("/login.html") ||
                path.equals("/register.html") ||
                path.equals("/forgot.html") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.startsWith("/api/login") ||
                path.startsWith("/api/register") ||
                path.startsWith("/api/reset-password");
                path.startsWith("/api/logout");// NOU ADAUGAT AICI

        if (esteResursaPublica) {
            // Îl lăsăm să treacă (are voie să vadă login-ul și să descarce CSS-ul/JS-ul)
            chain.doFilter(request, response);
            return;
        }

        // 2. Pentru restul paginilor (inclusiv "/" sau "/index.html"), verificăm sesiunea
        HttpSession session = req.getSession(false);
        boolean esteLogat = (session != null && session.getAttribute("idUtilizator") != null);

        if (!esteLogat) {
            // 3. Nu e logat? Îl zburăm instant pe pagina de login, înainte să vadă interfața!
            res.sendRedirect("/login.html");
            return;
        }

        // 4. Dacă e logat, îl lăsăm să acceseze resursa cerută
        chain.doFilter(request, response);
    }
}