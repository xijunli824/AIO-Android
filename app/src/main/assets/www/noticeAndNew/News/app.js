   var domain = "https://maio.capitaretail.com.cn";
   var token = "";

  document.addEventListener("deviceready", function(){
	
    Cordova.exec(function(result){
        token = result.userToken;
		init();
    },null,"UserInfo", "GetUserInfo",[]);	

 }, false);
 
 function init(){
     Ext.Loader.setConfig({
        enabled: true
    });
    
    Ext.application({
        name: 'iNews',
        views:['Main'],
        controllers: ['MainController'],

        launch:function(){
        
		          Ext.Viewport.add(Ext.create("iNews.view.Main"));
			      document.addEventListener("backbutton", function(){
			            var viewtype = Ext.ComponentQuery.query('mainpanel')[0];
			            
			            switch (viewtype.getActiveItem().xtype){
			                case "carouselview":
			                    iNews.app.getController('MainController').onExitApp();
			                    break;
			                case "detailview":
			                    Ext.ComponentQuery.query("navigationview")[0].pop();
			                    break;
			            }
			        }, false);
		    //load news data here
            Custom.ux.Loading.show({modal:true});
            Ext.Ajax.request({
                url:domain+'/api/GetBulletinAndNewsListNew',
                method:'POST',
                params:{
                    startpage:1,
                    perpage:6,
                    tokenkey:token
                },
                success:function(responese){
                    Custom.ux.Loading.hide();
                    var resObj = Ext.decode(responese.responseText),
                        carousel = Ext.ComponentQuery.query('carouselview')[0],
                        newsTpl;
                    if(resObj[0].result == "True"){
						if(resObj[0].NewsList.length == 0){
							alert("没有数据!");
						}else{
							for(var i=0;i<resObj[0].NewsList.length;i++){
								switch(i%2){
									case 0:
										if(resObj[0].NewsList[i].length == 6){
											newsTpl  =  Ext.create("iNews.view.NewsTpl1",{});
											newsTpl.setRecords(resObj[0].NewsList[i]);									
										}
									break;
									case 1:
										if(resObj[0].NewsList[i].length == 6){
											newsTpl  =  Ext.create("iNews.view.NewsTpl2",{});
											newsTpl.setRecords(resObj[0].NewsList[i]);									
										}
									break;
								}
								carousel.add(newsTpl);
							}						
						}
                    }else{
                        console.log("请求失败!");
                    }
                },
                failure:function(){
                    Custom.ux.Loading.hide();
                    console.log("网络不可用!");
                }
            });
        }
    });
 }
