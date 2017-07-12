<div class="row"><h1 class="page-header">Итоги</h1></div>
<#list converter.wrap(tournament.teams()) as team>
<div class="row"><h2 class="page-header">${team.name()}</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.clanStat(team)) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row">
                        <div class="col-lg-6">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Процентное соотношение набранных очков</h3>
                            </div>
                            <div class="panel-body">
                                <div id="clan-percent-${team.hashCode()}" style="width:100%; height: 300px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="col-lg-6">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Как заканчивалась игра</h3>
                            </div>
                            <div class="panel-body">
                                <div id="clan-finish-${team.hashCode()}" style="width:100%; height: 300px"></div>
                            </div>
                        </div>
                        </div>
                        </div>
</#list>
<script>
function renderClans(){
<#list converter.wrap(tournament.teams()) as team>
	renderClan('${team.hashCode()}', '${team.name()}', ${tournament.clanFinishStatus(team)}, ${tournament.clanPercents(team)});

</#list>
};
function renderClan(id, category, data,  data2){
$('#clan-percent-'+id).highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45
            }
        },
        title: {
            text: 'Процент набора очков'
        },
        subtitle: {
            text: category
        },
        plotOptions: {
            pie: {
                innerSize: 100,
                depth: 45
            }
        },
        series: data2
        });
$('#clan-finish-' + id).highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45
            }
        },
        title: {
            text: 'Как заканчивали игру'
        },
        subtitle: {
            text: category
        },
        plotOptions: {
            pie: {
                innerSize: 100,
                depth: 45
            }
        },
        series: data
        });
}
</script>