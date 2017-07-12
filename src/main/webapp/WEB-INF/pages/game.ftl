<#import "layout.ftl" as layout>
<@layout.layout>
<div id="wrapper">

        <!-- Navigation -->
        <#include "game/navigation.ftl">

        <div id="page-wrapper">

            <div class="container-fluid">
				<div class="summary section">
					<#include "game/summary.ftl">
				</div>
				<div class="messages section hide">
					<#include "game/messages.ftl">
				</div>
				<div class="players section hide">
					<#include "game/players.ftl">
				</div>
            </div>
            <!-- /.container-fluid -->

        </div>
        <!-- /#page-wrapper -->

    </div>
</@layout.layout>