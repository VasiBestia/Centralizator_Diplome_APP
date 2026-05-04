document.addEventListener("DOMContentLoaded", () => {
  let f;
  f = document.getElementById("registerForm");

  let s;
  s = document.getElementById("mesajStatus");

  f.onsubmit = async (ev) => {
    ev.preventDefault();

    let sel;
    sel = "button span";
    let b;
    b = f.querySelector(sel);
    b.innerText = "Se procesează...";

    let vNume;
    vNume = document.getElementById("nume").value;
    let vPrenume;
    vPrenume = document.getElementById("prenume").value;
    let vCnp;
    vCnp = document.getElementById("cnp").value;
    let vEmail;
    vEmail = document.getElementById("email").value;
    let vPass;
    vPass = document.getElementById("parola").value;

    let payload;
    payload = {};
    payload.nume = vNume;
    payload.prenume = vPrenume;
    payload.cnp = vCnp;
    payload.email = vEmail;
    payload.parola = vPass;

    try {
      let path;
      path = "/api/register";

      let config;
      config = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      };

      let r;
      r = await fetch(path, config);

      let success;
      success = r.ok;

      if (success === true) {
        let msgOk;
        msgOk = "Cont creat! Te redirecționăm la login...";
        s.innerText = msgOk;
        s.style.display = "block";

        setTimeout(() => {
          let target;
          target = "login.html";
          window.location.href = target;
        }, 2000);
      } else {
        let textErr;
        textErr = await r.text();
        s.innerText = textErr;
        s.style.display = "block";

        let bgErr;
        bgErr = "rgba(255, 107, 107, 0.1)";
        s.style.background = bgErr;
      }
    } catch (e) {
      alert("Eroare de conexiune la server.");
    } finally {
      let labelBtn;
      labelBtn = "Înregistrare";
      b.innerText = labelBtn;
    }
  };
});
