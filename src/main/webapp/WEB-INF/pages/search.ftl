<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Поисковик партий</title>
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<link rel="stylesheet" href="${rc.contextPath}/css/search/bootstrap.min.css">
<link rel="stylesheet" href="${rc.contextPath}/plugins/daterangepicker/daterangepicker.css">
<link rel="stylesheet" href="${rc.contextPath}/plugins/datepicker/datepicker3.css">
<link rel="stylesheet" href="${rc.contextPath}/plugins/timepicker/bootstrap-timepicker.min.css">
<link rel="stylesheet" href="${rc.contextPath}/plugins/select2/select2.min.css">
<link rel="stylesheet" href="${rc.contextPath}/plugins/bootstrap-slider/slider.css">
<link rel="stylesheet" href="${rc.contextPath}/css/AdminLTE.min.css">
<link rel="stylesheet" href="${rc.contextPath}/css/_all-skins.min.css">
<style type="text/css">
.slider-handle{
background-color:#f3be0b;
}
.tooltip-inner{
background-color:#2768c5;
}
.form-group {
margin-bottom:35px;
}

.select2-selection__choice {
    background-color: #00c0ef;
}
#countPlayers1 .slider-selection {
	background: #00c0ef;
}

#countRounds1 .slider-selection {
	background: #00c0ef;
}

#startYear1 .slider-selection {
    background: #00c0ef;
}

#startMonth1 .slider-selection {
    background: #00c0ef;
}

#startDay1 .slider-selection {
    background: #00c0ef;
}

</style>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<div class="content-wrapper" style="margin: 0px">
			<section class="content-header">
				<h1>Поиск партии по критериям</h1>
			</section>

			<section class="content">
				<div class="box box-default">
					<div class="box-header with-border">
						<h3 class="box-title"></h3>
					</div>
					<div class="box-body">
						<form id="searchForm" action="${rc.contextPath}/searchGame" method="post" style="margin:10px;">
							<div class="row">
								<div class="col-md-6">
									<div class="form-group">
										<label>Выберите Улицу</label> <select id="loc" name="loc"
											class="form-control select2" style="width: 100%;">
											<option></option>
											<#list converter.wrap(locations) as loc>
											<option>${loc.name()}</option>
											</#list>
											
										</select>
									</div>
									<div class="form-group">
										<label>Выберите исход партии</label> <select id="res" name="res"
											class="form-control select2" multiple="multiple"
											style="width: 100%;">
											<#list converter.wrap(results) as r>
											<option>${r.descr()}</option>
											</#list>
										</select>
									</div>
									<!-- /.form-group -->
									<div class="form-group">
										<label>Выберите игрока</label> <select id="pl" name="pl"
											class="form-control select2" 
											 style="width: 100%;" value="">
											 <option></option>
											<#list converter.wrap(players) as player>
											<option>${player}</option>
											</#list>
										</select>
									</div>
									<div class="form-group">
										<label>Выберите роль</label> <select id="role" name="role"
											class="form-control select2" multiple="multiple"
											style="width: 100%;">
											<#list converter.wrap(roles) as role>
											<option>${role.role()}</option>
											</#list>
											<option>Мафская роль</option>
											<option>Честная роль</option>
											<option>Комская роль</option>
										</select>
									</div>
									<div class="form-group">
									   <label>Совместно с </label> <select id="pl2" name="pl2" class="form-control select2"
									   style="width:100%;" multiple="multiple">
									   <option></option>
									   <#list converter.wrap(players) as player>
									   <option> ${player} </option>
									   </#list>
									   </select>
									   </div>

									
									<!-- /.form-group -->
								</div>

								<div class="col-md-6">

									<div class="form-group">
										<label>Количество игроков в партии</label> <input name="countPlayers"
											class="slider" id="countPlayers" data-slider-id='countPlayers1'
											type="text" data-provide="slider"
											data-slider-min="7" data-slider-max="21" data-slider-step="1"
											data-slider-tooltip="always" data-slider-value="[9,17]" />
									</div>

									<div class="form-group">
										<label>Количество раундов</label> <input class="slider" name="countRounds"
											id="countRounds" type="text" data-provide="slider"
											data-slider-id='countRounds1' data-slider-min="0"
											data-slider-max="130" data-slider-step="1"
											data-slider-tooltip="always" data-slider-value="[0,130]" />
									</div>
									<center><h3 style="color:green">Дата</h3></center>
									<div class="form-group">
										<label>Год</label> <input id="startYear" name="startYear"
											class="slider" data-slider-id='startYear1' type="text"
											data-provide="slider"
											data-slider-min="2012" data-slider-max="2017" data-slider-step="1"
											data-slider-tooltip="always" data-slider-value="[2012,2017]"/>
									</div>
									
									<div class="form-group">
										<label>Месяц</label> <input id="startMonth" name="startMonth"
											class="slider" data-slider-id='startMonth1' type="text"
											data-provide="slider"
											
											data-slider-min="1" data-slider-max="12" data-slider-step="1"
											data-slider-tooltip="always" data-slider-value="[1,12]"/>
									</div>
									<div class="form-group">
										<label>День</label> <input id="startDay" name="startDay"
											class="slider" data-slider-id='startDay1' type="text"
											data-provide="slider"
									
											data-slider-min="1" data-slider-max="31" data-slider-step="1"
											data-slider-tooltip="always" data-slider-value="[1,31]"/>
									</div>
				
									
									
								</div>

							</div>
							<div class="row">
								<button type="submit" class="btn btn-block btn-info">Найти</button>
							</div>
						</form>
					</div>
				</div>
			</section>
		</div>
	</div>

	<script src="${rc.contextPath}/js/simulation/jquery.min.js"></script>
	<script src="${rc.contextPath}/js/simulation/bootstrap.min.js"></script>
	<script src="${rc.contextPath}/plugins/select2/select2.full.min.js"></script>
	<!-- InputMask -->
	<script src="${rc.contextPath}/plugins/input-mask/jquery.inputmask.js"></script>
	<script
		src="${rc.contextPath}/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
	<script
		src="${rc.contextPath}/plugins/input-mask/jquery.inputmask.extensions.js"></script>
	<!-- date-range-picker -->
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.11.2/moment.min.js"></script>
	<script
		src="${rc.contextPath}/plugins/daterangepicker/daterangepicker.js"></script>
	<!-- bootstrap datepicker -->
	<script
		src="${rc.contextPath}/plugins/datepicker/bootstrap-datepicker.js"></script>
	<!-- bootstrap color picker -->
	<script
		src="${rc.contextPath}/plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
	<!-- bootstrap time picker -->
	<script
		src="${rc.contextPath}/plugins/timepicker/bootstrap-timepicker.min.js"></script>
	<script
		src="${rc.contextPath}/plugins/bootstrap-slider/bootstrap-slider.js"></script>
		<script
		src="${rc.contextPath}/js/main.js"></script>
	
	<!-- Page script -->
	<script>
  $(function () {
    //Initialize Select2 Elements
    $(".select2").select2();
    $('.slider').slider();
    //Date range picker with time picker
    $('#time').daterangepicker({timePicker: true, timePickerIncrement: 30, format: 'DD/MM/YYYY hh:mm'});
  });
</script>
</body>
</html>
