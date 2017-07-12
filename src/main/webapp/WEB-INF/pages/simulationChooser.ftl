<#import "layout.ftl" as layout>
<@layout.layout>
      <form id="simulationForm" class="form-signin" action="${rc.contextPath}/simulation" method="get">
        <h2 class="form-signin-heading">Просмотрщик партий</h2>
        <label for="number" class="sr-only">Партия</label>
        <div class="form-group input-group">
                                <span class="input-group-addon">#</span>
                                <#if simulation??>
                                <input id="number" type="text" class="form-control" required autofocus placeholder="Номер партии" disabled="disabled" value="${simulation.id()?c}">
   <select id="rounds" name="rounds" class="form-control" required autofocus placeholder="Заканчивая раундом">
    <#list converter.wrap(simulation.roundsList()) as round>
        <option value="${round}">${round}</option>
    </#list>
</select>
                                <#else>
                                <input id="number" type="text" class="form-control" required autofocus placeholder="Номер партии">
                                </#if>
                                
                            </div>
        <button id="analyse" class="btn btn-lg btn-primary btn-block" type="submit">Посмотреть</button>
      </form>
</@layout.layout>