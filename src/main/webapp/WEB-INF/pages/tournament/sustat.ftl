<div class="row"><h1 class="page-header">Статистика по ювентусу</h1></div>
                        <#list converter.wrap(tournament.parts()) as part>
                        <div class="row"><h1 class="page-header">${part.name()}</h1></div>
                        <#list converter.wrap(part.games()) as game>
                        <div class="panel panel-green">
                            <div class="panel-heading">
                                <div class="row">
                                    <div class="col-xs-3">
                                        <i class="fa fa-tasks fa-5x"></i>
                                    </div>
                                    <div class="col-xs-9 text-right">
                                        <div class="huge"><a target="_blank" href="${rc.contextPath}/gameAnalyser?url=${game.id()?c}"> ${game.id()}</a></div>
                                        <div></div>
                                    </div>
                                </div>
                            </div>
                                <div class="panel-footer">
                                    <span class="pull-left">Лучший: <a target="_blank" href="http://mafiaonline.ru/info/${game.statistics().playerStatistics().best().name()}"> ${game.statistics().playerStatistics().best().name()}</a> (${game.statistics().playerStatistics().best().achievementScore()})</span>
                                    <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
                                    <div class="clearfix"></div>
                                </div>
                        </div>
                         </#list>
                        </#list>