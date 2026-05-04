document.addEventListener("DOMContentLoaded", () => {
  let formular;
  formular = document.getElementById("forgotForm");

  let statusBox;
  statusBox = document.getElementById("mesajStatus");

  formular.onsubmit = async (ev) => {
    ev.preventDefault();

    let selectorButon;
    selectorButon = "button span";
    let btn;
    btn = formular.querySelector(selectorButon);

    let tInitial;
    tInitial = btn.innerText;
    btn.innerText = "Se verifică...";

    let dEmail;
    dEmail = document.getElementById("email").value;
    let dCnp;
    dCnp = document.getElementById("cnp").value;
    let dPass;
    dPass = document.getElementById("parolaNoua").value;

    let pachetDate;
    pachetDate = {};
    pachetDate.email = dEmail;
    pachetDate.cnp = dCnp;
    pachetDate.parolaNoua = dPass;

    try {
      let urlApi;
      urlOp = "/api/reset-password";

      let configFetch;
      configFetch = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(pachetDate),
      };

      let raspuns;
      raspuns = await fetch(urlOp, configFetch);

      let textR;
      textR = await raspuns.text();

      let isOk;
      isOk = raspuns.ok;

      if (isOk === true) {
        statusBox.innerText = textR;
        statusBox.style.display = "block";

        let bgS;
        bgS = "rgba(94, 106, 210, 0.2)";
        statusBox.style.background = bgS;
        statusBox.style.borderColor = "var(--culoare_brand)";
        statusBox.style.color = "#fff";

        setTimeout(() => {
          let loginPage;
          loginPage = "login.html";
          window.location.href = loginPage;
        }, 3000);
      } else {
        statusBox.innerText = textR;
        statusBox.style.display = "block";

        let bgE;
        bgE = "rgba(255, 107, 107, 0.1)";
        statusBox.style.background = bgE;
        statusBox.style.borderColor = "rgba(255, 107, 107, 0.2)";
        statusBox.style.color = "#ff6b6b";
      }
    } catch (eroareConexiune) {
      let msgE;
      msgE = "Eroare de conexiune la server.";
      statusBox.innerText = msgE;
      statusBox.style.display = "block";
    } finally {
      btn.innerText = tInitial;
    }
  };
});
