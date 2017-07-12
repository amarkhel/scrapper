<#import "layout.ftl" as layout>
<@layout.layout>
      <form id="tournamentForm" class="form-signin" action="tournament" method="get">
      <div class="form-group">
                                <label>Список турниров</label>
                                <select id="tournaments" class="form-control">
                                	<#list names as name>
                                    <option name="${name}">${name}</option>
                                    </#list>
                                </select>
                                <button type="submit" class="btn btn-primary">Посмотреть</button>
                            </div>
      </form>
</@layout.layout>