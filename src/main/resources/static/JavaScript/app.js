document.addEventListener("DOMContentLoaded", () => {
  let url_baza = "/api/documente";
  let formular_adaugare = document.getElementById("addDocForm");
  let zona_documente = document.getElementById("documentsContainer");
  let span_contor = document.getElementById("totalDocs");

  function verificaLogare(raspunsServer) {
    if (raspunsServer.status === 401) {
      window.location.replace("login.html");
      return false;
    }
    return true;
  }

  let btnProfil = document.getElementById("btnProfil");
  let dropdownProfil = document.getElementById("dropdownProfil");
  let btnDeconectare = document.getElementById("btnDeconectare");

  btnProfil.addEventListener("click", (e) => {
    dropdownProfil.classList.toggle("activ");
    e.stopPropagation();
  });

  document.addEventListener("click", () => {
    dropdownProfil.classList.remove("activ");
  });

  btnDeconectare.addEventListener("click", async () => {
    try {
      let r = await fetch("/api/logout", { method: "POST" });
      if (r.ok) {
        window.location.replace("login.html");
      }
    } catch (e) {}
  });

  function animatieNumaratoare(element, valoareStart, valoareFinala, durata) {
    let startTimestamp = null;
    let pasAnimatie = (timestamp) => {
      if (!startTimestamp) startTimestamp = timestamp;
      let progres = Math.min((timestamp - startTimestamp) / durata, 1);
      element.innerText = Math.floor(
        progres * (valoareFinala - valoareStart) + valoareStart,
      );
      if (progres < 1) window.requestAnimationFrame(pasAnimatie);
    };
    window.requestAnimationFrame(pasAnimatie);
  }

  async function incarca_acte() {
    try {
      let r;
      r = await fetch(url_baza);

      let l;
      l = verificaLogare(r);
      if (l === false || r.ok === false) {
        return;
      }

      let lista;
      lista = await r.json();
      window.listaActeGlobala = lista;

      let acteAprobate;
      acteAprobate = [];

      let uCurent;
      uCurent = window.utilizatorCurent;

      let eParinte;
      eParinte = false;
      if (uCurent) {
        let adm;
        adm = uCurent.esteAdministrator;
        if (adm === true || adm === "true") {
          eParinte = true;
        }
      }

      if (eParinte === false) {
        try {
          let rAprobate;
          rAprobate = await fetch("/api/cereri/aprobate");
          if (rAprobate.ok === true) {
            acteAprobate = await rAprobate.json();
          }
        } catch (errA) {}
      }

      zona_documente.innerHTML = "";

      let nr_elemente;
      nr_elemente = lista.length;

      let vCur;
      vCur = parseInt(span_contor.innerText) || 0;
      animatieNumaratoare(span_contor, vCur, nr_elemente, 800);

      if (nr_elemente === 0) {
        let empty;
        empty = '<p class="empty-state">Nu ai niciun document salvat.</p>';
        zona_documente.innerHTML = empty;
        return;
      }

      let i;
      i = 0;
      while (i < lista.length) {
        let d;
        d = lista[i];

        let data_str;
        data_str = "Fără expirare";

        if (d.dataExpirare) {
          let dE;
          dE = new Date(d.dataExpirare);
          data_str = "Expiră la: " + dE.toLocaleDateString("ro-RO");
        } else {
          if (d.dataIncarcare) {
            let dI;
            dI = new Date(d.dataIncarcare);
            data_str = "Încărcat la: " + dI.toLocaleDateString("ro-RO");
          }
        }

        let tip_raw;
        tip_raw = d.tip || d.tipDocument || "General";

        let tip_final;
        tip_final = "General";

        if (tip_raw && typeof tip_raw === "object") {
          if (tip_raw.name) {
            tip_final = tip_raw.name;
          }
        } else {
          if (typeof tip_raw === "string" && tip_raw !== "null") {
            tip_final = tip_raw;
          }
        }

        let numeAfisare;
        numeAfisare = tip_final;

        const dictionarTipuri = {
          Identitate: "Identitate",
          Pasaport: "Pașaport",
          Diploma: "Diplomă",
          CertificatCasatorie: "Certificat Căsătorie",
          ActeCasa: "Acte Casă",
          ActeMasina: "Acte Mașină",
        };

        let mapped;
        mapped = dictionarTipuri[tip_final];
        if (mapped) {
          numeAfisare = mapped;
        }

        let card_div;
        card_div = document.createElement("div");
        card_div.className = "doc-card";

        let sectiune_butoane;
        sectiune_butoane = "";

        let esteProprietarulActului;
        esteProprietarulActului = false;
        if (uCurent) {
          if (d.idProprietar === uCurent.id) {
            esteProprietarulActului = true;
          }
        }

        let areAccesAprobat;
        areAccesAprobat = acteAprobate.includes(d.idDoc);

        if (
          eParinte === true ||
          esteProprietarulActului === true ||
          areAccesAprobat === true
        ) {
          let butoaneExtra;
          butoaneExtra = "";

          if (eParinte === true || esteProprietarulActului === true) {
            let tEsc;
            tEsc = (d.titlu || "").replace(/'/g, "\\'");
            let pEsc;
            pEsc = (d.numeProprietar || "").replace(/'/g, "\\'");
            let iEsc;
            iEsc = (d.numeInstitutie || "").replace(/'/g, "\\'");

            let bEdit;
            bEdit =
              '<button class="btn-card btn-editare" onclick="pregatesteEditare(' +
              d.idDoc +
              ", '" +
              tEsc +
              "', '" +
              pEsc +
              "', '" +
              iEsc +
              "', '" +
              tip_final +
              "')\">✏️</button>";

            let bDel;
            bDel =
              '<button class="btn-card btn-stergere" onclick="stergeDocument(' +
              d.idDoc +
              ')">🗑️</button>';

            butoaneExtra = bEdit + bDel;
          }

          let bDown;
          bDown = '<div class="card-actions">';
          bDown =
            bDown +
            '<button class="btn-card btn-descarcare" onclick="window.location.href=\'/api/documente/download/' +
            d.idDoc +
            "'\">⬇️ Descarcă</button>";
          bDown = bDown + butoaneExtra + "</div>";
          sectiune_butoane = bDown;
        } else {
          let bReq;
          bReq = '<div class="card-actions">';
          bReq =
            bReq +
            '<button class="btn-card btn-solicita" onclick="trimiteCerereAcces(' +
            d.idDoc +
            ')">🔒 Solicită Acces</button>';
          bReq = bReq + "</div>";
          sectiune_butoane = bReq;
        }

        let content;
        content = "<div>";
        content =
          content +
          '<div class="doc-title">' +
          (d.titlu || "Fara titlu") +
          "</div>";
        content =
          content +
          '<div class="doc-owner">Proprietar: ' +
          (d.numeProprietar || "Nespecificat") +
          "</div>";
        content =
          content +
          '<div class="doc-owner">Sursa: ' +
          (d.numeInstitutie || "Nespecificat") +
          "</div>";
        content =
          content +
          '<div class="doc-owner"><strong>' +
          data_str +
          "</strong></div>";
        content =
          content + '<span class="doc-badge">' + numeAfisare + "</span>";
        content = content + "</div>" + sectiune_butoane;

        card_div.innerHTML = content;
        zona_documente.appendChild(card_div);

        i = i + 1;
      }
    } catch (errM) {}
  }

  window.incarcaCereriAsteptare = async function () {
    try {
      let r = await fetch("/api/cereri");
      if (r.ok) {
        let cereri = await r.json();
        let panou = document.getElementById("panou-cereri");
        let container = document.getElementById("lista-cereri-container");

        if (cereri.length === 0) {
          if (panou) panou.style.display = "none";
          return;
        }
        if (panou) panou.style.display = "block";

        let htmlFinal = "";
        for (let k = 0; k < cereri.length; k++) {
          let c = cereri[k];
          htmlFinal += `
            <div class="cerere-item">
              <span>Copilul <strong>${c.numeCopil}</strong> vrea să vadă <strong>${c.numeDoc}</strong></span>
              <div class="cerere-actions">
                <button class="btn-cerere btn-vezi" onclick="window.open('/api/documente/download/${c.idDoc}', '_blank')">👀 Vezi Act</button>
                <button class="btn-cerere btn-aproba" onclick="proceseazaCerere(${c.idCerere}, 'aprobare')">✅ Aprobă</button>
                <button class="btn-cerere btn-respinge" onclick="proceseazaCerere(${c.idCerere}, 'respingere')">❌ Respinge</button>
              </div>
            </div>
          `;
        }
        if (container) container.innerHTML = htmlFinal;
      }
    } catch (errC) {}
  };

  async function incarcaDateProfil() {
    try {
      let r = await fetch("/api/user/me");
      if (r.ok) {
        let user = await r.json();
        window.utilizatorCurent = user;

        let avatarHeader = document.getElementById("avatarInitiala");

        if (user.pozaProfil && user.pozaProfil.length > 10) {
          avatarHeader.innerText = "";
          avatarHeader.style.backgroundImage = "url(" + user.pozaProfil + ")";
          avatarHeader.style.backgroundSize = "cover";
          avatarHeader.style.backgroundPosition = "center";
        } else {
          avatarHeader.innerText = user.nume
            ? user.nume.charAt(0).toUpperCase()
            : "F";
        }

        let esteCopil =
          user.esteAdministrator === false ||
          user.esteAdministrator === "false";

        if (esteCopil) {
          let fBox = document.querySelector(".zona_formular_box");
          if (fBox) fBox.style.display = "none";
          let pFam = document.getElementById("panou-familie");
          if (pFam) pFam.style.display = "none";
          let zonaLista = document.querySelector(".zona_lista_acte");
          if (zonaLista) zonaLista.style.gridColumn = "span 12";
        } else {
          let fBoxVis = document.querySelector(".zona_formular_box");
          if (fBoxVis) fBoxVis.style.display = "block";
          let panouFamilie = document.getElementById("panou-familie");
          if (panouFamilie) panouFamilie.style.display = "block";

          if (typeof window.incarcaCereriAsteptare === "function")
            window.incarcaCereriAsteptare();
          if (typeof window.incarcaMembriFamilie === "function")
            window.incarcaMembriFamilie();
        }
      }
    } catch (errP) {}
  }

  incarcaDateProfil();
  window.idDocumentDeEditat = null;

  window.stergeDocument = async function (idDoc) {
    if (!confirm("Ești sigur că vrei să ștergi acest document definitiv?"))
      return;
    try {
      let r = await fetch("/api/documente/" + idDoc, { method: "DELETE" });
      if (r.ok) incarca_acte();
      else alert("A apărut o eroare la ștergerea documentului.");
    } catch (errD) {}
  };

  window.pregatesteEditare = function (id, nume, prop, src, tip) {
    window.idDocumentDeEditat = id;

    document.getElementById("titlu").value = nume;
    document.getElementById("numeProprietar").value = prop;
    document.getElementById("numeInstitutie").value = src;

    let selectTip = document.getElementById("tip");

    if (tip && tip !== "null") {
      let optiuni = selectTip.options;
      let gasit = false;

      for (let i = 0; i < optiuni.length; i++) {
        if (optiuni[i].text === tip || optiuni[i].value === tip) {
          selectTip.selectedIndex = i;
          gasit = true;
          break;
        }
      }

      if (!gasit) {
        selectTip.value = "Identitate";
      }
    }

    document.getElementById("fisierDocument").removeAttribute("required");
    document.querySelector(".zona_formular_box h2").innerText =
      "Editează Documentul";

    let butonSalvare = document.querySelector(
      "#addDocForm button[type='submit']",
    );
    let textButon = butonSalvare.querySelector("span");

    textButon.innerText = "Salvează Modificările";
    butonSalvare.classList.add("btn-modificare");

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  window.reseteazaFormular = function () {
    window.idDocumentDeEditat = null;
    formular_adaugare.reset();
    document.getElementById("fisierDocument").setAttribute("required", "true");
    document.querySelector(".zona_formular_box h2").innerText =
      "Adaugă Act Nou";

    let bSpan = formular_adaugare.querySelector('button[type="submit"] span');
    bSpan.innerText = "Adaugă Document";
    bSpan.parentElement.classList.remove("btn-modificare");
  };

  formular_adaugare.onsubmit = async (ev) => {
    ev.preventDefault();
    let bSpan = formular_adaugare.querySelector('button[type="submit"] span');
    let tInitial = bSpan.innerText;
    bSpan.innerText = "Se salvează...";

    let dForm = new FormData();
    dForm.append("nume", document.getElementById("titlu").value);
    dForm.append("prop", document.getElementById("numeProprietar").value);
    dForm.append("tip", document.getElementById("tip").value);
    dForm.append("src", document.getElementById("numeInstitutie").value || "");

    let vExp = document.getElementById("dataExpirare").value;
    if (vExp) dForm.append("dExpirare", vExp);

    let fisierLista = document.getElementById("fisierDocument").files;
    if (fisierLista.length > 0) {
      dForm.append("fisier", fisierLista[0]);
    } else if (window.idDocumentDeEditat === null) {
      alert("Te rugăm să atașezi un document!");
      bSpan.innerText = tInitial;
      return;
    }

    let urlFetch = window.idDocumentDeEditat
      ? url_baza + "/" + window.idDocumentDeEditat
      : url_baza;
    let metodaFetch = window.idDocumentDeEditat ? "PUT" : "POST";

    try {
      let r = await fetch(urlFetch, { method: metodaFetch, body: dForm });
      if (!verificaLogare(r)) return;

      if (r.ok) {
        window.reseteazaFormular();
        incarca_acte();
        incarcaAlerte();
      } else {
        alert("Eroare server: " + (await r.text()));
      }
    } catch (errF) {
      alert("Nu se poate face conexiunea cu serverul.");
    } finally {
      bSpan.innerText = "Adaugă Document";
    }
  };

  async function incarcaAlerte() {
    try {
      let r;
      r = await fetch("/api/alerte");

      let isOk;
      isOk = r.ok;

      if (isOk === true) {
        let alerte;
        alerte = await r.json();

        let badge;
        badge = document.getElementById("badgeAlerte");

        let n;
        n = alerte.length;

        if (n > 0) {
          badge.innerText = n;
          badge.style.display = "block";

          let btnA;
          btnA = document.getElementById("btnAlerte");
          btnA.style.animation = "pulse 1.5s infinite";
        } else {
          badge.style.display = "none";
          document.getElementById("btnAlerte").style.animation = "none";
        }

        document.getElementById("btnAlerte").onclick = () => {
          let nrAlerte;
          nrAlerte = alerte.length;

          if (nrAlerte === 0) {
            alert("Toate documentele tale sunt valabile! Nu ai nicio alertă.");
            return;
          }

          let mesajComplet;
          mesajComplet = "⚠️ DOCUMENTE CARE EXPIRĂ CURÂND:\n\n";

          let j;
          j = 0;
          while (j < alerte.length) {
            let item;
            item = alerte[j];

            let zileRamaseTxt;
            zileRamaseTxt = "";

            let acteG;
            acteG = window.listaActeGlobala;

            let docAsociat;
            docAsociat = null;
            if (acteG) {
              docAsociat = acteG.find((d) => d.titlu === item.titluDocument);
            }

            if (docAsociat && docAsociat.dataExpirare) {
              let azi;
              azi = new Date();
              let dataExp;
              dataExp = new Date(docAsociat.dataExpirare);

              azi.setHours(0, 0, 0, 0);
              dataExp.setHours(0, 0, 0, 0);

              let t1;
              t1 = dataExp.getTime();
              let t2;
              t2 = azi.getTime();

              let difMs;
              difMs = t1 - t2;

              let divider;
              divider = 1000 * 3600 * 24;

              let diffZile;
              diffZile = Math.ceil(difMs / divider);

              if (diffZile > 0) {
                zileRamaseTxt = "(mai sunt " + diffZile + " zile)";
              } else {
                if (diffZile === 0) {
                  zileRamaseTxt = "(expiră ASTĂZI)";
                } else {
                  let absVal;
                  absVal = Math.abs(diffZile);
                  zileRamaseTxt = "(expirat de " + absVal + " zile)";
                }
              }
            } else {
              let m;
              m = item.mesaj;
              if (m) {
                zileRamaseTxt = "- " + m;
              } else {
                zileRamaseTxt = "(Expiră curând)";
              }
            }

            let linie;
            linie = "- " + item.titluDocument + " " + zileRamaseTxt + "\n";
            mesajComplet = mesajComplet + linie;
            j = j + 1;
          }

          alert(mesajComplet);
        };
      }
    } catch (errA) {}
  }

  async function pornireAplicatie() {
    await incarcaDateProfil();
    await incarca_acte();
    await incarcaAlerte();
  }

  pornireAplicatie();
});

window.trimiteCerereAcces = async function (idDoc) {
  document.getElementById("modalDocId").value = idDoc;
  document.getElementById("modalMotiv").value = "";

  try {
    let r = await fetch("/api/cereri/parinti");
    if (r.ok) {
      let parinti = await r.json();
      let select = document.getElementById("modalParinte");
      select.innerHTML = "";
      for (let i = 0; i < parinti.length; i++) {
        select.innerHTML += `<option value="${parinti[i].id}">Părinte: ${parinti[i].nume}</option>`;
      }
    }
  } catch (errP) {}
  document.getElementById("modalCerere").style.display = "flex";
};

window.proceseazaCerere = async function (idCerere, actiune) {
  try {
    let r = await fetch(`/api/cereri/${idCerere}?actiune=${actiune}`, {
      method: "PUT",
    });
    if (r.ok) {
      alert(
        actiune === "aprobare"
          ? "✅ Cererea a fost aprobată!"
          : "❌ Cererea a fost respinsă!",
      );
      location.reload();
    } else {
      alert("A apărut o eroare la procesarea cererii.");
    }
  } catch (errPr) {}
};

window.trimiteCerereFinal = async function () {
  let idDoc = document.getElementById("modalDocId").value;
  let motiv = document.getElementById("modalMotiv").value;
  let idPar = document.getElementById("modalParinte").value;

  if (motiv.trim().length === 0) {
    alert("Te rugăm să introduci un motiv pentru cererea de acces.");
    return;
  }

  try {
    let sParams = new URLSearchParams();
    sParams.append("idDoc", idDoc);
    sParams.append("motiv", motiv);
    sParams.append("idParinteAtribuit", idPar);

    let r = await fetch("/api/cereri", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: sParams,
    });

    if (r.ok) {
      document.getElementById("modalCerere").style.display = "none";
      alert("✅ Cererea a fost trimisă părintelui ales!");
    } else {
      alert("❌ Eroare la trimiterea cererii.");
    }
  } catch (errTr) {}
};

window.incarcaMembriFamilie = async function () {
  try {
    let r = await fetch("/api/familie");
    if (r.ok) {
      let membri = await r.json();
      let container = document.getElementById("lista-membri-container");
      let htmlFinal = "";

      for (let i = 0; i < membri.length; i++) {
        let m = membri[i];
        let rolStr = m.esteAdministrator
          ? "<span class='badge-parinte'>Părinte</span>"
          : "<span class='badge-copil'>Copil</span>";

        htmlFinal += `
          <li class="membru-item">
            <div>
              <strong class="nume-membru">${m.nume}</strong>
              <small class="email-membru">${m.email}</small>
            </div>
            ${rolStr}
          </li>
        `;
      }
      container.innerHTML = htmlFinal;
    }
  } catch (errG) {}
};

window.adaugaMembruFamilie = async function () {
  let vNume = document.getElementById("nouNume").value;
  let vPre = document.getElementById("nouPrenume").value;
  let vCnp = document.getElementById("nouCNP").value;
  let vE = document.getElementById("nouEmail").value;
  let vP = document.getElementById("nouParola").value;
  let vPreRol = document.getElementById("nouRol").value;

  if (!vNume || !vCnp || !vPre || !vE || !vP) {
    alert("Te rugăm să completezi toate câmpurile!");
    return;
  }

  try {
    let pDate = new URLSearchParams();
    pDate.append("nume", vNume);
    pDate.append("prenume", vPre);
    pDate.append("cnp", vCnp);
    pDate.append("email", vE);
    pDate.append("parola", vP);
    pDate.append("esteAdmin", vPreRol === "1");

    let r = await fetch("/api/familie", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: pDate,
    });

    if (r.ok) {
      alert("✅ Membru adăugat cu succes!");
      document.getElementById("nouNume").value = "";
      document.getElementById("nouPrenume").value = "";
      document.getElementById("nouCNP").value = "";
      document.getElementById("nouEmail").value = "";
      document.getElementById("nouParola").value = "";
      incarcaMembriFamilie();
    } else {
      alert("❌ A apărut o eroare la adăugare.");
    }
  } catch (errAdd) {}
};
