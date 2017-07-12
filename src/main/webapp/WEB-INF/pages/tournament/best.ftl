<div class="row"><h2 class="page-header">Наибольшее количество игр</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th><b>Сыграл партий</b></th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>Побед</th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestCount()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td><b>${player.countPlayed()}</b></td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Наибольшее количество побед</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th><b>побед</b></th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestWin()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td><b>${player.winPoints()}</b></td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Лучший КПД</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th><b>КПД</b></th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>побед</th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestKPD()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td><b>${player.kpd()}</b></td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Наибольшее количество набранных очков</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th><b>Набрал очков</b></th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>побед</th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestPoints()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td><b>${player.points()}</b></td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Лучший КПД по системе ювентуса</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th><b>КПД по ювентусу</b></th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>побед</th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestPointssu()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td><b>${player.kpdsu()}</b></td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Количество раз, когда игрок был лучшим по системе ювентуса</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th><b>был лучшим(СЮ)</b></th>
                                        <th>побед</th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestsucount()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td><b>${player.bestinsu()}</b></td>
                                        <td>${player.winPoints()}</td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Количество очков за игру</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th><b>очков за игру</b></th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>побед</th>
                                        <th>процент Побед</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestperGame()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td><b>${player.pointsPerGame()}</b></td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                        <td>${player.percentWin()}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="row"><h2 class="page-header">Процент побед</h2></div>
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover table-striped">
                                <thead>
                                    <tr>
                                        <th>Игрок</th>
                                        <th>Сыграл партий</th>
                                        <th>Набрал очков</th>
                                        <th>Набрал очков по ювентусу</th>
                                        <th>КПД</th>
                                        <th>КПД по ювентусу</th>
                                        <th>Максимально возможное</th>
                                        <th>очков за игру</th>
                                        <th>был лучшим(СЮ)</th>
                                        <th>побед</th>
                                        <th><b>процент Побед</b></th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list converter.wrap(tournament.bestPercent()) as player>
                                    <tr>
                                        <td>${player.name()}</td>
                                        <td>${player.countPlayed()}</td>
                                        <td>${player.points()}</td>
                                        <td>${player.supoints()}</td>
                                        <td>${player.kpd()}</td>
                                        <td>${player.kpdsu()}</td>
                                        <td>${player.possiblePoints()}</td>
                                        <td>${player.pointsPerGame()}</td>
                                        <td>${player.bestinsu()}</td>
                                        <td>${player.winPoints()}</td>
                                        <td><b>${player.percentWin()}</b></td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>