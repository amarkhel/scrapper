<div class="row"><h1 class="page-header">Итоги</h1></div>
<#list converter.wrap(tournament.parts()) as part>
<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> ${part.name()}</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-${part.name()}" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
</div>
                        </#list>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Обшие очки без учета штрафов и урезаний</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-overall" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Обшие очки с учетом штрафов но без урезаний</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-overallPenalty" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Всего очков</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-overallTotal" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Всего очков в процентном соотношении с количеством возможных</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-overallPercent" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        
</div>
<div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Темп набора очковх</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-instant" style="width:100%; height: 800px"></div>
                            </div>
                        </div>
                        </div>
<div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Максимально возможное количество очков</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-possible" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Сумма команды по системе ювентуса</h3>
                            </div>
                            <div class="panel-body">
                                <div id="result-suPoints" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
<script>
Highcharts.setOptions({
     colors: ['#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263',   '#ccF203', '#FF0245', '#808080', '#227799', '#FFcc15', '#00F2FF', '#994463', '#111163', '#aa43cc',   '#6AF9C4']
    });
function renderParts(){
<#list converter.wrap(tournament.parts()) as part>
	renderResult('${part.name()}', '${part.name()} (без учета штрафа)', ${tournament.partResults(part?index)});
</#list>
renderResult('overall','Все партии без штрафов и урезаний', ${tournament.overallPoints()});
renderResult('overallPenalty', 'все партии со штрафами',${tournament.overallPenaltyPoints()});
renderResult('overallTotal', 'все партии(итого)',${tournament.overallTotalPoints()});
renderResult('overallPercent', 'процент набора очков',${tournament.overallPercentPoints()});
renderResult('possible', 'максимальное количество очков',${tournament.overallossiblePoints()});
renderResult('suPoints', 'Сумма команды по системе ювентуса',${tournament.overallsuPoints()});
$('#result-instant').highcharts({
        chart: {
            type: 'line'
        },
        title: {
            text: 'Темп набора очков'
        },

        yAxis: {
            title: {
                text: 'Очки'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: false
                },
                enableMouseTracking: false
            }
        },
        series: ${tournament.instantMap()}
        });
};

function renderResult(id, category, data){
	$('#result-' + id).highcharts({
        chart: {
           type: 'column'
        },
        title: {
            text: 'Очки за ' + category
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Очки'
            }

        },
        legend: {
            enabled: false
        },

		plotOptions: {
            series: {
                dataLabels: {
                    enabled: true
                }
            }
        },

        tooltip: {
            headerFormat: '<span style="font-size:13px">{series.name}</span><br>',
            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y}</b><br/>'
        },
        series: [{
            name: 'Команды',
            colorByPoint: true,
            data: data
        }]
        
    });
}
</script>