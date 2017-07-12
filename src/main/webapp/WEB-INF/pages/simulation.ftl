<body>
	<div id="snippetContent" style="padding-top: 10px;">
<link rel="stylesheet" href="${rc.contextPath}/css/simulation/bootstrap.min.css">
<link rel="stylesheet" href="${rc.contextPath}/css/simulation/bootstrap-theme.min.css">
<script src="${rc.contextPath}/js/simulation/jquery.min.js"></script>
<script src="${rc.contextPath}/js/simulation/bootstrap.min.js"></script>
<script src="${rc.contextPath}/js/main.js"></script>
<link href="${rc.contextPath}/css/simulation/font-awesome.min.css" rel="stylesheet">
		<div class="container bootstrap snippet">
			<div class="row">
			<div class="col-md-3">
					<div class=" row border-bottom padding-sm" style="height: 40px;">
						<strong style="font-size:16px;color:yellow;"><center>Все еще в игре</center></strong></div>
					<ul class="friend-list" style="max-height: 600px;overflow: auto;margin-top: 20px;">
					<#list converter.wrap(simulation.alived(order)) as player>
					<li class="active bounceInDown"><a href="#" class="clearfix">
								<img src="${player.avatar()}" alt="avatar"
								class="img-circle">
							<div class="friend-name" style="text-align:center;font-size:14px; padding-top: 10px;">
									<strong style="padding-left: 10px;">${player.name()}</strong>
								</div>
						</a></li>
                   </#list>
						
					</ul>
				</div>
				<div class="col-md-6">
				<center>
				<div class="navbar-header">
                <center><p class="navbar-brand" style="color:white;">Смотровик партий - от Упийц</p></center>
            </div>
            <form id="simulationForm" align="center" action="${rc.contextPath}/simulation" method="post">
            <button id="back" style="background-color: blue;color: white;" type="button" class="btn btn-lg">
  <span  aria-hidden="true"></span> Назад
</button>
        <select id="rounds" name="rounds" style="padding:13px">
    <#list converter.wrap(simulation.roundsList()) as round>
        <option value="${round}"<#if (round == order)> selected="selected"</#if>>${round}</option>
    </#list>
</select>
<button id="forward" type="button" style="background-color: blue;color: white;" class="btn btn-lg">
  <span  aria-hidden="true"></span>  Вперед
</button>
<input id="order" type="hidden" name="order" value="${order}">
<input id="number" type="hidden" name="number" value="${simulation.id()?c}">
<input id="prev" type="hidden" name="prev" value="${prev}">
<input id="maxMessage" type="hidden" name="maxMessage" value="${simulation.maxMessageIndex(order)}">
</form>
</center>
					<div class="chat-message" style="max-height: 600px;overflow: auto;min-width: 570px;">
						<ul class="chat">
						<#list converter.wrap(simulation.chat()) as message>
                                	
									
									
									<#if message.invisible()>
									<li id="message${message?index}" style="visibility:hidden; height:0px" class="left clearfix invisible"><span class="chat-img pull-left">
									
							</span>
							<div class="chat-body clearfix">
									<div class="header" >
										<strong class="primary-font"></strong> <small
											class="pull-right text-muted"><i
											class="fa fa-clock-o"></i> </small>
									</div>
									<div id="round${message.text()}"> <p>${message.text()}</p></div>
									<#else>
									<li id="${message?index}" class="left clearfix messageIndex"><span class="chat-img pull-left">
									<img src="${message.avatar()}"
									alt="">
							</span>
							<div class="chat-body clearfix">
									<div class="header">
										<strong class="primary-font">${message.from()}</strong> <small
											class="pull-right text-muted"><i
											class="fa fa-clock-o"></i> ${message.time()}</small>
									</div>
									<#if message.system()>
									<div><p style="color:red; font-weight:bold">${message.text()}</p></div>
									<#else>
									<div><p>${message.text()}</p></div>
									</#if>
									</#if>
								</div></li>
                                  </#list>
						</ul>
					</div>
				</div>
				<div class="col-md-3">
				
					<div class=" row border-bottom padding-sm" style="height: 40px;">
						<strong style="font-size:16px;color:yellow;"><center>Вне игры</center></strong></div>
					<div style="max-height: 600px;overflow: auto; margin-top: 20px;    margin-left: 10px;    padding-right: 1px;">
					<ul id="off" class="friend-list" >
					<#list converter.wrap(simulation.off(order)) as player>
					<li class="active bounceInDown"><a href="#" style="cursor:text" class="clearfix">
								<img src="${player.avatar()}" alt="avatar"
								class="img-circle">
								<img src="${simulation.roleAvatar(player)}" alt="avatar"
								class="img-circle" style="float:right">
							<div class="friend-name" style="text-align:center;font-size:13px;">
									<strong style="padding-left: 10px;">${player.name()}</strong>
								</div>
								<div class="friend-name" style="text-align:center;font-size:13px;">
									<strong style="padding-left: 10px; color:red">${simulation.status(player, order)}</strong>
								</div>
						</a></li>
                   </#list>
						
					</ul>
					<div class=" row border-bottom padding-sm" style="height: 40px; width:90%">
						<strong style="font-size:16px;color:yellow;"><center style="margin-left: 40px;">Ходы</center></strong></div>
					<div id="accordion" class="panel-group">
					<#list converter.wrap(simulation.vote(order)) as vote>
    <div id="${vote.index()}" class="panel panel-default vote">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapse${vote.hashCode()}">${vote.descr()}</a>
            </h4>
        </div>
        <div id="collapse${vote.hashCode()}" class="panel-collapse collapse">
            <div class="panel-body">
            <#list converter.wrap(vote.votes()) as v>
                <p><span style="font-weight:bold; font-size:13px;">${v.target().name()}</span> <img style="width:10%" src="${rc.contextPath}/img/arrow.png" /> <span style="font-weight:bold; font-size:13px;">${v.destination().name()}</span></p>
                </#list>
            </div>
        </div>
    </div>
    </#list>
				</div>
			</div>
			</div>
		</div>
		<style type="text/css">
body {
	padding-top: 0;
	font-size: 12px;
	color: #777;
    background-color: #162110;
	font-family: 'Open Sans', sans-serif;
	margin-top: 20px;
}

.bg-white {
	background-color: #fff;
}

.friend-list {
	list-style: none;
	margin-left: -40px;
}

.friend-list li {
	border-bottom: 1px solid #eee;
}

.friend-list li a img {
	float: left;
	width: 45px;
	height: 45px;
	margin-right: 0px;
}

.friend-list li a {
	position: relative;
	display: block;
	padding: 10px;
	transition: all .2s ease;
	-webkit-transition: all .2s ease;
	-moz-transition: all .2s ease;
	-ms-transition: all .2s ease;
	-o-transition: all .2s ease;
}

.friend-list li.active a {
	background-color: #f1f5fc;
}

.friend-list li a .friend-name, .friend-list li a .friend-name:hover {
	color: #777;
}

.friend-list li a .last-message {
	width: 65%;
	white-space: nowrap;
	text-overflow: ellipsis;
	overflow: hidden;
}

.friend-list li a .time {
	position: absolute;
	top: 10px;
	right: 8px;
}

small, .small {
	font-size: 85%;
}

.friend-list li a .chat-alert {
	position: absolute;
	right: 8px;
	top: 27px;
	font-size: 10px;
	padding: 3px 5px;
}

.chat-message {
	padding: 10px 20px;
    margin-bottom: 10px;
}

.chat {
	list-style: none;
	margin: 0;
	padding:0;
}

.chat-message {
	background: #f9f9f9;
}

.chat li img {
	width: 45px;
	height: 45px;
	border-radius: 50em;
	-moz-border-radius: 50em;
	-webkit-border-radius: 50em;
}

img {
	max-width: 100%;
}

.chat-body {
	padding-bottom: 20px;
}

.chat li.left .chat-body {
	margin-left: 70px;
	background-color: #fff;
}

.chat li .chat-body {
	position: relative;
	font-size: 13px;
	padding: 10px;
	border: 1px solid #f1f5fc;
	box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
	-moz-box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
	-webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, .05);
}

.chat li .chat-body .header {
	padding-bottom: 6px;
    border-bottom: 1px solid #f1f5fc;
    margin-bottom: 4px;
}

.chat li .chat-body p {
	margin: 0;
}

.chat li.left .chat-body:before {
	position: absolute;
	top: 10px;
	left: -8px;
	display: inline-block;
	background: #fff;
	width: 16px;
	height: 16px;
	border-top: 1px solid #f1f5fc;
	border-left: 1px solid #f1f5fc;
	content: '';
	transform: rotate(-45deg);
	-webkit-transform: rotate(-45deg);
	-moz-transform: rotate(-45deg);
	-ms-transform: rotate(-45deg);
	-o-transform: rotate(-45deg);
}

.chat li.right .chat-body:before {
	position: absolute;
	top: 10px;
	right: -8px;
	display: inline-block;
	background: #fff;
	width: 16px;
	height: 16px;
	border-top: 1px solid #f1f5fc;
	border-right: 1px solid #f1f5fc;
	content: '';
	transform: rotate(45deg);
	-webkit-transform: rotate(45deg);
	-moz-transform: rotate(45deg);
	-ms-transform: rotate(45deg);
	-o-transform: rotate(45deg);
}

.chat li {
	margin: 15px 0;
}

.chat li.right .chat-body {
	margin-right: 70px;
	background-color: #fff;
}

.chat-box {
	position: fixed;
	bottom: 0;
	left: 444px;
	right: 0;
	padding: 15px;
	border-top: 1px solid #eee;
	transition: all .5s ease;
	-webkit-transition: all .5s ease;
	-moz-transition: all .5s ease;
	-ms-transition: all .5s ease;
	-o-transition: all .5s ease;
}

.primary-font {
	color: #3c8dbc;
}

a:hover, a:active, a:focus {
	text-decoration: none;
	outline: 0;
}
</style>
		<script type="text/javascript">
		$( document ).ready(function() {
          var prev = $("#prev").val();
          var chat = $(".chat-message");
          var pos = chat.scrollTop() + $("#round" + prev).offset().top - 70
		  if (prev > 0) {
		    chat.animate({scrollTop:pos}, 1000);
		  }
		  var index = $("#maxMessage").val();
		  var order = $("#order").val();
		  $(".messageIndex").each(function() {
             if(parseInt($( this ).attr("id")) <= index) {$(this).show()} else {$(this).hide()}
          });
          $(".vote").each(function() {
             if(parseInt($( this ).attr("id")) <= order) {
               $(this).show()
             }
             else {
               $(this).hide()
             }
          });
        });
		</script>
	</div>
</body>