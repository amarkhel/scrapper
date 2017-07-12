<#import "layout.ftl" as layout>
<@layout.layout>
<div id="wrapper">

    <!-- Navigation -->
    <#include "tournament/navigation.ftl">

    <div id="page-wrapper">

        <div class="container-fluid">
			<div class="summary section">
				<#include "tournament/summary.ftl">
			</div>
			<div class="roles section hide">
				<#include "tournament/roles.ftl">
			</div>
			<div class="result section hide">
				<#include "tournament/result.ftl">
			</div>
			<div class="clan section hide">
				<#include "tournament/clan.ftl">
			</div>
			<div class="best section hide">
				<#include "tournament/best.ftl">
			</div>
			<div class="sustat section hide">
				<#include "tournament/sustat.ftl">
			</div>
			<div class="rules section hide">
				<#include "tournament/rules.ftl">
			</div>
			<div class="other section hide">
				<#include "tournament/other.ftl">
			</div>
			<div class="joke section hide">
				<#include "tournament/joke.ftl">
			</div>
			<div class="messages section hide">
				<#include "tournament/messages.ftl">
			</div>
			<div class="words section hide">
				<#include "tournament/words.ftl">
			</div>
        </div>
        <!-- /.container-fluid -->

    </div>
    <!-- /#page-wrapper -->

</div>
</@layout.layout>