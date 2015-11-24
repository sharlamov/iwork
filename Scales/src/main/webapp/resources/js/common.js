function handleLoginRequest(xhr, status, args) {
	if (!args.validationFailed && args.loggedIn) {
		window.location.replace(args.loggedPage);
	}
}

function calcNetto() {
	var bruto = document.getElementById("formDlg1:fbruto").value;
	var tara = document.getElementById("formDlg1:ftara").value;
	document.getElementById("formDlg1:fnetto").innerHTML = Math
			.round((bruto - tara) * 100) / 100;
}

function hasClass( elem, klass ) {
    return (" " + elem.className + " " ).indexOf( " "+klass+" " ) > -1;
}

function disableBlock(id, isDisabled){
	var inputs = document.getElementById(id).getElementsByTagName('input');
	var buttons = document.getElementById(id).getElementsByTagName('button');
	 
	for (var i = 0; i < inputs.length; i++) {
		inputs[i].disabled = isDisabled;
	}
	
	for (var i = 0; i < buttons.length; i++) {
		buttons[i].style.display = isDisabled ? "none" : "inline-block";
	}
	
	var divs = document.getElementById(id).getElementsByTagName('div');
	var size = isDisabled ? "300px" : "265px";
	for(var i = 0; i < divs.length; i++){
		if(hasClass(divs[i],'dropListFind')){
			var divInputs = divs[i].getElementsByTagName('input');
			for (var j = 0; j < divInputs.length; j++) {
				divInputs[j].style.width = size;
			}
		} 
	}
}

function openListEdit(formId, panelId, isOpen) {	
	disableBlock(formId, isOpen);

	var item0 = formId + ':' + panelId + ':itemEdit0';
	var item1 = formId + ':' + panelId + ':itemEdit1';
	
	disableBlock(item1, !isOpen);

	document.getElementById(item0).style.display = isOpen ? "none" : "block";
	document.getElementById(item1).style.display = isOpen ? "block" : "none";
}

function setDialogName(dialogName) {
	document.getElementById("formDlg1:editDialog_title").innerHTML = dialogName;
}

PrimeFaces.locales['ro'] = {
	closeText : 'Închide',
	prevText : 'Înapoi',
	nextText : 'Înainte',
	currentText : 'Acasă',
	monthNames : [ 'Ianuarie', 'Februarie', 'Martie', 'Aprilie', 'Mai',
			'Iunie', 'Iulie', 'August', 'Septembrie', 'Octombrie', 'Noiembrie',
			'Decembrie' ],
	monthNamesShort : [ 'Ian', 'Feb', 'Mar', 'Apr', 'Mai', 'Iun', 'Iul', 'Aug',
			'Sep', 'Oct', 'Noi', 'Dec' ],
	dayNames : [ 'Duminică', 'Luni', 'Marți', 'Miercuri', 'Joi', 'Vineri',
			'Sâmbătă' ],
	dayNamesShort : [ 'Dum', 'Lun', 'Mar', 'Mie', 'Joi', 'Vin', 'Sâm' ],
	dayNamesMin : [ 'D', 'L', 'M', 'Mi', 'J', 'V', 'S' ],
	weekHeader : 'Săptămâna',
	firstDay : 1,
	isRTL : false,
	showMonthAfterYear : false,
	yearSuffix : '',
	timeOnlyTitle : 'Numai timp',
	timeText : 'Timp',
	hourText : 'Ora',
	minuteText : 'Minut',
	secondText : 'Secunde',
	ampm : false,
	month : 'Luna',
	week : 'Săptămâna',
	day : 'Zi',
	allDayText : 'Toată ziua'
};