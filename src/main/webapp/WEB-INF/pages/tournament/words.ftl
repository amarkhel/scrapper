<div class="row">
	<h2 class="page-header">Статистика по употребляемым во время игры словам</h2>
</div>
<div class="table-responsive">
	<table id="overall" class="table table-bordered table-hover table-striped">
	    <thead>
	        <tr>
	            <th>Слово</th>
	            <th><b>Количество</b></th>
	            <th>Слово</th>
	            <th><b>Количество</b></th>
	            <th>Слово</th>
	            <th><b>Количество</b></th>
	            <th>Слово</th>
	            <th><b>Количество</b></th>
	        </tr>
	    </thead>
	    <tbody>

	    </tbody>
	</table>
</div>
<script>
	function renderTables(){
		renderTable('overall',${tournament.mostFrequentWords()});
	};
	function renderTable(id, data){
		var size = data.length;
		var table = $("#" + id + " tbody")
		var content = ""
		for(var i = 0; i < size;){
		    content += "<tr>"
			var count = 0;
		    if(size - i >= 4){
		       count = 4
		    } else {
		       count = size - i
		    }
		    for (var j = 0; j < count; j++) {
		        var elem = data[i+j];
		        content += "<td>" + elem[0] + "</td><td>" + elem[1] + "</td>";
		    }
		    content += "</tr>";
		    
		    i = i + count;
		}
		table.append(content);
    }
</script>
            