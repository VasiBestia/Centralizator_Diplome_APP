document.addEventListener("DOMContentLoaded", () => {
  let fSetari;
  fSetari = document.getElementById("settingsForm");

  let iNume;
  iNume = document.getElementById("numeSetari");
  let iPrenume;
  iPrenume = document.getElementById("prenumeSetari");
  let iPoza;
  iPoza = document.getElementById("inputPoza");
  let pCerc;
  pCerc = document.getElementById("previewPoza");
  let sBox;
  sBox = document.getElementById("mesajStatusSetari");

  let imgCache;
  imgCache = "";

  async function incarcaDateCurente() {
    let adresa;
    adresa = "/api/user/me";
    try {
      let r;
      r = await fetch(adresa);

      let st;
      st = r.status;
      if (st === 401) {
        let l;
        l = "login.html";
        window.location.replace(l);
        return;
      }

      let ok;
      ok = r.ok;
      if (ok === true) {
        let date;
        date = await r.json();

        let nV;
        nV = date.nume;
        iNume.value = nV;

        let pV;
        pV = date.prenume;
        iPrenume.value = pV;

        let pImg;
        pImg = date.pozaProfil;
        if (pImg) {
          pCerc.innerText = "";
          let urlCss;
          urlImg = "url(" + pImg + ")";
          pCerc.style.backgroundImage = urlImg;
          imgCache = pImg;
        } else {
          let initiala;
          initiala = nV.charAt(0);
          pCerc.innerText = initiala;
        }
      }
    } catch (eroare) {
      console.error(eroare);
    }
  }

  incarcaDateCurente();

  iPoza.addEventListener("change", (event) => {
    let t;
    t = event.target;
    let fisier;
    fisier = t.files[0];

    if (fisier) {
      let cititor;
      cititor = new FileReader();

      cititor.onloadend = () => {
        let continut;
        continut = cititor.result;
        imgCache = continut;

        pCerc.innerText = "";
        let cssVal;
        cssVal = "url(" + imgCache + ")";
        pCerc.style.backgroundImage = cssVal;
      };

      cititor.readAsDataURL(fisier);
    }
  });

  fSetari.onsubmit = async (ev) => {
    ev.preventDefault();

    let label;
    label = fSetari.querySelector("button span");
    label.innerText = "Se salvează...";

    let dForm;
    dForm = new FormData();

    let valN;
    valN = iNume.value;
    dForm.append("nume", valN);

    let valP;
    valP = iPrenume.value;
    dForm.append("prenume", valP);

    if (iPoza.files[0]) {
      dForm.append("poza", iPoza.files[0]);
    }

    try {
      let caleOp;
      caleOp = "/api/user/settings";

      let rOp;
      rOp = await fetch(caleOp, {
        method: "PUT",
        body: dForm,
      });

      let statusOp;
      statusOp = rOp.ok;

      if (statusOp === true) {
        let msgOk;
        msgOk = "Datele au fost salvate cu succes!";
        sBox.innerText = msgOk;
        sBox.style.display = "block";

        let bgS;
        bgS = "rgba(94, 106, 210, 0.2)";
        sBox.style.background = bgS;
        sBox.style.color = "#fff";
      } else {
        let msgErr;
        msgErr = "A apărut o eroare la salvare.";
        sBox.innerText = msgErr;
        sBox.style.display = "block";
      }
    } catch (errSys) {
      console.error(errSys);
    } finally {
      let tFinal;
      tFinal = "Salvează Modificările";
      label.innerText = tFinal;
    }
  };
});
