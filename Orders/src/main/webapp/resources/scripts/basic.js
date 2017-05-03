/**
 * Created by sharlamov on 16.03.2017.
 */

function openDialog(dlg, form, title) {
    //PF(dlg + "_title").innerHTML = title;
    document.getElementById(form + ':' + dlg + "_title").innerHTML = title;
    PF(dlg).show();
}

function dlgHide(dlg) {
    PF(dlg).hide();
}

function dlgName(dialogName, title) {//
    document.getElementById(dialogName + "_title").innerHTML = title;
}