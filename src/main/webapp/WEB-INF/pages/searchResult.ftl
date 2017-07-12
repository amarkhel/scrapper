<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>Поисковик партий</title>
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<link rel="stylesheet" href="${rc.contextPath}/css/search/bootstrap.min.css">
<link rel="stylesheet" href="${rc.contextPath}/css/AdminLTE.min.css">
<link rel="stylesheet" href="${rc.contextPath}/css/_all-skins.min.css">
<link rel="stylesheet" href="${rc.contextPath}/plugins/datatables/dataTables.bootstrap.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<div class="content-wrapper" style="margin: 0px">
			<section class="content-header">
				<h1>Найдены партии</h1>
			</section>

			<section class="content">
				<div class="box box-default">
					<div class="box-header with-border">
						<h3 class="box-title"></h3>
					</div>
					<div class="box-body">
						<div class="row"><div class="col-sm-12"><table data-page-length="100" id="example1" class="table table-bordered table-striped dataTable" role="grid" aria-describedby="example1_info">
                <thead>
                <tr role="row" style="    background-color: aliceblue;">
                <th class="sorting_asc" tabindex="0" aria-controls="example1" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Сортировка по номеру партии">Номер партии</th>
                <th class="sorting" tabindex="0" aria-controls="example1" rowspan="1" colspan="1" aria-label="Сортировка по улице">Улица</th>
                <th class="sorting" tabindex="0" aria-controls="example1" rowspan="1" colspan="1" aria-label="Сортировка по количеству игроков">Количество игроков</th>
                <th tabindex="0" aria-controls="example1" rowspan="1" colspan="1" aria-label="" style="width:40%">Участники</th>
                <th class="sorting" tabindex="0" aria-controls="example1" rowspan="1" colspan="1" aria-label="Сортировка по результатам" >Результат</th>
                <th class="sorting" tabindex="0" aria-controls="example1" rowspan="1" colspan="1" aria-label="Сортировка по дате">Дата</th></tr>
                </thead>
                <tbody>
                <#list converter.wrap(games) as game>
   <tr role="row" class="${(game?index % 2==0)?string("odd", "even")}">

                  <td class="sorting_1" style="text-align:center;"><a target="_blank" href="${rc.contextPath}/simulation/${game.productElement(0)?c}/2">${game.productElement(0)?c}</a>
                  <a target="_blank" style="padding-left:5px;" href="https://mafiaonline.ru/log/${game.productElement(0)?c}"><img alt="Посмотреть на сайте мафии" style="width:16px; height:16px;" src="https://www.mafiaonline.ru/favicon.ico"/></a></td>
                  <td>${game.productElement(1)}</td>
                  <td>${game.productElement(4)}</td>
                  <td>${game.productElement(3)}</td>
                  <td>${game.productElement(2)}</td>
                  <td>${game.productElement(6)}</td>
                </tr>
                </#list>
                </tbody>
              </table></div></div>
              
        
					</div>
				</div>
			</section>
		</div>
	</div>

	<script src="${rc.contextPath}/js/simulation/jquery.min.js"></script>
	<script src="${rc.contextPath}/js/simulation/bootstrap.min.js"></script>
	
<script src="${rc.contextPath}/plugins/datatables/jquery.dataTables.min.js"></script>
              <script src="${rc.contextPath}/plugins/datatables/dataTables.bootstrap.min.js"></script>
              <script>
  $(function () {
    $('#example1').DataTable({
      "language": {
            "url": "//cdn.datatables.net/plug-ins/9dcbecd42ad/i18n/Russian.json"
        }
    });
  });
</script>
</body>
</html>      