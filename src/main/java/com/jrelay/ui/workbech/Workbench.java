package com.jrelay.ui.workbech;

public sealed interface Workbench permits WorkbenchHttp, WorkbenchGraphQl, WorkbenchWebSocket, WorkbenchMcp {

}
