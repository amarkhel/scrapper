<div class="row"><h1 class="page-header">Статистика сообщений</h1></div>
<div class="row">
			<div class="col-lg-3 col-md-6">
                <div class="panel panel-green">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge">${game.statistics().messageStats().countMessages()}</div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Сообщений за партию</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                        </div>
                        <div class="col-lg-3 col-md-6">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge">${game.statistics().messageStats().messagesPerMinute()}</div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Сообщений в минуту</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                         </div>
                        <div class="col-lg-3 col-md-6">
                        <div class="panel panel-yellow">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge">${game.statistics().messageStats().avgMessagesRound()}</div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Сообщений за раунд(в среднем)</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                         </div>
						<div class="col-lg-3 col-md-6">
                        <div class="panel panel-red">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge">${game.statistics().messageStats().countSmileMessages()}</div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Сообщений со смайлами</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                         </div>
                         </div>
<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> График количества сообщений</h3>
                            </div>
                            <div class="panel-body">
                                <div id="messages-per-player-chart" style="width:100%"></div>
                            </div>
                        </div>
</div>
<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> График количества смайлов в сообщениях</h3>
                            </div>
                            <div class="panel-body">
                                <div id="smiles-per-player-chart" style="width:100%"></div>
                            </div>
                        </div>
</div>
<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> График количества сообщений за раунд</h3>
                            </div>
                            <div class="panel-body">
                                <div id="messages-per-round-chart" style="width:100%"></div>
                            </div>
                        </div>
</div>
<script>
function renderMessages(){
$('#messages-per-player-chart').highcharts({
        chart: {
           type: 'column',
			events: {
            	load: function(event) {
                	$(window).resize();
            	}
			}
        },
        title: {
            text: 'Количество сообщений от каждого игрока'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Количество сообщений'
            }

        },
        legend: {
            enabled: false
        },

		plotOptions: {
            series: {
                borderWidth: 5,
                dataLabels: {
                    enabled: true
                }
            }
        },

        tooltip: {
            headerFormat: '<span style="font-size:13px">{series.name}</span><br>',
            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y}</b> из ${game.statistics().messageStats().countMessages()}<br/>'
        },
        series: [{
            name: 'Игроки',
            colorByPoint: true,
            data: ${playerMessages}
        }]
        
    });

$('#smiles-per-player-chart').highcharts({
        chart: {
           type: 'column',
			events: {
            	load: function(event) {
                	$(window).resize();
            	}
			}
        },
        title: {
            text: 'Количество смайлов от каждого игрока'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Количество смайлов'
            }

        },
        legend: {
            enabled: false
        },

		plotOptions: {
            series: {
                borderWidth: 5,
                dataLabels: {
                    enabled: true
                }
            }
        },

        tooltip: {
            headerFormat: '<span style="font-size:13px">{series.name}</span><br>',
            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y}</b> из ${game.statistics().messageStats().countSmiles()}<br/>'
        },
        series: [{
            name: 'Игроки',
            colorByPoint: true,
            data: ${smileMessages}
        }]
        
    });
$('#messages-per-round-chart').highcharts({
        chart: {
           type: 'column',
			events: {
            	load: function(event) {
                	$(window).resize();
            	}
			}
        },
        title: {
            text: 'Количество сообщений в каждом раунде'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Количество сообщений'
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
            name: 'Раунды',
            colorByPoint: true,
            data: ${roundMessages}
        }]
        
    });
}

</script>
