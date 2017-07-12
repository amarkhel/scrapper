<div class="row"><h1 class="page-header">Статистика игроков по системе Ювентуса</h1></div>
<div class="row"><h1 class="page-header">Лучший игрок</h1></div>
<div class="row">
	<div class="panel panel-green">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge">${game.playerStats().best().achievementScore()}</div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left"><a href="http://mafiaonline.ru/info/${game.playerStats().best().name()}">${game.playerStats().best().name()}</a></span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                    </div>
    <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> График рейтинга игроков</h3>
                            </div>
                            <div class="panel-body">
                                <div id="players-chart" style="width:100%"></div>
                            </div>
                        </div>
</div>
<#list converter.wrap(game.players()) as player>
<div class="row">
                         <h2><a href="http://mafiaonline.ru/info/${player.name()}"> ${player.name()}</a> (${player.achievementScore()})</h2>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Достижение</th>
                                        <th>Вклад в рейтинг</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(player.achievements()) as ach>
                                    <tr>
                                        <td>${ach.description()}</td>
                                        <td>${ach.ratingPoints()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
</#list>
<script>
function renderPlayers(){

$('#players-chart').highcharts({
        chart: {
           type: 'column',
			events: {
            	load: function(event) {
                	$(window).resize();
            	}
			}
        },
        title: {
            text: 'Рейтинг игроков в данной партии'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Рейтинг'
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
            name: 'Игроки',
            colorByPoint: true,
            data: ${playersRating}
        }]
        
    });
}

</script>
</div>