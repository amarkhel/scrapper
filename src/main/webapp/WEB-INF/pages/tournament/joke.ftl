<div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Уровень юмора в партии</h3>
                            </div>
                            <div class="panel-body">
                                <div id="jokelevel" style="width:100%; height: 400px"></div>
                            </div>
                        </div>
             </div>
             <div class="row">
                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title"><i class="fa fa-long-arrow-right"></i>Лучший клан голосование</h3>
                            </div>
                            <div class="panel-body">
                            <div class="col-lg-6 col-md-6">
                                <div id="bestclan" style="height: 400px"></div>
                               </div>
                               <div class="col-lg-6 col-md-6">
                                <div class="form-group">
                                <label>Проголосовать за</label>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">ЛВМ
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Хантеры
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">НС
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">БИ
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">РМ
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteCorrect();" name="lvm" id="lvm" value="U" checked="">У
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Гангста
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">ГГ
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">ХФ
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Вендетта
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Адеренте
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">БЧ
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Бухта
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Ладохи
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Криминалс
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" onclick="voteWrong();" name="lvm" id="lvm" value="ЛВМ" checked="">Баги
                                    </label>
                                </div>
                                <h3 id="correct" style="color:red" class="red panel-title hide">Браво! Вы угадали правильный ответ</h3>
                                <h3 id="wrong" style="color:red" class="red panel-title hide">Неправильный ответ! Ваш голос отдан Упийцам</h3>
                            </div>
                            </div>
                            </div>
                        </div>
             </div>
<script>
function voteCorrect(){
		$('#correct').addClass('hide');
		$('#wrong').addClass('hide');
		$('#correct').removeClass('hide');
		setTimeout(function() {
        	$("#correct").addClass('hide');
    	}, 3000);
	}
function voteWrong(){
		$('#correct').addClass('hide');
		$('#wrong').addClass('hide');
		$('#wrong').removeClass('hide');
		setTimeout(function() {
        	$("#wrong").addClass('hide');
    	}, 3000);
	}
function renderJokes(){
	$('#jokelevel').highcharts({
        chart: {
            type: 'column',
            options3d: {
                enabled: true,
                alpha: 10,
                beta: 25,
                depth: 70
            }
        },
        title: {
            text: 'Уровень юмора в партиях'
        },
        plotOptions: {
            column: {
                depth: 25
            }
        },
        xAxis: {
            categories: ['ГГ','ХФ','Вендетта','БЧ','Бухта','Ладошки','Гангста','У','НС','Хантеры','Адеренте','РМ','Boardwalk Empire','ЛВМ','Криминалс','Боги мафии',]
        },
        yAxis: {
            title: {
                text: 'Уровень юмора'
            }
        },
        series: [{
            name: 'Юмор',
            data: [20, 20, 20, 20, 20, 20, 20, 100, 20, 20, 20, 20, 20, 20, 20, 0]
        }]
    });
    $('#bestclan').highcharts({
        chart: {
           type: 'column'
        },
        title: {
            text: 'голосов за '
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: 'Голоса'
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
            data: [
                ['ЛВМ', 9],
                ['ГГ', 9],
                ['ХФ', 8],
                ['Ладохи', 9],
                ['Гангста гирлс', 8],
                ['У', 57],
                ['НС', 8],
                ['Бухта', 9],
                ['Хантеры', 8],
                ['Адеренте', 9],
                ['Боги мафии', 8],
                ['БЧ', 9],
                ['РМ', 8],
                ['Криминалс', 9],
                ['Вендетта', 9],
                ['БИ', 8]
            ]
        }]
        
    });
}
</script>