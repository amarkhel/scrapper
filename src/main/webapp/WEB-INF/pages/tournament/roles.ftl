<div class="row"><h1 class="page-header">Распределение ролей</h1></div>
<#list converter.wrap(tournament.possibleRoles()) as role>
<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i> ${role.role()}</h3>
                            </div>
                            <div class="panel-body">
                                <div id="roles-${role.hashCode()}" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
</div>
                        </#list>
<script>
function renderRoles(){
<#list converter.wrap(tournament.possibleRoles()) as role>
	renderRole('${role.hashCode()}', '${role.role()}', ${tournament.distributionRoles(role)});
</#list>
};

function renderRole(id, category, data){
	$('#roles-' + id).highcharts({
        chart: {
           type: 'column'
        },
        title: {
            text: 'Распределение за роль ' + category
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Количество партий'
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