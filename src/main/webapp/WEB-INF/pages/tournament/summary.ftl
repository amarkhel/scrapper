
<div class="row"><h1 class="page-header">Общая статистика</h1></div>
                        <div class="row">
                         <h2>Участники</h2>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Команда</th>
                                        <th>Игроки</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.teams()) as team>
                                    <tr>
                                        <td>${team.name()}</td>
                                        <td>
                                        <#list converter.wrap(team.players()) as player>
                                        <a href="http://mafiaonline.ru/info/${player.name()}"> ${player.name()}</a>  
                                        </#list>
                                        </td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        <div class="row"><h1 class="page-header">Партии</h1></div>
                        <#list converter.wrap(tournament.parts()) as part>
                        <div class="row"><h2 class="page-header">${part.name()}</h2></div>
                        <div class="row">
                        <div class="col-lg-6">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Партия</th>
                                        <th>Результат</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(part.games()) as game>
                                    <tr>
                                        <td><a href="http://mafiaonline.ru/log/${game.id()?c}"> ${game.id()}</a> </td>
                                        <td>${game.tournamentResult().descr()}</a>  
                                        </td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        <div class="col-lg-6">
                        <div id="results-${part.name()}" style="height: 400px"></div>
                        </div>
                        </div>
                        </#list>
                        <div class="row"><h1 class="page-header">За все этапы</h1></div>
                        <div class="row">
                        <div class="col-lg-6">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Исход</th>
                                        <th>Количество</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.possibleResults()) as result>
                                    <tr>
                                        <td>${result.descr()}</td>
                                        <td>${tournament.countResults(result)}</a>  
                                        </td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        <div class="col-lg-6">
                        <div id="results" style="height: 400px"></div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="col-lg-6 col-md-6">
                <div class="panel panel-green">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge"><a target="_blank" href="/gameAnalyser?url=${tournament.longest().id()?c}"> ${tournament.longest().id()}</a></div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Самая длинная партия(${tournament.longest().duration()})</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                        </div>
                        <div class="col-lg-6 col-md-6">
                <div class="panel panel-green">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge"><a target="_blank" href="/gameAnalyser?url=${tournament.shortest().id()?c}"> ${tournament.shortest().id()}</a></div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Самая короткая партия(${tournament.shortest().duration()})</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                        </div>
                        </div>
<script>
function drawPie(id, data){
$('#' + id).highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            }
        },
        title: {
            text: 'Распределение итогов партии'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 35,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}'
                }
            }
        },
        series: [{
            type: 'pie',
            name: 'Распределение',
            data: data
        }]
    });
}
$(document).ready(function(){
    <#list converter.wrap(tournament.parts()) as part>
    drawPie("results-${part.name()}", ${part.resultDistribution()});
    </#list>
    $('#results').highcharts({
        chart: {
           type: 'column',
        },
        title: {
            text: 'Распределение итогов партий'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Исход'
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
            name: 'Исходы',
            colorByPoint: true,
            data: ${tournament.overallResults()}
        }]
        
    });
    });
</script>