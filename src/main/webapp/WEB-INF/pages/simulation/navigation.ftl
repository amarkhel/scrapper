<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->

            <!-- Top Menu Items -->
            <ul class="nav navbar-right top-nav">
            
            

            </ul>
            <!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
            <div class="collapse navbar-collapse navbar-ex1-collapse">
                <ul class="nav navbar-nav side-nav">
                    <div id="accordion" class="panel-group">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">Посаженные</a>
            </h4>
        </div>
        <div id="collapseOne" class="panel-collapse collapse">

            <div class="panel-body">
                <p>Тут отображаются все посаженные</p>
                <#list converter.wrap(simulation.prisoners()) as player>
                <p>${player.name()} - ${player.role().role()}</p>
                </#list>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo">Убитые</a>
            </h4>
        </div>
        <div id="collapseTwo" class="panel-collapse collapse in">
            <div class="panel-body">
                <p>Тут отображаются все убитые</p>
                <#list converter.wrap(simulation.killed()) as player>
                <p>${player.name()} - ${player.role().role()}</p>
                </#list>
            </div>
        </div>
    </div>
    <#if converter.wrap(simulation.timeouted())??>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion" href="#collapseThree">Вышедшие по таймауту</a>
            </h4>
        </div>
        <div id="collapseThree" class="panel-collapse collapse">
            <div class="panel-body">
                <p>Тут отображаются вышедшие по таймауту</p>
                <#list converter.wrap(simulation.timeouted()) as player>
                <p>${player.name()} - ${player.role().role()}</p>
                </#list>
            </div>
        </div>
    </div>
    </#if>
</div>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </nav>