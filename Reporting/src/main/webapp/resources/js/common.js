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

PrimeFaces.locales ['ru'] = {
    closeText: 'Закрыть',
    prevText: 'Назад',
    nextText: 'Вперёд',
    monthNames: ['Январь', 'Февраль' , 'Март' , 'Апрель' , 'Май' , 'Июнь' , 'Июль' , 'Август' , 'Сентябрь','Октябрь','Ноябрь','Декабрь' ],
    monthNamesShort: ['Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн', 'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек' ],
    dayNames: ['Воскресенье', 'Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Субота'],
    dayNamesShort: ['Воск','Пон' , 'Вт' , 'Ср' , 'Четв' , 'Пят' , 'Суб'],
    dayNamesMin: ['В', 'П', 'Вт', 'С ', 'Ч', 'П ', 'Сб'],
    weekHeader: 'Неделя',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix:'',
    timeOnlyTitle: 'Только время',
    timeText: 'Время',
    hourText: 'Час',
    minuteText: 'Минута',
    secondText: 'Секунда',
    currentText: 'Сегодня',
    ampm: false,
    month: 'Месяц',
    week: 'неделя',
    day: 'День',
    allDayText: 'Весь день'
};