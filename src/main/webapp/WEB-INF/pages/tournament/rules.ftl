<div class="row"><h2 class="page-header">Таблица начисления очков</h2></div>
<div class="row">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Роль</th>
                                        <th>Количество игроков</th>
                                        <th>Результат</th>
                                        <th>Вышел по таймауту</th>
                                        <th>Очки</th>
                                        <th>Формула(для маньяка)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.pointRules()) as rule>
                                    <tr>
                                        <td>${rule.role()}</td>
                                        <#if rule.count() gt 0>
                                        <td>${rule.count()}</td>
                                        <#else>
                                        <td>Любое</td>
                                        </#if>
                                        
                                        <td><#if rule.result()??>${rule.result()}</#if></td>
                                        <td>${rule.isTimeout()?string('Да', 'Нет')}</td>
                                        <#if rule.points() gte 0>
                                        <td>${rule.points()}</td>
                                        </#if>
                                        <td>${rule.formula()!'-'}</td>
                                        
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        </div>