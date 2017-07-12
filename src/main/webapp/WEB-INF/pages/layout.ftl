<#macro layout>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Анализатор партий</title>
    <!-- Bootstrap Core CSS -->
    <link href="${rc.contextPath}/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="${rc.contextPath}/css/sb-admin.css" rel="stylesheet">

    <!-- Morris Charts CSS -->
    <link href="${rc.contextPath}/css/plugins/morris.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="${rc.contextPath}/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <link rel='stylesheet' href='${rc.contextPath}/css/main.css'>
    <!-- jQuery -->
    <script src="${rc.contextPath}/js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="${rc.contextPath}/js/bootstrap.min.js"></script>

    <!-- Morris Charts JavaScript -->
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-3d.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
    <!-- <script src="js/plugins/morris/raphael.min.js"></script>
    <script src="js/plugins/morris/morris.min.js"></script>
    <script src="js/plugins/morris/morris-data.js"></script>-->
    <script src="${rc.contextPath}/js/main.js"></script>
</head>

<body>
        <#nested>
</body>
</html>
</#macro>