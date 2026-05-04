document.addEventListener("DOMContentLoaded", () => {
  let formularConectare;
  formularConectare = document.getElementById("loginForm");

  let zonaEroare;
  zonaEroare = document.getElementById("mesajEroare");

  formularConectare.onsubmit = async (ev) => {
    ev.preventDefault();

    let selectorTextButon;
    selectorTextButon = 'button[type="submit"] span';
    let elemBtn;
    elemBtn = formularConectare.querySelector(selectorTextButon);

    let textOriginal;
    textOriginal = elemBtn.innerText;

    elemBtn.innerText = "Se verifică...";

    let butonPrincipal;
    butonPrincipal = formularConectare.querySelector("button");
    butonPrincipal.disabled = true;

    zonaEroare.style.display = "none";

    let emailVal;
    emailVal = document.getElementById("email").value;
    let parolaVal;
    parolaVal = document.getElementById("parola").value;

    let dateAcces;
    dateAcces = {};
    dateAcces.email = emailVal;
    dateAcces.parola = parolaVal;

    try {
      let urlLogin;
      urlLogin = "/api/login";

      let setariFetch;
      setariFetch = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dateAcces),
      };

      let raspuns;
      raspuns = await fetch(urlLogin, setariFetch);

      let esteValid;
      esteValid = raspuns.ok;

      if (esteValid === true) {
        let dateUtilizator;
        dateUtilizator = await raspuns.json();

        let numeUtilizator;
        numeUtilizator = dateUtilizator.nume;
        localStorage.setItem("user_nume", numeUtilizator);

        let rolUtilizator;
        let verificareAdmin;
        verificareAdmin = dateUtilizator.esteAdministrator;

        if (verificareAdmin === true) {
          rolUtilizator = "PARINTE";
        } else {
          rolUtilizator = "COPIL";
        }

        localStorage.setItem("user_rol", rolUtilizator);

        let paginaPrincipala;
        paginaPrincipala = "index.html";
        window.location.href = paginaPrincipala;
      } else {
        let textErr;
        textErr = await raspuns.text();
        zonaEroare.innerText = textErr;
        zonaEroare.style.display = "block";
      }
    } catch (eroareSistem) {
      let mesajFiltru;
      mesajFiltru = "Nu s-a putut conecta la server. Verifică conexiunea.";
      zonaEroare.innerText = mesajFiltru;
      zonaEroare.style.display = "block";
    } finally {
      elemBtn.innerText = textOriginal;
      butonPrincipal.disabled = false;
    }
  };
});
