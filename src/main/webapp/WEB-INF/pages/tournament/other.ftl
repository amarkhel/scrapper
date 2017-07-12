<div class="row"><h1 class="page-header">Разное</h1></div>
<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Стастика убитых</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-killed" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
</div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> Статистика проверок</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-checked" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Статистика посадок</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-prisoned" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Статистика убийств первой ночью</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-killedFirst" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Статистика проверок первым ходом</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-checkedFirst" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        
</div>
<div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Статистика убийств первой ночью</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-prisonedFirst" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
                        <div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Самые алибные мафы</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-bestAlibi" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
<div class="row">
<div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Самые алибные мафы(процент дневных ходов)</h3>
                            </div>
                            <div class="panel-body">
                                <div id="other-bestAlibiPercent" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
                        </div>
<script>
function renderOther(){
renderPart('killed','Стастика убийств', ${tournament.distributionKilled()});
renderPart('checked', 'Проверки',${tournament.distributionChecked()});
renderPart('prisoned', 'Посажены',${tournament.distributionPrisoned()});
renderPart('killedFirst', 'убиты первый день',${tournament.distributionKilledFirst()});
renderPart('checkedFirst', 'проверены первый ход',${tournament.distributionCheckedFirst()});
renderPart('prisonedFirst', 'посажены первый день',${tournament.distributionPrisonedFirst()});
renderPart('bestAlibi', 'Наибольшее количество ходов на напарника',${tournament.distributionBestAlibi()});
renderPart('bestAlibiPercent', 'Наибольшее количество ходов на напарника(процент)',${tournament.distributionBestAlibiPercent()});
};
function renderPart(id, category, data){
	$('#other-' + id).highcharts({
        chart: {
           type: 'column'
        },
        title: {
            text: '' + category
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