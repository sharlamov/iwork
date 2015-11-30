/**
 * Created by sharlamov on 19.10.2015.
 */

$(document).ready(function () {
    setColorBoxPanel();
    colorSelectCulture();
});

function colorSelectCulture() {

    $('ul.ui-selectonemenu-items.ui-selectonemenu-list.ui-widget-content.ui-widget.ui-corner-all.ui-helper-reset li').each(function (ind, el) {
        var text = $(el).text();
        var ind = text.indexOf('#');
        if (ind > -1) {
            var color = text.substring(ind);
            var name = text.substring(0, ind);
            $(el).text(name);

            $(el).attr('data-icon','ui-icon-play');
            $(el).attr('data-label', name);

            var span = document.createElement('span')
            span.className = 'colorSpan';
            span.style.backgroundColor = color;
            $(el).prepend(span);
        }
    })

}

function setColorBoxPanel() {
    //console.log('test: ' + $('div.colorBoxPanel div'));
    $('div.colorBoxPanel div').each(function (ind, el) {
        var text = $(el).text();
        var ind = text.indexOf('#');
        if (ind > -1) {
            var color = text.substring(ind);
            var name = text.substring(0, ind);

            $(el).css('width', '150px');
            var bText = $(el).find('span.ui-button-text.ui-c')
            bText.text(name);
            bText.css('display', 'inline-block');

            $(el).find('span.colorSpan').remove();

            var span = document.createElement('span')
            span.className = 'colorSpan';
            span.style.backgroundColor = color;
            $(el).prepend(span);
        }
    });

}

function soldsModel() {
    this.cfg.animate = true;
    this.cfg.axes.yaxis.tickOptions.formatter = tickAxisNull;

}

function positionModel() {
    var ticksCount = this.cfg.axes.yaxis.ticks.length;

    this.cfg.stackSeries = true;
    this.cfg.seriesDefaults = {
        pointLabels: {show: true, formatString: '%d'},
        renderer: $.jqplot.BarRenderer,
        rendererOptions: {
            barDirection: 'horizontal',
            barWidth: (ticksCount < 4) ? 50 : null,
        }
    };
    this.cfg.axes.xaxis.tickOptions.labelPosition = 'end';
    this.cfg.axes.yaxis.tickRenderer = $.jqplot.CanvasAxisTickRenderer;
    this.cfg.axes.yaxis.renderer = $.jqplot.CategoryAxisRenderer;

}

function tickAxisNull(format, value) {
    return "";
}

function tickAxisFormat(format, value) {
    return ( Math.abs(value) > 999 ) ? (value / 1000 ).toString() + 't' : value;
}

function modelPaymentExt() {
    this.cfg.animate = true,
        this.cfg.legend = {
            show: true,
            location: 'e'
        },
        this.cfg.axes = {
            xaxis: {
                renderer: $.jqplot.DateAxisRenderer,
                tickOptions: {
                    formatString: '%d.%m.%y',
                    angle: -90
                }
            },
            yaxis: {
                min: 0,
                tickOptions: {
                    formatter: tickAxisFormat
                }
            }
        };
}

function horizontalBarExt() {
    this.cfg.animate = true;
    this.cfg.stackSeries = true;
    this.cfg.captureRightClick = true;
    this.cfg.seriesDefaults = {
        renderer: $.jqplot.BarRenderer,
        shadowAngle: 135,
        rendererOptions: {
            barDirection: 'horizontal',
            highlightMouseDown: true
        },
        pointLabels: {show: true, formatString: '%d'},
        linePattern: 'dotted',
    };

    this.cfg.axes.xaxis.tickOptions.labelPosition = 'end';
    this.cfg.axes.xaxis.tickOptions.formatter = tickAxisFormat;
    this.cfg.axes.yaxis.tickRenderer = $.jqplot.CanvasAxisTickRenderer;
    this.cfg.axes.yaxis.renderer = $.jqplot.CategoryAxisRenderer;
}

function elevatorsBarExt() {
    var ticksCount = this.cfg.axes.xaxis.ticks.length;
    var cWidth = ($(document).width() / ticksCount > 150) ? 100 : null


    this.cfg.animate = false;
    this.cfg.stackSeries = true;
    this.cfg.captureRightClick = true;
    this.cfg.seriesDefaults = {
        renderer: $.jqplot.BarRenderer,
        shadow: false,
        rendererOptions: {
            highlightMouseDown: true,
            barWidth: ($(document).width() / ticksCount > 150) ? 100 : null,
            smooth: false
        },
        markerOptions: {
            show: true,
            shadow: false,
            size: 2
        },

    };
    this.cfg.axes.yaxis.tickOptions.formatter = tickAxisFormat;
}


function calc() {

    $.getJSON("/elevatorsSold?season=" + 2015 + "&region=" + 1, function (result) {

        var chart = new CanvasJS.Chart("elevatorSoldChart", {
            title: {
                text: "Cost Of Pancake Ingredients, 2011"
            },
            animationEnabled: true,
            axisX: {
                interval: 1,
                labelFontSize: 10,
                lineThickness: 0
            },
            axisY: {
                valueFormatString: "0",
                lineThickness: 0
            },

            legend: {
                verticalAlign: "top",
                visible: false,
                horizontalAlign: "center"

            },
            data: result
        });

        chart.render();
        $(".canvasjs-chart-credit").remove();
    });
}