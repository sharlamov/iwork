function handleLoginRequest(xhr, status, args) {
    if (!args.validationFailed && args.loggedIn) {
        window.location.replace(args.loggedPage);
    }
}

function hasClass(elem, klass) {
    return (" " + elem.className + " " ).indexOf(" " + klass + " ") > -1;
}

PrimeFaces.locales['ro'] = {
    closeText: 'Închide',
    prevText: 'Înapoi',
    nextText: 'Înainte',
    currentText: 'Acasă',
    monthNames: ['Ianuarie', 'Februarie', 'Martie', 'Aprilie', 'Mai',
        'Iunie', 'Iulie', 'August', 'Septembrie', 'Octombrie', 'Noiembrie',
        'Decembrie'],
    monthNamesShort: ['Ian', 'Feb', 'Mar', 'Apr', 'Mai', 'Iun', 'Iul', 'Aug',
        'Sep', 'Oct', 'Noi', 'Dec'],
    dayNames: ['Duminică', 'Luni', 'Marți', 'Miercuri', 'Joi', 'Vineri',
        'Sâmbătă'],
    dayNamesShort: ['Dum', 'Lun', 'Mar', 'Mie', 'Joi', 'Vin', 'Sâm'],
    dayNamesMin: ['D', 'L', 'M', 'Mi', 'J', 'V', 'S'],
    weekHeader: 'Săptămâna',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Numai timp',
    timeText: 'Timp',
    hourText: 'Ora',
    minuteText: 'Minut',
    secondText: 'Secunde',
    ampm: false,
    month: 'Luna',
    week: 'Săptămâna',
    day: 'Zi',
    allDayText: 'Toată ziua'
};

