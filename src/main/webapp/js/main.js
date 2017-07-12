$(document).ready(function(){
	$('#searchForm').on("submit", function(event) {
        var form = $(this);
        var query =""
        var loc = $("#loc").val()
        if(loc) query += "location=" + loc + "&"
        var res = $("#res").val()
        if(res) query += "result=" + res + "&"
        var pl = $("#pl").val()
        if(pl) query += "player=" + pl + "&"
        var players = $("#pl2").val()
        if(players) query += "players=" + players + "&"
        var role = $("#role").val()
        if(role) query += "role=" + role + "&"
        var countPlayers = $("#countPlayers").val()
        if(countPlayers) query += "countPl=" + countPlayers + "&"
        var countRounds = $("#countRounds").val()
        if(countRounds) query += "countR=" + countRounds + "&"
        var sd = $("#startDay").val()
        if(sd) query += "day=" + sd + "&"
        var sm = $("#startMonth").val()
        if(sm) query += "month=" + sm + "&"
        var sy = $("#startYear").val()
        if(sy) query += "year=" + sy + "&"
        
        window.location = window.location.protocol + "//" + window.location.host +  form.attr('action') + '?' + query;
        return false;
    });
	$('#gameForm').on("submit", function(event) {
        var form = $(this);
        window.location = '' + form.attr('action') + '?url=' + $("#url").val();
        return false;
    });
    $('#simulationForm').on("submit", function(event) {
        var form = $(this);
        var r = $("#rounds").val()
        var a = ""
        if(r){
        	a = '/' + r
        }
        window.location = window.location.protocol + "//" + window.location.host +   form.attr('action') + '/' + $("#number").val() + a ;
        return false;
    });
    $('#tournamentForm').on("submit", function(event) {
        var form = $(this);
        window.location = '' + form.attr('action') + '/' + $("#tournaments").val();
        return false;
    });
    $('#url').keypress(function(event) {
        if (event.keyCode == 13) {
        	var form = $("#gameForm"); //wrap this in jQuery

            window.location = window.location.protocol + "//" + window.location.host + "/" +  form.attr('action') + '?url=' + $("#url").val();
            return false;
        }
    });
    $( "#rounds" ).change(function() {
    	var form = $("#simulationForm"); //wrap this in jQuery
    	  var order = $(this).val();
    	  window.location = window.location.protocol + "//" + window.location.host +   form.attr('action') + '/' + $("#number").val()+ '/' + order + "?prev=" + $("#rounds").val();
          return false;
    });
    $('#forward').click(function(event) {
        	var form = $("#simulationForm"); //wrap this in jQuery
        	window.location = window.location.protocol + "//" + window.location.host +   form.attr('action') + '/' + $("#number").val()+ '/' + (parseInt($("#order").val()) + 1) + "?prev=" + $("#order").val();
            return false;
    });
    $('#back').click(function(event) {
    	var form = $("#simulationForm"); //wrap this in jQuery
    	var a = form.attr('action')
    	var b = $("#number").val()
    	var c = (parseInt($("#order").val()) - 1)
    	window.location = window.location.protocol + "//" + window.location.host + form.attr('action') + '/' + $("#number").val()+ '/' + (parseInt($("#order").val()) - 1)+ "?prev=" + $("#order").val();
        return false;
});
    $('a.sectionLink').click(function(event) {
        var id = $(this).attr('id');
        $('div.section').addClass('hide');
        $('div.' + id).removeClass('hide');
        $('a.sectionLink').parent().removeClass('active');
        $(this).parent().addClass('active');
        if(id == 'messages'){
        	renderMessages();
        }
        if(id == 'players'){
        	renderPlayers();
        }
        if(id == 'roles'){
        	renderRoles();
        }
        if(id == 'result'){
        	renderParts();
        }
        if(id == 'clan'){
        	renderClans();
        }
        if(id == 'other'){
        	renderOther();
        }
        if(id == 'joke'){
        	renderJokes();
        }
        if(id == 'words'){
        	renderTables();
        }
        return false;
    });
});