Ext.define('iNews.view.Main', {
	extend: 'Ext.navigation.View',
	xtype: 'mainpanel',

	requires: [
        "iNews.view.NewsCarousel",
        "iNews.view.NewsTpl1",
        "iNews.view.NewsTpl2",
        "iNews.view.NewsTpl3",
        "iNews.view.BlockView",
        "iNews.view.DetailView"
	],
	
	config: {
        navigationBar:false,
		items: [
            {
               xtype:'carouselview'
            }
		]
	}
});