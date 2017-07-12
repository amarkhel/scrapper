<div class="row"><h2 class="page-header">Наибольшее количество сообщений</h2></div>
<div class="row">
                        <div class="col-lg-4"><div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th><b>Сообщений</b></th>
                                        <th>Сообщений за игру</th>
                                        <th>Смайлов</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestMessages()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td><b>${player.messages()}</b></td>
                                        <td>${player.messagesPerGame()}</td>
                                        <td>${player.smiles()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        <div class="col-lg-4"><div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сообщений</th>
                                        <th><b>Сообщений за игру</b></th>
                                        <th>Смайлов</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestMessagesPerGame()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.messages()}</td>
                                        <td><b>${player.messagesPerGame()}</b></td>
                                        <td>${player.smiles()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        <div class="col-lg-4"><div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сообщений</th>
                                        <th>Сообщений за игру</th>
                                        <th><b>Смайлов</b></th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestSmiles()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.messages()}</td>
                                        <td>${player.messagesPerGame()}</td>
                                        <td><b>${player.smiles()}</b></td>
                                        
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        </div>
<div class="row"><div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Смайл</th>
                                        <th>Количество</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.smilePopularity()) as smile>
                                    <tr>
                                        <td><img src="${smile.smile()}"/></td>
                                        <td>${smile.count()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Всего сообщений</h3>
                            </div>
                            <div class="panel-body">
                                <div id="message-overall" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Всего смайлов</h3>
                            </div>
                            <div class="panel-body">
                                <div id="message-smiles" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        
</div>
<div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Сообщений за игру</h3>
                            </div>
                            <div class="panel-body">
                                <div id="message-games" style="width:100%; height: 800px"></div>
                            </div>
                        </div>
                        </div>
<div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Сообщений за раунд</h3>
                            </div>
                            <div class="panel-body">
                                <div id="message-round" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Сообщений в секунду</h3>
                            </div>
                            <div class="panel-body">
                                <div id="message-second" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        
<script>
function renderMessages(){
renderMessage('overall','Количество сообщений за турнир', ${tournament.messages()});
renderMessage('smiles', 'Количество смайлов за турнир',${tournament.smiles()});
renderMessage('games', 'Количество сообщений за игру',${tournament.messagesGame()});
renderMessage('round', 'Количество сообщений за раунд',${tournament.messagesRound()});
renderMessage('second', 'Количество сообщений в минуту',${tournament.messagesMinute()});
}

function renderMessage(id, category, data){
	$('#message-' + id).highcharts({
        chart: {
           type: 'column'
        },
        title: {
            text: category
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Количество'
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