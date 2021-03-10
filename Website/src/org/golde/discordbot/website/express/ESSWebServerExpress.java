package org.golde.discordbot.website.express;

import org.golde.discordbot.website.express.routes.Route404;
import org.golde.discordbot.website.express.routes.RouteRoot;
import org.golde.discordbot.website.express.routes.api.RouteAPIRoot;
import org.golde.discordbot.website.express.routes.api.RouteMembers;
import org.golde.discordbot.website.express.routes.api.RouteHomePageStats;

import express.Express;

public class ESSWebServerExpress {

	public static void startWebServer() {
		Express app = new Express();
        app.bind(new RouteRoot()); // See class below
        
        //API
        app.bind(new RouteAPIRoot());
        app.bind(new RouteHomePageStats());
        app.bind(new RouteMembers());
        
        app.bind(new Route404());
        
        app.listen(8889);
	}
	
}
