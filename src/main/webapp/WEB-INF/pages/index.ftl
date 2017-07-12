<#import "layout.ftl" as layout>
<@layout.layout>
      <form id="gameForm" class="form-signin" action="gameAnalyser" method="get">
        <h2 class="form-signin-heading">Анализатор партий</h2>
        <label for="url" class="sr-only">Партия</label>
        <div class="form-group input-group">
                                <span class="input-group-addon">#</span>
                                <input id="url" type="text" class="form-control" required autofocus placeholder="Номер партии">
                            </div>
        <button id="analyse" class="btn btn-lg btn-primary btn-block" type="submit">Проанализировать</button>
      </form>
</@layout.layout>