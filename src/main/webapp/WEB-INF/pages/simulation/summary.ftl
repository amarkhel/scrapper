<div class="row">
            <div class="navbar-header">
                <a class="navbar-brand" href="index.html">Смотровик партий  - Собственность клана Упийцы</a>
            </div>
            <form id="simulationForm" action="simulation" method="post">
            <button id="back" type="button" class="btn btn-default btn-lg">
  <span  aria-hidden="true"></span> < Назад
</button>
        <select id="rounds" name="rounds" style="padding:13px">
    <#list converter.wrap(simulation.roundsList()) as round>
        <option value="${round}"<#if (round == simulation.order())> selected="selected"</#if>>${round}</option>
    </#list>
</select>
<button id="forward" type="button" class="btn btn-default btn-lg">
  <span  aria-hidden="true"></span> > Вперед
</button>
<input id="order" type="hidden" name="order" value="${simulation.order()}">
<input id="number" type="hidden" name="number" value="${simulation.id()?c}">
</form>

</div>
<div class="row"><h1 class="page-header">Все еще в партии</h1></div>
                        <div class="row">
                        <#list converter.wrap(simulation.alived()) as player>
                        <div class="media col-xs-3">
                                                <a class="pull-left" href="#">
                                                    <img class="media-object img-circle " src="/img/user.png">
                                                </a>
                                                <div class="media-body">
                                                    ${player.name()}
                                                </div>
                                            </div>
                                        
                                    
                                  </#list>
                        </div>
                        <div class="row"><h1 class="page-header">Чат</h1>
                        <#list converter.wrap(simulation.chat()) as message>
                                	${message}
                                  </#list>
                        </div>