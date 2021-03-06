<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.html">Анализатор партий  - Собственность клана Упийцы</a>
            </div>
            <!-- Top Menu Items -->
            <ul class="nav navbar-right top-nav">
                <form id="gameForm" class="form-inside" action="gameAnalyser" method="get">
        			<label for="url" class="sr-only">Партия</label>
        				<div class="input-group">
                            <span class="input-group-addon">#</span>
                            <input id="url" type="text" class="form-control" required autofocus placeholder="Номер партии">
                        </div>
      			</form>
            </ul>
            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
            <div class="collapse navbar-collapse navbar-ex1-collapse">
                <ul class="nav navbar-nav side-nav">
                    <li class="active">
                        <a class="sectionLink" id="summary" href="#summary"><i class="fa fa-fw fa-dashboard"></i> Основная информация</a>
                    </li>
                    <li>
                        <a class="sectionLink" id="messages" href="#messages"><i class="fa fa-fw fa-dashboard"></i> Стастистика сообщений</a>
                    </li>
                    <li>
                        <a class="sectionLink" id="players" href="#players"><i class="fa fa-fw fa-dashboard"></i> Стастистика по системе ювентуса</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </nav>